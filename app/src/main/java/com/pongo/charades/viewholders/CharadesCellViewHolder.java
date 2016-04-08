package com.pongo.charades.viewholders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.activities.MainActivity;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesCellViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTitleLabel;
    private final View mOverlay;
    private final MainActivity mContext;
    private CategoryModelHolder mCategory;

    public CharadesCellViewHolder(MainActivity context, View itemView) {
        super(itemView);
        mContext = context;
        mTitleLabel = (TextView) itemView.findViewById(R.id.cell_charades_title_label);
        mOverlay = itemView.findViewById(R.id.cell_charades_overlay);
        mOverlay.setVisibility(View.INVISIBLE);

        Typeface faTypeface = mContext.getFontAwesome().getTypeface();
        ((TextView)itemView.findViewById(R.id.cell_charades_icon_back)).setTypeface(faTypeface);
        ((TextView)itemView.findViewById(R.id.cell_charades_icon_edit)).setTypeface(faTypeface);
        ((TextView)itemView.findViewById(R.id.cell_charades_icon_delete)).setTypeface(faTypeface);
    }

    public CategoryModelHolder getModelHolder() {
        return mCategory;
    }

    public CategoryModel getCategory() { return mCategory.getModel(); }

    public void setData(CategoryModelHolder value) {
        mCategory = value;
        mTitleLabel.setText(value.getModel().getTitle());
        mOverlay.setVisibility(value.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    public void select() {
        mCategory.isSelected(true);
        mOverlay.setVisibility(View.VISIBLE);
    }

    public void unselect() {
        mCategory.isSelected(false);
        mOverlay.setVisibility(View.INVISIBLE);
    }
}
