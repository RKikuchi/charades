package com.pongo.charades.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pongo.charades.R;
import com.pongo.charades.models.ScoreTrackItem;

/**
 * Created by rsaki on 2/12/2016.
 */
public class ScoreItemViewHolder extends RecyclerView.ViewHolder {
    private TextView mLabel;

    public ScoreItemViewHolder(View itemView) {
        super(itemView);
        mLabel = (TextView) itemView.findViewById(R.id.score_item_text);
    }

    public void setData(ScoreTrackItem item) {
        int color = item.right ? R.color.colorRight : R.color.colorWrong;
        mLabel.setText(item.item);
        mLabel.setTextColor(ContextCompat.getColor(mLabel.getContext(), color));
    }
}
