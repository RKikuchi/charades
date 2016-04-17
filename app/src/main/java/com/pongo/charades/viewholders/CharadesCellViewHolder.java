package com.pongo.charades.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.activities.MainActivity;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesCellViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImage;
    private final TextView mTitleLabel;
    private final View mPlayButton;
    private final View mEditButton;
    private final View mUnhideButton;
    private final MainActivity mContext;
    private CategoryModelHolder mCategory;

    public CharadesCellViewHolder(MainActivity context, View itemView) {
        super(itemView);
        mContext = context;
        mImage = (ImageView) itemView.findViewById(R.id.card_image);
        mTitleLabel = (TextView) itemView.findViewById(R.id.cell_charades_title_label);
        mPlayButton = itemView.findViewById(R.id.play_button);
        mEditButton = itemView.findViewById(R.id.edit_button);
        mUnhideButton = itemView.findViewById(R.id.unhide_button);
    }

    private void loadImage() {
        Picasso
                .with(mContext)
                .load("http://lorempixel.com/400/200/?rnd=" + mCategory.getModel().getId())
                .placeholder(R.drawable.category_cell_placeholder)
                //.transform(new BlurTransform(mContext, 10))
                //.transform(new ContrastTransform(mContext, 0.33f, 1))
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mImage);
    }

    public CategoryModelHolder getModelHolder() {
        return mCategory;
    }

    public CategoryModel getCategory() { return mCategory.getModel(); }

    public void setData(CategoryModelHolder value) {
        CategoryModel model = value.getModel();
        mCategory = value;
        mTitleLabel.setText(model.getTitle());
        if (model.getIsHidden()) {
            mImage.setAlpha(0.5f);
            mUnhideButton.setVisibility(View.VISIBLE);
        } else {
            mImage.setAlpha(1.0f);
            mUnhideButton.setVisibility(View.INVISIBLE);
        }
        loadImage();
    }

    public View getTitleLabel() {
        return mTitleLabel;
    }

    public View getImage() {
        return mImage;
    }

    public View getPlayButton() {
        return mPlayButton;
    }

    public View getEditButton() {
        return mEditButton;
    }

    public View getUnhideButton() {
        return mUnhideButton;
    }
}
