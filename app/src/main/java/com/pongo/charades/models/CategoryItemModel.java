package com.pongo.charades.models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CategoryItemModel extends RealmObject {
    @Required
    private String value;
    private String definition;

    public CategoryItemModel() {}

    public CategoryItemModel(String value, String definition) {
        this.value = value;
        this.definition = definition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
