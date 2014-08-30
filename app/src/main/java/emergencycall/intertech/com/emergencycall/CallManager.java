package emergencycall.intertech.com.emergencycall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class CallManager {

    private Context mContext;

    public CallManager(Context context) {
        mContext = context;
    }

    public void call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + "453535654"));
        mContext.startActivity(intent);
    }

    private void setTelephonyManager() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //telephonyManager.listen(new TelephonyStateListener(getApplicationContext()));
    }

}
