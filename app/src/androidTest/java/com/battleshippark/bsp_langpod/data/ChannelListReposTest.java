package com.battleshippark.bsp_langpod.data;

import android.util.Log;

import com.battleshippark.bsp_langpod.dagger.DaggerChannelReposGraph;

import org.junit.Test;

/**
 */
public class ChannelListReposTest {
    @Test
    public void query() {
        ChannelInteractor<ChannelListData> interactor = DaggerChannelReposGraph.create().createListRepos();
        interactor.query(null)
                .subscribe(channelListData -> Log.i("", channelListData.toString()));
    }
}