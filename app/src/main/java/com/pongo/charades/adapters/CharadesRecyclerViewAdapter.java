package com.pongo.charades.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.activities.GameRoundActivity;
import com.pongo.charades.activities.MainActivity;
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
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_SHOW_ALL = 1;

    final private MainActivity mContext;
    final private Realm mRealm;
    final private LayoutInflater mLayoutInflater;
    private ArrayList<CategoryModelHolder> mItems;
    private int mMode = MODE_DEFAULT;

    public CharadesRecyclerViewAdapter(MainActivity context) {
        mContext = context;
        mRealm = Realm.getInstance(context);
        reload();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell = mLayoutInflater.inflate(R.layout.cell_charades, parent, false);
        final CharadesCellViewHolder holder = new CharadesCellViewHolder(mContext, cell);
        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameRoundActivity.class);
                intent.putExtra(GameRoundActivity.CATEGORY_ID, holder.getCategory().getId());
                mContext.startActivity(intent);
            }
        };
        holder.getImage().setOnClickListener(playListener);
        holder.getPlayButton().setOnClickListener(playListener);
        holder.getEditButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.manageCategory(holder);
            }
        });
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.hideCategory(holder);
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

    public void setMode(int mode) {
        if (mode == mMode) return;
        mMode = mode;
        reload();
        notifyDataSetChanged();
    }

    public void reload() {
        mItems = new ArrayList<>();
        RealmQuery<CategoryModel> query = mRealm.where(CategoryModel.class);
        switch (mMode) {
            case MODE_DEFAULT:
                query = query.equalTo("isHidden", false);
                break;
            case MODE_SHOW_ALL:
                break;
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
