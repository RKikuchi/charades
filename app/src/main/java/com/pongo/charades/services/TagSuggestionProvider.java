package com.pongo.charades.services;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryTagModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by rsaki on 7/3/2016.
 */
public class TagSuggestionProvider extends ContentProvider {

    List<String> mTags = new ArrayList<>();
    List<String> mTitles = new ArrayList<>();

    @Override
    public boolean onCreate() {
        loadTags();
        return mTags.size() > 0;
    }

    private void loadTags() {
        mTags.clear();
        Realm realm = Realm.getInstance(getContext());
        for (CategoryTagModel tag : realm.where(CategoryTagModel.class).findAll()) {
            mTags.add(tag.getValue());
        }
        for (CategoryModel category : realm.where(CategoryModel.class).findAll()) {
            mTitles.add(category.getTitle());
        }
        realm.close();
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
                        SearchManager.SUGGEST_COLUMN_ICON_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA
                }
        );

        String query = uri.getLastPathSegment().toUpperCase();
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

        int i = 0;
        int numTags = mTags.size();
        while (i < numTags && cursor.getCount() < limit) {
            String tag = mTags.get(i);
            if (tag.toUpperCase().contains(query)){
                cursor.addRow(new Object[]{ i, R.drawable.ic_label_white_24dp, tag, "tag:" + tag });
            }
            i++;
        }
        int numTitles = mTitles.size();
        while (i < numTags + numTitles && cursor.getCount() < limit) {
            String title = mTitles.get(i - numTags);
            if (title.toUpperCase().contains(query)){
                cursor.addRow(new Object[]{ i, R.drawable.ic_play_arrow_white_24dp, title, "title:" + title });
            }
            i++;
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
