package br.com.kiks.charades.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.kiks.charades.activities.HowToPlaySlideFragment;

/**
 * Created by rsaki on 4/27/2016.
 */
public class HowToPlayPagerAdapter extends FragmentStatePagerAdapter {
    public HowToPlayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HowToPlaySlideFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}