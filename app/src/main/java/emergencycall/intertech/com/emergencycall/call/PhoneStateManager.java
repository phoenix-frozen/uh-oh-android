package emergencycall.intertech.com.emergencycall.call;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import emergencycall.intertech.com.emergencycall.utils.LogUtils;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class PhoneStateManager extends PhoneStateListener {

    private Context mContext;
    private CallListener mCallListener;

    public PhoneStateManager(Context context, CallListener callListener) {
        mContext = context;
        mCallListener = callListener;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        LogUtils.log(this, "state=" + state + " incomingNumber=" + incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                // CALL_STATE_IDLE;
                LogUtils.log(this, "CALL_STATE_IDLE");
                mCallListener.onLineAvailable();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // CALL_STATE_OFFHOOK;
                LogUtils.log(this, "CALL_STATE_OFFHOOK");
                mCallListener.onLineNotAvailable();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // CALL_STATE_RINGING
                LogUtils.log(this, "CALL_STATE_RINGING");
                break;
            default:
                break;
        }
    }

    public interface CallListener {

        void onLineNotAvailable();
        void onLineAvailable();
        void onFakeCallFinished();

    }

}
