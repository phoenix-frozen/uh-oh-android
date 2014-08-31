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
    private Button mButtonSettings;
    private ImageView mButtonAbout;

    private CallManager mCallManager;
    private LocationTransmitter mLocationTransmitter;

    private String what_my_phone_thinks_my_number_is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mButtonPanic = (ImageView) findViewById(R.id.button_panic);
        mButtonAlert = (ImageView) findViewById(R.id.button_alert);
        mButtonSettings = (Button) findViewById(R.id.button_settings);
        mButtonAbout = (ImageView) findViewById(R.id.button_about);
        mButtonPanic.setOnClickListener(this);
        mButtonAlert.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonAbout.setOnClickListener(this);

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
            }
            boolean start = intent.getExtras().getBoolean(CallManager.ARG_START_CALLS, false);
            if (start) {
                mCallManager.stopSimulatedCall();
                mCallManager.call();
            }
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
                //make phone call
                mCallManager.reset(new HashSet<String>(friend_numbers.values()).toArray(new String[0]));
                mCallManager.call();
                break;

            case R.id.button_alert:
                //send text message
                mLocationTransmitter.transmitLocation(my_name, my_number, LocationTransmitter.Mode.Alert, friend_numbers);
                //do simulated call
                mCallManager.reset(new HashSet<String>(friend_numbers.values()).toArray(new String[0]));
                mCallManager.simulateCall();
                break;

            case R.id.button_settings:
                //activate settings ui
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.button_about:
                //activate about screen
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationTransmitter.stopWatching();
    }
}
