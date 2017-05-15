package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.EntireChannelData;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;

import java.util.List;

/**
 */

public class RealmMapper {
    public EntireChannelListData asData(List<EntireChannelRealm> entireChannelRealmList) {
        return EntireChannelListData.create(
                Stream.of(entireChannelRealmList)
                        .map(realm -> EntireChannelData.create(realm.getId(), realm.getOrder(), realm.getTitle(), realm.getDesc(), realm.getImage()))
                        .collect(Collectors.toList())
        );
    }

    public List<EntireChannelRealm> asRealm(EntireChannelListData entireChannelListData) {
        return Stream.of(entireChannelListData.items())
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
