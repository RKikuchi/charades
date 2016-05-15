package com.pongo.charades.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pongo.charades.R;
import com.pongo.charades.adapters.CategoryCatalogPagerAdapter;
import com.pongo.charades.async.OnlineCategoriesLoader;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;

public class MainActivity
        extends BaseActivity
        implements
        CategoryCatalogFragment.CategoryCatalogListener,
        OnlineCategoriesLoader.OnlineCategoriesLoaderCallback {
    public static final int REQUEST_CODE_MANAGE_CATEGORY = 1;
    public static final String EXTRA_CATEGORY_POSITION = "CATEGORY_POSITION";

    private Realm mRealm;

    // Views
    private Button mHowToPlayButton;
    private FloatingActionButton mFab;
    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavigation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTitle;
    private LinearLayout mTitleContainer;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ActionBarDrawerToggle mDrawerToggle;
    private CategoryCatalogPagerAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getInstance(this);
        setViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        setToolbarAnimator(mAppBarLayout, mTitle, mToolbar, mTitleContainer);
        setActionBarDrawerToggle();

        mHowToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHowToPlayActivity();
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategory();
            }
        });

        setupTabs();
        setup();
    }

    private void setupTabs() {
        mAdapter = new CategoryCatalogPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(CategoryCatalogFragment.FILTER_MAIN);
        mAdapter.addFragment(CategoryCatalogFragment.FILTER_FAVORITES);
        mAdapter.addFragment(CategoryCatalogFragment.FILTER_FAMILY);
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        mTabLayout.getTabAt(0).setText(R.string.main_categories);
        mTabLayout.getTabAt(1).setText(R.string.favorites);
        mTabLayout.getTabAt(2).setText(R.string.hidden);
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
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.item_main_categories:
                        //TODO mAdapter.setMode(CharadesRecyclerViewAdapter.MODE_DEFAULT);
                        mTitle.setText(R.string.main_categories);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.item_favorites:
                        //TODO mAdapter.setMode(CharadesRecyclerViewAdapter.MODE_FAVORITES);
                        mTitle.setText(R.string.favorites);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.item_hidden:
                        //TODO mAdapter.setMode(CharadesRecyclerViewAdapter.MODE_SHOW_ALL);
                        mTitle.setText(R.string.favorites);
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.item_settings:
                        intent = new Intent(activity, SettingsActivity.class);
                        startActivity(intent);
                        return false;
                    case R.id.item_how_to_play:
                        openHowToPlayActivity();
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
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mHowToPlayButton = (Button) findViewById(R.id.how_to_play_button);
        mFab = (FloatingActionButton) findViewById(R.id.create_fab);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MANAGE_CATEGORY:
                if (resultCode != RESULT_OK) break;

                String title = data.getStringExtra(ManageCategoryActivity.EXTRA_ITEM_TITLE);
                int filter = data.getIntExtra(ManageCategoryActivity.EXTRA_ORIGINAL_FILTER, 0);
                if (data.getBooleanExtra(ManageCategoryActivity.EXTRA_IS_NEW, true)) {
                    mAdapter.newItemAdded(filter);
                } else {
                    int pos = data.getIntExtra(EXTRA_CATEGORY_POSITION, -1);
                    mAdapter.itemChanged(filter, pos);
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
                //TODO mAdapter.setMode(CharadesRecyclerViewAdapter.MODE_SHOW_ALL);
                return true;
            case R.id.action_how_to_play:
                openHowToPlayActivity();
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
            setDefaultLanguage();
            //syncOnlineCategories();
        }
    }

    private void setDefaultLanguage() {
        String systemLanguage = Locale.getDefault().toString();
        String[] languages = getResources().getStringArray(R.array.pref_language_list_values);
        String language = languages[0];
        for (String availableLanguage : languages) {
            if (Objects.equals(availableLanguage, systemLanguage)) {
                language = systemLanguage;
                break;
            }
        }

        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(getString(R.string.pref_key_language), language)
                .commit();
    }

    private void openHowToPlayActivity() {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
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

    @Override
    public void onCategorySelected(String categoryName) {
        //TODO
    }

    @Override
    public void onCategoryHidden(final CategoryCatalogFragment fragment,
                                 final int position,
                                 final CharadesCellViewHolder holder) {
        String title = holder.getCategory().getTitle();

        Snackbar.make(mLayout, "Category \"" + title + "\" hidden.", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.unhideCategory(position, holder.getModelHolder());
                    }
                })
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                .show();
    }
}
