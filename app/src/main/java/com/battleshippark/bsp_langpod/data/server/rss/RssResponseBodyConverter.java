package com.battleshippark.bsp_langpod.data.server.rss;

import com.battleshippark.bsp_langpod.data.server.MyChannelJson;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class RssResponseBodyConverter implements Converter<ResponseBody, MyChannelJson> {
    private final RssResponseMapper mapper;

    RssResponseBodyConverter(RssResponseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public MyChannelJson convert(ResponseBody value) throws IOException {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(value.byteStream()));
            return mapper.map(feed);
        } catch (FeedException e) {
            throw new IOException(e);
        }
    }
}
