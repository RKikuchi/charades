package br.com.kiks.charades.modules;

import android.graphics.Typeface;

/**
 * Created by rsaki on 2/8/2016.
 */
public class FontAwesomeProvider {
    private final Typeface mTypeface;

    public FontAwesomeProvider(Typeface mTypeface) {
        this.mTypeface = mTypeface;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }
}
