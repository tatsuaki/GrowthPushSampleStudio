package grow.push.test.kin.com.mypushapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.growthbeat.Growthbeat;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthpush.GrowthPush;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import grow.push.test.kin.com.mypushapp.BillingUtil.IabHelper;
import grow.push.test.kin.com.mypushapp.BillingUtil.IabResult;
import grow.push.test.kin.com.mypushapp.BillingUtil.Inventory;
import grow.push.test.kin.com.mypushapp.BillingUtil.Purchase;

public class MainActivity extends AppCompatActivity implements OnClickListener, TJGetCurrencyBalanceListener, TJPlacementListener {
    private final String TAG = "MainActivity";

    public GrowthHelper mGrowthHelper = null;
    private Tracker mTracker;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public Button button1 = null;
    public Button button2 = null;
    public Button button3 = null;
    public Button button4 = null;
    public Button button5 = null;

    public Button tapJoy1 = null;
    public Button tapJoy2 = null;
    public Button tapJoy3 = null;
    public Button mconsumeButton = null;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    IabHelper mHelper;
    // Tapjoy Placements
    private TJPlacement directPlayPlacement;
    private boolean earnedCurrency = false;

    protected static final String SENDER_ID = "470626985372";
    private GoogleCloudMessaging gcm;
    private String regId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        createGCM();
     //   getRegistrationId();
        //初期化
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mHelper = new IabHelper(this, Configs.base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) {
                    return;
                }
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        mGrowthHelper = new GrowthHelper(this, getApplicationContext());
        registerReceiver(mHandleMessageReceiver, new IntentFilter(Configs.DISPLAY_MESSAGE_ACTION));
        Intent intent = new Intent(this, MyRegistrationIntentService.class);
        startService(intent);
    //    connectToTapjoy();
        settingView();

        BaseApplication application = (BaseApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //ログイン後のコールバック設定
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
//    //  LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {...});
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "FB onSuccess");
                AdjustHelper.sendEvent(AdjustHelper.FB_LOGIN);
                if (null != mGrowthHelper) {
                    mGrowthHelper.GrowthTrackEvent("FB LOGIN", getNowDate());
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FB onCancel");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "FB onError");
                // App code
            }
        });

