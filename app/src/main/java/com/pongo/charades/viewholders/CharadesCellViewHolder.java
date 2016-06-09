package com.pongo.charades.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.activities.CategoryCatalogFragment;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;
import com.pongo.charades.transforms.PaletteTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesCellViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImage;
    private final TextView mTitleLabel;
    private final View mActionGroup;
    private final ImageButton mPlayButton;
    private final ImageButton mEditButton;
    private final ImageButton mFavoriteButton;
    private final ImageButton mUnhideButton;
    private final Context mContext;
    private CategoryModelHolder mCategory;

    public CharadesCellViewHolder(CategoryCatalogFragment parent, View itemView) {
        super(itemView);
        mContext = parent.getContext();
        mImage = (ImageView) itemView.findViewById(R.id.card_image);
        mTitleLabel = (TextView) itemView.findViewById(R.id.cell_charades_title_label);
        mActionGroup = itemView.findViewById(R.id.cell_action_group);
        mPlayButton = (ImageButton) itemView.findViewById(R.id.play_button);
        mEditButton = (ImageButton) itemView.findViewById(R.id.edit_button);
        mFavoriteButton = (ImageButton) itemView.findViewById(R.id.favorite_button);
        mUnhideButton = (ImageButton) itemView.findViewById(R.id.unhide_button);
    }

    private void loadImage() {
        Picasso
                .with(mContext)
                .load(mCategory.getModel().getImagePath())
                .placeholder(R.drawable.category_cell_placeholder)
                .transform(PaletteTransform.instance())
                //.transform(new BlurTransform(mContext, 10))
                //.transform(new ContrastTransform(mContext, 0.33f, 1))
                .into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                        Palette palette = PaletteTransform.getPalette(bitmap);
                        Palette.Swatch swatch = palette.getLightMutedSwatch();
                        if (swatch == null)
                            swatch = palette.getVibrantSwatch();
                        if (swatch == null)
                            swatch = palette.getDarkVibrantSwatch();
                        if (swatch == null && palette.getSwatches().size() > 0)
                            swatch = palette.getSwatches().get(0);

                        if (swatch != null) {
                            mActionGroup.setBackgroundColor(swatch.getRgb());
                            mPlayButton.setColorFilter(swatch.getTitleTextColor());
                            mEditButton.setColorFilter(swatch.getBodyTextColor());
                            mFavoriteButton.setColorFilter(swatch.getBodyTextColor());
                            mUnhideButton.setColorFilter(swatch.getBodyTextColor());
                        }
                    }

                    @Override
                    public void onError() {}
                });
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
        mActionGroup.setBackgroundColor(
                ContextCompat.getColor(mContext, R.color.colorWhite));
        mPlayButton.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorAccent));
        mEditButton.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorDarkGray));
        mFavoriteButton.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorDarkGray));
        mUnhideButton.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorDarkGray));
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

    public View getFavoriteButton() {
        return mFavoriteButton;
    }

    public View getUnhideButton() {
        return mUnhideButton;
    }
}
