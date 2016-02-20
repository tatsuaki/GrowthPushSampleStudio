package grow.push.test.kin.com.mypushapp.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class AdjustHelper {
    private static final String TAG = "AdjustHelper";
    private Application mApplication = null;
//  public String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
    public String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;

    private final String APP_TOKEN = "6rspygco22o0";

    public AdjustHelper(Application application) {
        mApplication = application;
        initAdjust();
    }

    private void initAdjust() {
        Log.d(TAG, "initAdjust");
        AdjustConfig config = new AdjustConfig(mApplication, APP_TOKEN, environment);
        Adjust.onCreate(config);

    //  config.setLogLevel(LogLevel.VERBOSE);   // enable all logging
        config.setLogLevel(LogLevel.DEBUG);     // enable more logging
    //  config.setLogLevel(LogLevel.INFO);      // the default
    //  config.setLogLevel(LogLevel.WARN);      // disable info logging
    //  config.setLogLevel(LogLevel.ERROR);     // disable warnings as well
    //  config.setLogLevel(LogLevel.ASSERT);    // disable errors as well

        mApplication.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

        // Lanth
        AdjustEvent event = new AdjustEvent("5qcwso");
        Adjust.trackEvent(event);
    }

    public static void sendEvent(String key) {
        Log.i(TAG, "sendEvent key = " + key);
        AdjustEvent event = new AdjustEvent(key);
        Adjust.trackEvent(event);
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity){
            Adjust.onResume();
        };
        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }
        public void onActivityDestroyed(Activity activity){};
        public void onActivityCreated(Activity activity, Bundle bundle) {}
        public void onActivityStarted(Activity activity){};
        public void onActivityStopped(Activity activity){};
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle){};
    }
}
