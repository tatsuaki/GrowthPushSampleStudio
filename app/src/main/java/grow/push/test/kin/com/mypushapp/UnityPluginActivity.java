package grow.push.test.kin.com.mypushapp;

/**
 * Created by maki on 2016/03/11.
 * MyApps.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.test.kin.unity.uni.UnityPlayerActivity;

/**
 * Created by maki on 2016/03/11.
 * MyApps.
 */
public class UnityPluginActivity extends Activity {
    private final String TAG = "UnityPluginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Unity");

        createUnity();
    }

    public void createUnity() {
        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
