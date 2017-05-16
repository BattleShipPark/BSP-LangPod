package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;

/**
 */

public class RealmMapper {
    public List<EntireChannelData> asData(List<EntireChannelRealm> entireChannelRealmList) {
        return Stream.of(entireChannelRealmList)
                .map(entireChannelRealm -> EntireChannelData.create(entireChannelRealm.getId(),
                        entireChannelRealm.getOrder(), entireChannelRealm.getTitle(),
                        entireChannelRealm.getDesc(), entireChannelRealm.getImage()))
                .collect(Collectors.toList());
    }

    public List<MyChannelData> myChannelRealmAsData(List<MyChannelRealm> myChannelRealmList) {
        return Stream.of(myChannelRealmList)
                .map(myChannelRealm ->
                        MyChannelData.create(myChannelRealm.getId(),
                                myChannelRealm.getTitle(),
                                myChannelRealm.getDesc(),
                                myChannelRealm.getCopyright(),
                                myChannelRealm.getImage(),
                                episodeRealmAsData(myChannelRealm.getItems())
                        )
                ).collect(Collectors.toList());
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

    public List<EntireChannelRealm> asRealm(EntireChannelListJson entireChannelListJson) {
        return Stream.of(entireChannelListJson.items())
                .map(entireChannelData -> {
                    EntireChannelRealm entireChannelRealm = new EntireChannelRealm();
                    entireChannelRealm.setId(entireChannelData.id());
                    entireChannelRealm.setOrder(entireChannelData.order());
                    entireChannelRealm.setTitle(entireChannelData.title());
                    entireChannelRealm.setDesc(entireChannelData.desc());
                    entireChannelRealm.setImage(entireChannelData.image());
                    return entireChannelRealm;
                }).collect(Collectors.toList());
    }
}
