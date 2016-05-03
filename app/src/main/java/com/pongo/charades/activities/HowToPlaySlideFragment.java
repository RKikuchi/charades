package com.pongo.charades.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pongo.charades.R;

public class HowToPlaySlideFragment extends Fragment {
    private static final String ARG_SLIDE_NUMBER = "SLIDE_NUMBER";

    private int mSlideNumber;
    private ImageView mImage;
    private TextView mText;

    public HowToPlaySlideFragment() {}

    public static HowToPlaySlideFragment newInstance(int number) {
        HowToPlaySlideFragment fragment = new HowToPlaySlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SLIDE_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSlideNumber = getArguments().getInt(ARG_SLIDE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_how_to_play_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setViews(view);
        setSlideContent();
    }

    private void setViews(View view) {
        mImage = (ImageView) view.findViewById(R.id.image);
        mText = (TextView) view.findViewById(R.id.text);
    }

    private void setSlideContent() {
        switch (mSlideNumber) {
            case 0:
                setImage(R.drawable.placeholder_instruction_image);
                setText(R.string.how_to_play);
                break;
            case 1:
                setImage(R.drawable.how_to_play_1);
                setText(R.string.how_to_play_1);
                break;
            case 2:
                setImage(R.drawable.how_to_play_2);
                setText(R.string.how_to_play_2);
                break;
            case 3:
                setImage(R.drawable.placeholder_instruction_image);
                setText(R.string.how_to_play_3);
                break;
        }
    }

    private void setImage(int drawableId) {
        mImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
    }

    private void setText(int stringId) {
        mText.setText(stringId);
    }
}
