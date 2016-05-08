package com.pongo.charades.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pongo.charades.R;
import com.pongo.charades.adapters.CharadesRecyclerViewAdapter;
import com.pongo.charades.async.OnlineCategoriesLoader;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity
        extends BaseActivity
        implements OnlineCategoriesLoader.OnlineCategoriesLoaderCallback {
    private static final int REQUEST_CODE_MANAGE_CATEGORY = 1;
    public static final String EXTRA_CATEGORY_POSITION = "CATEGORY_POSITION";

    private Realm mRealm;

    // Views
    private FloatingActionButton mFab;
    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavigation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTitle;
    private LinearLayout mTitleContainer;

    private RecyclerView mRecyclerView;
    private CharadesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionBarDrawerToggle mDrawerToggle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getInstance(this);
        setViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        setToolbarAnimator(mAppBarLayout, mTitle, mToolbar, mTitleContainer);
        setActionBarDrawerToggle();

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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategory();
            }
        });

        setup();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setActionBarDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open_menu,
                R.string.close_menu);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        final MainActivity activity = this;
        mDrawerNavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_main_categories:
                        Toast.makeText(activity, "Main categories, yo", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item_settings:
                        Intent intent = new Intent(activity, SettingsActivity.class);
                        startActivity(intent);
                        return false;
                }
                return false;
            }
        });
    }

    private void setViews() {
        mLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNavigation = (NavigationView) findViewById(R.id.drawer_navigation);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle = (TextView) findViewById(R.id.title);
        mTitleContainer = (LinearLayout) findViewById(R.id.title_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.charades_recycler_view);
        mFab = (FloatingActionButton) findViewById(R.id.create_fab);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_all_items:
                mAdapter.setMode(CharadesRecyclerViewAdapter.MODE_SHOW_ALL);
                return true;
            case R.id.action_how_to_play:
                intent = new Intent(this, HowToPlayActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
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
        mAdapter.reload();
        mAdapter.notifyDataSetChanged();
    }

    public void createCategory() {
        Intent intent = new Intent(getBaseContext(), ManageCategoryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MANAGE_CATEGORY);
    }

    public void manageCategory(CharadesCellViewHolder holder) {
        int position = holder.getAdapterPosition();
        CategoryModel category = holder.getCategory();

        Intent intent = new Intent(getBaseContext(), ManageCategoryActivity.class);
        intent.putExtra(MainActivity.EXTRA_CATEGORY_POSITION, position);
        intent.putExtra(ManageCategoryActivity.CATEGORY_ID, category.getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String titleTransitionName = getString(R.string.transition_category_name);
            String imageTransitionName = getString(R.string.transition_category_image);
            Pair<View, String> p1 = Pair.create(holder.getTitleLabel(), titleTransitionName);
            Pair<View, String> p2 = Pair.create(holder.getImage(), imageTransitionName);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, p1, p2);

            ActivityCompat.startActivityForResult(this, intent,
                    REQUEST_CODE_MANAGE_CATEGORY, options.toBundle());
        } else {
            startActivityForResult(intent, REQUEST_CODE_MANAGE_CATEGORY);
        }
    }

    public void hideCategory(final CharadesCellViewHolder holder) {
        final CategoryModel category = holder.getCategory();
        final int position = holder.getAdapterPosition();
        String title = category.getTitle();

        mRealm.beginTransaction();
        category.setIsHidden(true);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.remove(position);

        Snackbar.make(mLayout, "Category \"" + title + "\" hidden.", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unhideCategory(position, holder.getModelHolder());
                    }
                })
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                .show();
    }

    private void unhideCategory(int position, CategoryModelHolder categoryHolder) {
        CategoryModel category = categoryHolder.getModel();
        mRealm.beginTransaction();
        category.setIsHidden(false);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.add(position, categoryHolder);
        if (position == mAdapter.getItemCount() - 1) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public void unhideCategory(final CharadesCellViewHolder holder) {
        final CategoryModel category = holder.getCategory();
        final int position = holder.getAdapterPosition();

        mRealm.beginTransaction();
        category.setIsHidden(false);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.notifyItemChanged(position);
    }

    public void deleteCategory(CategoryModel category) {
        mRealm.beginTransaction();
        mRealm.where(CategoryModel.class)
                .equalTo("id", category.getId())
                .findAll()
                .clear();
        mRealm.commitTransaction();
    }

    public void playCategory(CharadesCellViewHolder holder) {
        Intent intent = new Intent(this, GameRoundActivity.class);
        intent.putExtra(GameRoundActivity.CATEGORY_ID, holder.getCategory().getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = getString(R.string.transition_category_image);
            Pair<View, String> p2 = Pair.create(holder.getImage(), imageTransitionName);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, p2);

            //Explode explode = new Explode();
            //explode.setDuration(1000);
            //TransitionSet ts = new TransitionSet();
            //ts.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
            //ts.addTransition(explode);
            //TransitionManager.go(getContentScene(), ts);
            //getWindow().setExitTransition(ts);

            //ActivityCompat.startActivity(this, intent, options.toBundle());
            startActivity(intent);
        } else {
            startActivity(intent);
        }
    }
}
