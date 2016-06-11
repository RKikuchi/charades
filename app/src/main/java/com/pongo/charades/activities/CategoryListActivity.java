package com.pongo.charades.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.pongo.charades.R;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

public class CategoryListActivity
        extends AppCompatActivity
        implements CategoryCatalogFragment.CategoryCatalogListener {

    private CategoryCatalogFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragment = (CategoryCatalogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.catalog_fragment);
        mFragment.setup(CategoryCatalogFragment.FILTER_MAIN, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCategorySelected(String categoryName) {
        // TODO
    }

    @Override
    public void onCategoryHidden(CategoryCatalogFragment fragment, int position, CharadesCellViewHolder holder) {
        // TODO
    }

    @Override
    public void onCategoryFavorited(CategoryCatalogFragment fragment, int position) {
        // TODO
    }

    @Override
    public void onCategoryUnfavorited(CategoryCatalogFragment fragment, int position) {
        // TODO
    }
}
