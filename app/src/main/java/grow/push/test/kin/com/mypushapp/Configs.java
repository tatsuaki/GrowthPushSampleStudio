package grow.push.test.kin.com.mypushapp;

import android.content.Context;
import android.content.Intent;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class Configs {
    public static final String product01 = "grow.push.test.kin.com.mypushapp.item01";
    public static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCA" +
            "QEAkkao7Ap6VgR4MobRGl3Ql4bBod935FbT+Nzti5yVq/Y2lDrBOJbTKH+7JVNx4" +
            "a3WdNXbp4eq7Ka6FkclIlFyHy5yQrD3gJPau9lGySXnE53O2kHLGPjCte8Gy5liA" +
            "oFswVG+rpNiq34Eay1RtOcWV4epIK4oYLQxM6XqJU840sAgWJbHm3wBpffcf+FaZq" +
            "Cc7jOXrq8zf0ZXLfHmjKSVFZhn3Z3jDPEKP1wXqVYgcMF51hpvJ8ZsyPpzgQc/yAq" +
            "CrO0rLnbhdm9gKZ0sU9leVOoAe3PVDNU4" +
            "TAjjGSNui+MNSjUNX4kva34VBJLBx5HNbgBeEdqhyirV+dd75tu2ZQIDAQAB";

    /**
     *
     * Server API Key
     * AIzaSyDTWtYYzgSUbc2P5npv5fCyn6E0gXZWCJk
     *
     * Sender ID
     * 452173490404
     */
    /** GCMの通知メッセージを表示するインテントの定義名 */
    static final String DISPLAY_MESSAGE_ACTION = "DISPLAY_MESSAGE";

    /** メッセージのExtra定義名 */
    static final String EXTRA_MESSAGE = "message";
    /** UIに通知するメッセージ(直訳乙) */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
