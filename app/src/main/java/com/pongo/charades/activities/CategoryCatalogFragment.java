package com.pongo.charades.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pongo.charades.R;
import com.pongo.charades.adapters.CharadesRecyclerViewAdapter;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.models.CategoryModelHolder;
import com.pongo.charades.viewholders.CharadesCellViewHolder;

import io.realm.Realm;

public class CategoryCatalogFragment extends Fragment {
    private static final String ARG_FILTER_TYPE = "ARG_FILTER_TYPE";
    public static final int FILTER_MAIN = 1;
    public static final int FILTER_FAVORITES = 2;
    public static final int FILTER_FAMILY = 3;
    public static final int FILTER_HIDDEN = 4;

    private int mFilterType;

    private Realm mRealm;
    private CategoryCatalogListener mListener;

    // Views
    private RecyclerView mRecyclerView;
    private CharadesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public CategoryCatalogFragment() {}

    public static CategoryCatalogFragment newInstance(int filterType) {
        CategoryCatalogFragment fragment = new CategoryCatalogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FILTER_TYPE, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getContext());
        if (getArguments() != null) {
            mFilterType = getArguments().getInt(ARG_FILTER_TYPE);
            if (mAdapter != null)
                mAdapter.setFilter(mFilterType);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.charades_recycler_view);

        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new CharadesRecyclerViewAdapter(this);
        mAdapter.setFilter(mFilterType);
        mRecyclerView.setAdapter(mAdapter);

        //setupTouchHelper();
    }

    private void setupTouchHelper() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        CharadesCellViewHolder charadesHolder = (CharadesCellViewHolder) viewHolder;
                        hideCategory(charadesHolder);
                    }
                });
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_catalog, container, false);
    }

    public void onCategorySelected(String categoryName) {
        if (mListener != null) {
            mListener.onCategorySelected(categoryName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoryCatalogListener) {
            mListener = (CategoryCatalogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CategoryCatalogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void hideCategory(final CharadesCellViewHolder holder) {
        final CategoryModel category = holder.getCategory();
        final int position = holder.getAdapterPosition();

        mRealm.beginTransaction();
        category.setIsHidden(true);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.remove(position);
        if (mListener != null) {
            mListener.onCategoryHidden(this, position, holder);
        }
    }

    public void unhideCategory(int position, CategoryModelHolder categoryHolder) {
        CategoryModel category = categoryHolder.getModel();
        mRealm.beginTransaction();
        category.setIsHidden(false);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.add(position, categoryHolder);
        if (position == mAdapter.getItemCount() - 1) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public void unhideCategory(final CharadesCellViewHolder holder) {
        final CategoryModel category = holder.getCategory();
        final int position = holder.getAdapterPosition();

        mRealm.beginTransaction();
        category.setIsHidden(false);
        mRealm.copyToRealmOrUpdate(category);
        mRealm.commitTransaction();

        mAdapter.notifyItemChanged(position);
    }

    public void deleteCategory(CategoryModel category) {
        mRealm.beginTransaction();
        mRealm.where(CategoryModel.class)
                .equalTo("id", category.getId())
                .findAll()
                .clear();
        mRealm.commitTransaction();
    }

    public void playCategory(CharadesCellViewHolder holder) {
        Intent intent = new Intent(getContext(), GameRoundActivity.class);
        intent.putExtra(GameRoundActivity.CATEGORY_ID, holder.getCategory().getId());
        startActivity(intent);
    }

    public void manageCategory(CharadesCellViewHolder holder) {
        int position = holder.getAdapterPosition();
        CategoryModel category = holder.getCategory();

        Intent intent = new Intent(getContext(), ManageCategoryActivity.class);
        intent.putExtra(MainActivity.EXTRA_CATEGORY_POSITION, position);
        intent.putExtra(ManageCategoryActivity.EXTRA_ORIGINAL_FILTER, mFilterType);
        intent.putExtra(ManageCategoryActivity.CATEGORY_ID, category.getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String titleTransitionName = getString(R.string.transition_category_name);
            String imageTransitionName = getString(R.string.transition_category_image);
            Pair<View, String> p1 = Pair.create(holder.getTitleLabel(), titleTransitionName);
            Pair<View, String> p2 = Pair.create(holder.getImage(), imageTransitionName);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(), p1, p2);

            ActivityCompat.startActivityForResult(getActivity(), intent,
                    MainActivity.REQUEST_CODE_MANAGE_CATEGORY, options.toBundle());
        } else {
            startActivityForResult(intent, MainActivity.REQUEST_CODE_MANAGE_CATEGORY);
        }
    }

    public void reload() {
        if (mAdapter == null) return;
        mAdapter.reload();
    }

    public void newItemAdded(int filter) {
        if (mAdapter == null) return;

        if (mFilterType != filter) {
            mAdapter.reload();
            return;
        }

        final int lastPos = mAdapter.getItemCount();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.reload();
                mAdapter.notifyItemInserted(lastPos);
                mRecyclerView.smoothScrollToPosition(lastPos);
            }
        }, 500);
    }

    public void itemChanged(int filter, int pos) {
        if (mAdapter == null) return;

        if (mFilterType != filter) {
            mAdapter.reload();
            return;
        }

        if (pos != -1 && pos < mAdapter.getItemCount()) {
            mAdapter.notifyItemChanged(pos);
        } else {
            mAdapter.reload();
            mAdapter.notifyDataSetChanged();
        }
    }

    public interface CategoryCatalogListener {
        void onCategorySelected(String categoryName);
        void onCategoryHidden(final CategoryCatalogFragment fragment,
                              final int position,
                              final CharadesCellViewHolder holder);
    }
}