//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(
//                    AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                mGrowthHelper.GrowthTrackEvent("FB TOKEN", currentAccessToken.getUserId());
//            }
//        };
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        String userId = accessToken.getUserId();
//        Log.d(TAG, "FB userId = " + userId);
//        if (null != mGrowthHelper) {
//            mGrowthHelper.GrowthTrackEvent("FB userId", userId);
//        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, "onReceive 1 token = " + sentToken);
                } else {
                    Log.d(TAG, "onReceive 2");
                }
            }
        };
        String act = getIntent().getAction();
        if (act != null && act.equals(Configs.DISPLAY_MESSAGE_ACTION)) {
            String mes = getIntent().getStringExtra(Configs.EXTRA_MESSAGE);
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            // ダイアログの設定
            alertDialog.setTitle("titless");          //タイトル
            alertDialog.setMessage(mes);      //内容
            alertDialog.setIcon(R.drawable.com_facebook_send_button_icon);   //アイコン設定
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    //初回表示完了
                    //  setState(PREFERENCE_BOOTED);
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }
//    private void getRegistrationId() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                if (gcm == null) {
//                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                }
//                try {
//                    regId = gcm.register(SENDER_ID);
//                    Log.v("GCM", "regId is " + regId);
//                } catch (IOException e) {
//                    Log.e("GCM", "error " + e.getMessage());
//                }
//                return null;
//            }
//        }.execute();
//    }
//    public void createGCM() {
//        // check if it's right manifest.
//    //    GCMRegistrar.checkDevice(this);
//   //   GCMRegistrar.checkManifest(this);
//        final String regId = GCMRegistrar.getRegistrationId(this);
//        Log.e(TAG, "regId=" + regId);
//        if (regId.equals("")) {
//            Log.e(TAG, "GCMIntentService.onRegisteredがコールされる。");
//            // GCMIntentService.onRegisteredがコールされる。
//            GCMRegistrar.register(this, "317126540441");
//        } else {
//            Log.v(TAG, "Already exists " + regId);
//        }
//    }
// ブロードキャストレシーバーの登録
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 通知を受けた内容を画面のテキストビューに表示
            String newMessage = intent.getExtras().getString(Configs.EXTRA_MESSAGE);
            Toast.makeText(getApplicationContext(), newMessage, Toast.LENGTH_SHORT).show();
        //    mDisplay.append(newMessage + "\n");
        }
    };
    private void settingView() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        tapJoy1 = (Button) findViewById(R.id.tapJoy1);
        tapJoy2 = (Button) findViewById(R.id.tapJoy2);
        tapJoy3 = (Button) findViewById(R.id.tapJoy3);
        mconsumeButton = (Button) findViewById(R.id.consumeButton);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        tapJoy1.setOnClickListener(this);
        tapJoy2.setOnClickListener(this);
        tapJoy3.setOnClickListener(this);
        mconsumeButton.setOnClickListener(this);
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;
            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
            Purchase Product01 = inventory.getPurchase(Configs.product01);
            if (Product01 != null) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(Configs.product01), mConsumeFinishedListener);
                return;
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(Configs.product01)) {
                Log.d(TAG, "消費 product01");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        // RC_REQUEST
        if (mHelper == null) {
            return;
        }
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
        try {
            JSONObject purchaseDataJson = new JSONObject(purchaseData);
            String productId = purchaseDataJson.getString("productId");
            // getSkuDetails
            ArrayList<String> skuList = new ArrayList<>();
            skuList.add(productId);
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
            Bundle skuDetails = mHelper.mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
            Tapjoy.trackPurchase(responseList.get(0), purchaseData, dataSignature, null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        /* 課金処理 */
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
        Log.d(TAG, "onActivity Result Code : " + resultCode); // -1

        /**
         * Login:64206
         * Share:64207
         * Message:64208
         * Like:64209
         * GameRequest:64210
         * AppGroupCreate:64211
         * AppGroupJoin:64212
         * AppInvite:64213
         */
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /**
     * Attempts to connect to Tapjoy
     */
    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");
        String tapjoySDKKey = "vHhqPYD3Q-KfII5zvavslgECtBII68S8A9VBrVksJDz8GBiPg0HTBkS5hNxL";
        Tapjoy.setGcmSender("452173490404");
        Tapjoy.setDebugEnabled(true);
        Tapjoy.connect(getApplicationContext(), tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "connectToTapjoy onConnectSuccess");
                TapjoiConnectSuccess();
                //  Tapjoy.trackPurchase(null, null, null, null);
            }

            @Override
            public void onConnectFailure() {
                onConnectFail();
            }
        });
    }

    public void TapjoiConnectSuccess() {
        //  directPlayPlacement = new TJPlacement(this, "AppLaunch", this);
        directPlayPlacement = new TJPlacement(this, "AppLaunch", this);
        if (Tapjoy.isConnected()) {
            directPlayPlacement.requestContent();
        } else {
            Log.d("Tapjoi", "Tapjoy SDKはリクエスト前にconnectingを終えなければなりません.");
        }
        if (directPlayPlacement.isContentReady()) {
            Log.d("Tapjoi", "isContentReady true show");
            directPlayPlacement.showContent();
        } else {
            //表示すべきコンテンツが無い、またはまだダウンロードされていない場合の処理
        }
        // For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
        // must be called after requestTapjoyConnect.

        // Setup listener for Tapjoy currency callbacks
        Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                earnedCurrency = true;
                //  updateTextInUI("You've just earned " + amount + " " + currencyName);
                //  showPopupMessage("You've just earned " + amount + " " + currencyName);
            }
        });
//        // Setup listener for Tapjoy video callbacks
//        Tapjoy.setVideoListener(new TJVideoListener() {
//            @Override
//            public void onVideoStart() {
//                Log.i(TAG, "video has started");
//            }
//
//            @Override
//            public void onVideoError(int statusCode) {
//                Log.i(TAG, "there was an error with the video: " + statusCode);
//            }
//
//            @Override
//            public void onVideoComplete() {
//                Log.i(TAG, "video has completed");
//
//                // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
//            //  Tapjoy.getCurrencyBalance(this);
//            }
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("onResume");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        AppEventsLogger.activateApp(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        mTracker.setScreenName("onPause");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        AppEventsLogger.deactivateApp(this);
    }
    //================================================================================
    // TapjoyListener Methods
    //================================================================================
    @Override
    public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
        Log.i(TAG, "currencyName: " + currencyName);
        Log.i(TAG, "balance: " + balance);

        if (earnedCurrency) {
            //    updateTextInUI(displayText + "\n" + currencyName + ": " + balance);
            earnedCurrency = false;
        } else {
            //    updateTextInUI(currencyName + ": " + balance);
        }
        //   setButtonEnabledInUI(getCurrencyBalanceButton, true);
    }

    @Override
    public void onGetCurrencyBalanceResponseFailure(String error) {
        //    updateTextInUI("getCurrencyBalance error: " + error);
        //    setButtonEnabledInUI(getCurrencyBalanceButton, true);
    }

    /*
     * TJPlacement callbacks
     */
    // SDKがTapjoyのサーバーにコンタクトした際に呼ばれます。ただしコンテンツが利用可能になった訳ではありません。
    @Override
    public void onRequestSuccess(TJPlacement placement) {
        Log.d("Tapjoi", "onRequestSuccess");
    }

    // Tapjoyサーバーへコネクトする途中で問題が生じた際に呼ばれます。
    @Override
    public void onRequestFailure(TJPlacement placement, TJError error) {
        Log.d("Tapjoi", "onRequestFailure " + error.message);
    }

    // コンテンツが表示できるようになった時に呼ばれます。
    @Override
    public void onContentReady(TJPlacement placement) {
        Log.d("Tapjoi", "onContentReady");
        placement.showContent();
    }

    // コンテンツが表示される際に呼ばれます。
    @Override
    public void onContentShow(TJPlacement placement) {
        Log.d("Tapjoi", "onContentShow");
//        placement.showContent();
    }

    // コンテンツが退去する際に呼ばれます。
    @Override
    public void onContentDismiss(TJPlacement placement) {
        Log.i("Tapjoi", "Tapjoy direct play content did disappear");
        Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {
            @Override
            public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
                Log.i("Tapjoi", "getCurrencyBalance returned " + currencyName + ":" + balance);
            }

            @Override
            public void onGetCurrencyBalanceResponseFailure(String error) {
                Log.i("Tapjoi", "getCurrencyBalance error: " + error);
            }
        });
        placement = null;
        // Begin preloading the next placement after the previous one is dismissed
