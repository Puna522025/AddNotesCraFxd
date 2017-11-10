package addnote.vnps.addnotes.dropbox;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.analytics.AnalyticsApplication;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.dropbox.newdropbox.DropboxClient;
import addnote.vnps.addnotes.dropbox.newdropbox.UserAccountTask;
import butterknife.Bind;
import butterknife.ButterKnife;

public class DropBoxActivity extends AppCompatActivity implements OnClickListener {

    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "b6btg28rya3e792";
    private final static String ACCESS_SECRET = "5ne490yc9xuob5e";
    private static final String TAG = "DropBoxView";
    private static String ACCESS_TOKEN = "";
    // private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;
    DatabaseHandler database;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.loginBtn)
    Button loginBtn;
    @Bind(R.id.uploadFileBtn)
    Button uploadFileBtn;
    @Bind(R.id.listFilesBtn)
    Button listFilesBtn;
    @Bind(R.id.fab_save)
    FloatingActionButton fab_save;
    @Bind(R.id.dropbox_relative)
    RelativeLayout dropbox_relative;
    @Bind(R.id.tvWelcome)
    TextView tvWelcome;
    @Bind(R.id.ivUser)
    ImageView ivUser;

    //private DbxClientV2 dropboxApi;
    private boolean isUserLoggedIn;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropbox_authentication);
        ButterKnife.bind(this);

        loginBtn.setOnClickListener(this);
        uploadFileBtn.setOnClickListener(this);
        listFilesBtn.setOnClickListener(this);
        setSupportActionBar(toolbar);
        setTheme();

        database = new DatabaseHandler(this);
        loggedIn(false);
        // AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
        //AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

    /*    if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }*/

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        displayADD();
    }

    private void displayADD() {
        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        if (adView != null) {
            adView.loadAd(adRequest);
        }
    }

    private void setTheme() {
        SharedPreferences sharedpreferences = getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = sharedpreferences.getString(AddNotesView.MYTHEMECOLOR, "");
        Window window = this.getWindow();
        ApplicationContext.setContext(getApplicationContext());
        if (!TextUtils.isEmpty(themeColor)) {
            CommonUtilities.changeThemeColors(themeColor, toolbar, fab_save, window, false, null, null, null, null, null, null);
        } else {
            window.setStatusBarColor((ContextCompat.getColor(getApplicationContext(), R.color.primary_dark)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAccessToken();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                try {
                    if (isUserLoggedIn) {
                        //  dropboxApi.getSession().unlink();
                        loggedIn(false);
                    } else {
                        if (CommonUtilities.isNetworkAvailable()) {
                            // dropboxApi.getSession().startAuthentication(DropBoxActivity.this);
                            Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(dropbox_relative, "Please connect to the internet...", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error during Dropbox authentication",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.uploadFileBtn:
                if (CommonUtilities.isNetworkAvailable()) {
                    showDialogConfirmation();

                } else {
                    Snackbar snackbar = Snackbar
                            .make(dropbox_relative, "Please connect to the internet to save your notes to Dropbox.", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
                break;
            case R.id.listFilesBtn:
                if (CommonUtilities.isNetworkAvailable()) {
                    GetDBFile dbFile = new GetDBFile(DropboxClient.getClient(ACCESS_TOKEN), "AddNotes",
                            DropBoxActivity.this, database, dropbox_relative);
                    dbFile.execute();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(dropbox_relative, "Please connect to the internet to download your notes from Dropbox.", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
                break;
            default:
                break;
        }
    }

    private void showDialogConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_confirmation_dialog);
        TextView tvNotesViewText = (TextView) dialog.findViewById(R.id.tvNotesViewText);
        tvNotesViewText.setText(R.string.previousnotes);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setText(R.string.no);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setText(R.string.yes);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtilities.isNetworkAvailable()) {
                    if (!TextUtils.isEmpty(ACCESS_TOKEN)) {
                        UploadDBFile uploadDBFile = new UploadDBFile(DropBoxActivity.this, DropboxClient.getClient(ACCESS_TOKEN),
                                "AddNotes", dropbox_relative);
                        uploadDBFile.execute();
                        dialog.dismiss();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(dropbox_relative, "Please log in again to save your notes to Dropbox.", Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(dropbox_relative, "Please connect to the internet to save your notes to Dropbox.", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });
        dialog.show();
    }

    private void loggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
        uploadFileBtn.setEnabled(userLoggedIn);
        int loggedInColor = ContextCompat.getColor(getApplicationContext(), R.color.dropBox_color);
        int loggedOutColor = ContextCompat.getColor(getApplicationContext(), R.color.secondary_text);

        uploadFileBtn.setBackgroundColor(userLoggedIn ? loggedInColor : loggedOutColor);
        listFilesBtn.setEnabled(userLoggedIn);
        listFilesBtn.setBackgroundColor(userLoggedIn ? loggedInColor : loggedOutColor);
        loginBtn.setText(userLoggedIn ? "Sign Out" : "Sign In");

        if (!TextUtils.isEmpty(ACCESS_TOKEN)) {
            new UserAccountTask(DropboxClient.getClient(ACCESS_TOKEN), new UserAccountTask.TaskDelegate() {
                @Override
                public void onAccountReceived(FullAccount account) {
                    updateUI(account);
                }

                @Override
                public void onError(Exception error) {
                    Log.d("User data", "Error receiving account details.");
                }
            }).execute();
        }
    }

    private void updateUI(FullAccount account) {
        if (account != null && account.getName() != null && !TextUtils.isEmpty(account.getName().getDisplayName())) {
            tvWelcome.setText("Welcome: " + account.getName().getDisplayName());
        }
        if (account != null  && account.getProfilePhotoUrl() != null) {
            Picasso.with(this)
                    .load(account.getProfilePhotoUrl())
                    .into(ivUser);
        }
    }

    public void getAccessToken() {
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            ACCESS_TOKEN = accessToken;
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("dropboxintegration", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", accessToken).apply();
            loggedIn(true);
        }
    }

}