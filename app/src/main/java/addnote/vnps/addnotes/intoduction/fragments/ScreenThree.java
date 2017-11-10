package addnote.vnps.addnotes.intoduction.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.presenter.AddNotesPresenter;
import addnote.vnps.addnotes.addnotes.presenter.SkipClicked;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.common.CommonUtilities;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pkapo8 on 9/15/2016.
 */
public class ScreenThree extends Fragment implements View.OnClickListener {

    TextView tvNotesViewIntro,tvNotesViewIntro2;
    android.support.v7.widget.CardView card2,card1;
    FrameLayout notes_frame1,notes_frame2;
    Button skipButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.intro_screen_one, container, false);
        tvNotesViewIntro = (TextView) view.findViewById(R.id.tvNotesViewIntro);
        tvNotesViewIntro2 = (TextView) view.findViewById(R.id.tvNotesViewIntro2);

        card2 = (android.support.v7.widget.CardView) view.findViewById(R.id.card2);
        card1 = (android.support.v7.widget.CardView) view.findViewById(R.id.card1);

        notes_frame1 = (FrameLayout) view.findViewById(R.id.notes_frame1);
        notes_frame2 = (FrameLayout) view.findViewById(R.id.notes_frame2);

        tvNotesViewIntro.setText("Save your notes on cloud.");
        tvNotesViewIntro2.setText("Download and share your notes.");
        notes_frame1.setBackgroundColor(Color.parseColor("#f5ea01"));
        notes_frame2.setBackgroundColor(Color.parseColor("#FF5722"));
        skipButton = (Button)view.findViewById(R.id.skipButton);
        skipButton.setText("Done");
        skipButton.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.skipButton){
            ((SkipClicked) getActivity()).onSkipClicked();
        }
    }
}