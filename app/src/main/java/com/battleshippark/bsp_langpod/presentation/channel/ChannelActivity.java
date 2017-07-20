package com.battleshippark.bsp_langpod.presentation.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerDomainMapperGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.download.DownloadProgressParam;
import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;
import com.battleshippark.bsp_langpod.domain.DomainMapper;
import com.battleshippark.bsp_langpod.domain.DownloadMedia;
import com.battleshippark.bsp_langpod.domain.GetChannel;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ChannelActivity extends Activity implements OnItemListener {
    private static final String TAG = ChannelActivity.class.getSimpleName();
    private static final String KEY_ID = "keyId";
    private static final float MEGA_BYTE = 1024 * 1024 * 1.0f;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.channel_rv)
    RecyclerView rv;
    @BindView(R.id.msg_tv)
    TextView msgTextView;

    private CompositeSubscription subscription = new CompositeSubscription();
    private ChannelAdapter adapter;

    private GetChannel getChannel;
    private SubscribeChannel subscribeChannel;
    private Unbinder unbinder;

    private long channelId;
    private DownloadMedia downloadMedia;
    private PublishSubject<DownloadProgressParam> downloadProgress = PublishSubject.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        unbinder = ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();

        showChannel();
    }

    private void initData(Bundle savedInstanceState) {
        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();
        ChannelServerApi channelServerApi = DaggerServerApiGraph.create().channelApi();
        DomainMapper domainMapper = DaggerDomainMapperGraph.create().domainMapper();

        getChannel = new GetChannel(channelDbApi, channelServerApi, Schedulers.io(), AndroidSchedulers.mainThread(), domainMapper);
        subscribeChannel = new SubscribeChannel(channelDbApi);
        downloadMedia = new DownloadMedia(this, Schedulers.io(), AndroidSchedulers.mainThread(), new AppPhase(BuildConfig.DEBUG), downloadProgress);

        subscription.add(
                downloadProgress
                        .throttleFirst(500, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onDownloadProgress));

        adapter = new ChannelAdapter(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (adapter.getItemCount() == 0) {
                    rv.setVisibility(View.GONE);
                    msgTextView.setVisibility(View.VISIBLE);
                    msgTextView.setText(R.string.my_list_empty_msg);
                } else {
                    rv.setVisibility(View.VISIBLE);
                    msgTextView.setVisibility(View.GONE);
                }
            }
        });

        if (savedInstanceState == null) {
            channelId = getIntent().getLongExtra(KEY_ID, 0);
        } else {
            channelId = savedInstanceState.getLong(KEY_ID);
        }
    }

    private void initUI() {
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void showChannel() {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(
                getChannel.execute(channelId)
                        .subscribe(this::showData, this::showError, this::dataCompleted));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        unbinder.unbind();
        super.onDestroy();
    }

    void showData(List<ChannelRealm> channelRealmList) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        adapter.updateData((OrderedRealmCollection<ChannelRealm>) channelRealmList);
    }

    void showError(Throwable throwable) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
        msgTextView.setVisibility(View.VISIBLE);
        msgTextView.setText(R.string.my_list_error_msg);
        Log.w(TAG, throwable);
    }

    void dataCompleted() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBindHeaderViewHolder(ChannelAdapter.HeaderViewHolder holder, ChannelRealm channel) {
//        holder.itemView.setOnClickListener(v -> mListener.onClickMyChannelItem(item));

        Glide.with(holder.imageView.getContext()).load(channel.getImage()).into(holder.imageView);

        holder.descView.setText(channel.getDesc());
        holder.copyrightView.setText(channel.getCopyright());
        holder.episodeCountView.setText(String.valueOf(channel.getEpisodes().size()));

        holder.subscribeView.setSelected(channel.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(channel)
                        .subscribe(
                                aVoid -> {
                                },
                                throwable -> Log.w(TAG, throwable)
                        )
        );
    }

    @Override
    public void onBindEpisodeViewHolder(ChannelAdapter.EpisodeViewHolder holder, EpisodeRealm episode) {
        holder.itemView.setOnClickListener(v -> onClickEpisode(episode));

        holder.descView.setText(episode.getDesc());
        holder.dateView.setText(new SimpleDateFormat("MM/dd", Locale.US).format(episode.getDate()));
        holder.statusTv.setText(getStatusText(episode));

        Glide.with(this).load(getStatusImage(episode)).into(holder.statusIv);
/*        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
                        .subscribe(
                                aVoid -> {
                                },
                                throwable -> Log.w(TAG, throwable)
                        )
        );*/
    }

    private void onClickEpisode(EpisodeRealm episode) {
        if (episode.getDownloadState() == EpisodeRealm.DownloadState.NOT_DOWNLOADED) {
            startDownload(episode);
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADING) {
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADED) {
            if (episode.getPlayState() == EpisodeRealm.PlayState.NOT_PLAYED
                    || episode.getPlayState() == EpisodeRealm.PlayState.PLAYED) {
            } else if (episode.getPlayState() == EpisodeRealm.PlayState.PLAYING) {
            }
        }
    }

    private void startDownload(EpisodeRealm episode) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            new AlertDialog.Builder(this).setMessage(R.string.unavailable_network)
                    .show();
        } else {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Realm.getDefaultInstance().copyFromRealm(episode).setDownloadState(EpisodeRealm.DownloadState.DOWNLOADING);

                subscription.add(
                        downloadMedia.execute(new DownloadMedia.Param(episode.getId(), episode.getUrl()))
                                .subscribe(file -> Log.w("", "download"),
                                        Throwable::getStackTrace,
                                        () -> Log.w("", "downloaded"))
                );
            } else {
                Toast.makeText(this, "NOT WIFI", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this).setMessage(R.string.download_in_mobile_network)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show();
                        })
                        .show();
            }
        }
    }

    private String getStatusText(EpisodeRealm episode) {
        if (episode.getDownloadState() == EpisodeRealm.DownloadState.NOT_DOWNLOADED) {
            return getString(R.string.episode_not_downloaded);
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADING) {
            return getString(R.string.episode_downloading, episode.getDownloadedBytes() / MEGA_BYTE, episode.getTotalBytes() / MEGA_BYTE);
        }
        return "";
    }

    private int getStatusImage(EpisodeRealm episode) {
        if (episode.getDownloadState() == EpisodeRealm.DownloadState.NOT_DOWNLOADED) {
            return R.drawable.download;
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADING) {
            return R.drawable.downloading;
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADED) {
            if (episode.getPlayState() == EpisodeRealm.PlayState.NOT_PLAYED
                    || episode.getPlayState() == EpisodeRealm.PlayState.PLAYED) {
                return R.drawable.play;
            } else if (episode.getPlayState() == EpisodeRealm.PlayState.PLAYING) {
                return R.drawable.pause;
            }
        }
        return R.drawable.play;
    }

    private void onDownloadProgress(DownloadProgressParam param) {
        Log.w("TEST", String.valueOf(param.bytesRead));
/*        Stream.of(adapter.getData().get(0).getEpisodes())
                .map(episode -> new Pair<>(episode.getId(), episode))
                .filter(pair -> pair.first.equals(Long.valueOf(param.identifier)))
                .findFirst().ifPresent(pair -> {
                    pair.second.setDownloadedBytes(param.bytesRead);
                    pair.second.setTotalBytes(param.contentLength);
                    adapter.notifyDataSetChanged();
                }
        );*/
    }

    public static Intent createIntent(Context context, long id) {
        Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(KEY_ID, id);
        return intent;
    }
}
