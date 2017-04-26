package com.battleshippark.bsp_langpod.data;

import android.util.Log;

import org.junit.Test;

/**
 */
public class ChannelReposTest {
    @Test
    public void test() {
        ChannelInteractor<ChannelData> interactor = new ChannelRepos();
        interactor.query("http://enabler.kbs.co.kr/api/podcast_channel/feed.xml?channel_id=R2017-0027")
                .subscribe(channelData -> Log.i("", channelData.toString()));
    }
}