package com.pongo.charades.activities;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.adapters.CategoryItemsRecyclerViewAdapter;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryItemDto;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import io.realm.Realm;

public class ManageCategoryActivity
        extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener {
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String EXTRA_IS_NEW = "IS_NEW";
    public static final String EXTRA_ITEM_ID = "ITEM_ID";
    public static final String EXTRA_ITEM_TITLE = "ITEM_TITLE";
    private static final int ALPHA_ANIMATIONS_DURATION = 250;

    @Inject
    FontAwesomeProvider mFontAwesome;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTitle;
    private LinearLayout mTitleContainer;
    private FloatingActionButton mFab;

    private RecyclerView mRecyclerView;
    private CategoryItemsRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryDto mCategory;
    private EditText mNameEditText;
    private boolean mIsNew;
    private TextView mCategoryName;
    private ImageView mImage;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mAppBarLayout.addOnOffsetChangedListener(this);

        mTitle = (TextView) findViewById(R.id.title);
        mTitleContainer = (LinearLayout) findViewById(R.id.title_container);

        mToolbar = (Toolbar) findViewById(R.id.manage_category_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCategoryName = (TextView) findViewById(R.id.category_name);
        mImage = (ImageView) findViewById(R.id.category_image);
        mIsNew = !loadCategory();
        loadImage();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.manage_category_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryItemsRecyclerViewAdapter(this,
                mFontAwesome, mRecyclerView, mCategory);
        mRecyclerView.setAdapter(mAdapter);

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

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition = getWindow().getSharedElementEnterTransition();
            enterTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    mFab.hide();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    mFab.show();
                }

                @Override
                public void onTransitionCancel(Transition transition) {}

                @Override
                public void onTransitionPause(Transition transition) {}

                @Override
                public void onTransitionResume(Transition transition) {}
            });
        }

        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void startBackgroundAnimation(View v, int duration, int visibility) {
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        if (visibility == View.VISIBLE)
            transition.startTransition(duration);
        else
            transition.reverseTransition(duration);
    }

    private void loadImage() {
        Picasso
                .with(this)
                .load("http://lorempixel.com/400/200/?rnd=" + mCategory.id)
                .placeholder(R.drawable.category_cell_placeholder)
                //.transform(new BlurTransform(mContext, 10))
                //.transform(new ContrastTransform(mContext, 0.33f, 1))
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mImage);
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
        mCategoryName.setText(mCategory.title);
        mTitle.setText(mCategory.title);
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= 0.6f) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startBackgroundAnimation(mToolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startBackgroundAnimation(mToolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= 0.3f) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }
}
