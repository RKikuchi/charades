package com.pongo.charades;

import android.app.Application;

import com.pongo.charades.modules.AndroidModule;
import com.pongo.charades.modules.FontAwesomeModule;

import java.util.Arrays;

import dagger.ObjectGraph;

/**
 * Created by rsaki on 2/8/2016.
 */
public class CharadesApplication extends Application {
    private ObjectGraph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        //mGraph = ObjectGraph.create(getModules());
        mGraph = ObjectGraph.create(new FontAwesomeModule(this));
    }

    private Object[] getModules() {
        return new Object[] {
                new AndroidModule(this),
                new FontAwesomeModule(this)
        };
    }

    public void inject(Object object) {
        mGraph.inject(object);
    }
}
