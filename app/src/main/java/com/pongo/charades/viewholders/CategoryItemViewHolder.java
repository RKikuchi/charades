package com.pongo.charades.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryItemModel;

/**
 * Created by rsaki on 4/3/2016.
 */
public class CategoryItemViewHolder extends RecyclerView.ViewHolder  {
    private TextView mTitleLabel;
    private CategoryItemModel mItem;

    public CategoryItemViewHolder(View itemView) {
        super(itemView);
        mTitleLabel = (TextView) itemView.findViewById(R.id.cell_category_item_value);
    }

    public CategoryItemModel getCategory() { return mItem; }

    public void setData(CategoryItemModel value) {
        mItem = value;
        mTitleLabel.setText(value.getValue());
    }
}
