package com.pongo.charades.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryModel;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesCellViewHolder extends RecyclerView.ViewHolder {
    private TextView mTitleLabel;
    private CategoryModel mCategory;

    public CharadesCellViewHolder(View itemView) {
        super(itemView);
        mTitleLabel = (TextView) itemView.findViewById(R.id.cell_charades_title_label);
    }

    public CategoryModel getCategory() { return mCategory; }

    public void setData(CategoryModel value) {
        mCategory = value;
        mTitleLabel.setText(value.getTitle());
    }
}
