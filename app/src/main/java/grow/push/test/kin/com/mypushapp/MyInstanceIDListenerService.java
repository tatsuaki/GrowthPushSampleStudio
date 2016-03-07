package grow.push.test.kin.com.mypushapp;

/**
 * Created by maki on 2016/02/14.
 * MyApps.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, MyRegistrationIntentService.class);
        Log.d(TAG, "onTokenRefresh");
        Log.d(TAG, "startService MyRegistrationIntentService");
        startService(intent);
    }
    // [END refresh_token]
}