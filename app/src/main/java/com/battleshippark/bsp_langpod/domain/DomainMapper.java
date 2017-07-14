package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import java.util.List;
import java.util.Map;

import io.realm.RealmList;

/**
 */

public class DomainMapper {
    private RealmHelper realmHelper;

    public DomainMapper(RealmHelper realmHelper) {
        this.realmHelper = realmHelper;
    }

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

    public MyChannelData myChannelJsonAsData(String url, ChannelJson channelJson) {
        return MyChannelData.create(
                channelJson.title(),
                channelJson.desc(),
                channelJson.copyright(),
                channelJson.image(),
                url,
                Stream.of(channelJson.episodes()).map(this::episodeJsonAsData).collect(Collectors.toList())
        );
    }

    public EpisodeData episodeJsonAsData(EpisodeJson episodeJson) {
        return EpisodeData.create(
                episodeJson.title(),
                episodeJson.desc(),
                episodeJson.url()
        );
    }

    List<ChannelRealm> entireChannelListJsonAsRealm(List<ChannelRealm> channelRealmList, EntireChannelListJson entireChannelListJson) {
        //json을 사용하는데, 로컬에 있는 같은 id의 에피소드와 구독 여부를 참고한다
        return Stream.of(entireChannelListJson.items())
                .map(json -> {
                    for (ChannelRealm localRealm : channelRealmList) {
                        if (localRealm.getId() == json.id()) {
                            RealmList<EpisodeRealm> episodeRealmList = new RealmList<>();
                            episodeRealmList.addAll(realmHelper.fromRealm(localRealm.getEpisodes()));

                            return new ChannelRealm(json.id(), json.order(), json.title(), json.desc(),
                                    json.image(), json.url(), localRealm.getCopyright(),
                                    episodeRealmList, localRealm.isSubscribed());
                        }
                    }
                    return new ChannelRealm(json.id(), json.order(), json.title(), json.desc(), json.image(), json.url(), false);
                })
                .collect(Collectors.toList());
    }

    ChannelRealm channelJsonAsRealm(ChannelRealm channelRealm, ChannelJson channelJson) {
        Map<Integer, Long> curEpisodeHashId = Stream.of(channelRealm.getEpisodes())
                .collect(
                        Collectors.toMap(
                                episodeRealm -> (episodeRealm.getTitle() + episodeRealm.getDesc()).hashCode(),
                                EpisodeRealm::getId
                        ));

        RealmList<EpisodeRealm> episodeRealmList = Stream.of(channelJson.episodes())
                .map(episodeJson -> {
                    int hash = (episodeJson.title() + episodeJson.desc()).hashCode();
                    long id = curEpisodeHashId.get(hash) == null ? realmHelper.getNextEpisodeId() : curEpisodeHashId.get(hash);
                    return new EpisodeRealm(id, episodeJson.title(), episodeJson.desc(), episodeJson.url(), episodeJson.length(), episodeJson.date());
                })
                .collect(RealmList::new, RealmList::add);
        return new ChannelRealm(
                channelRealm.getId(),
                channelRealm.getOrder(),
                channelJson.title(),
                channelJson.desc(),
                channelJson.image(),
                channelRealm.getUrl(),
                channelJson.copyright(),
                episodeRealmList,
                channelRealm.isSubscribed());
    }
}
