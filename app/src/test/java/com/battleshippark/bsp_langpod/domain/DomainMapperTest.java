package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import io.realm.RealmList;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DomainMapperTest {
    @Mock
    RealmHelper realmHelper;

    @Test
    public void channelJsonAsRealm_에피소드가없을때() {
        DomainMapper domainMapper = new DomainMapper(realmHelper);

        ChannelRealm channelRealm = new ChannelRealm(1, 10, "title", "desc", "image", "url", "copyright", new RealmList<>(), false);
        ChannelJson channelJson = ChannelJson.create("title", "desc", "copyright2", "image2",
                Collections.singletonList(EpisodeJson.create("ep.title", "ep.desc", "ep.url", new Date(1234))));
        ChannelRealm newChannelRealm = domainMapper.channelJsonAsRealm(channelRealm, channelJson);

        assertThat(newChannelRealm.getTitle()).isEqualTo("title");
        assertThat(newChannelRealm.getDesc()).isEqualTo("desc");
        assertThat(newChannelRealm.getImage()).isEqualTo("image2");
        assertThat(newChannelRealm.getUrl()).isEqualTo("url");
        assertThat(newChannelRealm.getCopyright()).isEqualTo("copyright2");

        assertThat(newChannelRealm.getEpisodes()).hasSize(1);
        assertThat(newChannelRealm.getEpisodes().get(0).getTitle()).isEqualTo("ep.title");
        assertThat(newChannelRealm.getEpisodes().get(0).getDesc()).isEqualTo("ep.desc");
        assertThat(newChannelRealm.getEpisodes().get(0).getUrl()).isEqualTo("ep.url");
        assertThat(newChannelRealm.getEpisodes().get(0).getLength()).isEqualTo(111);
        assertThat(newChannelRealm.getEpisodes().get(0).getDate()).isEqualTo(new Date(1234));
    }

    @Test
    public void channelJsonAsRealm_에피소드추가및변경() {
        DomainMapper domainMapper = new DomainMapper(realmHelper);
        when(realmHelper.getNextEpisodeId()).thenReturn(2L);

        ChannelRealm channelRealm = new ChannelRealm(1, 10, "title", "desc", "image", "url", "copyright",
                new RealmList<>(new EpisodeRealm(1, "ep.title", "ep.desc", "ep.url", new Date(111))), false);
        ChannelJson channelJson = ChannelJson.create("title", "desc", "copyright2", "image2",
                Arrays.asList(
                        EpisodeJson.create("ep.title", "ep.desc", "ep.url1", new Date(1111)), //같은 title, desc에 내용이 바뀜
                        EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2", new Date(2222)) //새로운 에피소드
                ));


        ChannelRealm newChannelRealm = domainMapper.channelJsonAsRealm(channelRealm, channelJson);


        assertThat(newChannelRealm.getTitle()).isEqualTo("title");
        assertThat(newChannelRealm.getDesc()).isEqualTo("desc");
        assertThat(newChannelRealm.getImage()).isEqualTo("image2");
        assertThat(newChannelRealm.getUrl()).isEqualTo("url");
        assertThat(newChannelRealm.getCopyright()).isEqualTo("copyright2");

        assertThat(newChannelRealm.getEpisodes()).hasSize(2);
        assertThat(newChannelRealm.getEpisodes().get(0).getId()).isEqualTo(1);
        assertThat(newChannelRealm.getEpisodes().get(0).getTitle()).isEqualTo("ep.title");
        assertThat(newChannelRealm.getEpisodes().get(0).getDesc()).isEqualTo("ep.desc");
        assertThat(newChannelRealm.getEpisodes().get(0).getUrl()).isEqualTo("ep.url1");
        assertThat(newChannelRealm.getEpisodes().get(0).getLength()).isEqualTo(111);
        assertThat(newChannelRealm.getEpisodes().get(0).getDate()).isEqualTo(new Date(1111));

        assertThat(newChannelRealm.getEpisodes().get(1).getId()).isEqualTo(2);
        assertThat(newChannelRealm.getEpisodes().get(1).getTitle()).isEqualTo("ep.title2");
        assertThat(newChannelRealm.getEpisodes().get(1).getDesc()).isEqualTo("ep.desc2");
        assertThat(newChannelRealm.getEpisodes().get(1).getUrl()).isEqualTo("ep.url2");
        assertThat(newChannelRealm.getEpisodes().get(1).getLength()).isEqualTo(222);
        assertThat(newChannelRealm.getEpisodes().get(1).getDate()).isEqualTo(new Date(2222));
    }
}