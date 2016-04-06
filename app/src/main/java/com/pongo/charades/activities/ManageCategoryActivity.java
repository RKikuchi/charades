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
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;

import javax.inject.Inject;

import io.realm.Realm;

public class ManageCategoryActivity extends BaseActivity {
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";

    @Inject
    FontAwesomeProvider mFontAwesome;
    private RecyclerView mRecyclerView;
    private CategoryItemsRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryModel mCategory;
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

        mNameEditText = (EditText) findViewById(R.id.manage_category_name);
        if (mIsNew) {
            mNameEditText.requestFocus();
        }
    }

    private boolean loadCategory() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String categoryTitle = extras == null ? null : extras.getString(CATEGORY_TITLE);
        if (categoryTitle == null) {
            mCategory = new CategoryModel();
            mCategory.setIsCustom(true);
            mCategory.getItems().add(new CategoryItemModel("", null));
            return false;
        }

        Realm realm = Realm.getInstance(getApplicationContext());
        try {
            mCategory = realm.where(CategoryModel.class)
                    .equalTo("title", categoryTitle)
                    .findFirst();
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
        mCategory.setTitle(mNameEditText.getText().toString());

        if (mCategory.getId() == 0) {
            Number lastId = realm.where(CategoryModel.class).max("id");
            int nextId = lastId != null ? lastId.intValue() + 1 : 1;
            mCategory.setId(nextId);
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mCategory);
        realm.commitTransaction();
    }

    private Intent getSuccessIntent() {
        Intent intent = new Intent();
        intent.putExtra("IS_NEW", mIsNew);
        intent.putExtra("ID", mCategory.getId());
        return intent;
    }
}
