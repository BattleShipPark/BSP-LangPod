package com.battleshippark.bsp_langpod.presentation.channel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.battleshippark.bsp_langpod.domain.UpdateEpisode;
import com.battleshippark.bsp_langpod.player.PlayerService;
import com.battleshippark.bsp_langpod.player.PlayerServiceFacade;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ChannelActivity extends Activity implements OnItemListener {
    private static final String TAG = ChannelActivity.class.getSimpleName();
    private static final String KEY_ID = "keyId";
    private static final float MEGA_BYTE = 1024 * 1024 * 1.0f;
    private static final Logger logger = new Logger(TAG);

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.channel_rv)
    RecyclerView rv;
    @BindView(R.id.msg_tv)
    TextView msgTextView;

    private CompositeSubscription subscription = new CompositeSubscription();
    private Unbinder unbinder;
    private ChannelAdapter adapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long episodeId = intent.getLongExtra(PlayerService.KEY_EPISODE_ID, -1);
            Stream.of(channelRealm.getEpisodes())
                    .filter(episodeRealm -> episodeRealm.getId() == episodeId).findFirst()
                    .ifPresent(episodeRealm -> {
                        if (intent.getAction().equals(PlayerService.ACTION_PLAY)) {
                            episodeRealm.setPlayState(EpisodeRealm.PlayState.PLAYING);
                        } else if (intent.getAction().equals(PlayerService.ACTION_PAUSE)) {
                            episodeRealm.setPlayState(EpisodeRealm.PlayState.PLAYED);
                        }
                        adapter.notifyDataSetChanged();
                    });
        }
    };
    private IntentFilter intentFilter;

    private PlayerServiceFacade playerServiceFacade;

    private GetChannel getChannel;
    private SubscribeChannel subscribeChannel;
    private UpdateEpisode updateEpisode;
    private DownloadMedia downloadMedia;

    private long channelId;
    private ChannelRealm channelRealm;

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
        updateEpisode = new UpdateEpisode(channelDbApi, Schedulers.io(), AndroidSchedulers.mainThread());
        downloadMedia = new DownloadMedia(this, Schedulers.io(), AndroidSchedulers.mainThread(), new AppPhase(BuildConfig.DEBUG));

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
                    channelRealm = adapter.getItem(0);

                    rv.setVisibility(View.VISIBLE);
                    msgTextView.setVisibility(View.GONE);
                }
            }
        });

        playerServiceFacade = new PlayerServiceFacade(this);

        intentFilter = playerServiceFacade.createIntentFilter();

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
    protected void onStart() {
        super.onStart();
        playerServiceFacade.onStart();
        registerReceiver();
    }

    @Override
    protected void onStop() {
        unregisterReceiver();
        playerServiceFacade.onStop();
        super.onStop();
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
        logger.w(throwable);
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
                                throwable -> logger.w(throwable)
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
            tryDownload(episode);
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADING) {
        } else if (episode.getDownloadState() == EpisodeRealm.DownloadState.DOWNLOADED) {
            if (episode.getPlayState() == EpisodeRealm.PlayState.NOT_PLAYED
                    || episode.getPlayState() == EpisodeRealm.PlayState.PLAYED) {
                playEpisode(episode);
            } else if (episode.getPlayState() == EpisodeRealm.PlayState.PLAYING) {
                pauseEpisode(episode);
            }
        }
    }

    private void pauseEpisode(EpisodeRealm episode) {
        playerServiceFacade.pause(episode);

        episode.setPlayState(EpisodeRealm.PlayState.PLAYED);
        updateEpisode.execute(episode).subscribe();
    }

    private void playEpisode(EpisodeRealm episode) {
        playerServiceFacade.play(channelRealm, episode);

        episode.setPlayState(EpisodeRealm.PlayState.PLAYING);
        updateEpisode.execute(episode).subscribe();
    }

    private void tryDownload(EpisodeRealm episode) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            new AlertDialog.Builder(this).setMessage(R.string.unavailable_network)
                    .show();
        } else {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                startDownload(episode);
            } else {
                new AlertDialog.Builder(this).setMessage(R.string.download_in_mobile_network)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            startDownload(episode);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        })
                        .show();
            }
        }
    }

    private void startDownload(EpisodeRealm episode) {
        episode.setDownloadState(EpisodeRealm.DownloadState.DOWNLOADING);
        adapter.notifyDataSetChanged();

        PublishSubject<DownloadProgressParam> downloadProgress = PublishSubject.create();
        subscription.add(
                downloadProgress
                        .throttleLast(1000, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onDownloadProgress, logger::w));

        subscription.add(
                downloadMedia.execute(new DownloadMedia.Param(episode.getId(), episode.getUrl(), downloadProgress))
                        .subscribe(file -> onDownloadCompleted(episode, file),
                                Throwable::printStackTrace,
                                () -> logger.w("downloaded"))
        );
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

    private void onDownloadCompleted(EpisodeRealm episodeRealm, File file) {
        episodeRealm.setDownloadState(EpisodeRealm.DownloadState.DOWNLOADED);
        episodeRealm.setDownloadedPath(file.getAbsolutePath());
        updateEpisode.execute(episodeRealm).subscribe(aVoid -> {
        }, Throwable::printStackTrace);
    }

    private void onDownloadProgress(DownloadProgressParam param) {
        for (EpisodeRealm episodeRealm : channelRealm.getEpisodes()) {
            if (episodeRealm.getId() == Long.valueOf(param.identifier)) {
                episodeRealm.setDownloadedBytes(param.bytesRead);
                episodeRealm.setTotalBytes(param.contentLength);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void registerReceiver() {
        registerReceiver(receiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(receiver);
    }

    public static Intent createIntent(Context context, long id) {
        Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(KEY_ID, id);
        return intent;
    }
}
