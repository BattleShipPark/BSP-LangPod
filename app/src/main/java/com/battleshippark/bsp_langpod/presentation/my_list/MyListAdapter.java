package com.battleshippark.bsp_langpod.presentation.my_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.presentation.RealmRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

class MyListAdapter extends RealmRecyclerViewAdapter<ChannelRealm, MyListAdapter.ViewHolder> {
    private final OnItemListener mListener;

    MyListAdapter(OrderedRealmCollection<ChannelRealm> items, OnItemListener listener) {
        super(items, true);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChannelRealm channelRealm = getData().get(position);

        mListener.onBindViewHolder(holder, channelRealm);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_tv)
        TextView titleView;
        @BindView(R.id.image_iv)
        ImageView imageView;
        @BindView(R.id.subscribe_iv)
        ImageView subscribeView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
