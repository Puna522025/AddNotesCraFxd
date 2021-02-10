package addnote.vnps.addnotes.shopping;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.adapter.ShoppingListAdapter;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.analytics.AnalyticsApplication;
import addnote.vnps.addnotes.common.ApplicationContext;
import addnote.vnps.addnotes.common.CommonUtilities;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.editnotes.view.BroadcastManager;
import addnote.vnps.addnotes.pojo.NoteDetails;
import addnote.vnps.addnotes.pojo.ShoppingPojo;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.util.Log.d;

/**
 * @author Puneet
 */
public class ShoppingNote extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ShoppingNotesView";

    @BindView(R.id.etNoteText)
    EditText etNoteText;

    @BindView(R.id.titleShopping)
    EditText titleShopping;

    @BindView(R.id.totalSumItem)
    TextView totalSumItem;

    @BindView(R.id.listView)
    RecyclerView listView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.alertText)
    TextView alertText;

    @BindView(R.id.fab_save)
    FloatingActionButton fab_save;
    @BindView(R.id.shopping_note_coordinator_layout)
    CoordinatorLayout shopping_note_coordinator_layout;
    private String notesType = "";
    private ShoppingListAdapter recyclerAdapter;
    private DatabaseHandler database;
    private ArrayList<ShoppingPojo> shoppingArray;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Tracker mTracker;
    private int position;
    private boolean isDateChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        ApplicationContext.setContext(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTheme();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        database = new DatabaseHandler(this);
        shoppingArray = new ArrayList<>();
        recyclerAdapter = new ShoppingListAdapter(getApplicationContext(), shoppingArray);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.get(AddNotesView.NOTESACTIVITY_TYPE_KEY) != null) {
            notesType = bundle.get(AddNotesView.NOTESACTIVITY_TYPE_KEY).toString();
        }
        if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
            position = Integer.parseInt(bundle.get(AddNotesView.NOTESACTIVITY_TYPE_POSITION).toString());
            NoteDetails noteDetails = database.getNote(position);
            titleShopping.setText(noteDetails.getShoppingTitle());
            JSONObject json;
            try {
                json = new JSONObject(noteDetails.getShoppingString());
                if (json.optJSONArray(getString(R.string.json_Shopping_Array)) != null) {
                    JSONArray jArray = json.optJSONArray(getString(R.string.json_Shopping_Array));
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_obj = jArray.getJSONObject(i);   //get the 3rd item
                        ShoppingPojo shoppingPojo = new ShoppingPojo();
                        shoppingPojo.setValue(json_obj.getString("value"));
                        shoppingPojo.setItemToBuy(json_obj.getString("itemToBuy"));
                        shoppingPojo.setCountOfItemsToBuy(json_obj.getString("count"));
                        shoppingPojo.setDoneOrNot(json_obj.getString("doneOrNot"));
                        shoppingArray.add(shoppingPojo);
                    }
                }
                recyclerAdapter.notifyDataSetChanged();
                doSumOfItems(shoppingArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!noteDetails.getAlertTime().equalsIgnoreCase(getString(R.string.alertDefaultValue))) {
                alertText.setVisibility(View.VISIBLE);
                alertText.setText(noteDetails.getAlertTime());
            }
        }

        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNoteText.getText().toString().equalsIgnoreCase("")) {
                    ShoppingPojo shoppingPojo = new ShoppingPojo();
                    shoppingPojo.setItemToBuy(etNoteText.getText().toString());
                    shoppingPojo.setValue("");
                    shoppingPojo.setCountOfItemsToBuy("");
                    shoppingPojo.setDoneOrNot("no");
                    shoppingArray.add(shoppingPojo);
                    recyclerAdapter.notifyDataSetChanged();
                    doSumOfItems(shoppingArray);
                    etNoteText.setText("");
                } else {
                    Snackbar snackbar = Snackbar
                            .make(shopping_note_coordinator_layout, "Oops!! Enter the item to buy..", Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        listView.setLayoutManager(llm);
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new ShoppingListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (v.getId() == R.id.imgDeleteNote) {
                    shoppingArray.remove(position);
                    doSumOfItems(shoppingArray);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    getExtraParams(position);
                }
            }
        });
        recyclerAdapter.setOnCheckedChangeListener(new ShoppingListAdapter.MyCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int adapterPosition, CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isShown()) {
                    if (!isChecked) {
                        buttonView.setChecked(true);
                        shoppingArray.get(adapterPosition).setDoneOrNot("yes");
                    } else {
                        buttonView.setChecked(false);
                        shoppingArray.get(adapterPosition).setDoneOrNot("no");
                    }
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void doSumOfItems(ArrayList<ShoppingPojo> shoppingArray) {
        int sum = 0;
        for (int i = 0; i < shoppingArray.size(); i++) {
            if (!TextUtils.isEmpty(shoppingArray.get(i).getValue()) && !TextUtils.isEmpty(shoppingArray.get(i).getCountOfItemsToBuy())) {
                int totalValue = Integer.parseInt(shoppingArray.get(i).getValue()) *
                        Integer.parseInt(shoppingArray.get(i).getCountOfItemsToBuy());
                sum = sum + totalValue;
            }
        }
        if (sum != 0) {
            totalSumItem.setText((getString(R.string.shopping_total_amount,sum)));
            totalSumItem.setVisibility(View.VISIBLE);
        } else {
            totalSumItem.setVisibility(View.GONE);
        }
    }

    private void getExtraParams(final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.shopping_extra_param);

        Button btn_confirm = (Button) dialog.findViewById(R.id.saveExtraParams);
        Button btn_cancel = (Button) dialog.findViewById(R.id.CancelExtraParams);
        final EditText et_value = (EditText) dialog.findViewById(R.id.etValue);
        final EditText et_count = (EditText) dialog.findViewById(R.id.etCount);

        et_value.setText(shoppingArray.get(position).getValue());
        et_count.setText(shoppingArray.get(position).getCountOfItemsToBuy());
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingArray.get(position).setValue(et_value.getText().toString());
                shoppingArray.get(position).setCountOfItemsToBuy(et_count.getText().toString());
                recyclerAdapter.notifyDataSetChanged();
                doSumOfItems(shoppingArray);
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        dialog.show();
    }

    private void setTheme() {
        SharedPreferences sharedpreferences = getSharedPreferences(AddNotesView.MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = sharedpreferences.getString(AddNotesView.MYTHEMECOLOR, "");
        if (!TextUtils.isEmpty(themeColor)) {
            Window window = this.getWindow();
            CommonUtilities.changeThemeColors(themeColor, toolbar, fab_save, window, false, null, null, null, null, null, null);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void saveShoppingDetails() {
        String title = titleShopping.getText().toString();
        String type = "Shopping";
        JSONObject json = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < shoppingArray.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("itemToBuy", shoppingArray.get(i).getItemToBuy());
                jsonObject.put("value", shoppingArray.get(i).getValue());
                jsonObject.put("count", shoppingArray.get(i).getCountOfItemsToBuy());
                jsonObject.put("doneOrNot", shoppingArray.get(i).getDoneOrNot());
                jsonArray.put(i, jsonObject);
            }
            json.put(getString(R.string.json_Shopping_Array), jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String shoppingNote = json.toString();
        SimpleDateFormat sdf;
        String note = "Shopping- " + titleShopping.getText().toString();
        String alert = alertText.getText().toString();
        int pendingIntentId = 0;
        if (isDateChanged) {
            if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
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
            if (secureRandom != null) {
                pendingIntentId = secureRandom.nextInt();
            }
            Intent myIntent1 = new Intent(this, BroadcastManager.class);
            myIntent1.putExtra("note", note);
            myIntent1.setAction(getString(R.string.broadcastSent));
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, pendingIntentId, myIntent1,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent1);
        } else {
            if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
                NoteDetails noteDetails = database.getNote(position);
                if (noteDetails.getPendingIntentId() != 0) {
                    pendingIntentId = noteDetails.getPendingIntentId();
                }
            }
        }
        if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_ADD)) {
            database.addNote(new NoteDetails("", "", "", alert, pendingIntentId, "", shoppingNote, type, title));
        } else if (notesType.equalsIgnoreCase(AddNotesView.NOTESACTIVITY_TYPE_UPDATE)) {
            database.updateNote(new NoteDetails("", "", "", alert, pendingIntentId, "", shoppingNote, type, title), position);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //getUserConfirmation();
        if (shoppingArray != null && shoppingArray.size() > 0) {
            saveShoppingDetails();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_alert) {
            setDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                .make(shopping_note_coordinator_layout, "Oops!!Invalid Time..", Snackbar.LENGTH_SHORT);
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

        alertText.setText("Alert Set ".concat(formatedDate).concat(", ").concat(aTime));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
