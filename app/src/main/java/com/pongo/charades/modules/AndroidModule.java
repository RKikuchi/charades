package com.pongo.charades.modules;

import android.content.Context;

import com.pongo.charades.CharadesApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rsaki on 2/8/2016.
 */
@Module(library = true)
public class AndroidModule {
    private final CharadesApplication mApplication;

    public AndroidModule(CharadesApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApplication;
    }
}
