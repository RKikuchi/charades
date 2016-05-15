package com.pongo.charades.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.pongo.charades.services.PicturePickerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import io.realm.Realm;

public class ManageCategoryActivity extends BaseActivity {
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String EXTRA_IS_NEW = "IS_NEW";
    public static final String EXTRA_ITEM_ID = "ITEM_ID";
    public static final String EXTRA_ITEM_TITLE = "ITEM_TITLE";
    public static final String EXTRA_ORIGINAL_FILTER = "ORIGINAL_FILTER";
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_IMG_PICKER_PERMISSION = 2;

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
    private View mNewItemButton;
    private TextView mCategoryName;
    private ImageView mImage;
    private PicturePickerService mPicturePicker;

    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        setViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarAnimator(mAppBarLayout, mTitle, mToolbar, mTitleContainer);

        mPicturePicker = new PicturePickerService(this);
        mIsNew = !loadCategory();
        loadImage();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryItemsRecyclerViewAdapter(this,
                mFontAwesome, mRecyclerView, mCategory);
        mRecyclerView.setAdapter(mAdapter);

        mNewItemButton.setOnClickListener(new View.OnClickListener() {
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

        if (mIsNew) {
            mNameEditText.requestFocus();
        } else {
            mNameEditText.setText(mCategory.title);
        }

        mFab.hide();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mPicturePicker.GetIntent();
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });

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
    }

    private void setViews() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mTitle = (TextView) findViewById(R.id.title);
        mTitleContainer = (LinearLayout) findViewById(R.id.title_container);
        mToolbar = (Toolbar) findViewById(R.id.manage_category_toolbar);
        mCategoryName = (TextView) findViewById(R.id.category_name);
        mImage = (ImageView) findViewById(R.id.category_image);
        mRecyclerView = (RecyclerView) findViewById(R.id.manage_category_recycler_view);
        mNameEditText = (EditText) findViewById(R.id.manage_category_name);
        mNewItemButton = findViewById(R.id.manage_category_new_item_button);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void loadImage() {
        Picasso
                .with(this)
                .load(mCategory.imagePath)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = mPicturePicker.getResult(data, REQUEST_IMG_PICKER_PERMISSION);
                    if (imageUri != null) {
                        mCategory.imagePath = imageUri.toString();
                        loadImage();
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMG_PICKER_PERMISSION:
                boolean success = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        success = false;
                        break;
                    }
                }
                if (!success)
                    break;

                Uri imageUri = mPicturePicker.getResultAfterPermission();
                if (imageUri != null) {
                    mCategory.imagePath = imageUri.toString();
                    loadImage();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
