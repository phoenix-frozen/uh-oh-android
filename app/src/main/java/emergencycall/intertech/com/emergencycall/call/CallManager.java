package emergencycall.intertech.com.emergencycall.call;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import emergencycall.intertech.com.emergencycall.R;
import emergencycall.intertech.com.emergencycall.ui.MainActivity;
import emergencycall.intertech.com.emergencycall.utils.LogUtils;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class CallManager implements PhoneStateManager.CallListener {

    private static final int CALL_NOTIFICATION_ID = 7;
    public static final String ARG_CANCEL_CALLS = "arg_cancel_calls";

    private Activity mContext;

    private String[] mNumbersToCall;
    private NotificationManager mNotificationManager;
    private TelephonyManager mTelephonyManager;
    private int mTryCounter = 0;
    private boolean mCallInitiated = false;

    public CallManager(Activity context) {
        mContext = context;
        setTelephonyManager();
        setNotificationManager();
    }

    public void reset(String[] phoneNumbers) {
        mNumbersToCall = phoneNumbers;
        mTryCounter = 0;
    }

    public void call() {
        if (isCallingAvailable()) {
           if (isAnotherNumberAvailable()) {
               mCallInitiated = true;
               Intent intent = new Intent(Intent.ACTION_CALL);
               intent.setData(Uri.parse("tel:" + mNumbersToCall[mTryCounter]));
               mContext.startActivity(intent);
               showNotification();
           } else {
               // TODO fake call
               hideNotification();
           }
        }
    }

    public void stop() {
        mCallInitiated = false;
        hideNotification();
    }

    private void hideNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(CALL_NOTIFICATION_ID);
    }

    private void showNotification() {
        int when = (int) System.currentTimeMillis();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(ARG_CANCEL_CALLS, true);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, when, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(android.R.drawable.ic_menu_call)
                        .setContentTitle(mContext.getString(R.string.app_name))
                        .setContentText(mContext.getString(R.string.calling_friends))
                        .addAction(new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel, mContext.getString(R.string.cancel), contentIntent));
        mNotificationManager.notify(CALL_NOTIFICATION_ID, mBuilder.build());
    }

    private void setTelephonyManager() {
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(new PhoneStateManager(mContext, this), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void setNotificationManager() {
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
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
