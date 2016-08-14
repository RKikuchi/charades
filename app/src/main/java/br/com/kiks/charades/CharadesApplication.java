package br.com.kiks.charades;

import android.app.Application;

import br.com.kiks.charades.modules.AndroidModule;
import br.com.kiks.charades.modules.FontAwesomeModule;

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
