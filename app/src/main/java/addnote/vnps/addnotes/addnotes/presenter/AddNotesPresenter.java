package addnote.vnps.addnotes.addnotes.presenter;

import java.util.List;

import addnote.vnps.addnotes.pojo.NoteDetails;

/**
 * Created by 410181 on 2/14/2016.
 */
public interface AddNotesPresenter {

    /**
     * button selected by the user.
     *
     * @param id
     */
    void userSelection(int id);

    void renderView(int deleteOrNot);

    interface View {

        /**
         * view the options to user.
         */
        void viewOptions();

        /**
         * view the options to add notes to the user.
         */
        void addNotes();

        /**
         * view the delete the note to user.
         */
        void deleteNotes();

        /**
         * share the note to user.
         */
        void shareNotes();
        /**
         * renders the view.
         */
        void renderView(int deleteOrNot);
    }
}
