package com.pongo.charades.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.modules.FontAwesomeProvider;
import com.pongo.charades.viewholders.CategoryItemViewHolder;

import java.util.List;

/**
 * Created by rsaki on 4/3/2016.
 */
public class CategoryItemsRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int NO_FOCUS = -1;

    final private FontAwesomeProvider mFontAwesome;
    final private LayoutInflater mLayoutInflater;
    final private List<String> mItems;
    final private RecyclerView mRecyclerView;
    private int mFocusPosition;

    public CategoryItemsRecyclerViewAdapter(Context context,
                                            FontAwesomeProvider fontAwesome,
                                            RecyclerView recyclerView,
                                            CategoryDto category) {
        mFontAwesome = fontAwesome;
        mRecyclerView = recyclerView;
        mItems = category.items;
        mLayoutInflater = LayoutInflater.from(context);
        mFocusPosition = NO_FOCUS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell = mLayoutInflater.inflate(R.layout.cell_category_item, parent, false);
        final CategoryItemViewHolder holder = new CategoryItemViewHolder(mFontAwesome, cell);
        View removeButton = cell.findViewById(R.id.cell_category_item_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                mItems.remove(pos);
                notifyItemRemoved(pos);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryItemViewHolder itemHolder = (CategoryItemViewHolder)holder;
        itemHolder.setData(mItems.get(position));
        if (position == mFocusPosition) {
            itemHolder.focus();
            mFocusPosition = NO_FOCUS;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void focusItem(int position) {
        mFocusPosition = position;
        CategoryItemViewHolder holder =
                (CategoryItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.focus();
            mFocusPosition = NO_FOCUS;
        }
    }
}
