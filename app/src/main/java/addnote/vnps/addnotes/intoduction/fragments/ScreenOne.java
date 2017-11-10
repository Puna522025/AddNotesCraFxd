package addnote.vnps.addnotes.intoduction.fragments;

/**
 * Created by pkapo8 on 9/15/2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import addnote.vnps.addnotes.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.presenter.SkipClicked;
import addnote.vnps.addnotes.common.CommonUtilities;

/**
 * Created by pkapo8 on 9/15/2016.
 */
public class ScreenOne extends Fragment implements View.OnClickListener {

    TextView tvNotesViewIntro, tvNotesViewIntro2;
    Button skipButton;
    android.support.v7.widget.CardView card2, card1;
    FrameLayout notes_frame1, notes_frame2;
    SkipClicked skipClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.intro_screen_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skipButton = (Button) view.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);
        tvNotesViewIntro = (TextView) view.findViewById(R.id.tvNotesViewIntro);
        tvNotesViewIntro2 = (TextView) view.findViewById(R.id.tvNotesViewIntro2);

        card2 = (android.support.v7.widget.CardView) view.findViewById(R.id.card2);
        card1 = (android.support.v7.widget.CardView) view.findViewById(R.id.card1);

        notes_frame1 = (FrameLayout) view.findViewById(R.id.notes_frame1);
        notes_frame2 = (FrameLayout) view.findViewById(R.id.notes_frame2);

        skipButton = (Button) view.findViewById(R.id.skipButton);
        skipButton.setText("Skip");
        skipButton.setOnClickListener(this);
        tvNotesViewIntro.setText("Add your notes instantly.");
        tvNotesViewIntro2.setText("Customize them.");
        notes_frame1.setBackgroundColor(Color.parseColor("#b7da00"));
        notes_frame2.setBackgroundColor(Color.parseColor("#f93f3e"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.skipButton) {
            ((SkipClicked) getActivity()).onSkipClicked();
        }
    }
}