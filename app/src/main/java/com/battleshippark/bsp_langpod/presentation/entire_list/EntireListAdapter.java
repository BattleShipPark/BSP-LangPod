package com.battleshippark.bsp_langpod.presentation.entire_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.presentation.RealmRecyclerViewAdapter;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireListFragment.OnListFragmentInteractionListener;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;

class EntireListAdapter extends RealmRecyclerViewAdapter<ChannelRealm, EntireListAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;

    EntireListAdapter(OrderedRealmCollection<ChannelRealm> items, OnListFragmentInteractionListener listener) {
        super(items, true);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_entire_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChannelRealm channelRealm = getData().get(position);

        holder.titleView.setText(channelRealm.getTitle());

        Glide.with(holder.imageView.getContext()).load(channelRealm.getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(channelRealm);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_tv)
        TextView titleView;
        @BindView(R.id.image_iv)
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
