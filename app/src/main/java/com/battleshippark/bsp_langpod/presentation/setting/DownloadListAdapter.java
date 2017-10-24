package com.battleshippark.bsp_langpod.presentation.setting;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {
    private final OnItemListener mListener;
    private List<DownloadRealm> list = new ArrayList<>();

    DownloadListAdapter(OnItemListener listener) {
        super();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_setting_download_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mListener.onBindViewHolder(holder, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void setData(List<DownloadRealm> downloadRealmList) {
        this.list = downloadRealmList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_iv)
        ImageView imageView;
        @BindView(R.id.channel_tv)
        TextView channelView;
        @BindView(R.id.episode_tv)
        TextView episodeView;
        @BindView(R.id.date_tv)
        TextView dateView;
        @BindView(R.id.status_tv)
        TextView statusView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
