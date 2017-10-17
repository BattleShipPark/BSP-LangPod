package com.battleshippark.bsp_langpod.data.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class DownloadDbApiTest {
    private RealmConfiguration configuration;
    private Realm realm;
    private DownloadDbApi api;
    private DownloadRealm downloadRealm1, downloadRealm2;
    private TestSubscriber testSubscriber = new TestSubscriber();

    @Before
    public void before() {
        configuration = RealmConfigurationFactory.createTest();
        realm = Realm.getInstance(configuration);
        api = new DownloadDbApi(configuration);

        downloadRealm1 = new DownloadRealm();
        downloadRealm1.setDownloadDate(new Date(0x1234));

        downloadRealm2 = new DownloadRealm();
        downloadRealm2.setDownloadDate(new Date(0x2345));
    }

    @After
    public void after() {
        realm.close();
    }

    @Test
    public void all() {
        realm.executeTransaction(realm1 -> {
            realm1.insert(downloadRealm1);
            realm1.insert(downloadRealm2);
        });

        List<DownloadRealm> realmList = api.all().toBlocking().single();
        assertThat(realmList).hasSize(2);
        assertThat(realmList.get(0).getDownloadDate()).isEqualTo(new Date(0x1234));
        assertThat(realmList.get(1).getDownloadDate()).isEqualTo(new Date(0x2345));
    }

    @Test
    public void insert후에_all로확인() {
        api.insert(downloadRealm1).subscribe(testSubscriber);
        testSubscriber.assertCompleted();

        List<DownloadRealm> realmList = api.all().toBlocking().single();
        assertThat(realmList).hasSize(1);
        assertThat(realmList.get(0).getDownloadDate()).isEqualTo(new Date(0x1234));

        testSubscriber = new TestSubscriber();
        api.insert(downloadRealm2).subscribe(testSubscriber);
        testSubscriber.assertCompleted();

        realmList = api.all().toBlocking().single();
        assertThat(realmList).hasSize(2);
        assertThat(realmList.get(0).getDownloadDate()).isEqualTo(new Date(0x1234));
        assertThat(realmList.get(1).getDownloadDate()).isEqualTo(new Date(0x2345));
    }

}