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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pongo.charades.R;
import com.pongo.charades.adapters.CategoryCatalogPagerAdapter;
import com.pongo.charades.async.OnlineCategoriesLoader;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryTagModel;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity
        extends BaseActivity
        implements
        CategoryCatalogFragment.CategoryCatalogListener,
        OnlineCategoriesLoader.OnlineCategoriesLoaderCallback {
    public static final int REQUEST_CODE_MANAGE_CATEGORY = 1;
    public static final String EXTRA_CATEGORY_POSITION = "CATEGORY_POSITION";

    private Realm mRealm;
    private HashSet<String> mTags;

    // Views
    private Button mLanguageButton;
    private Button mHowToPlayButton;
    private FloatingActionButton mFab;
    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavigation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTitle;
    private FrameLayout mTitleContainer;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ActionBarDrawerToggle mDrawerToggle;
    private CategoryCatalogPagerAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getInstance(this);
        mTags = new HashSet<>();
        setViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        setToolbarAnimator(mAppBarLayout, mTitle, mToolbar, mTitleContainer);
        setActionBarDrawerToggle();

        mLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
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
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        mTabLayout.getTabAt(0).setText(R.string.main_categories);
        mTabLayout.getTabAt(1).setText(R.string.favorites);
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

    @Override
    protected void onResume() {
        super.onResume();
        String language = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_language), null);
        if (language != null) {
            String[] parts = language.split("_");
            String lang = parts.length > 0 ? parts[0] : "";
            String country = parts.length > 1 ? parts[1] : "";
            String variant = parts.length > 2 ? parts[2] : "";
            Locale locale = new Locale(lang, country, variant);
            mLanguageButton.setText(locale.getDisplayLanguage(locale));
        }
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
                        mTabLayout.getTabAt(0).select();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.item_favorites:
                        mTabLayout.getTabAt(1).select();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.item_hidden:
                        intent = new Intent(activity, CategoryListActivity.class);
                        startActivity(intent);
                        return false;
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
        mTitleContainer = (FrameLayout) findViewById(R.id.layout_title);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mLanguageButton = (Button) findViewById(R.id.language_button);
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
        RealmResults<CategoryTagModel> tags = mRealm.where(CategoryTagModel.class).findAll();
        mTags.clear();
        for (CategoryTagModel tag : tags) {
            mTags.add(tag.getValue());
        }

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
        mRealm.copyToRealmOrUpdate(categories);
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

    @Override
    public void onCategoryFavorited(CategoryCatalogFragment fragment, int position) {
        mAdapter.itemFavorited(fragment, position);
    }

    @Override
    public void onCategoryUnfavorited(CategoryCatalogFragment fragment, int position) {
        mAdapter.itemUnfavorited(fragment, position);
    }
}
