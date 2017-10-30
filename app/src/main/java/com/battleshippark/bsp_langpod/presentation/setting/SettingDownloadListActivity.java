package com.battleshippark.bsp_langpod.presentation.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.domain.GetDownloadList;
import com.battleshippark.bsp_langpod.presentation.EpisodeDateFormat;
import com.battleshippark.bsp_langpod.service.downloader.DownloaderBroadcastReceiver;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.battleshippark.bsp_langpod.Const.MEGA_BYTE;

public class SettingDownloadListActivity extends Activity implements OnItemListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.download_rv)
    RecyclerView downloadRv;
    @BindView(R.id.msg_tv)
    TextView msgTv;

    private static final String TAG = SettingDownloadListActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private final CompositeSubscription subscription = new CompositeSubscription();
    private final EpisodeDateFormat dateFormat = new EpisodeDateFormat();
    private Unbinder unbinder;
    private DownloadListAdapter adapter;
    private GetDownloadList getDownloadList;
    private DownloaderBroadcastReceiver downloaderBcReceiver;
    private List<DownloadRealm> downloadRealmList = Collections.EMPTY_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_download_list);

        unbinder = ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();

        loadData();
    }

    private void loadData() {
        subscription.add(
                getDownloadList.execute(GetDownloadList.Type.ENTIRE).subscribe(this::showData));
    }

    private void initUI() {
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        downloadRv.setLayoutManager(new LinearLayoutManager(this));
        downloadRv.setAdapter(adapter);
    }

    private void initData(Bundle savedInstanceState) {
        adapter = new DownloadListAdapter(this);
        getDownloadList = new GetDownloadList(
                DaggerDbApiGraph.create().downloadApi(), Schedulers.computation(), AndroidSchedulers.mainThread());
        downloaderBcReceiver = new DownloaderBroadcastReceiver(this,
                this::onDownloadProgress, this::onDownloadCompleted, this::onDownloadError);
    }

    private void onDownloadProgress(DownloadProgressParam param) {
        findDownload(param.identifier())
                .ifPresent(downloadRealm -> {
                    downloadRealm.setDownloadState(DownloadRealm.DownloadState.DOWNLOADING);
                    downloadRealm.getEpisodeRealm().setDownloadedBytes(param.bytesRead());
                    downloadRealm.getEpisodeRealm().setTotalBytes(param.contentLength());
                    adapter.notifyDataSetChanged();
                    logger.d("episode=%d, bytes=%d, total=%d", downloadRealm.getId(), param.bytesRead(), param.contentLength());
                });
    }

    private void onDownloadCompleted(DownloadCompleteParam param) {
        findDownload(param.identifier())
                .ifPresent(downloadRealm -> {
                    downloadRealm.setDownloadState(DownloadRealm.DownloadState.DOWNLOADED);
                    downloadRealm.getEpisodeRealm().setDownloadedPath(param.file().getAbsolutePath());
                    adapter.notifyDataSetChanged();
                    logger.d("episode=%d completed", downloadRealm.getId());
                });
    }

    private void onDownloadError(DownloadErrorParam param) {
        findDownload(param.identifier())
                .ifPresent(downloadRealm -> {
                    downloadRealm.setDownloadState(DownloadRealm.DownloadState.FAILED_DOWNLOAD);
                    adapter.notifyDataSetChanged();
                    logger.d("episode=%d error", downloadRealm.getId());
                });
        logger.w(param.throwable());
    }

    private Optional<DownloadRealm> findDownload(String episodeId) {
        return Stream.of(downloadRealmList)
                .filter(downloadRealm -> downloadRealm.getEpisodeId() == Long.valueOf(episodeId))
                .findFirst();
    }

    private void showData(List<DownloadRealm> downloadRealms) {
        this.downloadRealmList = downloadRealms;
        adapter.setData(downloadRealms);
        if (downloadRealms.isEmpty()) {
            msgTv.setVisibility(View.VISIBLE);
        } else {
            msgTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloaderBcReceiver.register();
    }

    @Override
    protected void onStop() {
        downloaderBcReceiver.unregister();
        super.onStop();
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
    protected void onDestroy() {
        subscription.unsubscribe();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBindViewHolder(DownloadListAdapter.ViewHolder holder, DownloadRealm downloadRealm) {
        Glide.with(holder.imageView.getContext()).load(downloadRealm.getChannelRealm().getImage()).into(holder.imageView);
        holder.channelView.setText(downloadRealm.getChannelRealm().getTitle());

        holder.episodeView.setText(downloadRealm.getEpisodeRealm().getDesc());
        holder.dateView.setText(dateFormat.format(downloadRealm.getEpisodeRealm().getDate()));
        holder.statusView.setText(getStatusText(downloadRealm.getDownloadState(), downloadRealm.getEpisodeRealm()));
    }

    private String getStatusText(DownloadRealm.DownloadState state, EpisodeRealm episodeRealm) {
        switch (state) {
            case NOT_DOWNLOADED:
                return getString(R.string.download_list_episode_not_downloaded);
            case DOWNLOADING:
                return getString(R.string.download_list_episode_downloading, episodeRealm.getDownloadedBytes() / MEGA_BYTE, episodeRealm.getTotalBytes() / MEGA_BYTE);
            case DOWNLOADED:
                return getString(R.string.download_list_episode_downloaded);
            case FAILED_DOWNLOAD:
                return getString(R.string.download_list_episode_failed_download);
        }
        return "";
    }
}
