package com.pongo.charades.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pongo.charades.CharadesApplication;

/**
 * Created by rsaki on 2/8/2016.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CharadesApplication)getApplication()).inject(this);
    }
}
