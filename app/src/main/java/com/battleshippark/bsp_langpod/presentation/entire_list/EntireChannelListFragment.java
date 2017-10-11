package com.battleshippark.bsp_langpod.presentation.entire_list;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.battleshippark.bsp_langpod.Const;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerDomainMapperGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.domain.DomainMapper;
import com.battleshippark.bsp_langpod.domain.GetEntireChannelList;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.battleshippark.bsp_langpod.presentation.channel.ChannelActivity;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EntireChannelListFragment extends Fragment implements OnItemListener {
    private static final String TAG = EntireChannelListFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    private RecyclerView rv;

    private EntireListFragmentListener mListener;
    private CompositeSubscription subscription = new CompositeSubscription();
    private EntireChannelListAdapter adapter;

    private GetEntireChannelList getEntireChannelList;
    private SubscribeChannel subscribeChannel;

    public EntireChannelListFragment() {
    }

    public static EntireChannelListFragment newInstance() {
        return new EntireChannelListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EntireListFragmentListener) {
            mListener = (EntireListFragmentListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();
        DomainMapper domainMapper = DaggerDomainMapperGraph.create().domainMapper();

        getEntireChannelList = new GetEntireChannelList(channelDbApi, DaggerServerApiGraph.create().channelApi(),
                Schedulers.io(), AndroidSchedulers.mainThread(), domainMapper);

        subscribeChannel = new SubscribeChannel(channelDbApi, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entire_channel_list, container, false);
        rv = ButterKnife.findById(view, R.id.entire_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new EntireChannelListAdapter(this);
        rv.setAdapter(adapter);

        requestList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUEST_CODE_LAUNCH_CHANNEL_FROM_ENTIRE) {
            loadList();
        }
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void showData(List<ChannelRealm> channelRealmList) {
        adapter.setItems(channelRealmList);
    }

    void showError(Throwable throwable) {
        logger.w(throwable);
    }

    @Override
    public void onBindViewHolder(EntireChannelListAdapter.ViewHolder holder, ChannelRealm item) {
        holder.itemView.setOnClickListener(v -> //mListener.onClickEntireListItem(item, Const.REQUEST_CODE_LAUNCH_CHANNEL_FROM_ENTIRE));
                startActivityForResult(
                        ChannelActivity.createIntent(EntireChannelListFragment.this.getActivity(), item.getId()),
                        Const.REQUEST_CODE_LAUNCH_CHANNEL_FROM_ENTIRE));
        holder.titleView.setText(item.getTitle());

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(v ->
                subscription.add(
                        subscribeChannel.execute(item)
                                .subscribe(subscribed -> loadList(),
                                        logger::w)
                )
        );
    }

    private void requestList() {
        subscription.add(
                getEntireChannelList.execute(GetEntireChannelList.Type.DB_AND_SERVER).subscribe(this::showData, this::showError));
    }

    private void loadList() {
        subscription.add(
                getEntireChannelList.execute(GetEntireChannelList.Type.ONLY_DB).subscribe(this::showData, this::showError));
    }

    public interface EntireListFragmentListener {
        void onClickEntireListItem(ChannelRealm item, int requestCode);
    }
}
