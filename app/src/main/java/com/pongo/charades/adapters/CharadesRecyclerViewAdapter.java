package com.pongo.charades.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.activities.GameRoundActivity;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by rsaki on 1/3/2016.
 */
public class CharadesRecyclerViewAdapter extends RecyclerView.Adapter {
    final private Context mContext;
    final private Realm mRealm;
    final private LayoutInflater mLayoutInflater;
    private RealmResults<CategoryModel> mItems;

    public CharadesRecyclerViewAdapter(Context context) {
        mContext = context;
        mRealm = Realm.getInstance(context);
        mItems = mRealm.where(CategoryModel.class).findAll();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell = mLayoutInflater.inflate(R.layout.cell_charades, parent, false);
        final CharadesCellViewHolder holder = new CharadesCellViewHolder(cell);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameRoundActivity.class);
                intent.putExtra(GameRoundActivity.CATEGORY_TITLE, holder.getCategory().getTitle());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CharadesCellViewHolder charadesHolder = (CharadesCellViewHolder)holder;
        charadesHolder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void reload() {
        mItems = mRealm.where(CategoryModel.class).findAll();
    }
}
