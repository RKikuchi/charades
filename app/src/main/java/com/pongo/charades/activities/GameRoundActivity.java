package com.pongo.charades.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.adapters.ScoreTrackRecyclerViewAdapter;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.modules.FontAwesomeProvider;
import com.pongo.charades.services.SoundService;
import com.pongo.charades.services.TiltSensorService;
import com.pongo.charades.transforms.BlurTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import io.realm.Realm;

public class GameRoundActivity extends BaseActivity implements TiltSensorService.TiltEventListener {
    public static final String CATEGORY_ID = "CATEGORY_ID";

    private enum State {
        COUNTDOWN,
        PLAYING,
        GAME_OVER
    }

    @Inject
    FontAwesomeProvider mFontAwesome;
    private CategoryModel mCategory;
    private ArrayList<CategoryItemModel> mItems;
    private int mCurrentItemIndex;
    private CategoryItemModel mCurrentItem;
    private State mState;
    private int mTotalRoundTime;

    private View mLayout;
    private CardView mCard;
    private View mCardLayout;
    private ImageView mImage;
    private TextView mMainText;
    private TextView mCountdownText;
    private TextView mTopText;
    private LinearLayout mSkipButton;
    private LinearLayout mBackButton;
    private LinearLayout mReplayButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private AnimatorSet mAnimatorOutDown;
    private AnimatorSet mAnimatorOutUp;
    private AnimatorSet mAnimatorInDown;
    private AnimatorSet mAnimatorInUp;

    private Bundle mExtras;
    private TiltSensorService mTiltSensor;
    private SoundService mSoundService;

