package emergencycall.intertech.com.emergencycall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import emergencycall.intertech.com.emergencycall.utils.LogUtils;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class CallManager implements PhoneStateManager.CallListener {

    private Activity mContext;

    private String[] mNumbersToCall;
    private TelephonyManager mTelephonyManager;
    private int mTryCounter = 0;
    private long mCallTime = -1;
    private boolean mCallInitiated = false;

    public CallManager(Activity context) {
        mContext = context;
        setTelephonyManager();
    }

    public void reset(String[] phoneNumbers) {
        mNumbersToCall = phoneNumbers;
        mTryCounter = 0;
    }

    public void call() {
        if (isCallingAvailable() && isAnotherNumberAvailable()) {
            mCallInitiated = true;
            mCallTime = new Date().getTime();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mNumbersToCall[mTryCounter]));
            mContext.startActivity(intent);
        }
    }

    private void setTelephonyManager() {
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(new PhoneStateManager(mContext, this), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private boolean isCallingAvailable() {
        return mTelephonyManager != null && mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
    }

    private boolean isAnotherNumberAvailable() {
        return mNumbersToCall != null && mTryCounter < mNumbersToCall.length;
    }

    @Override
    public void onLineNotAvailable() {
        mTryCounter++;
        LogUtils.toastMessage(mContext, mContext.getString(R.string.line_busy));
    }

    @Override
    public void onLineAvailable() {
        if (mCallInitiated) {
            call();
        }
    }

}
