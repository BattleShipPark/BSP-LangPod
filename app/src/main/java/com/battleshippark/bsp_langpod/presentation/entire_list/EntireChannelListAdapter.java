package com.battleshippark.bsp_langpod.presentation.entire_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class EntireChannelListAdapter extends RecyclerView.Adapter<EntireChannelListAdapter.ViewHolder> {
    private final OnItemListener mListener;
    private List<ChannelRealm> channelRealmList = Collections.EMPTY_LIST;

    EntireChannelListAdapter(OnItemListener listener) {
        super();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_entire_channel_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChannelRealm channelRealm = channelRealmList.get(position);

        mListener.onBindViewHolder(holder, channelRealm);
    }

    @Override
    public int getItemCount() {
        return channelRealmList.isEmpty() ? 0 : channelRealmList.size();
    }

    public void setItems(List<ChannelRealm> channelRealmList) {
        this.channelRealmList = channelRealmList;
        notifyDataSetChanged();
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
