package br.com.kiks.charades.services;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.kiks.charades.R;
import br.com.kiks.charades.models.CategoryModel;
import br.com.kiks.charades.models.CategoryTagModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by rsaki on 7/3/2016.
 */
public class TagSuggestionProvider extends ContentProvider {

    private String mLanguage;
    List<String> mTags = new ArrayList<>();
    List<String> mTitles = new ArrayList<>();

    @Override
    public boolean onCreate() {
        loadTags();
        return mTags.size() > 0;
    }

    private void loadTags() {
        String lastLanguage = mLanguage;
        mLanguage = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.pref_key_language), null);

        if (!mTags.isEmpty() && !mTitles.isEmpty() && mLanguage == lastLanguage)
            return;

        Realm realm = Realm.getInstance(getContext());
        mTags.clear();
        for (CategoryTagModel tag : findAll(realm, CategoryTagModel.class)) {
            mTags.add(tag.getValue());
        }
        mTitles.clear();
        for (CategoryModel category : findAll(realm, CategoryModel.class)) {
            mTitles.add(category.getTitle());
        }
        realm.close();
    }

    private <E extends RealmObject> RealmResults<E> findAll(Realm realm, Class<E> clazz) {
        RealmQuery<E> query = realm.where(clazz);
        if (mLanguage != null) {
            query.beginGroup()
                    .isNull("language")
                    .or()
                    .equalTo("language", mLanguage);
            query.endGroup();
        }
        return query.findAll();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
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
        int count = mTags.size() + mTitles.size();
        mTags.clear();
        mTitles.clear();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
