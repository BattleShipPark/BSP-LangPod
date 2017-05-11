package com.battleshippark.bsp_langpod;

import android.support.test.rule.ActivityTestRule;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
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
    public void myChannelList_Realm() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();

            MyChannelRealm channelRealm = realm1.createObject(MyChannelRealm.class);
            channelRealm.setId(RealmHelper.getNextId(realm1, MyChannelRealm.class));
            channelRealm.setTitle("title");
            channelRealm.setDesc("desc");
            channelRealm.setCopyright("me");

            EpisodeRealm channelItem = realm1.createObject(EpisodeRealm.class);
            channelItem.setTitle("title1");
            channelItem.setDesc("desc1");
            channelItem.setUrl("url1");
            channelRealm.getItems().add(channelItem);

            channelItem = realm1.createObject(EpisodeRealm.class);
            channelItem.setTitle("title2");
            channelItem.setDesc("desc2");
            channelItem.setUrl("url2");
            channelRealm.getItems().add(channelItem);
        });

        RealmQuery<MyChannelRealm> query = realm.where(MyChannelRealm.class);
        RealmResults<MyChannelRealm> channelRealmResults = query.findAll();
        MyChannelRealm channelRealm = channelRealmResults.first();
        System.out.println(channelRealm);
    }
}