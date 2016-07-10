package com.pongo.charades.models;

import java.util.HashSet;

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
    @Required
    private Boolean isFavorite;
    private String icon;
    private String imagePath;
    private RealmList<CategoryTagModel> tags = new RealmList<>();
    private RealmList<CategoryItemModel> items = new RealmList<>();

    public static CategoryModel loadDto(CategoryDto dto) {
        CategoryModel model = new CategoryModel();
        model.setIsCustom(false);
        model.setIsHidden(false);
        model.setIsFavorite(false);
        model.setId(dto.id);
        model.setTitle(dto.title);
        model.setLanguage(dto.language);
        model.setImagePath(dto.imagePath);
        HashSet<String> tags = new HashSet<>();
        for (String tag : dto.tags) {
            if (tag == null || tag.trim() == "") continue;
            tags.add(tag);
        }
        for (String tag : tags) {
            model.getTags().add(new CategoryTagModel(tag, dto.language));
        }
        for (String item : dto.items) {
            if (item == null || item.trim() == "") continue;
            model.getItems().add(new CategoryItemModel(item));
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

    public RealmList<CategoryTagModel> getTags() {
        return tags;
    }

    public void setTags(RealmList<CategoryTagModel> tags) {
        this.tags = tags;
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

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean favorite) {
        isFavorite = favorite;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
