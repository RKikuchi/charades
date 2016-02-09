package com.pongo.charades.modules;

import android.content.Context;
import android.graphics.Typeface;

import com.pongo.charades.activities.GameRoundActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rsaki on 2/8/2016.
 */
@Module(injects = GameRoundActivity.class)
public class FontAwesomeModule {
    Context mContext;

    public FontAwesomeModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    FontAwesomeProvider provideFontAwesomeProvider() {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
        return new FontAwesomeProvider(font);
    }
}
