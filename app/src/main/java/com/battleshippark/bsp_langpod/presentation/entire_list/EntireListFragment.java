package com.battleshippark.bsp_langpod.presentation.entire_list;

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
import android.widget.Toast;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.domain.DomainMapper;
import com.battleshippark.bsp_langpod.domain.GetEntireChannelList;
import com.battleshippark.bsp_langpod.domain.SubscribeChannel;
import com.battleshippark.bsp_langpod.domain.UseCase;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EntireListFragment extends Fragment implements OnItemListener {
    private static final String TAG = EntireListFragment.class.getSimpleName();
    private RecyclerView rv;

    //    private OnListFragmentInteractionListener mListener;
    private Subscription subscription;
    private EntireListAdapter adapter;

    private GetEntireChannelList getEntireChannelList;
    private SubscribeChannel subscribeChannel;

    public EntireListFragment() {
    }

    public static EntireListFragment newInstance() {
        return new EntireListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();

        getEntireChannelList = new GetEntireChannelList(channelDbApi, DaggerServerApiGraph.create().channelApi(),
                Schedulers.io(), AndroidSchedulers.mainThread(), new DomainMapper());

        subscribeChannel = new SubscribeChannel(channelDbApi);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entire_list, container, false);
        rv = ButterKnife.findById(view, R.id.entire_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscription = getEntireChannelList.execute(null)
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
//        mListener = null;
    }

    void showData(List<ChannelRealm> channelRealmList) {
        adapter = new EntireListAdapter((OrderedRealmCollection<ChannelRealm>) channelRealmList, this);
        rv.setAdapter(adapter);
    }

    void showError(Throwable throwable) {
        Log.w(TAG, throwable);
    }

    @Override
    public void onBindViewHolder(EntireListAdapter.ViewHolder holder, ChannelRealm item) {
        holder.itemView.setOnClickListener(v -> Toast.makeText(holder.itemView.getContext(), "CLICK", Toast.LENGTH_SHORT).show());
        holder.titleView.setText(item.getTitle());

        Glide.with(holder.imageView.getContext()).load(item.getImage()).into(holder.imageView);

        holder.subscribeView.setSelected(item.isSubscribed());
        holder.subscribeView.setOnClickListener(
                v -> subscribeChannel.execute(item)
//                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aVoid -> {
                                },
                                Throwable::printStackTrace
                        )
        );
    }
}
