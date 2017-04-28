package com.battleshippark.bsp_langpod.data;

import android.util.Log;

import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;

import org.junit.Test;

/**
 */
public class ChannelListApiTest {
    @Test
    public void query() {
        ChannelInteractor<ChannelListData> interactor = DaggerServerApiGraph.create().channelListApi();
        interactor.query(null)
                .subscribe(channelListData -> Log.i("", channelListData.toString()));
    }
}