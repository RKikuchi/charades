package br.com.kiks.charades.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kiks.charades.R;
import br.com.kiks.charades.adapters.HowToPlayPagerAdapter;

public class HowToPlayActivity
        extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private LinearLayout mDotsLayout;
    private ImageView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPager = (ViewPager) findViewById(R.id.pager);
        mDotsLayout = (LinearLayout) findViewById(R.id.dots);
        mPagerAdapter = new HowToPlayPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
        setDots();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mDots.length; i++) {
            setSelected(i, i == position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void setDots() {
        mDots = new ImageView[mPagerAdapter.getCount()];

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new ImageView(this);
            setSelected(i, i == 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            mDotsLayout.addView(mDots[i], params);
        }
    }

    private void setSelected(int dotIndex, boolean selected) {
        mDots[dotIndex].setImageDrawable(ContextCompat.getDrawable(this,
                selected ? R.drawable.shape_dot_selected : R.drawable.shape_dot_unselected));
    }
}
