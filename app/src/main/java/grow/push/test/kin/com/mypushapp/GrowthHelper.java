package grow.push.test.kin.com.mypushapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.growthbeat.Growthbeat;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.intenthandler.IntentHandler;
import com.growthbeat.intenthandler.NoopIntentHandler;
import com.growthbeat.intenthandler.UrlIntentHandler;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.model.CustomIntent;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grow.push.test.kin.com.mypushapp.BuildConfig;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class GrowthHelper {
    private final String TAG = "GrowthHelper";
    private final String APPLICATION_ID = "PdpAovLfxNPYSlEj";
    private final String CREDENTIAL_ID = "drkjpiXa2hTtvMxtz4pW0Z0EQrUC6QSs";
    private final String SENDER_ID = "470626985372";

    private Context mContext = null;
    private Activity mActivity = null;

    public GrowthHelper(Activity acticity, Context context) {
        mActivity = acticity;
        mContext = context;

        GrowthSetting();
    }

    private void GrowthSetting() {
        // デバイス登録、認証
        // Growth Push、Growth Analytics、Growth Messageの初期化（Growth Linkは別途初期化が必要）
        // 基本情報の送信
        // public void initialize(Context context, String applicationId, String credentialId) SDK key
        //  Growthbeat.getInstance().initialize(getApplicationContext(), "Pca3W6MVVWJIPNIe", "vMjPslOZCzZItRLsFQkuewkllqZBkeG5");
        Growthbeat.getInstance().initialize(mContext, APPLICATION_ID, CREDENTIAL_ID);

        // GrowthPush.getInstance().requestRegistrationId("YOUR_SENDER_ID", BuildConfig.DEBUG ? Environment.development : Environment.production);
        // プロジェクトモーダル内のプロジェクト番号がGCMに必要な senderIdとなります。
        GrowthPush.getInstance().requestRegistrationId(SENDER_ID, Environment.production);
    //  GrowthPush.getInstance().requestRegistrationId(SENDER_ID, BuildConfig.DEBUG ? Environment.development : Environment.production);
    // GrowthPush.getInstance().registerClient("token", BuildConfig.DEBUG ? Environment.development : Environment.production);
        // Launchイベントの取得
        GrowthPush.getInstance().trackEvent("Launch");
        // DeviceTagの取得
        GrowthPush.getInstance().setDeviceTags();

        // Growthbeatの初期化処理の後に、Growth Linkの初期化処理を追加してください。
        // public void initialize(Context context, String applicationId, String credentialId)
        GrowthLink.getInstance().initialize(mContext, APPLICATION_ID, CREDENTIAL_ID);
        // IntentFilterを設定したActivityのonCreateで、handleOpenUrlメソッドを呼び出してください。
        GrowthLink.getInstance().handleOpenUrl(mActivity.getIntent().getData());
        String ana = GrowthAnalytics.getInstance().getCredentialId();
        Log.d(TAG, "GrowthAnalytics.getInstance().getCredentialId() = " + ana);

        GrowthAnalytics.getInstance().setBasicTags();
        GrowthAnalytics.getInstance().open();
        // ディープリンクアクションの実装
        List<IntentHandler> intentHandlers = new ArrayList<IntentHandler>();
        intentHandlers.add(new UrlIntentHandler(GrowthbeatCore.getInstance().getContext()));
        intentHandlers.add(new NoopIntentHandler());
        intentHandlers.add(new IntentHandler() {
            public boolean handle(com.growthbeat.model.Intent intent) {
                if (intent.getType() != com.growthbeat.model.Intent.Type.custom)
                    return false;
                Map<String, String> extra = ((CustomIntent) intent).getExtra();
                // TODO ここにアプリ内の画面を開く処理を実装します。
                Log.d(TAG, "Link extra: " + extra);
                return true;
            }
        });
        GrowthbeatCore.getInstance().setIntentHandlers(intentHandlers);
    }

    public void  GrowthTrackEvent(String key, String value) {
        Log.d(TAG, "GrowthTrackEvent key = " + key + " values = " + value);
        GrowthPush.getInstance().trackEvent(key, value);
        String ids = GrowthLink.getInstance().getCredentialId();
        Log.d(TAG, "GrowthLink.getInstance().getCredentialId() = " + ids);
    }

    public void GrowthSendTag(String key, String value) {
        Log.d(TAG, "GrowthSendTag key = " + key + " values = " + value);
        GrowthPush.getInstance().setTag(key, value);
    }

    public void GrowPurchase(int price, String key, String value) {
        Log.d(TAG, "GrowPurchase price = " + price + " key = " + key + " values = " + value);
        GrowthAnalytics.getInstance().purchase(price, key, value);
    }
}
