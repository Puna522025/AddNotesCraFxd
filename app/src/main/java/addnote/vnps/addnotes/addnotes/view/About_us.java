package addnote.vnps.addnotes.addnotes.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import addnote.vnps.addnotes.BuildConfig;
import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pkapo8 on 9/12/2016.
 */
public class About_us extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.version)
    TextView version;

    @Bind(R.id.fab_save)
    FloatingActionButton fab_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        ApplicationContext.setContext(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        version.setText(getString(R.string.version_number)+ " " + BuildConfig.VERSION_NAME);
        setTheme();
    }

    private void setTheme() {
        SharedPreferences sharedpreferences = getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = sharedpreferences.getString(AddNotesView.MYTHEMECOLOR, "");
        Window window = this.getWindow();
        if (!TextUtils.isEmpty(themeColor)) {
            CommonUtilities.changeThemeColors(themeColor, toolbar, fab_save, window, false, null, null, null, null, null, null);
        } else {
            window.setStatusBarColor((ContextCompat.getColor(getApplicationContext(), R.color.primary_dark)));
        }
    }
}
