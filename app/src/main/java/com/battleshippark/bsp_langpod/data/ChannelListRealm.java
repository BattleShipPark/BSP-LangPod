package com.battleshippark.bsp_langpod.data;

import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 */

public class ChannelListRealm extends RealmObject {
    private RealmList<ChannelListItemRealm> items;

    public RealmList<ChannelListItemRealm> getItems() {
        return items;
    }

    public void setItems(ChannelListItemRealm items) {
        this.items.clear();
        this.items.add(items);
    }

    @Override
    public String toString() {
        return "ChannelListRealm{" +
                "items=" + Arrays.toString(items.toArray()) +
                '}';
    }
}
