package com.pongo.charades.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pongo.charades.R;
import com.pongo.charades.adapters.CharadesRecyclerViewAdapter;
import com.pongo.charades.async.OnlineCategoriesLoader;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements OnlineCategoriesLoader.OnlineCategoriesLoaderCallback {
    private static final int REQUEST_CODE_MANAGE_CATEGORY = 1;
    public static final String EXTRA_CATEGORY_POSITION = "CATEGORY_POSITION";

    @Inject
    FontAwesomeProvider mFontAwesome;

    private Realm mRealm;
    private CoordinatorLayout mLayout;
    private RecyclerView mRecyclerView;
    private CharadesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getInstance(this);

        mLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.charades_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new CharadesRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        CharadesCellViewHolder charadesHolder = (CharadesCellViewHolder) viewHolder;
                        hideCategory(charadesHolder);
                    }
                });
        touchHelper.attachToRecyclerView(mRecyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategory();
            }
        });

        setup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MANAGE_CATEGORY:
                if (resultCode != RESULT_OK) break;

                String title = data.getStringExtra(ManageCategoryActivity.EXTRA_ITEM_TITLE);
                if (data.getBooleanExtra(ManageCategoryActivity.EXTRA_IS_NEW, true)) {
                    final int lastPos = mAdapter.getItemCount();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.reload();
                            mAdapter.notifyItemInserted(lastPos);
                            mRecyclerView.smoothScrollToPosition(lastPos);
                        }
                    }, 500);
                } else {
                    int pos = data.getIntExtra(EXTRA_CATEGORY_POSITION, -1);
                    if (pos != -1 && pos < mAdapter.getItemCount()) {
                        mAdapter.notifyItemChanged(pos);
                    } else {
                        mAdapter.reload();
                        mAdapter.notifyDataSetChanged();
                    }
                    String msg = "Category \"" + title + "\" saved.";
                    Snackbar.make(mLayout, msg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sync_online_categories:
                syncOnlineCategories();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setup() {
        if (mRealm.where(CategoryModel.class).count() == 0) {
            loadHardcodedCategories();
            //syncOnlineCategories();
        }
    }

    private void syncOnlineCategories() {
        Snackbar.make(mLayout, R.string.online_categories_not_available, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        return;
//        Snackbar.make(mLayout, R.string.syncing_online_categories, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//        new OnlineCategoriesLoader().load(this);
    }

    private void loadHardcodedCategories() {
        ArrayList<CategoryModel> categories = new ArrayList<CategoryModel>();
        try {
            Number lastId = mRealm.where(CategoryModel.class).max("id");
            int prevId = lastId != null ? lastId.intValue() : 0;
            String[] files = getAssets().list("categories");
            for (String file : files) {
                InputStream is = getAssets().open("categories/" + file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Gson gson = new Gson();

                List<CategoryDto> dtos =
                        gson.fromJson(reader, new TypeToken<List<CategoryDto>>(){}.getType());
                for (CategoryDto dto : dtos) {
                    dto.id = ++prevId;
                    categories.add(CategoryModel.loadDto(dto));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadCategories(categories);
    }

    @Override
    public void categoriesReceived(List<CategoryModel> categories) {
        if (categories == null) {
            Snackbar.make(mLayout, R.string.error_loading_online_data, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                    .show();
            return;
        }

        loadCategories(categories);
    }

    private void loadCategories(List<CategoryModel> categories) {
        mRealm.beginTransaction();
        mRealm.where(CategoryModel.class)
                .equalTo("isCustom", false)
                .findAll()
                .clear();
        for (CategoryModel category : categories) {
            mRealm.copyToRealm(category);
        }
        mRealm.commitTransaction();
        mAdapter.notifyDataSetChanged();
    }

    public void createCategory() {
        Intent intent = new Intent(getBaseContext(), ManageCategoryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MANAGE_CATEGORY);
    }

    public void manageCategory(int position, CategoryModel category) {
        Intent intent = new Intent(getBaseContext(), ManageCategoryActivity.class);
        intent.putExtra(MainActivity.EXTRA_CATEGORY_POSITION, position);
        intent.putExtra(ManageCategoryActivity.CATEGORY_ID, category.getId());
        startActivityForResult(intent, REQUEST_CODE_MANAGE_CATEGORY);
    }

    public void hideCategory(CharadesCellViewHolder holder) {
        mAdapter.remove(holder.getAdapterPosition());

        CategoryModel category = holder.getCategory();
        String title = category.getTitle();

        // TODO: Undo
        Snackbar.make(mLayout, "Category \"" + title + "\" hidden.", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                .show();
    }

    public void deleteCategory(CategoryModel category) {
        mRealm.beginTransaction();
        mRealm.where(CategoryModel.class)
                .equalTo("id", category.getId())
                .findAll()
                .clear();
        mRealm.commitTransaction();
    }
}
