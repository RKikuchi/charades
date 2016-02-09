package com.pongo.charades.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import io.realm.Realm;

public class GameRoundActivity extends BaseActivity {
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";

    private enum State {
        COUNTDOWN,
        PLAYING,
        GAME_OVER
    };

    @Inject
    FontAwesomeProvider mFontAwesome;
    private CategoryModel mCategory;
    private ArrayList<CategoryItemModel> mItems;
    private int mCurrentItemIndex;
    private State mState;
    private int mScore;
    private TextView mMainText;
    private TextView mTopText;
    private LinearLayout mSkipButton;
    private LinearLayout mBackButton;
    private LinearLayout mReplayButton;
    private Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        mState = State.COUNTDOWN;
        Intent intent = getIntent();
        mExtras = intent.getExtras();

        mMainText = (TextView) findViewById(R.id.main_text);
        mTopText = (TextView) findViewById(R.id.top_text);
        mSkipButton = (LinearLayout) findViewById(R.id.skip_button);
        mBackButton = (LinearLayout) findViewById(R.id.back_button);
        mReplayButton = (LinearLayout) findViewById(R.id.replay_button);

        ((TextView)findViewById(R.id.skip_icon)).setTypeface(mFontAwesome.getTypeface());
        ((TextView)findViewById(R.id.back_icon)).setTypeface(mFontAwesome.getTypeface());
        ((TextView)findViewById(R.id.replay_icon)).setTypeface(mFontAwesome.getTypeface());

        mMainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeWord(true);
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWord(false);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        loadCategory();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        start();
    }

    private void loadCategory() {
        String categoryTitle = mExtras.getString(CATEGORY_TITLE);
        Realm realm = Realm.getInstance(getApplicationContext());
        try {
            mCategory = realm.where(CategoryModel.class)
                                          .equalTo("title", categoryTitle)
                                          .findFirst();

            mItems = new ArrayList<>(mCategory.getItems());

            // Shuffle up and deal!
            Random rng;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                rng = ThreadLocalRandom.current();
            } else {
                rng = new Random();
            }
            for (int i = mItems.size() - 1; i > 0; i--) {
                int index = rng.nextInt(i + 1);
                CategoryItemModel item = mItems.get(index);
                mItems.set(index, mItems.get(i));
                mItems.set(i, item);
            }
        } finally {
            realm.close();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mMainText.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void setupRoundTimer() {
        new CountDownTimer(10000 + 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                mTopText.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }

            @Override
            public void onFinish() {
                mState = State.GAME_OVER;

                mSkipButton.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.VISIBLE);
                mReplayButton.setVisibility(View.VISIBLE);
                mTopText.setText("GAME OVER");
                mMainText.setText("Score: " + mScore);
            }
        }.start();
    }

    private void changeWord(Boolean score) {
        if (mState != State.PLAYING) return;

        if (score) mScore++;

        mCurrentItemIndex = (mCurrentItemIndex + 1) % mItems.size();
        CategoryItemModel currentItem = mItems.get(mCurrentItemIndex);
        mMainText.setText(currentItem.getValue());
    }

    private void start() {
        mTopText.setText(mCategory.getTitle());
        mSkipButton.setVisibility(View.INVISIBLE);
        mBackButton.setVisibility(View.INVISIBLE);
        mReplayButton.setVisibility(View.INVISIBLE);
        hide();
        new CountDownTimer(3500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMainText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mState = State.PLAYING;
                mScore = 0;
                mSkipButton.setVisibility(View.VISIBLE);
                setupRoundTimer();
                changeWord(false);
            }
        }.start();
    }
}
