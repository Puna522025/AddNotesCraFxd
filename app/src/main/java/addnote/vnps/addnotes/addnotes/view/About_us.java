package addnote.vnps.addnotes.addnotes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import addnote.vnps.addnotes.BuildConfig;
import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pkapo8 on 9/12/2016.
 */
public class About_us extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.version)
    TextView version;

    @BindView(R.id.fab_save)
    FloatingActionButton fab_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        ApplicationContext.setContext(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        version.setText(getString(R.string.version_number) + " " + BuildConfig.VERSION_NAME);
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
