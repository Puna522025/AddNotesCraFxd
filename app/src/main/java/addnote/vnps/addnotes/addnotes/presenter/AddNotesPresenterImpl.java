package addnote.vnps.addnotes.addnotes.presenter;

import java.util.List;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.pojo.NoteDetails;

/**
 * Created by 410181 on 2/14/2016.
 */
public class AddNotesPresenterImpl implements AddNotesPresenter {

    private final AddNotesPresenter.View view;

    public AddNotesPresenterImpl(AddNotesPresenter.View view) {
        this.view = view;
    }

    @Override
    public void userSelection(int id) {
        switch (id) {
            case R.id.fab_action:
                view.viewOptions();
                break;
            case R.id.fab_add:
                view.addNotes();
                break;
            case R.id.fab_delete:
                view.deleteNotes();
                break;
            case R.id.fab_share:
                view.shareNotes();
                break;
            default:
                break;
        }
    }

    @Override
    public void renderView(int deleteOrNot) {
        view.renderView(deleteOrNot);
    }
}
