package br.com.kiks.charades.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiks.charades.R;
import br.com.kiks.charades.models.ScoreTrackItem;
import br.com.kiks.charades.viewholders.ScoreItemViewHolder;

import java.util.ArrayList;

/**
 * Created by rsaki on 2/12/2016.
 */
public class ScoreTrackRecyclerViewAdapter extends RecyclerView.Adapter {
    final private Context mContext;
    final private ArrayList<ScoreTrackItem> mItems = new ArrayList<>();
    final private LayoutInflater mLayoutInflater;

    public ScoreTrackRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.score_item_view, parent, false);
        final ScoreItemViewHolder holder = new ScoreItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ScoreItemViewHolder scoreItemHolder = (ScoreItemViewHolder)holder;
        scoreItemHolder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(String value, Boolean score) {
        mItems.add(new ScoreTrackItem(value, score));
    }

    public void clear() {
        mItems.clear();
    }
}
