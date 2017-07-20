package com.battleshippark.bsp_langpod.data.server.rss;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 */

public class RssResponseMapper {
    ChannelJson map(SyndFeed feed) {
        Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        FeedInformation feedInfo = (FeedInformation) module;

        return ChannelJson.create(
                feed.getTitle(), feed.getDescription(), feed.getCopyright(), feedInfo.getImage().toString(),
                Stream.of(feed.getEntries())
                        .map(syndEntry ->
                                EpisodeJson.create(
                                        syndEntry.getTitle(),
                                        syndEntry.getDescription().getValue(),
                                        syndEntry.getUri(),
                                        syndEntry.getEnclosures().get(0).getLength(),
                                        syndEntry.getPublishedDate()))
                        .collect(Collectors.toList())
        );
    }
}
