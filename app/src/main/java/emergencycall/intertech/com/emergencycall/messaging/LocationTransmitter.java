package emergencycall.intertech.com.emergencycall.messaging;

import android.location.*;
import android.os.*;
import android.content.*;
import android.util.Log;
import android.telephony.*;
import java.io.*;
import java.util.Map;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import emergencycall.intertech.com.emergencycall.R;

/**
 * This class finds your location, and then sends it to someone.
 * It does so by sending a message through a web service, and also via SMS.
 *
 * Created by justin on 30/08/14.
 */
public class LocationTransmitter implements LocationListener {
    private Location location;
    private LocationManager locationManager;
    private SmsManager smsManager;
    private boolean watching = false;
    private Criteria locationCriteria = new Criteria();
    private String url;

    public LocationTransmitter(Context context) {
        //get a reference to the location and telephony managers
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        smsManager = SmsManager.getDefault();

        //set up our location query criteria
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        //fill location with an approximate location to get started
        //not that not using the global criteria object is *deliberate*
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

        //request a fine location update for whenever it's available
        locationManager.requestSingleUpdate(locationCriteria, this, null);

        //look up the web service url
        url = context.getString(R.string.webservice_url);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("LocationTransmitter", "location update received");
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //don't really care...
    }

    @Override
    public void onProviderEnabled(String s) {
        //don't really care...
    }

    @Override
    public void onProviderDisabled(String s) {
        //don't really care...
    }

    /**
     * Put us in 'watching' mode, which turns on continuous location updates.
     *
     * @param looper Thread on which the update methods are called.
     */
    public void startWatching(Looper looper) {
        watching = true;
        locationManager.requestLocationUpdates(0 /* no minimum time */, 0 /* no minimum distance */, locationCriteria, this, looper);
    }

    public void stopWatching() {
        watching = false;
        locationManager.removeUpdates(this);
    }

    /**
     * Query the location of the phone.
     * Also implicitly updates the stored state.
     *
     * @return The location of the phone.
     */
    public Location queryLocation() {
        if(!watching)
            //if we're not in watching mode, deliberately ask for the last known location
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(locationCriteria, true));

        return location;
    }

    public void transmitLocation(String myName, String myNumber, Mode mode, Map<String, String> destinations) {
        transmitLocation(myName, myNumber, mode, location, destinations);
    }

    /**
     * Transmit a location to where it's supposed to go.
     *
     * @param location Location to transmit. Must not be null.
     */
    public void transmitLocation(String myName, String myNumber, Mode mode, Location location, Map<String, String> destinations) {
        if(location == null || mode == null || destinations == null || destinations.size() == 0) {
            return;
        }

        //tx message
        doSmsTransmission(myName, myNumber, mode, location, destinations);
        doWebServiceTransmission(myName, myNumber, mode, location, destinations);
    }

    private void doWebServiceTransmission(String myName, String myNumber, Mode mode, Location location, Map<String, String> destinations) {
        //Generate JSON object
        WebSvcMessage message;
        try {
            message = new WebSvcMessage(myName, myNumber, mode, location, destinations);
        } catch (JSONException e) {
            //well THAT shouldn't have happened...
            throw new RuntimeException(e);
        }
        Log.d("LocationTransmitter", "doing tx: " + message.toString());

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String message = params[0];

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");

                try {
                    httpPost.setEntity(new StringEntity(message));
                    Log.i("LocationTransmitter", "doing web request now...");
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity responsecontent = httpResponse.getEntity();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(responsecontent.getContent()));

                    StringBuilder buf = new StringBuilder();

                    Log.d("LocationTransmitter", "web service's response: ");

                    for(String s = reader.readLine(); s != null; s = reader.readLine()) {
                        Log.d("LocationTransmitter", s);
                        buf.append(s);
                    }

                    Log.d("LocationTransmitter", "web service request complete");

                    JSONObject response;
                    try {
                        response = new JSONObject(buf.toString());
                    } catch (JSONException e) {
                        response = null;
                    }

                    if(response == null || response.has("error"))
                        Log.i("LocationTransmitter", "web service message transmission failed");
                    else
                        Log.i("LocationTransmitter", "web service message transmission succeeded");

                    reader.close();
                    responsecontent.consumeContent();
                } catch (UnsupportedEncodingException e) {
                    Log.e("LocationTransmitter", "Web service post data encoding failed");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("LocationTransmitter", "Web service post failed");
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(message.toString());
    }

    private void doSmsTransmission(String myName, String myNumber, Mode mode, Location location, Map<String, String> destinations) {
        for(Map.Entry<String, String> entry : destinations.entrySet()) {
            String message;

            switch(mode) {
                case Emergency:
                    message = "Hey, it's %s. I'm in serious trouble. Please come help me, or call the police. I'm here: https://www.google.com/maps/@%s,%s17z";
                    break;

                default:
                    message = "Hey, it's %s. I'm in a sketchy area, so I wanted someone to know where I was. I'm here: https://www.google.com/maps/@%s,%s17z";
                    break;
            }

            message = String.format(message, myName, Location.convert(location.getLatitude(), Location.FORMAT_DEGREES), Location.convert(location.getLongitude(), Location.FORMAT_DEGREES));

            smsManager.sendTextMessage(entry.getValue(), null, message, null, null);
        }
    }

    public enum Mode {Alert, Emergency}
}
