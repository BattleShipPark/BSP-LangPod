package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import java.util.List;

import io.realm.RealmList;

/**
 */

public class DomainMapper {
    public List<EntireChannelData> asData(List<ChannelRealm> channelRealmList) {
        return Stream.of(channelRealmList)
                .map(entireChannelRealm -> EntireChannelData.create(entireChannelRealm.getId(),
                        entireChannelRealm.getOrder(), entireChannelRealm.getTitle(),
                        entireChannelRealm.getDesc(), entireChannelRealm.getImage()))
                .collect(Collectors.toList());
    }

    public List<MyChannelData> myChannelRealmAsData(List<MyChannelRealm> myChannelRealmList) {
        return Stream.of(myChannelRealmList)
                .map(myChannelRealm ->
                        MyChannelData.create(myChannelRealm.getId(),
                                myChannelRealm.getOrder(),
                                myChannelRealm.getTitle(),
                                myChannelRealm.getDesc(),
                                myChannelRealm.getCopyright(),
                                myChannelRealm.getImage(),
                                myChannelRealm.getUrl(),
                                null
                        )
                ).collect(Collectors.toList());
    }

    public MyChannelData myChannelRealmAsData(MyChannelRealm myChannelRealm) {
        return MyChannelData.create(myChannelRealm.getId(),
                myChannelRealm.getOrder(),
                myChannelRealm.getTitle(),
                myChannelRealm.getDesc(),
                myChannelRealm.getCopyright(),
                myChannelRealm.getImage(),
                myChannelRealm.getUrl(),
                episodeRealmAsData(myChannelRealm.getItems())
        );
    }

    List<EpisodeData> episodeRealmAsData(List<EpisodeRealm> episodeRealmList) {
        return Stream.of(episodeRealmList).map(this::episodeRealmAsData).collect(Collectors.toList());
    }

    EpisodeData episodeRealmAsData(EpisodeRealm episodeRealm) {
        return EpisodeData.create(episodeRealm.getTitle(), episodeRealm.getDesc(), episodeRealm.getUrl());
    }

    public List<EntireChannelData> asData(EntireChannelListJson entireChannelListJson) {
        return Stream.of(entireChannelListJson.items())
                .map(entireChannelData -> EntireChannelData.create(entireChannelData.id(),
                        entireChannelData.order(),
                        entireChannelData.title(),
                        entireChannelData.desc(),
                        entireChannelData.image())
                ).collect(Collectors.toList());
    }

    public MyChannelData myChannelJsonAsData(String url, MyChannelJson myChannelJson) {
        return MyChannelData.create(
                myChannelJson.title(),
                myChannelJson.desc(),
                myChannelJson.copyright(),
                myChannelJson.image(),
                url,
                Stream.of(myChannelJson.episodes()).map(this::episodeJsonAsData).collect(Collectors.toList())
        );
    }

    public EpisodeData episodeJsonAsData(EpisodeJson episodeJson) {
        return EpisodeData.create(
                episodeJson.title(),
                episodeJson.desc(),
                episodeJson.url()
        );
    }

    public List<ChannelRealm> entireChannelListJsonAsRealm(List<ChannelRealm> channelRealmList, EntireChannelListJson entireChannelListJson) {
        //json을 사용하는데, 로컬에 있는 같은 id의 isSubscribed()를 참고한다
        return Stream.of(entireChannelListJson.items())
                .map(json -> {
                    for (ChannelRealm localRealm : channelRealmList) {
                        if (localRealm.getId() == json.id()) {
                            return new ChannelRealm(json.id(), json.order(), json.title(), json.desc(), json.image(), json.url(), localRealm.isSubscribed());
                        }
                    }
                    return new ChannelRealm(json.id(), json.order(), json.title(), json.desc(), json.image(), json.url(), false);
                })
                .collect(Collectors.toList());
    }

    public ChannelRealm myChannelJsonAsRealm(ChannelRealm channelRealm, MyChannelJson myChannelJson) {
        RealmList<EpisodeRealm> episodeRealmList = Stream.of(myChannelJson.episodes())
                .map(episodeJson -> new EpisodeRealm(episodeJson.title(), episodeJson.desc(), episodeJson.url()))
                .collect(RealmList::new, RealmList::add);
        channelRealm.setTitle(myChannelJson.title());
        channelRealm.setDesc(myChannelJson.desc());
        channelRealm.setCopyright(myChannelJson.copyright());
        channelRealm.setImage(myChannelJson.image());
        channelRealm.setEpisodes(episodeRealmList);
        return channelRealm;
    }
}
