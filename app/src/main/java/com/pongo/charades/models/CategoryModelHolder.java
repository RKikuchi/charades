package com.pongo.charades.models;

/**
 * Created by rsaki on 4/7/2016.
 */
public class CategoryModelHolder {
    private final CategoryModel mModel;
    private boolean mIsSelected;

    public CategoryModelHolder(CategoryModel model) {
        mModel = model;
        mIsSelected = false;
    }

    public CategoryModel getModel() {
        return mModel;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void isSelected(boolean value) {
        mIsSelected = value;
    }
}
