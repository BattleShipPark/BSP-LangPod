package com.battleshippark.bsp_langpod.presentation.my_list;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.domain.GetMyChannelList;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import rx.Subscription;

public class MyListFragment extends Fragment implements OnItemListener {
    private static final String TAG = MyListFragment.class.getSimpleName();
    private RecyclerView rv;
    private TextView msgTextView;

    private MyListFragmentListener mListener;
    private Subscription subscription;
    private MyListAdapter adapter;

    private GetMyChannelList getMyChannelList;
    private SubscribeChannel subscribeChannel;

    public MyListFragment() {
    }

    public static MyListFragment newInstance() {
        return new MyListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof MyListFragmentListener) {
            mListener = (MyListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();

        getMyChannelList = new GetMyChannelList(channelDbApi);
        subscribeChannel = new SubscribeChannel(channelDbApi);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);
        rv = ButterKnife.findById(view, R.id.my_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        msgTextView = ButterKnife.findById(view, R.id.msg_tv);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscription = getMyChannelList.execute(null)
                .subscribe(this::showData, this::showError);
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
        if (channelRealmList.isEmpty()) {
            rv.setVisibility(View.GONE);
            msgTextView.setVisibility(View.VISIBLE);
            msgTextView.setText(R.string.my_list_empty_msg);
        } else {
            adapter = new MyListAdapter((OrderedRealmCollection<ChannelRealm>) channelRealmList, this);
            rv.setAdapter(adapter);
            rv.setVisibility(View.VISIBLE);
            msgTextView.setVisibility(View.GONE);
        }
    }

    void showError(Throwable throwable) {
        rv.setVisibility(View.GONE);
        msgTextView.setVisibility(View.VISIBLE);
        msgTextView.setText(R.string.my_list_error_msg);
        Log.w(TAG, throwable);
    }

    @Override
    public void onBindViewHolder(MyListAdapter.ViewHolder holder, ChannelRealm item) {
//        holder.itemView.setOnClickListener(v -> mListener.onClickEntireListItem(item));
        holder.titleView.setText(item.getTitle());

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
                        .subscribe(
                                aVoid -> {
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    public interface MyListFragmentListener {
        void onClickMyChannelItem(ChannelRealm item);
    }
}
