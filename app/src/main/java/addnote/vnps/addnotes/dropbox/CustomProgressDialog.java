package addnote.vnps.addnotes.dropbox;

/**
 * @author Puneet
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import addnote.vnps.addnotes.R;

public class CustomProgressDialog extends ProgressDialog {
    private AnimationDrawable animation;
    private static int progressTypeInfo;

    public static ProgressDialog myCustomProgressDialog(Context context, int progressType) {
        CustomProgressDialog dialog = new CustomProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        progressTypeInfo = progressType;
        return dialog;
    }

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.progress_bar);

        ImageView progressImage = (ImageView) findViewById(R.id.animation);
        progressImage.setBackgroundResource(R.drawable.progress_anim);
        animation = (AnimationDrawable) progressImage.getBackground();
        TextView event = (TextView) findViewById(R.id.progressInfo);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Basing Regular.ttf");
        event.setTypeface(tf, Typeface.BOLD);
        if (progressTypeInfo == 0) {//upload
            event.setText(R.string.uploading);
        } else {//download
            event.setText(R.string.downloading);
        }
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
    }
}