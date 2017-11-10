package addnote.vnps.addnotes.editnotes.presenter;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.presenter.AddNotesPresenter;

/**
 * Created by 410181 on 2/14/2016.
 */
public class EditNotesPresenterImpl implements EditNotesPresenter {




    private final EditNotesPresenter.View view;

    public EditNotesPresenterImpl(EditNotesPresenter.View view) {
        this.view = view;
    }


    @Override
    public void buttonClicked(int id) {
        switch (id) {
            case R.id.fab_save:
                view.saveDetails();
                break;
            case R.id.btnSpeak:
                view.promptText();
                break;
            case R.id.btnTTSstart:
                view.textToSpeech();
                break;
            case R.id.btnTTSPause:
                view.stopSpeechButtonImpl();
                break;

            case R.id.rlNote:
                view.keyboardVisibility();
            default:
                break;
        }
    }
}
