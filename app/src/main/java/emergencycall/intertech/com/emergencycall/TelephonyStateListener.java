package emergencycall.intertech.com.emergencycall;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class TelephonyStateListener extends PhoneStateListener {

    private Context mContext;

    public TelephonyStateListener(Context context) {
        mContext = context;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                // CALL_STATE_IDLE;
                Toast.makeText(mContext, "CALL_STATE_IDLE",
                        Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // CALL_STATE_OFFHOOK;
                Toast.makeText(mContext, "CALL_STATE_OFFHOOK",
                        Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // CALL_STATE_RINGING
                Toast.makeText(mContext, incomingNumber,
                        Toast.LENGTH_LONG).show();
                Toast.makeText(mContext, "CALL_STATE_RINGING",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void toastMessage(String message) {

    }

}
