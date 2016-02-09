package com.pongo.charades.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CategoryModel extends RealmObject {
    @Required
    private String title;
    @Required
    private Boolean isCustom;
    private String icon;
    private RealmList<CategoryItemModel> items = new RealmList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<CategoryItemModel> getItems() {
        return items;
    }

    public void setItems(RealmList<CategoryItemModel> items) {
        this.items = items;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
