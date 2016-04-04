package com.pongo.charades.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.activities.GameRoundActivity;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.viewholders.CategoryItemViewHolder;

import io.realm.RealmList;

/**
 * Created by rsaki on 4/3/2016.
 */
public class CategoryItemsRecyclerViewAdapter extends RecyclerView.Adapter {
    final private Context mContext;
    final private LayoutInflater mLayoutInflater;
    private final RealmList<CategoryItemModel> mItems;


    public CategoryItemsRecyclerViewAdapter(Context context, CategoryModel category) {
        mContext = context;
        mItems = category.getItems();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell = mLayoutInflater.inflate(R.layout.cell_category_item, parent, false);
        final CategoryItemViewHolder holder = new CategoryItemViewHolder(cell);
        View removeButton = cell.findViewById(R.id.cell_category_item_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryItemViewHolder charadesHolder = (CategoryItemViewHolder)holder;
        charadesHolder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
