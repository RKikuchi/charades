package com.pongo.charades.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pongo.charades.activities.CategoryCatalogFragment;

import java.util.ArrayList;

/**
 * Created by rsaki on 5/14/2016.
 */
public class CategoryCatalogPagerAdapter extends FragmentPagerAdapter {
    ArrayList<CategoryCatalogFragment> mFragments = new ArrayList<>();

    public CategoryCatalogPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(int filterType) {
        mFragments.add(CategoryCatalogFragment.newInstance(filterType, null));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void newItemAdded(int filter) {
        for (CategoryCatalogFragment f : mFragments) {
            f.newItemAdded(filter);
        }
    }

    public void itemChanged(int filter, int pos) {
        for (CategoryCatalogFragment f : mFragments) {
            f.itemChanged(filter, pos);
        }
    }

    public void reload() {
        for (CategoryCatalogFragment f : mFragments) {
            f.reload();
        }
    }

    public void itemFavorited(CategoryCatalogFragment fragment, int position) {
        for (CategoryCatalogFragment f : mFragments) {
            f.itemFavorited(position, f == fragment);
        }
    }

    public void itemUnfavorited(CategoryCatalogFragment fragment, int position) {
        for (CategoryCatalogFragment f : mFragments) {
            f.itemUnfavorited(position, f == fragment);
        }
    }
}
