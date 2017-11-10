package addnote.vnps.addnotes.addnotes.view;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.presenter.AddNotesPresenter;
import addnote.vnps.addnotes.addnotes.presenter.AddNotesPresenterImpl;
import addnote.vnps.addnotes.addnotes.presenter.SkipClicked;
import addnote.vnps.addnotes.analytics.AnalyticsApplication;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.dropbox.DropBoxActivity;
import addnote.vnps.addnotes.editnotes.view.BroadcastManager;
import addnote.vnps.addnotes.editnotes.view.EditNotesView;
import addnote.vnps.addnotes.intoduction.ViewPagerAdapter;
import addnote.vnps.addnotes.intoduction.fragments.ScreenOne;
import addnote.vnps.addnotes.intoduction.fragments.ScreenThree;
import addnote.vnps.addnotes.intoduction.fragments.ScreenTwo;
import addnote.vnps.addnotes.pojo.NoteDetails;
import addnote.vnps.addnotes.pojo.ShoppingPojo;
import addnote.vnps.addnotes.shopping.ShoppingNote;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static android.util.Log.d;

public class AddNotesView extends AppCompatActivity implements View.OnClickListener,
        AddNotesPresenter.View, SkipClicked {

    public static final String NOTESACTIVITY_TYPE_KEY = "type_key";
    public static final String NOTESACTIVITY_TYPE_UPDATE = "type_update";
    public static final String NOTESACTIVITY_TYPE_ADD = "type_add";
    public static final String NOTESACTIVITY_TYPE_POSITION = "type_position";
    public static final String MyPREFERENCES = "myPreference";
    public static final String PREF_VERSION_CODE_KEY = "version_code";

    public static final String MYTHEMECOLOR = "myThemeColor";
    private static final String TAG = "AddNotesView";
    @Bind(R.id.fab_action)
    FloatingActionButton fabAction;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    @Bind(R.id.fab_delete)
    FloatingActionButton fabDelete;
    @Bind(R.id.fab_share)
    FloatingActionButton fab_share;
    @Bind(R.id.notes_container)
    GridLayout notes_container;
    @Bind(R.id.imageBackground)
    ImageView imageBackground;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rlBackgroundNote)
    RelativeLayout rlBackgroundNote;

    @Bind(R.id.firstTimeExperience)
    RelativeLayout firstTimeExperience;


    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.viewPagerCountDots)
    LinearLayout linearLayout;
    ImageButton btnTTSStop1, btnTTS1;
    private TextToSpeech tts;
    private DatabaseHandler database;
    private Boolean isFabOpen = false;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private AddNotesPresenter addNotesPresenter;
    private Dialog dialog, dialogTheme;
    private Tracker mTracker;
    SharedPreferences sharedpreferences;
    private ImageView[] dots;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Class> listFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new AutoTransition());
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_viewnotes);
        ApplicationContext.setContext(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        database = new DatabaseHandler(this);
        addNotesPresenter = new AddNotesPresenterImpl(this);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fabAction.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
        fab_share.setOnClickListener(this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1);
                }
            }
        });

        setTheme();
        setGridAndView(false);
        displayADD();
        if (!checkFirstRun()) {
            firstTimeExperience.setVisibility(View.GONE);
            fabAction.setClickable(true);
        } else {
            setFirstTimeUserExp();
        }

        Intent intent = getIntent();

        Bundle remoteInput = android.support.v4.app.RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {
            String s = (String) remoteInput.getCharSequence("KEY");
            Toast.makeText(this, "" + s, Toast.LENGTH_LONG).show();
            // Do whatever with Text
        }


    }

    private boolean checkFirstRun() {
        final int DOESNT_EXIST = -1;
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        // Check for first run or upgrade
        if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            // Update the shared preferences with the current version code
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, 0).apply();
            return true;
        }
        return false;
    }

    private void setFirstTimeUserExp() {
        firstTimeExperience.setVisibility(View.VISIBLE);
        animateFAB();
        fabAction.setClickable(false);
        fabAdd.setClickable(false);
        fabDelete.setClickable(false);
        fab_share.setClickable(false);
        listFragments = new ArrayList<>();
        listFragments.add(ScreenOne.class);
        listFragments.add(ScreenTwo.class);
        listFragments.add(ScreenThree.class);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.setFragments(listFragments);

        viewPager.setAdapter(viewPagerAdapter);
        drawPageSelectionIndicators(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                drawPageSelectionIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void drawPageSelectionIndicators(int mPosition) {
        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }
        dots = new ImageView[listFragments.size()];
        for (int i = 0; i < listFragments.size(); i++) {
            dots[i] = new ImageView(getBaseContext());
            if (i == mPosition) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));
            } else {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(15, 0, 15, 0);
            linearLayout.addView(dots[i], params);
        }
    }

    private void setTheme() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = sharedpreferences.getString(MYTHEMECOLOR, "");
        if (!TextUtils.isEmpty(themeColor)) {
            Window window = this.getWindow();
            CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction,
                    fabAdd, fabDelete, fab_share, imageBackground, rlBackgroundNote);
        } else {
            Picasso.with(this)
                    .load(R.drawable.super_man_logo)
                    .into(imageBackground);
            //imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.super_man_logo));
            rlBackgroundNote.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.app_bg3_c));
        }
    }

    /**
     * Setting the number of notes in a row according to the screen size.
     *
     * @param isRenderViewCalled
     */
    private void setGridAndView(boolean isRenderViewCalled) {
        float scalefactor = getResources().getDisplayMetrics().density * 120;
        int number = getWindowManager().getDefaultDisplay().getWidth();
        int columns = (int) ((float) number / (float) scalefactor);
        if (notes_container.getChildCount() > 0) {
            notes_container.removeAllViews();
        }
        notes_container.setColumnCount(columns);
        if (isRenderViewCalled) {
            addNotesPresenter.renderView(0);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setGridAndView(true);
        if (null != dialog && dialog.isShowing()) {
            //stopSpeech();
            dialog.dismiss();
            settingDimensionsOnIntermediateDialog(dialog);
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        stopSpeech();
        super.onPause();
    }

    private void displayADD() {
        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void settingDimensionsOnIntermediateDialog(Dialog dialog) {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE); // for activity use context instead of getActivity()
        Display display = wm.getDefaultDisplay(); // getting the screen size of device
        Point size = new Point();
        display.getSize(size);
        int width = size.x - 150;  // Set your heights
        int height = size.y - 250; // set your widths
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addNotesPresenter.renderView(0);
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void renderView(final int deleteOrNot) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<NoteDetails> noteDetails = database.getAllNotes();
                if (notes_container.getChildCount() > 0) {
                    notes_container.removeAllViews();
                }
                if (null != noteDetails && !noteDetails.isEmpty()) {
                    for (int i = 0; i < noteDetails.size(); i++) {
                        addNotes(noteDetails.get(i), i, deleteOrNot);
                    }
                }
            }
        });

    }

    private void addNotes(final NoteDetails noteDetails, final int i, int deleteOrNot) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.view_note, null);
        TextView tvNotesViewText = (TextView) view.findViewById(R.id.tvNotesViewText);
        TextView tvLockedTitle = (TextView) view.findViewById(R.id.tvLockedTitle);
        ImageView imgDeleteNote = (ImageView) view.findViewById(R.id.imgDeleteNote);
        final ImageView imgShareNote = (ImageView) view.findViewById(R.id.imgShareNote);
        ImageView imgAlertNote = (ImageView) view.findViewById(R.id.imgAlertNote);
        ImageView imgLock = (ImageView) view.findViewById(R.id.imgLocked);
        ImageView imgPin = (ImageView) view.findViewById(R.id.imgTapeNote);
        ImageView imgCart = (ImageView) view.findViewById(R.id.imgTapeShopping);
        int visibility = (!noteDetails.getAlertTime().equalsIgnoreCase(getString(R.string.alertDefaultValue))) ? View.VISIBLE : View.GONE;
        imgAlertNote.setVisibility(visibility);
        if (deleteOrNot == 1) {
            imgDeleteNote.setVisibility(View.VISIBLE);
        } else {
            imgDeleteNote.setVisibility(View.GONE);
        }
        if (null != noteDetails.getTypeOfNote() && !noteDetails.getTypeOfNote().equalsIgnoreCase("Shopping")) {
            if (!TextUtils.isEmpty(noteDetails.getFontSelected()) && !noteDetails.getFontSelected().equalsIgnoreCase("default")) {
                Typeface type = Typeface.createFromAsset(getAssets(), "fonts/" + noteDetails.getFontSelected());
                tvNotesViewText.setTypeface(type);
            }
            tvNotesViewText.setText(noteDetails.getNote());
            imgPin.setVisibility(View.VISIBLE);
            imgCart.setVisibility(View.GONE);
            tvNotesViewText.setTextColor(Color.parseColor(noteDetails.getTextColor()));
            FrameLayout notes_frame = (FrameLayout) view.findViewById(R.id.notes_frame);
            notes_frame.setBackgroundColor(Color.parseColor(noteDetails.getColor()));
            if (noteDetails.getLockedOrNot() == 1) {
                imgLock.setVisibility(View.VISIBLE);
                notes_frame.setBackgroundColor(Color.parseColor("#252525"));
                imgPin.setVisibility(View.GONE);
                imgAlertNote.setVisibility(View.GONE);
                tvLockedTitle.setText(noteDetails.getLockTitle());
                tvNotesViewText.setVisibility(View.GONE);
                tvLockedTitle.setText(noteDetails.getLockTitle());
                tvLockedTitle.setVisibility(View.VISIBLE);
            }
        } else if (null != noteDetails.getTypeOfNote() && noteDetails.getTypeOfNote().equalsIgnoreCase("Shopping")) {
            tvNotesViewText.setText(noteDetails.getShoppingTitle());
            imgPin.setVisibility(View.GONE);
            imgCart.setVisibility(View.VISIBLE);
        }
        imgShareNote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!noteDetails.getTypeOfNote().equalsIgnoreCase("Shopping")) {
                            if (noteDetails.getLockedOrNot() == 0) {
                                shareTheNote(noteDetails);
                            } else {
                                setUnlockNote(noteDetails, null, "share", i);
                            }
                        } else {
                            ArrayList<ShoppingPojo> shoppingArray = new ArrayList<>();
                            JSONObject json = null;
                            try {
                                json = new JSONObject(noteDetails.getShoppingString());

                                JSONArray jArray = json.optJSONArray("shoppingArray");
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_obj = jArray.getJSONObject(i);
                                    ShoppingPojo shoppingPojo = new ShoppingPojo();
                                    shoppingPojo.setValue(json_obj.getString("value"));
                                    shoppingPojo.setItemToBuy(json_obj.getString("itemToBuy"));
                                    shoppingPojo.setCountOfItemsToBuy(json_obj.getString("count"));
                                    shoppingPojo.setDoneOrNot(json_obj.getString("doneOrNot"));
                                    shoppingArray.add(shoppingPojo);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String toSend = "Shopping List - \n";
                            for (int i = 0; i < shoppingArray.size(); i++) {
                                toSend = toSend + "" + (shoppingArray.get(i).getItemToBuy());
                                if (!TextUtils.isEmpty(shoppingArray.get(i).getCountOfItemsToBuy())) {
                                    toSend = toSend + "  " + "Count: " + shoppingArray.get(i).getCountOfItemsToBuy();
                                }
                                if (!TextUtils.isEmpty(shoppingArray.get(i).getValue())) {
                                    toSend = toSend + "  " + "Value: " + shoppingArray.get(i).getValue();
                                }
                                toSend = toSend + "\n";
                            }
                            Intent i = new Intent(android.content.Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shopping List");
                            i.putExtra(android.content.Intent.EXTRA_TEXT, toSend);
                            startActivity(Intent.createChooser(i, "Share via"));
                        }
                    }
                });
        imgDeleteNote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoteDetails noteDetails = database.getNote(i + 1);
                        //If alarm is present,show dialog.
                        if (!noteDetails.getTypeOfNote().equalsIgnoreCase("Shopping")) {
                            if (noteDetails.getLockedOrNot() == 0) {
                                deleteTheNote(noteDetails, i, 1);
                            } else {
                                setUnlockNote(noteDetails, null, "delete", i);
                            }
                        } else {
                            deleteTheNote(noteDetails, i, 1);
                        }
                    }
                }
        );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNavigationToIntermediate(i, noteDetails);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleNavigationToIntermediate(i, noteDetails);
                return false;
            }
        });
        if (notes_container.getChildCount() == 0) {
            notes_container.addView(view);
        } else if (notes_container.getChildCount() < 100) {
            notes_container.addView(view, notes_container.indexOfChild(view));
        }

    }

    private void handleNavigationToIntermediate(int i, NoteDetails noteDetails) {
        int value = i + 1;
        final Bundle type = new Bundle();
        type.putString(NOTESACTIVITY_TYPE_KEY, NOTESACTIVITY_TYPE_UPDATE);
        type.putString(NOTESACTIVITY_TYPE_POSITION, Integer.toString(value));
        if (noteDetails.getTypeOfNote().equalsIgnoreCase("Shopping")) {
            Intent intent = new Intent(getApplicationContext(), ShoppingNote.class);
            intent.putExtras(type);
            startActivity(intent);
        } else {
            if (noteDetails.getLockedOrNot() == 0) {
                moveToEditNote(type);
            } else {
                setUnlockNote(noteDetails, type, "viewClick", i);
            }
        }
    }

    private void deleteTheNote(NoteDetails noteDetails, int i, int deleteOrNotIcon) {
        if (noteDetails.getPendingIntentId() != 0) {
            showDialogDelete(noteDetails, i + 1);
        } else {
            //Simply deleting the particular note.
            deleteDatabaseEntry(i + 1, deleteOrNotIcon);
        }
    }

    private void shareTheNote(NoteDetails noteDetails) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Note");
        i.putExtra(Intent.EXTRA_TEXT, noteDetails.getNote());
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void setUnlockNote(final NoteDetails noteDetails, final Bundle type, final String selectionType, final int i) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lock_note_password);

        Button btn_cancel = (Button) dialog.findViewById(R.id.Cancel);

        final Button lockIt = (Button) dialog.findViewById(R.id.lockIt);
        TextView lockItOrNot = (TextView) dialog.findViewById(R.id.tvLockNoteOrNot);
        final TextView tvIncorrectPassword = (TextView) dialog.findViewById(R.id.tvIncorrectPassword);

        final EditText etTitleLock = (EditText) dialog.findViewById(R.id.etTitle);
        final EditText etPassword = (EditText) dialog.findViewById(R.id.etPassword);
        final EditText etConfirmPassword = (EditText) dialog.findViewById(R.id.etConfirmPassword);
        lockIt.setText("UnLock");
        tvIncorrectPassword.setVisibility(View.GONE);
        lockItOrNot.setText(getString(R.string.unLock_this_note));
        etConfirmPassword.setVisibility(View.GONE);
        etTitleLock.setVisibility(View.GONE);
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
                if (etPassword.getText().toString().equalsIgnoreCase(noteDetails.getLockPassword())) {
                    tvIncorrectPassword.setVisibility(View.GONE);
                    dialog.dismiss();
                    switch (selectionType) {
                        case "viewClick":
                            moveToEditNote(type);
                            break;
                        case "share":
                            shareTheNote(noteDetails);
                            sampleCheck();
                            break;
                        case "delete":
                            deleteTheNote(noteDetails, i, 1);
                            break;
                        default:
                            break;
                    }
                } else {
                    tvIncorrectPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sampleCheck() {


    }

    private void moveToEditNote(Bundle type) {
        Intent intent = new Intent(getApplicationContext(), EditNotesView.class);
        intent.putExtras(type);
        showIntermediate(intent, type);
    }

    private void showIntermediate(final Intent intent, Bundle bundle) {
        ttsListeners();
        final int position = Integer.parseInt(bundle.get(AddNotesView.NOTESACTIVITY_TYPE_POSITION).toString());
        final NoteDetails noteDetails = database.getNote(position);

        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        dialog.getWindow().setExitTransition(new AutoTransition());
        dialog.getWindow().setEnterTransition(new AutoTransition());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.intermediate_view);

        settingDimensionsOnIntermediateDialog(dialog);
        final TextView text = (TextView) dialog.findViewById(R.id.tvText);
        TextView tvAlarmText = (TextView) dialog.findViewById(R.id.tvAlarmText);
        CardView cardView = (CardView) dialog.findViewById(R.id.card1);
        ScrollView scrollView = (ScrollView) dialog.findViewById(R.id.svDialogScroll);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.editNote);
        ImageView imgAlertNote = (ImageView) dialog.findViewById(R.id.imgAlertNote);
        final ImageButton btnTTS = (ImageButton) dialog.findViewById(R.id.btnTTS);
        final ImageButton btnTTSStop = (ImageButton) dialog.findViewById(R.id.btnTTSStop);
        final ImageView deleteNote = (ImageView) dialog.findViewById(R.id.deleteNote);
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeech();
                deleteTheNote(noteDetails, position - 1, 2);
                dialog.dismiss();
            }
        });
        btnTTSStop1 = btnTTSStop;
        btnTTS1 = btnTTS;

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopSpeech();
                startActivity(intent);
                dialog.dismiss();
                return false;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopSpeech();
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    stopSpeech();
                    dialog.dismiss();
                }
                return true;
            }
        });
        if (!noteDetails.getAlertTime().equalsIgnoreCase(getString(R.string.alertDefaultValue))) {
            tvAlarmText.setText(noteDetails.getAlertTime());
            tvAlarmText.setTextColor(Color.parseColor(noteDetails.getTextColor()));
        }
        if (!TextUtils.isEmpty(noteDetails.getFontSelected()) && !noteDetails.getFontSelected().equalsIgnoreCase("default")) {
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/" + noteDetails.getFontSelected());
            text.setTypeface(type);
            tvAlarmText.setTypeface(type);
        }
        text.setText(noteDetails.getNote());
        text.setTextColor(Color.parseColor(noteDetails.getTextColor()));
        btnTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnTTSStop.getVisibility() == View.GONE) {
                    String toSpeak = text.getText().toString();
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                    btnTTSStop.setVisibility(View.VISIBLE);
                    btnTTS.setVisibility(View.GONE);
                }
            }
        });
        btnTTSStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnTTSStop.getVisibility() == View.VISIBLE) {

                    stopSpeech();
                }
            }
        });

        cardView.setBackgroundColor(Color.parseColor(noteDetails.getColor()));
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopSpeech();
                startActivity(intent);
                dialog.dismiss();
                return false;
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeech();
                startActivity(intent);
                dialog.dismiss();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeech();
                startActivity(intent);
                dialog.dismiss();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeech();
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void stopSpeech() {
        if (tts.isSpeaking()) {
            if (null != btnTTSStop1 && null != btnTTS1) {
                btnTTSStop1.setVisibility(View.GONE);
                btnTTS1.setVisibility(View.VISIBLE);
            }
            tts.stop();
        }
    }

    private void showDialogDelete(final NoteDetails noteDetails, final int z) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_confirmation_dialog);
        TextView tvNotesViewText = (TextView) dialog.findViewById(R.id.tvNotesViewText);
        tvNotesViewText.setText(R.string.deleteDialog);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setText(R.string.alarm);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm(noteDetails);
                noteDetails.setAlertTime(getString(R.string.alertDefaultValue));
                noteDetails.setPendingIntentId(0);
                database.updateNote(noteDetails, z);
                renderView(1);
                dialog.dismiss();
            }
        });
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setText(R.string.both);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm(noteDetails);
                deleteDatabaseEntry(z, 1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteDatabaseEntry(int z, int deleteOrNot) {
        try {
            database.deleteNoteByID(z);
            List<NoteDetails> noteDetailses = database.getAllNotes();
            for (int i = 0; i < noteDetailses.size(); i++) {
                database.updateAllNotes(noteDetailses.get(i), i + 1);
            }
        } catch (Exception e) {
            d(TAG, "DB exception");
        }
        if(deleteOrNot == 1 ) {
            renderView(1);
        }else{
            renderView(2);
        }
    }

    /**
     * Cancels the existing alarm for the particular note.
     *
     * @param noteDetails
     */
    private void cancelAlarm(NoteDetails noteDetails) {
        int pendingIntentPrevious = noteDetails.getPendingIntentId();
        Intent intent = new Intent(getApplicationContext(), BroadcastManager.class);
        intent.setAction(getString(R.string.broadcastSent));
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(getApplicationContext(), pendingIntentPrevious, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntentCancel);
        pendingIntentCancel.cancel();
    }

    /**
     * Makes the delete option visible.
     *
     * @param visibility
     */
    private void makeDeleteOptionVisible(final int visibility) {
        for (int child = 0; child < notes_container.getChildCount(); child++) {
            View view = notes_container.getChildAt(child);
            ImageView imgDeleteNote = (ImageView) view.findViewById(R.id.imgDeleteNote);
            imgDeleteNote.setVisibility(visibility);
            imgDeleteNote.setEnabled(false);

            if (visibility == View.VISIBLE) {
                imgDeleteNote.setEnabled(true);
            }
        }
    }

    /**
     * Makes the share option visible.
     *
     * @param visibility
     */
    private void makeShareOptionVisible(final int visibility) {
        for (int child = 0; child < notes_container.getChildCount(); child++) {
            View view = notes_container.getChildAt(child);
            ImageView imgShareNote = (ImageView) view.findViewById(R.id.imgShareNote);
            imgShareNote.setVisibility(visibility);
            imgShareNote.setEnabled(false);
            if (visibility == View.VISIBLE) {
                imgShareNote.setEnabled(true);
            }
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            fabAction.startAnimation(rotateBackward);
            fabAdd.startAnimation(fabClose);
            fabDelete.startAnimation(fabClose);
            fab_share.startAnimation(fabClose);
            fabAdd.setClickable(false);
            fabDelete.setClickable(false);
            fab_share.setClickable(false);
            isFabOpen = false;
        } else {
            fabAction.startAnimation(rotateForward);
            fabAdd.startAnimation(fabOpen);
            fabDelete.startAnimation(fabOpen);
            fab_share.startAnimation(fabOpen);
            fabAdd.setClickable(true);
            fabDelete.setClickable(true);
            fab_share.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void viewOptions() {
        animateFAB();
        makeDeleteOptionVisible(View.GONE);
        makeShareOptionVisible(View.GONE);
    }

    /**
     * Opens the EditNotesView for the user to add a new note.
     */
    @Override
    public void addNotes() {
        makeDeleteOptionVisible(View.GONE);
        makeShareOptionVisible(View.GONE);
        setType();
    }

    @Override
    public void deleteNotes() {
        makeDeleteOptionVisible(View.VISIBLE);
    }

    @Override
    public void shareNotes() {
        makeShareOptionVisible(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_theme) {
            showThemeDialog();
            return true;
        }
        if (id == R.id.action_dropBox) {
            showDropBox();
            return true;
        }
        if (id == R.id.action_rate_us) {
            rateApp();
            return true;
        }
        if (id == R.id.action_about_us) {
            aboutUs();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutUs() {
        Intent intent = new Intent(this, About_us.class);
        startActivity(intent);
    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            if ((rateIntent.resolveActivity(getPackageManager()) != null)) {
                startActivity(rateIntent);
            }
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("http://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void showDropBox() {
        Intent intent = new Intent(this, DropBoxActivity.class);
        startActivity(intent);
    }

    private void showThemeDialog() {

        dialogTheme = new Dialog(this);
        dialogTheme.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        dialogTheme.getWindow().setExitTransition(new AutoTransition());
        dialogTheme.getWindow().setEnterTransition(new AutoTransition());
        dialogTheme.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTheme.setContentView(R.layout.background_color_change);

        android.support.v7.widget.CardView rlRedYellow = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlRedYellow);
        android.support.v7.widget.CardView rlOrangRed = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlOrangRed);
        android.support.v7.widget.CardView rlPurpleGreen = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlPurpleGreen);
        android.support.v7.widget.CardView rlPinkYellow = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlPinkYellow);
        android.support.v7.widget.CardView rlGreenBrown = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlGreenBrown);
        android.support.v7.widget.CardView rlblue = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlblue);
        android.support.v7.widget.CardView rlOrange = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlOrange);
        android.support.v7.widget.CardView rlHelloKitty = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlPink_helloKitty);
        android.support.v7.widget.CardView rlDeepPurple = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlDeepPurple);
        android.support.v7.widget.CardView rlBlackRed = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlBlackRed);
        android.support.v7.widget.CardView rlNeonBlue = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlNeonBlue);
        android.support.v7.widget.CardView rlWhiteJoker = (android.support.v7.widget.CardView) dialogTheme.findViewById(R.id.rlWhiteJoker);

        Context context = ApplicationContext.getApplicationContext();
        ImageView imgDeadpool = (ImageView) dialogTheme.findViewById(R.id.imgDeadpool);
        Picasso.with(context).load(R.drawable.deadpool_cc).fit().into(imgDeadpool);

        ImageView imgBatmanNeon = (ImageView) dialogTheme.findViewById(R.id.imgBatmanNeon);
        Picasso.with(context).load(R.drawable.xa_c).fit().into(imgBatmanNeon);

        ImageView imgJoker = (ImageView) dialogTheme.findViewById(R.id.imgJoker);
        Picasso.with(context).load(R.drawable.joker_c).fit().into(imgJoker);

        ImageView imgBatmanLogo = (ImageView) dialogTheme.findViewById(R.id.imgBatmanLogo);
        Picasso.with(context).load(R.drawable.batman_logo).fit().into(imgBatmanLogo);

        ImageView imgHulk = (ImageView) dialogTheme.findViewById(R.id.imgHulk);
        Picasso.with(context).load(R.drawable.hulk).fit().into(imgHulk);

        ImageView imgSuperman = (ImageView) dialogTheme.findViewById(R.id.imgSuperman);
        Picasso.with(context).load(R.drawable.super_man_logo).fit().into(imgSuperman);

        ImageView imgKitty = (ImageView) dialogTheme.findViewById(R.id.imgKitty);
        Picasso.with(context).load(R.drawable.hellokitty).fit().into(imgKitty);

        ImageView imgMinion = (ImageView) dialogTheme.findViewById(R.id.imgMinion);
        Picasso.with(context).load(R.drawable.minion2).fit().into(imgMinion);

        ImageView imgIronMan = (ImageView) dialogTheme.findViewById(R.id.imgIronMan);
        Picasso.with(context).load(R.drawable.iron_man).fit().into(imgIronMan);

        ImageView imgCapAmerica = (ImageView) dialogTheme.findViewById(R.id.imgCapAmerica);
        Picasso.with(context).load(R.drawable.cap_america).fit().into(imgCapAmerica);

        rlRedYellow.setOnClickListener(this);
        rlPinkYellow.setOnClickListener(this);
        rlPurpleGreen.setOnClickListener(this);
        rlOrangRed.setOnClickListener(this);
        rlGreenBrown.setOnClickListener(this);
        rlblue.setOnClickListener(this);
        rlOrange.setOnClickListener(this);
        rlHelloKitty.setOnClickListener(this);
        rlDeepPurple.setOnClickListener(this);
        rlBlackRed.setOnClickListener(this);
        rlNeonBlue.setOnClickListener(this);
        rlWhiteJoker.setOnClickListener(this);
        dialogTheme.show();
    }

    @Override
    public void onClick(View v) {
        addNotesPresenter.userSelection(v.getId());
        final SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        String themeColor = "";
        Window window = this.getWindow();
        switch (v.getId()) {
            //IronMan
            case R.id.rlRedYellow:
                editor.putString(MYTHEMECOLOR, getString(R.string.redYellow));
                editor.commit();
                themeColor = getString(R.string.redYellow);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //Hulk
            case R.id.rlPurpleGreen:
                editor.putString(MYTHEMECOLOR, getString(R.string.purpleGreen));
                editor.commit();
                themeColor = getString(R.string.purpleGreen);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //SuperMan
            case R.id.rlOrangRed:
                editor.putString(MYTHEMECOLOR, getString(R.string.orangeRed));
                editor.commit();
                themeColor = getString(R.string.orangeRed);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //Cap America
            case R.id.rlPinkYellow:
                editor.putString(MYTHEMECOLOR, getString(R.string.pinkYellow));
                editor.commit();
                themeColor = getString(R.string.pinkYellow);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //BatMan
            case R.id.rlGreenBrown:
                editor.putString(MYTHEMECOLOR, getString(R.string.greenBrown));
                editor.commit();
                themeColor = getString(R.string.greenBrown);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //minion
            case R.id.rlblue:
                editor.putString(MYTHEMECOLOR, getString(R.string.blue));
                editor.commit();
                themeColor = getString(R.string.blue);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //Hello Kitty
            case R.id.rlPink_helloKitty:
                editor.putString(MYTHEMECOLOR, getString(R.string.pink_hello_kitty));
                editor.commit();
                themeColor = getString(R.string.pink_hello_kitty);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //Orange
            case R.id.rlOrange:
                editor.putString(MYTHEMECOLOR, getString(R.string.orange));
                editor.commit();
                themeColor = getString(R.string.orange);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //DeepPurple
            case R.id.rlDeepPurple:
                editor.putString(MYTHEMECOLOR, getString(R.string.deepPurple));
                editor.commit();
                themeColor = getString(R.string.deepPurple);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            //BlackRed //deadpool
            case R.id.rlBlackRed:
                editor.putString(MYTHEMECOLOR, getString(R.string.blackRed));
                editor.commit();
                themeColor = getString(R.string.blackRed);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            ///neonBlue //batman
            case R.id.rlNeonBlue:
                editor.putString(MYTHEMECOLOR, getString(R.string.neonBlue));
                editor.commit();
                themeColor = getString(R.string.neonBlue);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            ///WhiteJoker
            case R.id.rlWhiteJoker:
                editor.putString(MYTHEMECOLOR, getString(R.string.whiteJoker));
                editor.commit();
                themeColor = getString(R.string.whiteJoker);
                CommonUtilities.changeThemeColors(themeColor, toolbar, null, window, true, fabAction, fabAdd, fabDelete,
                        fab_share, imageBackground, rlBackgroundNote);
                dialogTheme.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        return true;
    }

    /**
     * Listeners for Text to speech.CallBack after finished reading the text.
     */
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
                                    btnTTSStop1.setVisibility(View.GONE);
                                    btnTTS1.setVisibility(View.VISIBLE);
                                }
                            });

                        } catch (Exception e) {
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
                    btnTTSStop1.setVisibility(View.GONE);
                    btnTTS1.setVisibility(View.VISIBLE);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }

    private void setType() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setExitTransition(new AutoTransition());
        dialog.getWindow().setEnterTransition(new AutoTransition());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.row_type_of_note);

        RelativeLayout rlShoppingSelected = (RelativeLayout) dialog.findViewById(R.id.rlShoppingSelected);
        RelativeLayout rlNoteSelected = (RelativeLayout) dialog.findViewById(R.id.rlNoteSelected);

        rlShoppingSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle type = new Bundle();
                Intent intent = new Intent(getApplicationContext(), ShoppingNote.class);
                intent.putExtra(NOTESACTIVITY_TYPE_KEY, NOTESACTIVITY_TYPE_ADD);
                intent.putExtras(type);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        rlNoteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle type = new Bundle();
                Intent intent = new Intent(getApplicationContext(), EditNotesView.class);
                intent.putExtra(NOTESACTIVITY_TYPE_KEY, NOTESACTIVITY_TYPE_ADD);
                intent.putExtras(type);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onSkipClicked() {
        fabAction.setClickable(true);
        firstTimeExperience.setVisibility(View.GONE);
        animateFAB();
    }
}
