package com.battleshippark.bsp_langpod.data;

import android.util.Log;

import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;

import org.junit.Test;

/**
 */
public class ChannelListApiTest {
    @Test
    public void query() {
        ChannelRepository<ChannelListData> channelListApi = DaggerServerApiGraph.create().channelListApi();
        channelListApi.query(null)
                .subscribe(channelListData -> Log.i("", channelListData.toString()));
    }
}