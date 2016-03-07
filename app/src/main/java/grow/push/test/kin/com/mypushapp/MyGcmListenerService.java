package grow.push.test.kin.com.mypushapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.growthpush.GrowthPush;

import java.util.Set;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    static int counts = 0;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "onMessageReceived from = " + from + " data=" + data.toString());
        String datas = data.getString("growthpush");
        Log.d(TAG, "datas " + datas);
        if (null != datas && datas.length() > 0) {
            System.out.println( "部分一致です" );
            Intent intent = new Intent();
            intent.putExtras(data);
            GrowthPush.getInstance().getReceiveHandler().onReceive(this.getApplicationContext(), intent);
        } else {
            System.out.println( "部分一致ではありません" );
            String message = data.getString("message");
            Log.d(TAG, "onMessageReceived From: " + from);
            Log.d(TAG, "onMessageReceived Message: " + message);
            Log.d(TAG, "sendNotification");
            sendNotification("my " + message);
        }
//        if (from.equals("470626985372")) {
//            Intent intent = new Intent();
//            intent.putExtras(data);
//            GrowthPush.getInstance().getReceiveHandler().onReceive(this.getApplicationContext(), intent);
//        }
//        if (from.equals(MyRegistrationIntentService.MY_SENDER_ID)) {
//            String message = data.getString("message");
//            Log.d(TAG, "onMessageReceived From: " + from);
//            Log.d(TAG, "onMessageReceived Message: " + message);
//            Log.d(TAG, "sendNotification");
//            sendNotification("my " + message);
//        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Configs.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Configs.EXTRA_MESSAGE, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(counts /* ID of notification */, notificationBuilder.build());
        counts++;
    }
}