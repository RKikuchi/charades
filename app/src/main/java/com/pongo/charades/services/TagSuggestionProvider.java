package com.pongo.charades.services;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.pongo.charades.models.CategoryTagModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by rsaki on 7/3/2016.
 */
public class TagSuggestionProvider extends ContentProvider {

    Realm mRealm;
    List<String> mTags = new ArrayList<>();

    @Override
    public boolean onCreate() {
        mRealm = Realm.getInstance(getContext());
        loadTags();
        return mTags.size() > 0;
    }

    private void loadTags() {
        mTags.clear();
        for (CategoryTagModel tag : mRealm.where(CategoryTagModel.class).findAll()) {
            mTags.add(tag.getValue());
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (mTags.isEmpty())
            loadTags();

        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA
                }
        );

        String query = uri.getLastPathSegment().toUpperCase();
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

        int length = mTags.size();
        for (int i = 0; i < length && cursor.getCount() < limit; i++) {
            String tag = mTags.get(i);
            if (tag.toUpperCase().contains(query)){
                cursor.addRow(new Object[]{ i, tag, tag });
            }
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
