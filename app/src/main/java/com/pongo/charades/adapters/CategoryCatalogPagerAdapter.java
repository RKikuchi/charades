package com.pongo.charades.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
        Log.d("test", "Adding " + mFragments.get(mFragments.size()-1).getId());
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("test", "Getting " + mFragments.get(position).getId());
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
            Log.d("test", "testing " + f.getId() + " vs " + fragment.getId() + " = " + f.isSameAs(fragment));
            f.itemFavorited(position, f.isSameAs(fragment));
        }
    }

    public void itemUnfavorited(CategoryCatalogFragment fragment, int position) {
        for (CategoryCatalogFragment f : mFragments) {
            Log.d("test", "testing " + f.getId() + " vs " + fragment.getId() + " = " + f.isSameAs(fragment));
            f.itemUnfavorited(position, f.isSameAs(fragment));
        }
    }
}
