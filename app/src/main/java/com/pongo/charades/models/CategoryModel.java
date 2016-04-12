package com.pongo.charades.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CategoryModel extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String title;
    private String language;
    @Required
    private Boolean isCustom;
    @Required
    private Boolean isHidden;
    private String icon;
    private RealmList<CategoryItemModel> items = new RealmList<>();

    public static CategoryModel loadDto(CategoryDto dto) {
        CategoryModel model = new CategoryModel();
        model.setIsCustom(false);
        model.setIsHidden(false);
        model.setId(dto.id);
        model.setTitle(dto.title);
        model.setLanguage(dto.language);
        for (CategoryItemDto itemDto : dto.items) {
            if (itemDto.value == null || itemDto.value.trim() == "") continue;
            model.getItems().add(new CategoryItemModel(itemDto.value, itemDto.definition));
        }
        return model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
