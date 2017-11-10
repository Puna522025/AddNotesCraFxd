package addnote.vnps.addnotes.editnotes.presenter;

/**
 * Created by 410181 on 2/14/2016.
 */
public interface EditNotesPresenter {


    void buttonClicked(int id);


    interface View {


        void changeTextColor();

        void changeBackground();

        void saveDetails();

        void keyboardVisibility();

        void promptText();

        void textToSpeech();

        void stopSpeechButtonImpl();

    }
}
