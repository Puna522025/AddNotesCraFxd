package addnote.vnps.addnotes.editnotes.view;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import android.text.TextUtils;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.adapter.RecyclerAdapter;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.analytics.AnalyticsApplication;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.editnotes.presenter.EditNotesPresenter;
import addnote.vnps.addnotes.editnotes.presenter.EditNotesPresenterImpl;
import addnote.vnps.addnotes.pojo.NoteDetails;
import addnote.vnps.addnotes.widget.WidgetUI;
import butterknife.BindView;
import butterknife.ButterKnife;
import yuku.ambilwarna.AmbilWarnaDialog;

import static android.util.Log.d;

/**
 * @author Puneet
 */
public class EditNotesView extends AppCompatActivity implements View.OnClickListener, EditNotesPresenter.View {
    private static final String TAG = "EditNotesView";
    String notesType;
    DatabaseHandler database;
    String colorDefault = "#EDEDED";
    String colorTextDefault = "#3C5899";
    int color = 0xffffff00;
    @BindView(R.id.etNoteText)
    EditText etNoteText;
    @BindView(R.id.add_note_coordinator_layout)
    CoordinatorLayout add_note_coordinator_layout;
    @BindView(R.id.rlNote)
    RelativeLayout rlNote;
    @BindView(R.id.fab_save)
    FloatingActionButton fab_save;
    @BindView(R.id.alertText)
    TextView alertText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnSpeak)
    ImageButton btnSpeak;
    @BindView(R.id.btnTTSstart)
    ImageButton btnTTSStart;
    @BindView(R.id.btnTTSPause)
    ImageButton btnTTSPause;

    String titleLocked = "";
    String lockedPassword = "";
    int lockedOrnot = 0;

    TextToSpeech tts;
    RecyclerAdapter recyclerAdapter;
    RecyclerView listView;
    private Boolean isFabOpen = false;
    int mYear, mMonth, mDay, mHour, mMinute;
    private EditNotesPresenter editNotesPresenter;
    private Bundle bundle;
    private int position;
    private boolean isDateChanged = false;
    private Animation slideInLeft, fabClose, slideInRight, slideOutRight, slideOutLeft;
    Tracker mTracker;
    private String fontsSelected = "default";
    private SharedPreferences sharedpreferences;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ArrayList<String> fontList;
    private boolean isUpdateWidget = false;
    private boolean isNotePresent = false;
    private boolean isWidgetView = false;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int getWidgetIdNotes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new AutoTransition());
        getWindow().setExitTransition(new AutoTransition());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        ApplicationContext.setContext(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTheme();
        slideInLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        slideOutLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        editNotesPresenter = new EditNotesPresenterImpl(this);
        fab_save.setOnClickListener(this);
        btnTTSPause.setOnClickListener(this);
        database = new DatabaseHandler(this);
        rlNote.setBackgroundColor(Color.parseColor(colorDefault));
        bundle = getIntent().getExtras();

        if (isNonWidget()) {
            isWidgetView = false;
            getNonWidgetUI();
        } else if (isWidgetUI()) {
            isWidgetView = true;
            invalidateOptionsMenu();
            getWidgetUI();
        }
        btnSpeak.setOnClickListener(this);
        btnTTSStart.setOnClickListener(this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1);
                }
            }
        });
        rlNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
        ttsListeners();
        displayADD();
    }


    private void getNonWidgetUI() {
        if (bundle.get(AddNotesView.NOTESACTIVITY_TYPE_KEY) != null) {
            notesType = bundle.get(AddNotesView.NOTESACTIVITY_TYPE_KEY).toString();
        }
        if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
            position = Integer.parseInt(bundle.get(AddNotesView.NOTESACTIVITY_TYPE_POSITION).toString());
            NoteDetails noteDetails = database.getNote(position);
            getWidgetIdNotes = noteDetails.getWidgetId();
            if (database.getNote(position).getLockedOrNot() == 1) {
                lockedPassword = database.getNote(position).getLockPassword();
                lockedOrnot = database.getNote(position).getLockedOrNot();
                titleLocked = database.getNote(position).getLockTitle();
            }
            setDetailsToView(noteDetails);
        }
    }

    private void getWidgetUI() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        List<NoteDetails> noteDetailsList = database.getAllNotes();
        //already present widget.
        if (getIntent().getStringExtra("widgetNoteDetails") != null) {
            for (int i = 0; i < noteDetailsList.size(); i++) {
                if (noteDetailsList.get(i).getNote().equalsIgnoreCase(getIntent().getStringExtra("widgetNoteDetails"))
                        && !noteDetailsList.get(i).getTypeOfNote().equalsIgnoreCase("Shopping")) {
                    isUpdateWidget = true;
                    isNotePresent = true;
                    position = i + 1;
                    NoteDetails noteDetails = database.getNote(position);
                    if (database.getNote(position).getLockedOrNot() == 1) {
                        lockedPassword = database.getNote(position).getLockPassword();
                        lockedOrnot = database.getNote(position).getLockedOrNot();
                        titleLocked = database.getNote(position).getLockTitle();
                    }
                    setDetailsToView(noteDetails);
                    break;
                }
            }
            if (!isNotePresent) {
                isUpdateWidget = false;
                etNoteText.setText(getIntent().getStringExtra("widgetNoteDetails"));
            }
        } else {
            //1st time adding widget.
            String noteText = loadTitlePref(EditNotesView.this, mAppWidgetId);
            for (int i = 0; i < noteDetailsList.size(); i++) {
                if (noteDetailsList.get(i).getNote().equalsIgnoreCase(noteText) && !noteDetailsList.get(i).getTypeOfNote().equalsIgnoreCase("Shopping")) {
                    isUpdateWidget = true;
                    position = i + 1;
                    NoteDetails noteDetails = database.getNote(position);
                    if (database.getNote(position).getLockedOrNot() == 1) {
                        lockedPassword = database.getNote(position).getLockPassword();
                        lockedOrnot = database.getNote(position).getLockedOrNot();
                        titleLocked = database.getNote(position).getLockTitle();
                    }
                    setDetailsToView(noteDetails);
                    break;
                }
            }
            if (isUpdateWidget) {
                etNoteText.setText(noteText);
            }
        }
    }

    private static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        }
        return "";
    }

    private void ttsListeners() {
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    if (utteranceId.equalsIgnoreCase("UniqueID")) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnTTSStart.setVisibility(View.VISIBLE);
                                    btnTTSPause.setVisibility(View.GONE);
                                }
                            });

                        } catch (Exception e) {
                            Log.d("error", e.toString());
                        }
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        } else {
            int listenerResult = tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    btnTTSPause.setVisibility(View.GONE);
                    btnTTSStart.setVisibility(View.VISIBLE);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }

    private void setTheme() {
        sharedpreferences = getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = sharedpreferences.getString(AddNotesView.MYTHEMECOLOR, "");
        if (!TextUtils.isEmpty(themeColor)) {
            Window window = this.getWindow();
            CommonUtilities.changeThemeColors(themeColor, toolbar, fab_save, window, false, null, null, null, null, null, null);
        }
    }

    public void setDetailsToView(NoteDetails noteDetails) {
        etNoteText.setText(noteDetails.getNote());
        etNoteText.setTextColor(Color.parseColor(noteDetails.getTextColor()));
        rlNote.setBackgroundColor(Color.parseColor(noteDetails.getColor()));
        colorDefault = noteDetails.getColor();
        colorTextDefault = noteDetails.getTextColor();
        if (!TextUtils.isEmpty(noteDetails.getFontSelected()) && !noteDetails.getFontSelected().equalsIgnoreCase("default")) {
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/" + noteDetails.getFontSelected());
            fontsSelected = noteDetails.getFontSelected();
            setFontToEditText(type);
        }
        setAlertToScreen(noteDetails);
    }

    private void setAlertToScreen(NoteDetails noteDetails) {
        //check if alert is present or not first
        if (!noteDetails.getAlertTime().equalsIgnoreCase(getString(R.string.alertDefaultValue))) {
            alertText.setVisibility(View.VISIBLE);
            alertText.setTextColor(Color.parseColor(noteDetails.getTextColor()));
            alertText.setText(noteDetails.getAlertTime());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);

        MenuItem action_alert = menu.findItem(R.id.action_alert);
        MenuItem action_font = menu.findItem(R.id.action_font);
        MenuItem action_lock = menu.findItem(R.id.action_lock);
        if (isWidgetView) {
            action_alert.setVisible(false);
            action_font.setVisible(false);
            action_lock.setVisible(false);
            action_alert.setEnabled(false);
            action_font.setEnabled(false);
            action_lock.setEnabled(false);
        } else {
            action_alert.setVisible(true);
            action_font.setVisible(true);
            action_lock.setVisible(true);
            action_alert.setEnabled(true);
            action_font.setEnabled(true);
            action_lock.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_alert) {
            setDate();
            return true;
        }
        if (id == R.id.action_color) {
            backgroundColorPickerDialog();
            return true;
        }
        if (id == R.id.action_font) {
            setFont();
            return true;
        }
        if (id == R.id.action_lock) {
            setLock();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLock() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lock_note_password);

        Button btn_cancel = (Button) dialog.findViewById(R.id.Cancel);

        final Button lockIt = (Button) dialog.findViewById(R.id.lockIt);
        TextView lockItOrNot = (TextView) dialog.findViewById(R.id.tvLockNoteOrNot);

        final EditText etTitleLock = (EditText) dialog.findViewById(R.id.etTitle);
        final EditText etPassword = (EditText) dialog.findViewById(R.id.etPassword);
        final EditText etConfirmPassword = (EditText) dialog.findViewById(R.id.etConfirmPassword);

        final TextView tvIncorrectPassword = (TextView) dialog.findViewById(R.id.tvIncorrectPassword);
        tvIncorrectPassword.setVisibility(View.GONE);
        tvIncorrectPassword.setText("Incorrect Password");
        if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
            if (lockedOrnot == 0) {
                lockIt.setText("Lock");
                lockItOrNot.setText(getString(R.string.lock_this_note));
            } else {
                lockIt.setText("UnLock");
                lockItOrNot.setText(getString(R.string.unLock_this_note));
                etConfirmPassword.setVisibility(View.GONE);
                etTitleLock.setVisibility(View.GONE);
            }
        }
        dialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lockIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockIt.getText().toString().equalsIgnoreCase("Lock")) {
                    if (!etTitleLock.getText().toString().equalsIgnoreCase("")) {
                        if (etPassword.getText().toString().equalsIgnoreCase(etConfirmPassword.getText().toString())) {
                            titleLocked = etTitleLock.getText().toString();
                            lockedOrnot = 1;
                            lockedPassword = etPassword.getText().toString();
                            Snackbar snackbar = Snackbar
                                    .make(add_note_coordinator_layout, "Locked..", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();
                            dialog.dismiss();
                        } else {
                            tvIncorrectPassword.setText("Password do not Match");
                            tvIncorrectPassword.setVisibility(View.VISIBLE);
                            Snackbar snackbar = Snackbar
                                    .make(add_note_coordinator_layout, "Oops!!password do not match..", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();
                        }
                    } else {
                        tvIncorrectPassword.setText("Please enter the title");
                        tvIncorrectPassword.setVisibility(View.VISIBLE);
                        Snackbar snackbar = Snackbar
                                .make(add_note_coordinator_layout, "Oops!!Please enter the title..", Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    }
                } else {
                    if (etPassword.getText().toString().equalsIgnoreCase(lockedPassword)) {
                        lockedOrnot = 0;
                        Snackbar snackbar = Snackbar
                                .make(add_note_coordinator_layout, "Unlocked..", Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                        dialog.dismiss();
                    } else {
                        tvIncorrectPassword.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setFont() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setExitTransition(new AutoTransition());
        dialog.getWindow().setEnterTransition(new AutoTransition());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_font);
        listView = (RecyclerView) dialog.findViewById(R.id.listView);
        TextView tvFonts = (TextView) dialog.findViewById(R.id.tvFonts);
        Typeface typeHeading = Typeface.createFromAsset(getAssets(), "fonts/Basing Regular.ttf");
        tvFonts.setTypeface(typeHeading);
        listView.setLayoutManager(new LinearLayoutManager(this));

        //getting list of fonts.
        fontList = new ArrayList<>();
        String[] f = null;
        try {
            f = getAssets().list("fonts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String f1 : f) {
            fontList.add(f1);
        }
        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), fontList, R.layout.row, fontsSelected);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        listView.setLayoutManager(llm);
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(recyclerAdapter);
        ((RecyclerAdapter) recyclerAdapter).setOnItemClickListener(new RecyclerAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                Typeface type = Typeface.createFromAsset(getAssets(), "fonts/" + fontList.get(position));
                setFontToEditText(type);
                fontsSelected = fontList.get(position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setFontToEditText(Typeface type) {
        etNoteText.setTextSize(22);
        alertText.setTextSize(14);
        etNoteText.setTypeface(type);
        alertText.setTypeface(type);
    }

    private void backgroundColorPickerDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.row_type_of_note);

        ImageView imgNote = (ImageView) dialog.findViewById(R.id.imgNote);
        ImageView imgShopping = (ImageView) dialog.findViewById(R.id.imgShopping);
        imgNote.setImageResource(R.drawable.backgroundchange);
        imgShopping.setImageResource(R.drawable.textchange2);

        TextView shopping = (TextView) dialog.findViewById(R.id.shopping);
        shopping.setText("Text");
        TextView note = (TextView) dialog.findViewById(R.id.note);
        note.setText("Notepad");

        imgNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackground();
            }
        });

        imgShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor();
            }
        });

        dialog.show();

    }

    private void setDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = view.getYear() - 1900;
                        mMonth = view.getMonth();
                        mDay = view.getDayOfMonth();
                        showTimePicker();
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.getWindow().getAttributes().windowAnimations = R.style.DialogDate;
        dpd.show();
    }

    private void showTimePicker() {
        final TimePickerDialog tpd = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker viewTime, int hourOfDay,
                                          int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        SimpleDateFormat sdf;
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        cal.clear();
                        sdf = new SimpleDateFormat("yyyy");
                        int formatedyear = Integer.parseInt(sdf.format(new Date(mYear, mMonth, mDay)));
                        sdf = new SimpleDateFormat("MM");
                        int formatedmonth = Integer.parseInt(sdf.format(new Date(mYear, mMonth, mDay)));
                        int month = formatedmonth - 1;
                        cal.set(formatedyear, month, mDay, mHour, mMinute, 0);

                        Calendar calSystem = Calendar.getInstance();
                        calSystem.setTimeInMillis(System.currentTimeMillis());

                        if (calSystem.getTimeInMillis() > cal.getTimeInMillis()) {
                            checkTime();
                        } else {
                            mHour = hourOfDay;
                            mMinute = minute;
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                            String formatedDate = sdf1.format(new Date(mYear, mMonth, mDay));
                            alertText.setVisibility(View.VISIBLE);
                            updateTime(mHour, mMinute, alertText, formatedDate);
                            isDateChanged = true;
                        }
                    }
                }
                , mHour, mMinute, false);

        tpd.getWindow().getAttributes().windowAnimations = R.style.DialogTime;
        tpd.show();
    }

    private void checkTime() {
        Snackbar snackbar = Snackbar
                .make(add_note_coordinator_layout, "Oops!!Invalid Time..", Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins, TextView alertText, String formatedDate) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        alertText.setText("Alert Set " + formatedDate + ", " + aTime);
        alertText.setTextColor(Color.parseColor(colorTextDefault));

    }

    private void addNotesToDB() {
        SimpleDateFormat sdf;
        String note = etNoteText.getText().toString();
        String textColor = colorTextDefault;
        String color = colorDefault;
        String alert = alertText.getText().toString();
        int pendingIntentId = 0;
        if (isDateChanged) {
            if (isNonWidget() && notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
                if (database.getAllNotes().size() > 0) {
                    NoteDetails noteDetails = database.getNote(position);
                    if (noteDetails.getPendingIntentId() != 0) {
                        int pendingIntentPrevious = noteDetails.getPendingIntentId();
                        Intent intent = new Intent(this, BroadcastManager.class);
                        intent.setAction(getString(R.string.broadcastSent));
                        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this.getApplicationContext(), pendingIntentPrevious, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntentCancel);
                        pendingIntentCancel.cancel();
                    }
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.clear();
            sdf = new SimpleDateFormat("yyyy");
            int formatedyear = Integer.parseInt(sdf.format(new Date(mYear, mMonth, mDay)));
            sdf = new SimpleDateFormat("MM");
            int formatedmonth = Integer.parseInt(sdf.format(new Date(mYear, mMonth, mDay)));
            int month = formatedmonth - 1;
            cal.set(formatedyear, month, mDay, mHour, mMinute, 0);
            d("time::", "" + cal.getTime());
            SecureRandom secureRandom = null;
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            pendingIntentId = secureRandom.nextInt();
            Intent myIntent1 = new Intent(this, BroadcastManager.class);
            myIntent1.putExtra("note", note);
            myIntent1.setAction(getString(R.string.broadcastSent));
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, pendingIntentId, myIntent1,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent1);
        } else {
            if (isNonWidget() && notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
                NoteDetails noteDetails = database.getNote(position);
                if (noteDetails.getPendingIntentId() != 0) {
                    pendingIntentId = noteDetails.getPendingIntentId();
                }
            }
        }
        String type = "Note";
        String shoppingString = "";
        if (isWidgetUI()) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(EditNotesView.this);
            WidgetUI.updateAppWidget(EditNotesView.this, appWidgetManager, mAppWidgetId, note, database, color, textColor);
            if (!isUpdateWidget) {
                database.addNote(new NoteDetails(note, textColor, color, alert, fontsSelected, pendingIntentId, shoppingString, type, titleLocked, lockedOrnot, lockedPassword, mAppWidgetId, "", ""));
            } else {
                database.updateNote(new NoteDetails(note, textColor, color, alert, fontsSelected, pendingIntentId, shoppingString, type, titleLocked, lockedOrnot, lockedPassword, mAppWidgetId, "", ""), position);
            }
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            SharedPreferences prefs = getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_PREFIX_KEY + mAppWidgetId, note).commit();
            finish();
        } else {
            if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_ADD)) {
                database.addNote(new NoteDetails(note, textColor, color, alert, fontsSelected, pendingIntentId, shoppingString, type,
                        titleLocked, lockedOrnot, lockedPassword, 0, "", ""));
            } else if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
                database.updateNote(new NoteDetails(note, textColor, color, alert, fontsSelected, pendingIntentId, shoppingString, type,
                        titleLocked, lockedOrnot, lockedPassword, getWidgetIdNotes, "", ""), position);
                if (getWidgetIdNotes != 0) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(EditNotesView.this);
                    WidgetUI.updateAppWidget(EditNotesView.this, appWidgetManager, getWidgetIdNotes, note, database, color, textColor);
                }
            }
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        editNotesPresenter.buttonClicked(id);
    }

    private void colorPickerDialog(final String type) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, color, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String colorFormat = String.format("0x%08x", color);
                String[] colorCode = colorFormat.split("0xff");
                if (colorCode.length > 0) {
                    String finalColor = colorCode[1];
                    if (type.equalsIgnoreCase(getString(R.string.backgroundColor))) {
                        colorDefault = "#" + finalColor;
                        rlNote.setBackgroundColor(Color.parseColor(colorDefault));
                    }
                    if (type.equalsIgnoreCase(getString(R.string.textColor))) {
                        colorTextDefault = "#" + finalColor;
                        etNoteText.setTextColor(Color.parseColor(colorTextDefault));
                        etNoteText.setHintTextColor(Color.parseColor(colorTextDefault));
                        alertText.setTextColor(Color.parseColor(colorTextDefault));
                    }
                }
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        getUserConfirmation();
    }

    private void getUserConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_confirmation_dialog);

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeech();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void changeTextColor() {
        colorPickerDialog(getString(R.string.textColor));
    }

    @Override
    public void changeBackground() {
        colorPickerDialog(getString(R.string.backgroundColor));
    }

    @Override
    public void saveDetails() {
        if (!etNoteText.getText().toString().equalsIgnoreCase("")) {
            stopSpeech();
            addNotesToDB();
        } else {
            Snackbar snackbar = Snackbar
                    .make(add_note_coordinator_layout, "Oops!! Nothing to save..", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    @Override
    public void keyboardVisibility() {
        InputMethodManager inputMgr = (InputMethodManager) getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void promptText() {
        stopSpeech();
        if (internetConnected()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Speak now");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Your device doesn\\'t support speech input",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(add_note_coordinator_layout, "Oops!! Please connect to the Internet for voice input.", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private boolean internetConnected() {
        boolean isInternetConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting()) {
            isInternetConnected = true;
        }
        return isInternetConnected;
    }

    @Override
    public void textToSpeech() {
        if (btnTTSPause.getVisibility() == View.GONE) {
            String toSpeak = etNoteText.getText().toString();
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
            btnTTSPause.setVisibility(View.VISIBLE);
            btnTTSStart.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void stopSpeechButtonImpl() {
        if (btnTTSPause.getVisibility() == View.VISIBLE) {
            stopSpeech();
        }
    }

    public void stopSpeech() {
        if (tts.isSpeaking()) {
            btnTTSPause.setVisibility(View.GONE);
            btnTTSStart.setVisibility(View.VISIBLE);
            tts.stop();
        }
    }

    private void displayADD() {
        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void changeTheme(int toolbarColor, int statusBar, int accent, int fab2, int fab3, int fab4) {
        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), toolbarColor));
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor((ContextCompat.getColor(getApplicationContext(), statusBar)));
        fab_save.setBackgroundTintList(ColorStateList.valueOf((ContextCompat.getColor(getApplicationContext(), accent))));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSpeech();
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (null != result && result.size() > 0) {
                        etNoteText.setText(etNoteText.getText().toString().concat(result.get(0)));
                    }
                }
                break;
            }
        }
    }

    private boolean isWidgetUI() {
        return null != getIntent() && null != getIntent().getAction() && getIntent().getAction().equals("android.appwidget.action.APPWIDGET_CONFIGURE");
    }

    private boolean isNonWidget() {
        return null == getIntent() || null == getIntent().getAction() || !getIntent().getAction().equals("android.appwidget.action.APPWIDGET_CONFIGURE");
    }
}