//        directPlayPlacement = new TJPlacement(this, "video_unit", this);
//        directPlayPlacement.requestContent();
    }

    @Override
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
        Log.d("Tapjoi", "onPurchaseReques");
    }

    @Override
    public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
        Log.d("Tapjoi", "onRewardRequest");
    }

    /**
     * Handles a failed connect to Tapjoy
     */
    public void onConnectFail() {
        Log.e("Tapjoi", "connect call failed");
    }



    public void onStart() {
        super.onStart();
        Growthbeat.getInstance().start();
        Tapjoy.onActivityStart(this);
        mTracker.setScreenName("onStart");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
        Growthbeat.getInstance().stop();
        mTracker.setScreenName("onStop");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onDestory() {
        super.onDestroy();
        unregisterReceiver(mHandleMessageReceiver);
        accessTokenTracker.stopTracking();
        GrowthAnalytics.getInstance().close();
    }

    public static String getNowDate() {
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    @Override
    public void onClick(View view) {
        if (view == button1) {
            mGrowthHelper.GrowthTrackEvent("button1", getNowDate());

            AdjustHelper.sendEvent(AdjustHelper.BUTTON1);

            // 課金リクエスト開始
            mHelper.launchPurchaseFlow(this, Configs.product01, IabHelper.ITEM_TYPE_INAPP, RC_REQUEST, mPurchaseFinishedListener, null);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Purchase")
                    .setAction("Click")
                    .setLabel("button1")
                    .build());
        } else if (view == button2) {
            mGrowthHelper.GrowthSendTag("purchase", getNowDate());
            mGrowthHelper.GrowPurchase(345, "categorys", "itemID");

            AdjustHelper.sendEvent(AdjustHelper.BUTTON2);

            // Get tracker.
            if (null == mTracker) {
                BaseApplication application = (BaseApplication) getApplication();
                mTracker = application.getDefaultTracker();
            }
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("category")
                    .setAction("Click")
                    .setLabel("button2")
                    .build());
        } else if (view == button3) {
            mGrowthHelper.GrowthTrackEvent("button3", getNowDate());
            AdjustHelper.sendEvent(AdjustHelper.CKICKS);
            // Get tracker.
            if (null == mTracker) {
                BaseApplication application = (BaseApplication) getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.enableAdvertisingIdCollection(true);
            }
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("category")
                    .setAction("Click")
                    .setLabel("button3")
                    .build());
        } else if (view == button4) {
            mGrowthHelper.GrowthTrackEvent("button4", getNowDate());
            AdjustHelper.sendEvent(AdjustHelper.ENDPART);
        } else if (view == button5) {
            mGrowthHelper.GrowthTrackEvent("button5", getNowDate());
        } else if (view == tapJoy1) {
            TJPlacement tapJoy1 = new TJPlacement(this, "StageFailed", this);
            if (Tapjoy.isConnected()) {
                tapJoy1.requestContent();
            } else {
                Log.d("Tapjoi", "Tapjoy SDKはリクエスト前にconnectingを終えなければなりません.");
            }
        } else if (view == tapJoy2) {
            TJPlacement tapJoy2 = new TJPlacement(this, "InsufficientCurrency", this);
            if (Tapjoy.isConnected()) {
                tapJoy2.requestContent();
            } else {
                Log.d("Tapjoi", "Tapjoy SDKはリクエスト前にconnectingを終えなければなりません.");
            }
        } else if (view == tapJoy3) {
            TJPlacementListener placementListener = this;
            TJPlacement tapJoy3 = new TJPlacement(this, "Shows", placementListener);
            if (Tapjoy.isConnected()) {
                tapJoy3.requestContent();
            } else {
                Log.d("Tapjoi", "Tapjoy SDKはリクエスト前にconnectingを終えなければなりません.");
            }
            if (tapJoy3.isContentAvailable()) {
                tapJoy3.showContent();
            }
        } else if (view == mconsumeButton) {
            Log.d(TAG, "消費");
            /** 全消費 */
            mHelper.getAllPurchases();
        }
    }
}