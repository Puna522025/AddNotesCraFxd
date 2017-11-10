package addnote.vnps.addnotes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.editnotes.view.EditNotesView;

public class WidgetUI extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        DatabaseHandler database = new DatabaseHandler(context);
        database.deleteWidgetByID(appWidgetIds[0]);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        DatabaseHandler database = new DatabaseHandler(context);
            List<WidgetPOJO> widgetPOJOs = database.getAllWidgets();
            for (int i = 0; i < widgetPOJOs.size(); i++) {
                WidgetUI.setView(context, widgetPOJOs.get(i).getWidgetNote(),
                        widgetPOJOs.get(i).getWidgetId(),  widgetPOJOs.get(i).getColorWidget(),  widgetPOJOs.get(i).getTextColorWidget());
            }
    }

    @Override
    public void onEnabled(Context context) {
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String note, DatabaseHandler database, String color, String textColor) {
        Intent intent = new Intent(context, EditNotesView.class);
        intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("widgetNoteDetails",note);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Creating a pending intent, which will be invoked when the user
        // clicks on the widget
        DatabaseHandler databaseHandler = new DatabaseHandler(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        databaseHandler.addWidget(new WidgetPOJO(note, appWidgetId, color, textColor, ""));

        RemoteViews views= new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.appwidget_text, note);
        views.setInt(R.id.bgcolor, "setColorFilter", Color.parseColor(color));
        views.setTextColor(R.id.appwidget_text, Color.parseColor(textColor));
        views.setOnClickPendingIntent(R.id.rlWidget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void setView(Context context, String note, int appWidgetId, String colorWidget, String textColorWidget) {
        Intent intent = new Intent(context, EditNotesView.class);
        intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("widgetNoteDetails",note);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views= new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.appwidget_text, note);
        views.setTextColor(R.id.appwidget_text, Color.parseColor(textColorWidget));
        views.setInt(R.id.bgcolor, "setColorFilter", Color.parseColor(colorWidget));
        views.setOnClickPendingIntent(R.id.rlWidget, pendingIntent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


