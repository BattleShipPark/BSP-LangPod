package com.battleshippark.bsp_langpod;

import android.support.test.rule.ActivityTestRule;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void rome() throws IOException, FeedException {
        String url = "http://enabler.kbs.co.kr/api/podcast_channel/feed.xml?channel_id=R2017-0027";
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        System.out.println(feed.getTitle());
    }
}