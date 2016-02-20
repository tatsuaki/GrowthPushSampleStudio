package grow.push.test.kin.com.mypushapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;

import grow.push.test.kin.com.mypushapp.helper.AdjustHelper;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AdjustHelper adjustHelper = new AdjustHelper(this);
    }
}