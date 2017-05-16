package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.EntireChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;

/**
 */

public class RealmMapper {
    public EntireChannelListJson asData(List<EntireChannelRealm> entireChannelRealmList) {
        return EntireChannelListJson.create(
                Stream.of(entireChannelRealmList)
                        .map(realm -> EntireChannelJson.create(realm.getId(), realm.getOrder(), realm.getTitle(), realm.getDesc(), realm.getImage()))
                        .collect(Collectors.toList())
        );
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
