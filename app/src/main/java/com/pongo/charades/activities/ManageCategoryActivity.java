package com.pongo.charades.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.pongo.charades.R;
import com.pongo.charades.adapters.CategoryItemsRecyclerViewAdapter;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryItemDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;

import java.util.ArrayList;

import javax.inject.Inject;

import io.realm.Realm;

public class ManageCategoryActivity extends BaseActivity {
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String EXTRA_IS_NEW = "IS_NEW";
    public static final String EXTRA_ITEM_ID = "ITEM_ID";
    public static final String EXTRA_ITEM_TITLE = "ITEM_TITLE";

    @Inject
    FontAwesomeProvider mFontAwesome;
    private RecyclerView mRecyclerView;
    private CategoryItemsRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryDto mCategory;
    private EditText mNameEditText;
    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        mIsNew = !loadCategory();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.manage_category_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryItemsRecyclerViewAdapter(this,
                mFontAwesome, mRecyclerView, mCategory);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View newItemButton = findViewById(R.id.manage_category_new_item_button);
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int lastPos = mCategory.items.size();
                CategoryItemDto newItem = new CategoryItemDto();
                mCategory.items.add(newItem);
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

        mNameEditText = (EditText) findViewById(R.id.manage_category_name);
        if (mIsNew) {
            mNameEditText.requestFocus();
        } else {
            mNameEditText.setText(mCategory.title);
        }
    }

    private boolean loadCategory() {
        Intent intent = getIntent();
        int categoryId = intent.getIntExtra(CATEGORY_ID, -1);
        if (categoryId == -1) {
            mCategory = new CategoryDto();
            mCategory.items = new ArrayList<>();
            mCategory.items.add(new CategoryItemDto());
            return false;
        }

        Realm realm = Realm.getInstance(getApplicationContext());
        try {
            CategoryModel model = realm
                    .where(CategoryModel.class)
                    .equalTo("id", categoryId)
                    .findFirst();
            mCategory = CategoryDto.fromModel(model);
        } finally {
            realm.close();
        }
        return true;
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
                save();
                setResult(RESULT_OK, getSuccessIntent());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        Realm realm = Realm.getInstance(getApplicationContext());
        mCategory.title = mNameEditText.getText().toString();

        if (mCategory.id == 0) {
            Number lastId = realm.where(CategoryModel.class).max("id");
            int nextId = lastId != null ? lastId.intValue() + 1 : 1;
            mCategory.id = nextId;
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(CategoryModel.loadDto(mCategory));
        realm.commitTransaction();
    }

    private Intent getSuccessIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IS_NEW, mIsNew);
        intent.putExtra(EXTRA_ITEM_ID, mCategory.id);
        intent.putExtra(EXTRA_ITEM_TITLE, mCategory.title);
        int adapterPosition = getIntent().getIntExtra(MainActivity.EXTRA_CATEGORY_POSITION, -1);
        if (adapterPosition != -1) {
            intent.putExtra(MainActivity.EXTRA_CATEGORY_POSITION, adapterPosition);
        }
        return intent;
    }
}
