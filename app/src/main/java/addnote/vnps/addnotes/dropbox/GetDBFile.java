package addnote.vnps.addnotes.dropbox;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.editnotes.view.BroadcastManager;
import addnote.vnps.addnotes.pojo.NoteDetails;

public class GetDBFile extends AsyncTask<Void, Void, Boolean> {

    DatabaseHandler database;
    Context context;
    File file;
    List<NoteDetails> defaultNotes;
    private String path;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private DbxClientV2 dropboxApi;

    public GetDBFile(DbxClientV2 dropboxApi, String path, Context context1, DatabaseHandler database, RelativeLayout dropbox_relative) {
        this.dropboxApi = dropboxApi;
        this.path = path;
        this.context = context1;
        this.database = database;
        this.relativeLayout = dropbox_relative;
        progressDialog = CustomProgressDialog.myCustomProgressDialog(context, 1);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isFetchingSuccess = true;
        FileOutputStream outputStream = null;
        if (database != null) {
            defaultNotes = database.getAllNotes();
            try {
                String databasePath = context.getDatabasePath("notesManager").getPath();
                file = new File(databasePath);
                outputStream = new FileOutputStream(file);
                FileMetadata fileMetadata = dropboxApi.files().download("/"+path).download(outputStream);
             } catch (DbxException e) {
                isFetchingSuccess = false;
            } catch (FileNotFoundException e) {
                Log.d("check", e.getMessage());
                isFetchingSuccess = false;
            } catch (SQLiteException e) {
                Log.d("checkSQLiteException", e.getMessage());
                isFetchingSuccess = false;
            } catch (IOException e) {
                e.printStackTrace();
                isFetchingSuccess = false;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        isFetchingSuccess = false;
                    }
                }
            }
        } else {
            isFetchingSuccess = false;
        }
        return isFetchingSuccess;
    }

    @Override
    protected void onPostExecute(Boolean isFetchingSuccess) {
        progressDialog.hide();
        SQLiteDatabase checkDB = null;
        if (isFetchingSuccess) {
            try {
                checkDB = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                Log.e("DbExampleLog", "database does't exist yet");
                isFetchingSuccess = false;
                //database does't exist yet.
            }

            List<NoteDetails> fetchedNotes = database.getAllNotes();
            int id = defaultNotes.size() + 1;
            for (int j = 0; j < fetchedNotes.size(); j++) {
                fetchedNotes.get(j).set_id(id);
                id++;
            }
            boolean isNoteAvailable;
            for (int i = 0; i < defaultNotes.size(); i++) {
                isNoteAvailable = false;
                for (int j = 0; j < fetchedNotes.size(); j++) {
                    if (defaultNotes.get(i).getNote().equalsIgnoreCase(fetchedNotes.get(j).getNote())) {
                        isNoteAvailable = true;
                    }
                }
                if (!isNoteAvailable) {
                    database.addNote(defaultNotes.get(i));
                    //setAlarmsForNotAvailableNotes(defaultNotes.get(i));
                    id++;
                }
            }
            if (checkDB != null) {
                checkDB.close();
            }
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Successfully updated your notes...", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "No saved notes found...", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void setAlarmsForNotAvailableNotes(NoteDetails noteDetails) {
        if (noteDetails.getPendingIntentId() != 0 && !TextUtils.isEmpty(noteDetails.getAlertTime())) {

            Intent intent1 = new Intent(context, BroadcastManager.class);
            intent1.putExtra("note", noteDetails.getNote());
            intent1.setAction(context.getString(R.string.broadcastSent));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, noteDetails.getPendingIntentId(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            SimpleDateFormat sdf;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.clear();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
            Date marDate;
            String strAlert = noteDetails.getAlertTime();
            String strDate[] = strAlert.split("Alert Set ");

            if (strDate.length > 1) {
                try {
                    String newDate = strDate[1];
                    marDate = formatter.parse(newDate);
                    int mYear = marDate.getYear();
                    int mDay = marDate.getDate();
                    int mMonth = marDate.getMonth();

                    int mHour = marDate.getHours();
                    int mMinute = marDate.getMinutes();

                    sdf = new SimpleDateFormat("yyyy");
                    int formatedyear = Integer.parseInt(sdf.format(new java.sql.Date(mYear, mMonth, mDay)));
                    sdf = new SimpleDateFormat("MM");
                    int formatedmonth = Integer.parseInt(sdf.format(new java.sql.Date(mYear, mMonth, mDay)));
                    int month = formatedmonth - 1;
                    cal.set(formatedyear, month, mDay, mHour, mMinute, 0);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }
        }

    }
}
