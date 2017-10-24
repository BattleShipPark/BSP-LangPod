package com.battleshippark.bsp_langpod.presentation.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.domain.GetDownloadList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SettingDownloadListActivity extends Activity implements OnItemListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.download_rv)
    RecyclerView downloadRv;
    @BindView(R.id.msg_tv)
    TextView msgTv;

    private Unbinder unbinder;
    private DownloadListAdapter adapter;
    private GetDownloadList getDownloadList;
    private CompositeSubscription subscription = new CompositeSubscription();

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

        downloadRv.setAdapter(adapter);
    }

    private void initData(Bundle savedInstanceState) {
        adapter = new DownloadListAdapter(this);
        getDownloadList = new GetDownloadList(
                DaggerDbApiGraph.create().downloadApi(), Schedulers.computation(), AndroidSchedulers.mainThread());
    }

    private void showData(List<DownloadRealm> downloadRealms) {
        adapter.setData(downloadRealms);
        if (downloadRealms.isEmpty()) {
            msgTv.setVisibility(View.VISIBLE);
        } else {
            msgTv.setVisibility(View.GONE);
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

    }
}
