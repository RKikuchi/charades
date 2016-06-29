package com.pongo.charades.models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CategoryItemModel extends RealmObject {
    @Required
    private String value;

    public CategoryItemModel() {}

    public CategoryItemModel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
