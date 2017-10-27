package com.battleshippark.bsp_langpod.domain;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

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

    List<ChannelRealm> entireChannelListJsonAsRealm(List<ChannelRealm> channelRealmList, EntireChannelListJson entireChannelListJson) {
        //json을 사용하는데, 로컬에 있는 같은 id의 에피소드와 구독 여부를 참고한다
        return Stream.of(entireChannelListJson.items())
                .map(json -> {
                    for (ChannelRealm localRealm : channelRealmList) {
                        if (localRealm.getId() == json.id()) {
                            RealmList<EpisodeRealm> episodeRealmList = new RealmList<>();
                            episodeRealmList.addAll(localRealm.getEpisodes());

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
        Map<Integer, EpisodeRealm> curEpisodeHashRealmMap = Stream.of(channelRealm.getEpisodes())
                .collect(
                        Collectors.toMap(
                                episodeRealm -> (episodeRealm.getTitle() + episodeRealm.getDesc()).hashCode()
                        ));

        RealmList<EpisodeRealm> episodeRealmList = Stream.of(channelJson.episodes())
                .map(episodeJson -> {
                    int hash = (episodeJson.title() + episodeJson.desc()).hashCode();
                    EpisodeRealm episodeRealm = curEpisodeHashRealmMap.get(hash);
                    if (episodeRealm == null) {
                        return new EpisodeRealm(realmHelper.getNextEpisodeId(), episodeJson.title(), episodeJson.desc(), episodeJson.url(), episodeJson.length(), episodeJson.date());
                    } else {
                        EpisodeRealm newEpisodeRealm = new EpisodeRealm(episodeRealm);
                        newEpisodeRealm.setTitle(episodeJson.title());
                        newEpisodeRealm.setUrl(episodeJson.url());
                        newEpisodeRealm.setLength(episodeJson.length());
                        newEpisodeRealm.setDate(episodeJson.date());
                        return newEpisodeRealm;
                    }
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

    public DownloadRealm asDownloadRealm(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        DownloadRealm downloadRealm = DownloadRealm.of(channelRealm, episodeRealm);
        downloadRealm.setId(realmHelper.getNextDownloadId());
        return downloadRealm;
    }
}
