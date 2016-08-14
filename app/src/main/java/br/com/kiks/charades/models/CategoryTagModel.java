package br.com.kiks.charades.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CategoryTagModel extends RealmObject {
    @PrimaryKey
    private String value;

    private String language;

    public CategoryTagModel() {}

    public CategoryTagModel(String value, String language) {
        this.value = value;
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
