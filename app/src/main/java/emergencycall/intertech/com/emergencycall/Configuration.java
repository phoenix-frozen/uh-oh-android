package emergencycall.intertech.com.emergencycall;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.*;

/**
 * Created by justin on 30/08/14.
 */
public class Configuration {
    public enum Mode {Alert, Emergency}

    private Mode mode = Mode.Alert;
    private String myNumber = null;

    private Configuration() {}

    public Configuration(Context context) {
        if(context != null) {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(telephony != null)
                myNumber = telephony.getLine1Number();
        }

        if(myNumber == null)
            myNumber = "";
    }

    //<name, number>
    private final Map<String, String> peopleToCall = new HashMap<String, String>();

    public Map<String, String> getPeopleToCall() {
        return peopleToCall; //XXX: we return a reference so that you can just fiddle with it yourself
    }

    public void setPeopleToCall(HashMap<String, String> peopleToCall) {
        this.peopleToCall.clear();
        this.peopleToCall.putAll(peopleToCall);
    }

    public void addPersonToCall(String name, String number) {
        this.peopleToCall.put(name, number);
    }

    public void removePersonToCall(String name) {
        this.peopleToCall.remove(name);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(String myNumber) {
        if(myNumber == null)
            throw new NullPointerException();

        this.myNumber = myNumber;
    }
}
