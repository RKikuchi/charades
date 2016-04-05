package com.pongo.charades.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.modules.FontAwesomeProvider;

/**
 * Created by rsaki on 4/3/2016.
 */
public class CategoryItemViewHolder extends RecyclerView.ViewHolder  {
    private FontAwesomeProvider mFontAwesome;
    private EditText mTitleLabel;
    private CategoryItemModel mItem;

    public CategoryItemViewHolder(FontAwesomeProvider fontAwesome, View itemView) {
        super(itemView);
        mFontAwesome = fontAwesome;
        mTitleLabel = (EditText) itemView.findViewById(R.id.cell_category_item_value);
        TextView xButton = (TextView) itemView.findViewById(R.id.cell_category_item_remove_button);
        xButton.setTypeface(mFontAwesome.getTypeface());
    }

    public CategoryItemModel getItem() { return mItem; }

    public void setData(CategoryItemModel value) {
        mItem = value;
        mTitleLabel.setText(value.getValue());
    }

    public void focus() {
        mTitleLabel.requestFocus();
    }
}
