package grow.push.test.kin.com.mypushapp.BillingUtil;

import android.view.View;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;

/**
 * Created by maki on 2016/02/20.
 * MyApps.
 */
public class TapjoyHelper implements TJGetCurrencyBalanceListener, TJPlacementListener {
    @Override
    public void onGetCurrencyBalanceResponse(String s, int i) {

    }

    @Override
    public void onGetCurrencyBalanceResponseFailure(String s) {

    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {

    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {

    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }
}
