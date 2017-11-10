package addnote.vnps.addnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import addnote.vnps.addnotes.pojo.NoteDetails;
import addnote.vnps.addnotes.widget.WidgetPOJO;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "notesManager";

    // Notes and Widgets table name
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_PI = "widget";
    // Notes Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOTE = "note";
    private static final String KEY_COLOR = "color";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_ALERT_TIME = "alert_time";
    private static final String KEY_FONT_SELECTED = "font_selected";
    private static final String KEY_PENDING_INTENT_ID = "pending_intent_id";
    private static final String KEY_SHOPPING_NOTE_STRING = "shoppingNoteString";
    private static final String KEY_TYPE_OF_NOTE = "noteType";
    private static final String KEY_SHOPPING_TITLE = "shoppingTitle";
    private static final String KEY_TITLE_LOCK = "title_lock";
    private static final String KEY_LOCKED_OR_NOT = "lockedOrNot";
    private static final String KEY_LOCKED_PASSWORD = "password";
    private static final String KEY_WIDGET_ID_NOTES = "widgetId";
    private static final String KEY_PARAM_1 = "param1";
    private static final String KEY_PARAM_2 = "param2";
    // Widgets Table
    private static final String KEY_WIDGETID = "wid_id";
    private static final String KEY_WID_NOTE = "wid_note";
    private static final String KEY_COLOR_WIDGET = "wid_color";
    private static final String KEY_WID_TEXT_COLOR = "wid_textColor";
    private static final String KEY_WID_PARAM2 = "wid_param2";

    private static final String DATABASE_ALTER_KEY_WIDGET_ID_NOTES = "ALTER TABLE "
            + TABLE_NOTES + " ADD COLUMN " + KEY_WIDGET_ID_NOTES + " INTEGER;";

    private static final String DATABASE_ALTER_KEY_PARAM_1 = "ALTER TABLE "
            + TABLE_NOTES + " ADD COLUMN " + KEY_PARAM_1 + " TEXT;";

    private static final String DATABASE_ALTER_KEY_PARAM_2 = "ALTER TABLE "
            + TABLE_NOTES + " ADD COLUMN " + KEY_PARAM_2 + " TEXT;";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTE + " TEXT," + KEY_TEXT_COLOR + " TEXT,"
                + KEY_COLOR + " TEXT," + KEY_ALERT_TIME + " TEXT," + KEY_PENDING_INTENT_ID + " INTEGER,"
                + KEY_FONT_SELECTED + " TEXT," + KEY_SHOPPING_NOTE_STRING + " TEXT," + KEY_TYPE_OF_NOTE + " TEXT," + KEY_SHOPPING_TITLE + " TEXT,"
                + KEY_TITLE_LOCK + " TEXT," + KEY_LOCKED_OR_NOT + " INTEGER,"
                + KEY_LOCKED_PASSWORD + " TEXT,"
                + KEY_WIDGET_ID_NOTES + " INTEGER," + KEY_PARAM_1 + " TEXT," + KEY_PARAM_2 + " TEXT" + ")";

        db.execSQL(CREATE_NOTES_TABLE);
        String CREATE_PI_TABLE = "CREATE TABLE " + TABLE_PI + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WID_NOTE + " TEXT,"
                + KEY_WIDGETID + " INTEGER," + KEY_COLOR_WIDGET + " TEXT," + KEY_WID_TEXT_COLOR + " TEXT," + KEY_WID_PARAM2 + " TEXT" + ")";

        db.execSQL(CREATE_PI_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {

            db.execSQL(DATABASE_ALTER_KEY_WIDGET_ID_NOTES);
            db.execSQL(DATABASE_ALTER_KEY_PARAM_1);
            db.execSQL(DATABASE_ALTER_KEY_PARAM_2);

            String CREATE_PI_TABLE = "CREATE TABLE " + TABLE_PI + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WID_NOTE + " TEXT,"
                    + KEY_WIDGETID + " INTEGER," + KEY_COLOR_WIDGET + " TEXT," + KEY_WID_TEXT_COLOR + " TEXT," + KEY_WID_PARAM2 + " TEXT" + ")";

            db.execSQL(CREATE_PI_TABLE);
        }
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new note
    public void addNote(NoteDetails noteDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NOTE, noteDetails.getNote());
            values.put(KEY_TEXT_COLOR, noteDetails.getTextColor());
            values.put(KEY_COLOR, noteDetails.getColor());
            values.put(KEY_ALERT_TIME, noteDetails.getAlertTime());
            values.put(KEY_PENDING_INTENT_ID, noteDetails.getPendingIntentId());
            values.put(KEY_FONT_SELECTED, noteDetails.getFontSelected());

            values.put(KEY_SHOPPING_NOTE_STRING, noteDetails.getShoppingString());
            values.put(KEY_TYPE_OF_NOTE, noteDetails.getTypeOfNote());
            values.put(KEY_SHOPPING_TITLE, noteDetails.getShoppingTitle());


            values.put(KEY_TITLE_LOCK, noteDetails.getLockTitle());
            values.put(KEY_LOCKED_OR_NOT, noteDetails.getLockedOrNot());
            values.put(KEY_LOCKED_PASSWORD, noteDetails.getLockPassword());

            values.put(KEY_WIDGET_ID_NOTES, noteDetails.getWidgetId());
            values.put(KEY_PARAM_1, noteDetails.getParam1());
            values.put(KEY_PARAM_2, noteDetails.getParam2());

            // Inserting Row
            db.insert(TABLE_NOTES, null, values);
        } catch (SQLiteException e) {
            Log.d("check", e.getMessage());
        }
    }

    // Getting single note
    public NoteDetails getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_ID,
                        KEY_NOTE, KEY_TEXT_COLOR, KEY_COLOR, KEY_ALERT_TIME, KEY_PENDING_INTENT_ID, KEY_FONT_SELECTED, KEY_SHOPPING_NOTE_STRING, KEY_TYPE_OF_NOTE, KEY_SHOPPING_TITLE,
                        KEY_TITLE_LOCK, KEY_LOCKED_OR_NOT, KEY_LOCKED_PASSWORD, KEY_WIDGET_ID_NOTES, KEY_PARAM_1, KEY_PARAM_2}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        NoteDetails note = null;
        if (cursor != null) {
            note = new NoteDetails(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)
                    , cursor.getString(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12),
                    cursor.getInt(13), cursor.getString(14), cursor.getString(15));
            cursor.close();
        }
        // return note.

        return note;
    }

    // Getting All notes
    public List<NoteDetails> getAllNotes() {
        List<NoteDetails> noteList = new ArrayList<>();
        // Select All Query
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NOTES;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    NoteDetails noteDetails = new NoteDetails();
                    noteDetails.set_id(Integer.parseInt(cursor.getString(0)));
                    noteDetails.setNote(cursor.getString(1));
                    noteDetails.setTextColor(cursor.getString(2));
                    noteDetails.setColor(cursor.getString(3));
                    noteDetails.setAlertTime(cursor.getString(4));
                    noteDetails.setPendingIntentId(cursor.getInt(5));
                    noteDetails.setFontSelected(cursor.getString(6));
                    noteDetails.setShoppingString(cursor.getString(7));
                    noteDetails.setTypeOfNote(cursor.getString(8));
                    noteDetails.setShoppingTitle(cursor.getString(9));
                    noteDetails.setLockTitle(cursor.getString(10));
                    noteDetails.setLockedOrNot(cursor.getInt(11));
                    noteDetails.setLockPassword(cursor.getString(12));
                    noteDetails.setWidgetId(cursor.getInt(13));
                    noteDetails.setParam1(cursor.getString(14));
                    noteDetails.setParam2(cursor.getString(15));
                    // Adding notes to list
                    noteList.add(noteDetails);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.d("check", e.getMessage());
        }
        return noteList;
    }

    // Updating single note
    public int updateNote(NoteDetails noteDetails, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, noteDetails.getNote());
        values.put(KEY_TEXT_COLOR, noteDetails.getTextColor());
        values.put(KEY_COLOR, noteDetails.getColor());
        values.put(KEY_ALERT_TIME, noteDetails.getAlertTime());
        values.put(KEY_PENDING_INTENT_ID, noteDetails.getPendingIntentId());
        values.put(KEY_FONT_SELECTED, noteDetails.getFontSelected());

        values.put(KEY_SHOPPING_NOTE_STRING, noteDetails.getShoppingString());
        values.put(KEY_TYPE_OF_NOTE, noteDetails.getTypeOfNote());
        values.put(KEY_SHOPPING_TITLE, noteDetails.getShoppingTitle());

        values.put(KEY_TITLE_LOCK, noteDetails.getLockTitle());
        values.put(KEY_LOCKED_OR_NOT, noteDetails.getLockedOrNot());
        values.put(KEY_LOCKED_PASSWORD, noteDetails.getLockPassword());

        values.put(KEY_WIDGET_ID_NOTES, noteDetails.getWidgetId());
        values.put(KEY_PARAM_1, noteDetails.getParam1());
        values.put(KEY_PARAM_2, noteDetails.getParam2());

        // updating row
        return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(position)});
    }

    public int updateAllNotes(NoteDetails noteDetails, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, noteDetails.getNote());
        values.put(KEY_TEXT_COLOR, noteDetails.getTextColor());
        values.put(KEY_COLOR, noteDetails.getColor());
        values.put(KEY_ID, position);
        values.put(KEY_ALERT_TIME, noteDetails.getAlertTime());
        values.put(KEY_PENDING_INTENT_ID, noteDetails.getPendingIntentId());
        values.put(KEY_FONT_SELECTED, noteDetails.getFontSelected());

        values.put(KEY_SHOPPING_NOTE_STRING, noteDetails.getShoppingString());
        values.put(KEY_TYPE_OF_NOTE, noteDetails.getTypeOfNote());
        values.put(KEY_SHOPPING_TITLE, noteDetails.getShoppingTitle());

        values.put(KEY_TITLE_LOCK, noteDetails.getLockTitle());
        values.put(KEY_LOCKED_OR_NOT, noteDetails.getLockedOrNot());
        values.put(KEY_LOCKED_PASSWORD, noteDetails.getLockPassword());

        values.put(KEY_WIDGET_ID_NOTES, noteDetails.getWidgetId());
        values.put(KEY_PARAM_1, noteDetails.getParam1());
        values.put(KEY_PARAM_2, noteDetails.getParam2());

        // updating row
        return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(noteDetails.get_id())});
    }

    // Deleting single note
    public void deleteNote(NoteDetails noteDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[]{String.valueOf(noteDetails.get_id())});
        db.close();
    }

    // Deleting single note
    public void deleteNoteByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void addWidget(WidgetPOJO widgetPOJO) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WID_NOTE, widgetPOJO.getWidgetNote());
            values.put(KEY_WIDGETID, widgetPOJO.getWidgetId());
            values.put(KEY_COLOR_WIDGET, widgetPOJO.getColorWidget());
            values.put(KEY_WID_TEXT_COLOR, widgetPOJO.getTextColorWidget());
            values.put(KEY_WID_PARAM2, widgetPOJO.getParam2());

            // Inserting Row
            db.insert(TABLE_PI, null, values);
        } catch (SQLiteException e) {
            Log.d("check", e.getMessage());
        }
    }

    // Deleting single note
    public void deleteWidgetByID(int widgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PI, KEY_WIDGETID + " = ?",
                new String[]{String.valueOf(widgetId)});
        db.close();
    }

    // Getting All Widgets
    public List<WidgetPOJO> getAllWidgets() {
        List<WidgetPOJO> widgetList = new ArrayList<>();
        // Select All Query
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_PI;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Log.d("check", "cursor move to next started");
                    WidgetPOJO widgetPOJO = new WidgetPOJO();
                    widgetPOJO.setWidgetNote(cursor.getString(1));
                    widgetPOJO.setWidgetId(Integer.parseInt(cursor.getString(2)));
                    widgetPOJO.setColorWidget(cursor.getString(3));
                    widgetPOJO.setTextColorWidget(cursor.getString(4));
                    widgetPOJO.setParam2(cursor.getString(5));
                    // Adding widget to list
                    widgetList.add(widgetPOJO);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.d("check", e.getMessage());
        }
        return widgetList;
    }

}