package com.battleshippark.bsp_langpod.data.rss;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.ChannelData;
import com.battleshippark.bsp_langpod.data.ChannelItemData;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 */

public class RssResponseMapper {
    ChannelData map(SyndFeed feed) {
        Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        FeedInformation feedInfo = (FeedInformation) module;

        ChannelData channelData = ChannelData.create(
                feed.getTitle(), feed.getDescription(), feed.getCopyright(), feedInfo.getImage().toString(),
                Stream.of(feed.getEntries()).map(syndEntry -> {
                    ChannelItemData channelItemData = new ChannelItemData();
                    channelItemData.title = syndEntry.getTitle();
                    channelItemData.desc = syndEntry.getDescription().getValue();
                    channelItemData.url = syndEntry.getUri();
                    return channelItemData;
                }).collect(Collectors.toList()));

        return channelData;
    }
}
