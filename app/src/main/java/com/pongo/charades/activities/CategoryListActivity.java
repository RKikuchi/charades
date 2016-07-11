package com.pongo.charades.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.pongo.charades.R;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CategoryListActivity
        extends AppCompatActivity
        implements CategoryCatalogFragment.CategoryCatalogListener {

    public static final String LIST_NAME = "LIST_NAME";
    public static final String FILTER = "FILTER";
    public static final String TAGS = "TAGS";

    private CategoryCatalogFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragment = (CategoryCatalogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.catalog_fragment);

        setupList(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setupList(intent);
    }

    private void setupList(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mFragment.setup(
                    CategoryCatalogFragment.FILTER_SEARCH,
                    new ArrayList<>(Arrays.asList(query)));
            setTitle(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String[] queryArgs = intent.getDataString().split(":", 2);
            if (Objects.equals(queryArgs[0], "tag")) {
                mFragment.setup(
                        CategoryCatalogFragment.FILTER_SEARCH,
                        new ArrayList<>(Arrays.asList(queryArgs[1])));
            } else if (Objects.equals(queryArgs[0], "title")) {
                mFragment.setup(queryArgs[1]);
            }
            setTitle(queryArgs[1]);
        } else {
            Bundle extras = intent.getExtras();
            setTitle(extras.getString(LIST_NAME));
            mFragment.setup(
                    extras.getInt(FILTER),
                    extras.getStringArrayList(TAGS));
        }
    }

    private void setTitle(String title) {
        getSupportActionBar().setTitle(title);
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
    public void onCategoryAttached(CategoryCatalogFragment fragment) {
        // TODO
    }

    @Override
    public void onCategoryDetached(CategoryCatalogFragment fragment) {
        // TODO
    }

    @Override
    public void onCategoryHidden(CategoryCatalogFragment fragment, int position, CharadesCellViewHolder holder) {
        // TODO
    }

    @Override
    public void onCategoryFavorited(CategoryCatalogFragment fragment, int position, CharadesCellViewHolder holder) {
        // TODO
    }

    @Override
    public void onCategoryUnfavorited(CategoryCatalogFragment fragment, int position, CharadesCellViewHolder holder) {
        // TODO
    }
}