    private int mScore;
    private ScoreTrackRecyclerViewAdapter mScoreTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);

        mState = State.COUNTDOWN;
        mTotalRoundTime = Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_round_time), "60"));
        Intent intent = getIntent();
        mExtras = intent.getExtras();
        mTiltSensor = new TiltSensorService(this, this);
        mSoundService = new SoundService(this);
        mScoreTrack = new ScoreTrackRecyclerViewAdapter(this);

        mLayout = findViewById(R.id.game_round_layout);
        mCard = (CardView) findViewById(R.id.card);
        mCardLayout = findViewById(R.id.card_layout);
        mImage = (ImageView) findViewById(R.id.category_image);
        mMainText = (TextView) findViewById(R.id.main_text);
        mCountdownText = (TextView) findViewById(R.id.countdown_text);
        mTopText = (TextView) findViewById(R.id.top_text);
        mSkipButton = (LinearLayout) findViewById(R.id.skip_button);
        mBackButton = (LinearLayout) findViewById(R.id.back_button);
        mReplayButton = (LinearLayout) findViewById(R.id.replay_button);

        mRecyclerView = (RecyclerView) findViewById(R.id.score_track_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mScoreTrack);

        ((TextView)findViewById(R.id.skip_icon)).setTypeface(mFontAwesome.getTypeface());
        ((TextView)findViewById(R.id.back_icon)).setTypeface(mFontAwesome.getTypeface());
        ((TextView)findViewById(R.id.replay_icon)).setTypeface(mFontAwesome.getTypeface());

        loadAnimators();

        mMainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipOrScore(true);
                changeWord();
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipOrScore(false);
                changeWord();
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

    private void loadAnimators() {
        mAnimatorOutDown = (AnimatorSet) AnimatorInflater.loadAnimator(
                getApplicationContext(),
                R.animator.card_out_down);
        mAnimatorOutUp = (AnimatorSet) AnimatorInflater.loadAnimator(
                getApplicationContext(),
                R.animator.card_out_up);
        mAnimatorInDown = (AnimatorSet) AnimatorInflater.loadAnimator(
                getApplicationContext(),
                R.animator.card_in_down);
        mAnimatorInUp = (AnimatorSet) AnimatorInflater.loadAnimator(
                getApplicationContext(),
                R.animator.card_in_up);

        mAnimatorOutDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorInUp.setTarget(mCard);
                mAnimatorInUp.start();
            }
        });
        mAnimatorOutUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorInDown.setTarget(mCard);
                mAnimatorInDown.start();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hideUi();
        start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTiltSensor.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTiltSensor.pause();
    }

    @Override
    public void onTiltChanged(TiltSensorService.State oldState, TiltSensorService.State newState) {
        if (mState != State.PLAYING) return;

        switch (newState) {
            case UPWARDS:
                skipOrScore(true);
                mCardLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRightBg));
                break;
            case DOWNWARDS:
                skipOrScore(false);
                mCardLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWrongBg));
                break;
            case NEUTRAL:
                changeWord();
                mCardLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNeutralBg));
                break;
        }
    }

    private void loadCategory() {
        int categoryId = mExtras.getInt(CATEGORY_ID);
        Realm realm = Realm.getInstance(getApplicationContext());
        try {
            mCategory = realm.where(CategoryModel.class)
                                          .equalTo("id", categoryId)
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
        loadImage();
    }

    private void loadImage() {
        Picasso
                .with(this)
                .load("http://lorempixel.com/400/200/?rnd=" + mCategory.getId())
                .placeholder(R.drawable.category_cell_placeholder)
                //.transform(new BlurTransform(this, 10))
                //.transform(new ContrastTransform(mContext, 0.33f, 1))
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mImage);
    }

    private void hideUi() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void setupRoundTimer() {
        new CountDownTimer(mTotalRoundTime * 1000 + 500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                mTopText.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }

            @Override
            public void onFinish() {
                if (mTiltSensor.getState() == TiltSensorService.State.NEUTRAL)
                    mScoreTrack.add(mCurrentItem.getValue(), false);
                mState = State.GAME_OVER;

                mSkipButton.setVisibility(View.INVISIBLE);
                mBackButton.setVisibility(View.VISIBLE);
                mReplayButton.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mMainText.setVisibility(View.GONE);
                mCardLayout.setBackgroundColor(
                        ContextCompat.getColor(mLayout.getContext(), R.color.colorNeutralBg));
                mScoreTrack.notifyDataSetChanged();
                mTopText.setText("Score: " + mScore);
                mSoundService.playFinish();
            }
        }.start();
    }

    private void skipOrScore(Boolean score) {
        if (score) {
            mAnimatorOutUp.setTarget(mCard);
            mAnimatorOutUp.start();
        } else {
            mAnimatorOutDown.setTarget(mCard);
            mAnimatorOutDown.start();
        }

        if (score) {
            mSoundService.playSuccess();
            mScore++;
        } else {
            mSoundService.playSkip();
        }

        if (mCurrentItem != null) {
            mScoreTrack.add(mCurrentItem.getValue(), score);
        }
    }

    private void changeWord() {
        if (mState != State.PLAYING) return;

        mCurrentItemIndex = (mCurrentItemIndex + 1) % mItems.size();
        mCurrentItem = mItems.get(mCurrentItemIndex);
        mMainText.setText(mCurrentItem.getValue());
    }

    private void start() {
        mTopText.setText(mCategory.getTitle());
        mCard.setVisibility(View.GONE);
        mSkipButton.setVisibility(View.INVISIBLE);
        mBackButton.setVisibility(View.INVISIBLE);
        mReplayButton.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCountdownText.setVisibility(View.VISIBLE);
        new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCountdownText.setText(String.valueOf(millisUntilFinished / 1000));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mCountdownText.getWidth() / 2;
                    int cy = mCountdownText.getHeight() / 2;
                    float finalRadius = (float) Math.max(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(
                            mCountdownText, cx, cy, 0, finalRadius);
                    anim.setDuration(250);
                    anim.start();
                }
                mSoundService.playTick();
            }

            @Override
            public void onFinish() {
                mState = State.PLAYING;
                mScore = 0;
                mScoreTrack.clear();
                mCountdownText.setVisibility(View.GONE);
                mCard.setVisibility(View.VISIBLE);
                mAnimatorInUp.setTarget(mCard);
                mAnimatorInUp.start();
                mMainText.setVisibility(View.VISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                setupRoundTimer();
                changeWord();
                mSoundService.playStart();
            }
        }.start();
    }
}
