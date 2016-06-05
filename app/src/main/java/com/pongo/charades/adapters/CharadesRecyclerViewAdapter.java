package com.pongo.charades.adapters;

import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.activities.CategoryCatalogFragment;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesRecyclerViewAdapter extends RecyclerView.Adapter {
    final private CategoryCatalogFragment mParent;
    final private Realm mRealm;
    final private LayoutInflater mLayoutInflater;
    private ArrayList<CategoryModelHolder> mItems;
    private int mFilter = CategoryCatalogFragment.FILTER_MAIN;
    private String mLanguage;

    public CharadesRecyclerViewAdapter(CategoryCatalogFragment parent) {
        mParent = parent;
        mRealm = Realm.getInstance(parent.getContext());
        reload();
        mLayoutInflater = LayoutInflater.from(parent.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell = mLayoutInflater.inflate(R.layout.cell_charades, parent, false);
        final CharadesCellViewHolder holder = new CharadesCellViewHolder(mParent, cell);
        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.playCategory(holder);
            }
        };
        holder.getImage().setOnClickListener(playListener);
        holder.getPlayButton().setOnClickListener(playListener);
        holder.getEditButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.manageCategory(holder);
            }
        });
        holder.getUnhideButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.unhideCategory(holder);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryModelHolder item = mItems.get(position);
        CharadesCellViewHolder charadesHolder = (CharadesCellViewHolder)holder;
        charadesHolder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setFilter(int filter) {
        mFilter = filter;
        reload();
        notifyDataSetChanged();
    }

    public void reload() {
        mLanguage = PreferenceManager
                .getDefaultSharedPreferences(mParent.getContext())
                .getString(mParent.getString(R.string.pref_key_language), null);
        mItems = new ArrayList<>();

        RealmQuery<CategoryModel> query = mRealm.where(CategoryModel.class);
        if (mLanguage != null) {
            query.beginGroup()
                    .isNull("language")
                    .or()
                    .equalTo("language", mLanguage);
            query.endGroup();
        }
        switch (mFilter) {
            case CategoryCatalogFragment.FILTER_MAIN:
                query = query.equalTo("isHidden", false);
                break;
            case CategoryCatalogFragment.FILTER_HIDDEN:
            case CategoryCatalogFragment.FILTER_FAMILY:
                query = query.equalTo("isHidden", true);
                break;
        }
        ArrayList<String> tags = mParent.getTags();
        if (tags != null && tags.size() > 0)
        {
            query.beginGroup();
            for (String tag : tags) {
                 query.equalTo("tags.value", tag);
            }
            query.endGroup();
        }

        RealmResults<CategoryModel> categories = query.findAll();
        for (CategoryModel model : categories) {
            mItems.add(new CategoryModelHolder(model));
        }
    }

    public void add(int position, CategoryModelHolder categoryHolder) {
        mItems.add(position, categoryHolder);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}
