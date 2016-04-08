package com.pongo.charades.adapters;

import android.content.Context;
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

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesRecyclerViewAdapter extends RecyclerView.Adapter {
    final private MainActivity mContext;
    final private Realm mRealm;
    final private LayoutInflater mLayoutInflater;
    private ArrayList<CategoryModelHolder> mItems;
    private CategoryModelHolder mSelectedItem;
    private CharadesCellViewHolder mSelectedItemHolder;

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
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedItem == null) {
                    Intent intent = new Intent(mContext, GameRoundActivity.class);
                    intent.putExtra(GameRoundActivity.CATEGORY_TITLE, holder.getCategory().getTitle());
                    mContext.startActivity(intent);
                } else if (mSelectedItem.getModel() == holder.getCategory()) {
                    holder.unselect();
                    mSelectedItem = null;
                } else {
                    mSelectedItem.isSelected(false);
                    if (mSelectedItemHolder != null)
                        mSelectedItemHolder.unselect();
                    holder.select();
                    mSelectedItem = holder.getModelHolder();
                    mSelectedItemHolder = holder;
                }
            }
        });
        cell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mSelectedItem != null && mSelectedItem.getModel() == holder.getCategory()) {
                    holder.unselect();
                    mSelectedItem = null;
                } else {
                    if (mSelectedItem != null) {
                        mSelectedItem.isSelected(false);
                        if (mSelectedItemHolder != null)
                            mSelectedItemHolder.unselect();
                    }
                    holder.select();
                    mSelectedItem = holder.getModelHolder();
                    mSelectedItemHolder = holder;
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == mSelectedItemHolder)
            mSelectedItemHolder = null;

        CategoryModelHolder item = mItems.get(position);
        CharadesCellViewHolder charadesHolder = (CharadesCellViewHolder)holder;
        charadesHolder.setData(item);
        if (item.isSelected())
            mSelectedItemHolder = charadesHolder;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void reload() {
        mItems = new ArrayList<>();
        for (CategoryModel model : mRealm.where(CategoryModel.class).findAll()) {
            mItems.add(new CategoryModelHolder(model));
        }
    }
}
