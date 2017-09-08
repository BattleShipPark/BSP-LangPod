package com.battleshippark.bsp_langpod.presentation.my_list;

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
import android.widget.TextView;

import com.battleshippark.bsp_langpod.Const;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.domain.GetMyChannelList;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.battleshippark.bsp_langpod.presentation.channel.ChannelActivity;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyChannelListFragment extends Fragment implements OnItemListener {
    private static final String TAG = MyChannelListFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    private RecyclerView rv;
    private TextView msgTextView;

    private MyListFragmentListener mListener;
    private Subscription subscription;
    private MyChannelListAdapter adapter;

    private GetMyChannelList getMyChannelList;
    private SubscribeChannel subscribeChannel;

    public MyChannelListFragment() {
    }

    public static MyChannelListFragment newInstance() {
        return new MyChannelListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyListFragmentListener) {
            mListener = (MyListFragmentListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();

        getMyChannelList = new GetMyChannelList(channelDbApi, Schedulers.io(), AndroidSchedulers.mainThread());
        subscribeChannel = new SubscribeChannel(channelDbApi, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_channel_list, container, false);
        rv = ButterKnife.findById(view, R.id.my_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyChannelListAdapter(this);
        rv.setAdapter(adapter);

        msgTextView = ButterKnife.findById(view, R.id.msg_tv);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUEST_CODE_LAUNCH_CHANNEL_FROM_MY) {
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

        if (adapter.getItemCount() == 0) {
            rv.setVisibility(View.GONE);
            msgTextView.setVisibility(View.VISIBLE);
            msgTextView.setText(R.string.my_list_empty_msg);
        } else {
            rv.setVisibility(View.VISIBLE);
            msgTextView.setVisibility(View.GONE);
        }
    }

    void showError(Throwable throwable) {
        rv.setVisibility(View.GONE);
        msgTextView.setVisibility(View.VISIBLE);
        msgTextView.setText(R.string.my_list_error_msg);
        logger.w(throwable);
    }

    @Override
    public void onBindViewHolder(MyChannelListAdapter.ViewHolder holder, ChannelRealm item) {
        holder.itemView.setOnClickListener(v ->
                startActivityForResult(
                        ChannelActivity.createIntent(MyChannelListFragment.this.getActivity(), item.getId()),
                        Const.REQUEST_CODE_LAUNCH_CHANNEL_FROM_MY));
        holder.titleView.setText(item.getTitle());

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
                        .subscribe(subscribed -> loadList(),
                                logger::w)
        );
    }

    private void loadList() {
        subscription = getMyChannelList.execute(null)
                .subscribe(this::showData, this::showError);
    }

    public interface MyListFragmentListener {
        void onClickMyChannelItem(ChannelRealm item, int requestCode);
    }
}
