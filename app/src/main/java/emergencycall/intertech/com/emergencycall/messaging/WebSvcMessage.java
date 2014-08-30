package emergencycall.intertech.com.emergencycall.messaging;

import android.location.Location;
import org.json.*;
import java.util.Map;

/**
 * Created by justin on 30/08/14.
 */
public class WebSvcMessage extends JSONObject {
    private static JSONArray convertLocation(Location location) throws JSONException {
        JSONArray ar = new JSONArray();

        ar.put(Location.convert(location.getLatitude(), Location.FORMAT_DEGREES));
        ar.put(Location.convert(location.getLongitude(), Location.FORMAT_DEGREES));

        return ar;
    }

    private WebSvcMessage() {}

    public WebSvcMessage(String myName, String myNumber, LocationTransmitter.Mode mode, Location location, Map<String, String> destinations) throws JSONException {
        super();

        JSONObject from = new JSONObject();
        JSONObject to = new JSONObject();

        from.put("name", myName);
        from.put("number", myNumber);

        for(Map.Entry<String, String> entry : destinations.entrySet()) {
            to.put("name", entry.getKey());
            to.put("number", entry.getValue());
        }

        put("from", from);
        put("mode", mode.name().toLowerCase());
        put("uniqID", myNumber);
        put("gpsCoords", convertLocation(location));
        put("numbersToCall", to);
    }
}
