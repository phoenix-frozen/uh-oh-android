package emergencycall.intertech.com.emergencycall.messaging;

import android.location.*;
import android.os.*;
import android.content.*;
//import android.telephony.*; //for when I do SMS
import org.json.JSONException;

import emergencycall.intertech.com.emergencycall.Configuration;

/**
 * This class finds your location, and then sends it to someone.
 * It does so by sending a message through a web service, and also via SMS.
 *
 * TODO: Permissions
 * coarse location
 * fine location
 * internet
 *
 * Created by justin on 30/08/14.
 */
public class LocationTransmitter implements LocationListener {
    private Location location;
    private LocationManager locationManager;
    private boolean watching = false;
    private Criteria locationCriteria = new Criteria();

    private Configuration config = null;

    public LocationTransmitter(Context context) {
        this(context, null);
    }

    public LocationTransmitter(Context context, Configuration config) {
        //configuration object
        this.config = config;

        //get a reference to the location manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        //set up our location query criteria
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        //fill location with an approximate location to get started
        //not that not using the global criteria object is *deliberate*
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

        //request a fine location update for whenever it's available
        locationManager.requestSingleUpdate(locationCriteria, this, null); //TODO: is having a NULL looper a terrible idea?
    }

    @Override
    public void onLocationChanged(Location location) {
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
        locationManager.requestLocationUpdates(0 /* no minimum time */, 0 /* no minimum distance */, locationCriteria, this, looper);
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

    public void transmitLocation() {
        transmitLocation(config.getMyNumber(), config.getMode(), config.getPeopleToCall().values().toArray(new String[0]));
    }

    public void transmitLocation(String myNumber, Configuration.Mode mode, String[] destinations) {
        transmitLocation(myNumber, mode, location, destinations);
    }

    /**
     * Transmit a location to where it's supposed to go.
     *
     * @param location Location to transmit. Must not be null.
     */
    public void transmitLocation(String myNumber, Configuration.Mode mode, Location location, String[] destinations) {
        if(location == null || mode == null || destinations == null) {
            throw new NullPointerException();
        }
        if(destinations.length == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        //Generate JSON object
        WebSvcMessage webMessage;
        try {
            webMessage = new WebSvcMessage(myNumber, mode, location, destinations);
        } catch (JSONException e) {
            //well THAT shouldn't have happened...
            throw new RuntimeException(e);
        }

        //TODO: tx message

        /*TODO:
         * Phase2:
         * Generate SMS message
         * Tx SMS to configured recipients
         */
    }
}
