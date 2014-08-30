package emergencycall.intertech.com.emergencycall;

import java.util.*;

/**
 * Created by justin on 30/08/14.
 */
public class Configuration {
    public enum Mode {Alert, Emergency}

    private Mode mode = Mode.Alert;

    //<name, number>
    private final Map<String, String> peopleToCall = new HashMap<String, String>();

    public Map<String, String> getPeopleToCall() {
        return Collections.unmodifiableMap(peopleToCall);
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

}
