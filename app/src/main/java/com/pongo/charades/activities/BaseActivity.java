package com.pongo.charades.activities;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.pongo.charades.CharadesApplication;

/**
 * Created by rsaki on 2/8/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private static final int ALPHA_ANIMATIONS_DURATION = 250;

    private class ToolbarOffsetAnimator implements AppBarLayout.OnOffsetChangedListener {
        private boolean mIsTheTitleVisible = false;
        private boolean mIsTheTitleContainerVisible = true;

        private View mTitle;
        private View mToolbar;
        private View mTitleContainer;

        private ToolbarOffsetAnimator(View title, View toolbar, View titleContainer) {
            mTitle = title;
            mToolbar = toolbar;
            mTitleContainer = titleContainer;
            startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);
        }

        private void handleToolbarTitleVisibility(float percentage) {
            if (percentage >= 0.9f) {
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
    }

    private ToolbarOffsetAnimator mToolbarAnimator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CharadesApplication)getApplication()).inject(this);
    }

    protected void setToolbarAnimator(
            AppBarLayout appBarLayout, View title, View toolbar, View titleContainer) {
        mToolbarAnimator = new ToolbarOffsetAnimator(title, toolbar, titleContainer);
        appBarLayout.addOnOffsetChangedListener(mToolbarAnimator);
    }
}
