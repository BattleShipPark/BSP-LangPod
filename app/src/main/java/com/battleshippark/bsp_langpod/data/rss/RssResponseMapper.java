package com.battleshippark.bsp_langpod.data.rss;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.MyChannelData;
import com.battleshippark.bsp_langpod.data.EpisodeData;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 */

public class RssResponseMapper {
    MyChannelData map(SyndFeed feed) {
        Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        FeedInformation feedInfo = (FeedInformation) module;

        MyChannelData myChannelData = MyChannelData.create(
                feed.getTitle(), feed.getDescription(), feed.getCopyright(), feedInfo.getImage().toString(),
                Stream.of(feed.getEntries()).map(syndEntry -> {
                    EpisodeData episodeData = new EpisodeData();
                    episodeData.title = syndEntry.getTitle();
                    episodeData.desc = syndEntry.getDescription().getValue();
                    episodeData.url = syndEntry.getUri();
                    return episodeData;
                }).collect(Collectors.toList()));

        return myChannelData;
    }
}
