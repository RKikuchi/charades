package com.pongo.charades.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pongo.charades.R;
import com.pongo.charades.adapters.CategoryItemsRecyclerViewAdapter;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;

import io.realm.Realm;

public class ManageCategoryActivity extends AppCompatActivity {
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";

    private RecyclerView mRecyclerView;
    private CategoryItemsRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryModel mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        loadCategory();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.manage_category_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryItemsRecyclerViewAdapter(this, mRecyclerView, mCategory);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View newItemButton = findViewById(R.id.manage_category_new_item_button);
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int lastPos = mCategory.getItems().size();
                mCategory.getItems().add(new CategoryItemModel("", null));
                mAdapter.notifyItemInserted(lastPos);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.focusItem(lastPos);
                        mRecyclerView.smoothScrollToPosition(lastPos);
                    }
                });
            }
        });
    }

    private void loadCategory() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String categoryTitle = extras == null ? null : extras.getString(CATEGORY_TITLE);
        if (categoryTitle == null) {
            mCategory = new CategoryModel();
            mCategory.getItems().add(new CategoryItemModel("Test 1", null));
            mCategory.getItems().add(new CategoryItemModel("Test 2", null));
            mCategory.getItems().add(new CategoryItemModel("Test 3", null));
            return;
        }

        Realm realm = Realm.getInstance(getApplicationContext());
        try {
            mCategory = realm.where(CategoryModel.class)
                    .equalTo("title", categoryTitle)
                    .findFirst();
        } finally {
            realm.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_menu_save:
                // TODO
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
