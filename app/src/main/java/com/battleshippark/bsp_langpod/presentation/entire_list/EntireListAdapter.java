package com.battleshippark.bsp_langpod.presentation.entire_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.presentation.RealmRecyclerViewAdapter;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireListFragment.OnListFragmentInteractionListener;

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
        holder.mIdView.setText(String.valueOf(getData().get(position).getId()));
        holder.mTitleView.setText(getData().get(position).getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(getData().get(position));
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id)
        TextView mIdView;
        @BindView(R.id.title_tv)
        TextView mTitleView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
