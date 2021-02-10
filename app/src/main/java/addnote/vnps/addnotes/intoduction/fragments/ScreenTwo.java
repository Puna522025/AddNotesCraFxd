package addnote.vnps.addnotes.intoduction.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.presenter.SkipClicked;

/**
 * Created by pkapo8 on 9/15/2016.
 */
public class ScreenTwo extends Fragment implements View.OnClickListener {

    TextView tvNotesViewIntro,tvNotesViewIntro2;
    Button skipButton;
    CardView card2,card1;
    FrameLayout notes_frame1,notes_frame2;
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
        tvNotesViewIntro = (TextView) view.findViewById(R.id.tvNotesViewIntro);
        tvNotesViewIntro2 = (TextView) view.findViewById(R.id.tvNotesViewIntro2);

        card2 = (CardView) view.findViewById(R.id.card2);
        card1 = (CardView) view.findViewById(R.id.card1);

        notes_frame1 = (FrameLayout) view.findViewById(R.id.notes_frame1);
        notes_frame2 = (FrameLayout) view.findViewById(R.id.notes_frame2);

        skipButton = (Button)view.findViewById(R.id.skipButton);
        skipButton.setText("Skip");
        skipButton.setOnClickListener(this);
        tvNotesViewIntro.setText("Select your Superhero Theme.");
        tvNotesViewIntro2.setText("Add note widgets instantly.");
        notes_frame1.setBackgroundColor(Color.parseColor("#FF83EB"));
        notes_frame2.setBackgroundColor(Color.parseColor("#943FBF"));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.skipButton){
            ((SkipClicked) getActivity()).onSkipClicked();
        }
    }
}