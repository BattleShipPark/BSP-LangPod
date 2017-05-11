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
                        .map(realm -> EntireChannelData.create(realm.getTitle(), realm.getDesc(), realm.getImage()))
                        .collect(Collectors.toList())
        );
    }
}
