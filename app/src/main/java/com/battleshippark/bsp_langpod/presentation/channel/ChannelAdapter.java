package com.battleshippark.bsp_langpod.presentation.channel;

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

class ChannelAdapter extends RealmRecyclerViewAdapter<ChannelRealm, RecyclerView.ViewHolder> {
    private final OnItemListener mListener;

    ChannelAdapter(OnItemListener listener) {
        super(null, true);
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (ViewType.values()[viewType]) {
            case HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_channel_item_header, parent, false);
                return new HeaderViewHolder(view);
            case EPISODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_channel_item_episode, parent, false);
                return new EpisodeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ChannelRealm channelRealm = getData().get(0);

        switch (ViewType.values()[getItemViewType(position)]) {
            case HEADER:
                mListener.onBindHeaderViewHolder((HeaderViewHolder) holder, channelRealm);
                break;
            case EPISODE:
                mListener.onBindEpisodeViewHolder((EpisodeViewHolder) holder, channelRealm.getEpisodes().get(position - 1));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ViewType.HEADER.ordinal() : ViewType.EPISODE.ordinal();
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount == 0 ? 0 : itemCount + getData().get(0).getEpisodes().size();
    }

    enum ViewType {HEADER, EPISODE}

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_iv)
        ImageView imageView;
        @BindView(R.id.desc_tv)
        TextView descView;
        @BindView(R.id.copyright_tv)
        TextView copyrightView;
        @BindView(R.id.episode_count_tv)
        TextView episodeCountView;
        @BindView(R.id.subscribe_iv)
        ImageView subscribeView;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.desc_tv)
        TextView descView;
        @BindView(R.id.date_tv)
        TextView dateView;
        @BindView(R.id.status_tv)
        TextView statusTv;
        @BindView(R.id.status_iv)
        ImageView statusIv;

        EpisodeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
