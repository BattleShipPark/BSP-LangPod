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
                        .map(realm -> EntireChannelData.create(realm.getId(), realm.getTitle(), realm.getDesc(), realm.getImage()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * @return 결과 클래스의 id는 비어 있으므로 저장할 때 채워줘야 한다
     */
    public EntireChannelRealm asRealm(EntireChannelData entireChannelData) {
        EntireChannelRealm entireChannelRealm = new EntireChannelRealm();
        entireChannelRealm.setTitle(entireChannelData.title());
        entireChannelRealm.setDesc(entireChannelData.desc());
        entireChannelRealm.setImage(entireChannelData.image());
        return entireChannelRealm;
    }
}
