package com.pongo.charades.models;

/**
 * Created by rsaki on 2/12/2016.
 */
public class ScoreTrackItem {
    public String item;
    public Boolean right;

    public ScoreTrackItem(String item, Boolean right) {
        this.item = item;
        this.right = right;
    }
}
