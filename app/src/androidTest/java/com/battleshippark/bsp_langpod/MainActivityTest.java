package com.battleshippark.bsp_langpod;

import android.support.test.rule.ActivityTestRule;

import com.battleshippark.bsp_langpod.data.ChannelItemRealm;
import com.battleshippark.bsp_langpod.data.ChannelRealm;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void rome() throws IOException, FeedException {
        String url = "http://enabler.kbs.co.kr/api/podcast_channel/feed.xml?channel_id=R2017-0027";
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));

        Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        FeedInformation feedInfo = (FeedInformation) module;
        System.out.println(feedInfo.getImage());
    }

    @Test
    public void realm() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            ChannelRealm channelRealm = realm1.createObject(ChannelRealm.class);
            channelRealm.setTitle("title");
            channelRealm.setDesc("desc");
            channelRealm.setCopyright("me");

            ChannelItemRealm channelItem = realm1.createObject(ChannelItemRealm.class);
            channelItem.setTitle("title1");
            channelItem.setDesc("desc1");
            channelItem.setUrl("url1");
            channelRealm.getItems().add(channelItem);

            channelItem = realm1.createObject(ChannelItemRealm.class);
            channelItem.setTitle("title2");
            channelItem.setDesc("desc2");
            channelItem.setUrl("url2");
            channelRealm.getItems().add(channelItem);
        });

        RealmQuery<ChannelRealm> query = realm.where(ChannelRealm.class);
        RealmResults<ChannelRealm> channelRealmResults = query.findAll();
        ChannelRealm channelRealm = channelRealmResults.first();
        System.out.println(channelRealm);
    }
}