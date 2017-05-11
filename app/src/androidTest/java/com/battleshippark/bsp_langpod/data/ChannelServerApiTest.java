package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.dagger.DaggerServerApiGraph;

import org.junit.Test;

/**
 */
public class ChannelServerApiTest {
    @Test
    public void entireChannelList() {
        ChannelServerRepository channelApi = DaggerServerApiGraph.create().channelApi();
        channelApi.entireChannelList()
                .subscribe(channelData -> System.out.println(channelData.toString()));
    }

    @Test
    public void channel() {
        ChannelServerRepository channelApi = DaggerServerApiGraph.create().channelApi();
        channelApi.channel("http://enabler.kbs.co.kr/api/podcast_channel/feed.xml?channel_id=R2017-0027")
                .subscribe(channelData -> System.out.println(channelData.toString()));
    }
}