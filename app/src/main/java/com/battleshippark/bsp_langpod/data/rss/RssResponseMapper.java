package com.battleshippark.bsp_langpod.data.rss;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.ChannelData;
import com.battleshippark.bsp_langpod.data.ChannelItemData;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 */

public class RssResponseMapper {
    ChannelData map(SyndFeed feed) {
        ChannelData channelData = new ChannelData();
        channelData.title = feed.getTitle();
        channelData.desc = feed.getDescription();
        channelData.copyright = feed.getCopyright();

        channelData.items = Stream.of(feed.getEntries()).map(syndEntry -> {
            ChannelItemData channelItemData = new ChannelItemData();
            channelItemData.title = syndEntry.getTitle();
            channelItemData.desc = syndEntry.getDescription().getValue();
            channelItemData.url = syndEntry.getUri();
            return channelItemData;
        }).collect(Collectors.toList());

        return channelData;
    }
}
