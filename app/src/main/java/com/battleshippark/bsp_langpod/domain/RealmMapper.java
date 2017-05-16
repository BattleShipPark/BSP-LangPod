package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
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
