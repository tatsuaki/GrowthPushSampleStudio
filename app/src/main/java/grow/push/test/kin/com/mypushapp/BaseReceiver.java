package grow.push.test.kin.com.mypushapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adjust.sdk.AdjustReferrerReceiver;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.link.InstallReferrerReceiver;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class BaseReceiver  extends BroadcastReceiver {
    private final String TAG = "BaseReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        /** Adjust */
        AdjustReferrerReceiver adjustReceiver = new AdjustReferrerReceiver();
        adjustReceiver.onReceive(context, intent);

        /** GrowthLink */
        InstallReferrerReceiver growthLink = new InstallReferrerReceiver();
        growthLink.onReceive(context, intent);
    }
}