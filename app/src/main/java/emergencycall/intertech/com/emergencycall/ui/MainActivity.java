package emergencycall.intertech.com.emergencycall.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.HashSet;

import emergencycall.intertech.com.emergencycall.CallManager;
import emergencycall.intertech.com.emergencycall.R;
import emergencycall.intertech.com.emergencycall.messaging.LocationTransmitter;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button mButtonPanic;
    private Button mButtonAlert;
    private Button mButtonSettings;

    private CallManager mCallManager;
    private LocationTransmitter mLocationTransmitter;

    private String what_my_phone_thinks_my_number_is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mButtonPanic = (Button) findViewById(R.id.button_panic);
        mButtonAlert = (Button) findViewById(R.id.button_alert);
        mButtonSettings = (Button) findViewById(R.id.button_settings);
        mButtonPanic.setOnClickListener(this);
        mButtonAlert.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);

        mCallManager = new CallManager(this);
        mLocationTransmitter = new LocationTransmitter(getApplicationContext());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean cancel = intent.getExtras().getBoolean(CallManager.ARG_CANCEL_CALLS, false);
            if (cancel) {
                mCallManager.stop();
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
            preferences.edit().putString("my_number", my_number);
        }

        //query the numbers of the three friends we're supposed to call
        HashSet<String> friend_number_set = new HashSet<String>();
        friend_number_set.add(preferences.getString("friend1_number", ""));
        friend_number_set.add(preferences.getString("friend2_number", ""));
        friend_number_set.add(preferences.getString("friend3_number", ""));

        friend_number_set.remove(null);
        friend_number_set.remove("");

        String[] friend_numbers = new String[friend_number_set.size()];
        friend_numbers = friend_number_set.toArray(friend_numbers);

        switch (v.getId()) {
            case R.id.button_panic:
                //switch LocationTransmitter into watching mode
                mLocationTransmitter.startWatching(null);
                //send text message
                mLocationTransmitter.transmitLocation(my_number, LocationTransmitter.Mode.Emergency, friend_numbers);
                //make phone call
                mCallManager.reset(friend_numbers);
                mCallManager.call();
                //TODO: send text messages every few seconds?
                break;

            case R.id.button_alert:
                //switch LocationTransmitter into watching mode to make sure it's up to date
                mLocationTransmitter.startWatching(null);
                //send text message
                mLocationTransmitter.transmitLocation(my_number, LocationTransmitter.Mode.Alert, friend_numbers);
                //TODO: do simulated call
                break;

            case R.id.button_settings:
                //activate settings ui
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationTransmitter.stopWatching();
    }
}
