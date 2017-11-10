package addnote.vnps.addnotes.editnotes.view;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;
import addnote.vnps.addnotes.database.DatabaseHandler;
import addnote.vnps.addnotes.pojo.NoteDetails;

import static android.util.Log.d;

public class BroadcastManager extends BroadcastReceiver {
    private static String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (null != intent && null != intent.getAction() && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            d("ReBooting Done", "GOT THE INTENT BOOTING RELATED");
            DatabaseHandler database = new DatabaseHandler(context);
            List<NoteDetails> noteDetails = database.getAllNotes();
            for (int i = 0; i < noteDetails.size(); i++) {
                if (noteDetails.get(i).getPendingIntentId() != 0 && !TextUtils.isEmpty(noteDetails.get(i).getAlertTime())) {

                    Intent intent1 = new Intent(context, BroadcastManager.class);
                    intent1.putExtra("note", noteDetails.get(i).getNote());
                    intent1.setAction(context.getString(R.string.broadcastSent));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, noteDetails.get(i).getPendingIntentId(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    SimpleDateFormat sdf;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.clear();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
                    Date marDate;
                    String strAlert = noteDetails.get(i).getAlertTime();
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
                        Calendar calNow = Calendar.getInstance();
                        calNow.setTimeInMillis(System.currentTimeMillis());
                        if (calNow.getTimeInMillis() < cal.getTimeInMillis()) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                        }
                    }
                }
            }
        } else if(null != intent && null != intent.getAction() && intent.getAction().equals(context.getString(R.string.broadcastSent))){
            String note = intent.getStringExtra("note");
            generateNotification(context, note);
        }
    }

    private void generateNotification(Context context, String message) {

        // Open MainActivity Class on Notification Click
        Intent intent = new Intent(context, AddNotesView.class);

        //intent.putExtra("content", message);
        //intent.putExtra(AddNotesView.NOTESACTIVITY_TYPE_KEY,"notificationView");
        /*// Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", message);
        // Open NotificationView.java Activity*/
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int pendingIntentId = secureRandom.nextInt();
        // Adds the Intent to the top of the stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AddNotesView.class);
        stackBuilder.addNextIntent(intent);

        String replyLabel = "Type here";
        RemoteInput remoteInput =
                new RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel(replyLabel)
                        .build();


        PendingIntent contentIntent = stackBuilder.getPendingIntent(pendingIntentId, PendingIntent.FLAG_UPDATE_CURRENT);
        int icon = R.drawable.note_new;
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        //builder.setColor(ContextCompat.getColor(context, R.color.colorFAB2));

        /*NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                       R.drawable.note_new,
                        "Reply", contentIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        builder.addAction(replyAction);*/

        notification = builder.setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.note_new))
                .setSmallIcon(icon).setTicker(appname).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message).build();

        notificationManager.notify(pendingIntentId, notification);

    }

}
