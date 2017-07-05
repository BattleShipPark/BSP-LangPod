package com.battleshippark.bsp_langpod.presentation.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;
import com.battleshippark.bsp_langpod.domain.DomainMapper;
import com.battleshippark.bsp_langpod.domain.GetChannel;
import com.battleshippark.bsp_langpod.domain.GetMyChannelList;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.battleshippark.bsp_langpod.presentation.MainActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChannelActivity extends Activity implements OnItemListener {
    private static final String TAG = ChannelActivity.class.getSimpleName();
    private static final String KEY_ID = "keyId";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.channel_rv)
    RecyclerView rv;
    @BindView(R.id.msg_tv)
    TextView msgTextView;

    private MyListFragmentListener mListener;
    private Subscription subscription;
    private ChannelAdapter adapter;

    private GetChannel getChannel;
    private SubscribeChannel subscribeChannel;
    private Unbinder unbinder;

    private long channelId;

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

        getChannel = new GetChannel(channelDbApi, channelServerApi, Schedulers.io(), AndroidSchedulers.mainThread(), new DomainMapper());
        subscribeChannel = new SubscribeChannel(channelDbApi);

        adapter = new ChannelAdapter(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
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
        subscription = getChannel.execute(channelId)
                .subscribe(this::showData, this::showError);
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    void showData(List<ChannelRealm> channelRealmList) {
        adapter.updateData((OrderedRealmCollection<ChannelRealm>) channelRealmList);
    }

    void showError(Throwable throwable) {
        rv.setVisibility(View.GONE);
        msgTextView.setVisibility(View.VISIBLE);
        msgTextView.setText(R.string.my_list_error_msg);
        Log.w(TAG, throwable);
    }

    @Override
    public void onBindHeaderViewHolder(ChannelAdapter.HeaderViewHolder holder, ChannelRealm item) {
        holder.itemView.setOnClickListener(v -> mListener.onClickMyChannelItem(item));

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.descView.setText(item.getDesc());
        holder.copyrightView.setText(item.getCopyright());
        holder.episodeCountView.setText("" + item.getEpisodes().size());

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
                        .subscribe(
                                aVoid -> {
                                },
                                throwable -> Log.w(TAG, throwable)
                        )
        );
    }

    @Override
    public void onBindEpisodeViewHolder(ChannelAdapter.EpisodeViewHolder holder, ChannelRealm item) {
/*        holder.itemView.setOnClickListener(v -> mListener.onClickMyChannelItem(item));
        holder.titleView.setText(item.getTitle());

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
                        .subscribe(
                                aVoid -> {
                                },
                                throwable -> Log.w(TAG, throwable)
                        )
        );*/
    }

    public static Intent createIntent(Context context, long id) {
        Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(KEY_ID, id);
        return intent;
    }

    public interface MyListFragmentListener {
        void onClickMyChannelItem(ChannelRealm item);
    }
}
