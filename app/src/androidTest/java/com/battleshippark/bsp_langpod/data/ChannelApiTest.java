package com.battleshippark.bsp_langpod.data;

import android.util.Log;

import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;

import org.junit.Test;

/**
 */
public class ChannelApiTest {
    @Test
    public void test() {
        ChannelInteractor<ChannelData> interactor = DaggerServerApiGraph.create().channelApi();
        interactor.query("http://enabler.kbs.co.kr/api/podcast_channel/feed.xml?channel_id=R2017-0027")
                .subscribe(channelData -> Log.i("", channelData.toString()));
    }
}