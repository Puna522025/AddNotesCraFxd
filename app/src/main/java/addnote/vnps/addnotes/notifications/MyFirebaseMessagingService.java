package addnote.vnps.addnotes.notifications;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import addnote.vnps.addnotes.R;
import addnote.vnps.addnotes.addnotes.view.AddNotesView;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static String KEY_TEXT_REPLY = "key_text_reply";
/**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String msg = remoteMessage.getNotification().getBody();
        if (!TextUtils.isEmpty(msg)) {
            sendNotification(msg);

        }
    }


/**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, AddNotesView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int pendingIntentId = 0;
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            pendingIntentId = secureRandom.nextInt();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingIntentId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.note_new)
                .setContentTitle("Add Notes")
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(pendingIntentId, notificationBuilder.build());
    }
}
