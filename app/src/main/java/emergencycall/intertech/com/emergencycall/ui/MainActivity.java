package emergencycall.intertech.com.emergencycall.ui;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import emergencycall.intertech.com.emergencycall.R;
import emergencycall.intertech.com.emergencycall.call.*;
import emergencycall.intertech.com.emergencycall.messaging.LocationTransmitter;


public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView mButtonPanic;
    private ImageView mButtonAlert;
    private ImageView mButtonSettings;
    private ImageView mButtonAbout;
    private ImageView mButtonCancelAlert;
    private ImageView mButtonCancelPanic;

    private CallManager mCallManager;
    private LocationTransmitter mLocationTransmitter;

    private String what_my_phone_thinks_my_number_is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mButtonPanic = (ImageView) findViewById(R.id.button_panic);
        mButtonAlert = (ImageView) findViewById(R.id.button_alert);
        mButtonSettings = (ImageView) findViewById(R.id.button_settings);
        mButtonAbout = (ImageView) findViewById(R.id.button_about);
        mButtonCancelAlert = (ImageView) findViewById(R.id.button_cancel_alert);
        mButtonCancelPanic = (ImageView) findViewById(R.id.button_cancel_panic);
        mButtonPanic.setOnClickListener(this);
        mButtonAlert.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonAbout.setOnClickListener(this);
        mButtonCancelAlert.setOnClickListener(this);
        mButtonCancelPanic.setOnClickListener(this);

        mCallManager = new CallManager(this);
        mLocationTransmitter = new LocationTransmitter(getApplicationContext());

        //switch LocationTransmitter into watching mode, to make sure we get a GPS fix
        mLocationTransmitter.startWatching(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            boolean cancel = intent.getExtras().getBoolean(CallManager.ARG_CANCEL_CALLS, false);
            if (cancel) {
                mCallManager.stop();
                mCallManager.stopSimulatedCall();
                restoreButtons();
            }
//            boolean start = intent.getExtras().getBoolean(CallManager.ARG_START_CALLS, false);
//            if (start) {
//                mCallManager.stopSimulatedCall();
//                mCallManager.call();
//            }
        }
    }

    @Override
    public void onClick(View v) {
        //attempt to query phone number
        if(what_my_phone_thinks_my_number_is == null) {
            try {
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                what_my_phone_thinks_my_number_is = tMgr.getLine1Number();
            } catch (Exception e) {
                Log.e("MainActivity", "phone number query failed");
                e.printStackTrace();
            }
        }

        //sync phone's own number with the one in preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String my_number = preferences.getString("my_number", what_my_phone_thinks_my_number_is);
        if(what_my_phone_thinks_my_number_is != null && !what_my_phone_thinks_my_number_is.equals("")) {
            my_number = what_my_phone_thinks_my_number_is;
            SharedPreferences.Editor editor = preferences.edit();
            if(editor != null) {
                editor.putString("my_number", my_number);
                editor.apply();
            }
        }

        String my_name = preferences.getString("my_name", "noname");

        //query the numbers of the three friends we're supposed to call
        TreeMap<String, String> friend_numbers = new TreeMap<String, String>();
        friend_numbers.put(preferences.getString("friend1_name", ""), preferences.getString("friend1_number", ""));
        friend_numbers.put(preferences.getString("friend2_name", ""), preferences.getString("friend2_number", ""));
        friend_numbers.put(preferences.getString("friend3_name", ""), preferences.getString("friend3_number", ""));

        friend_numbers.remove("");
        for(Map.Entry<String, String> entry : new HashSet<Map.Entry<String, String>>(friend_numbers.entrySet())) {
            if(entry.getValue() == null || entry.getValue().equals(""))
                friend_numbers.remove(entry.getKey());
        }

        switch (v.getId()) {
            case R.id.button_panic:
                //send text message
                mLocationTransmitter.transmitLocation(my_name, my_number, LocationTransmitter.Mode.Emergency, friend_numbers);
                //stop simulated call if any

                //make phone call
                mCallManager.reset(new HashSet<String>(friend_numbers.values()).toArray(new String[0]));
                mCallManager.call();
                //replace our button with cancel
                restoreAlertButton();
                togglePanicButton();

                break;

            case R.id.button_alert:
                //send text message
                mLocationTransmitter.transmitLocation(my_name, my_number, LocationTransmitter.Mode.Alert, friend_numbers);
                //stop simulated call if any
                mCallManager.stopSimulatedCall();
                mCallManager.stop();
                //do simulated call
                mCallManager.reset(new HashSet<String>(friend_numbers.values()).toArray(new String[0]));
                mCallManager.simulateCall();
                //replace our button with cancel
                restorePanicButton();
                toggleAlertButton();
                break;

            case R.id.button_settings:
                //activate settings ui
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.button_about:
                //activate about screen
                startActivity(new Intent(this, AboutActivity.class));
                break;

            case R.id.button_cancel_alert:
                //mLocationTransmitter.doWebServiceOkTransmission();
                mCallManager.stopSimulatedCall();
                mCallManager.stop();
                toggleAlertButton();
                break;
            case R.id.button_cancel_panic:
                //mLocationTransmitter.doWebServiceOkTransmission();
                //stop simulated call if any
                mCallManager.stopSimulatedCall();
                mCallManager.stop();
                togglePanicButton();
                break;



        }
    }

    private void toggleAlertButton() {
        if (mButtonAlert.getVisibility() == View.VISIBLE) {
            mButtonAlert.setVisibility(ImageView.GONE);
            mButtonCancelAlert.setVisibility(ImageView.VISIBLE);
        } else {
            restoreAlertButton();
        }
    }

    private void togglePanicButton() {
        if (mButtonPanic.getVisibility() == View.VISIBLE) {
            mButtonPanic.setVisibility(ImageView.GONE);
            mButtonCancelPanic.setVisibility(ImageView.VISIBLE);
        } else {
            restorePanicButton();
        }

    }

    private void restorePanicButton() {
        mButtonPanic.setVisibility(ImageView.VISIBLE);
        mButtonCancelPanic.setVisibility(ImageView.GONE);
    }

    private void restoreAlertButton() {
        mButtonAlert.setVisibility(ImageView.VISIBLE);
        mButtonCancelAlert.setVisibility(ImageView.GONE);
    }

    private void restoreButtons() {
        restoreAlertButton();
        restorePanicButton();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationTransmitter.stopWatching();
    }
}
