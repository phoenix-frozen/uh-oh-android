package emergencycall.intertech.com.emergencycall.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class LogUtils {

    private static long sMeasureStartTime;
    private static String sMeasureTag;

    /**
     * Starts measuring time.
     * @param tag Tag that will be used for log message as a class name.
     */
    public static void startMeasure(Object tag) {
        sMeasureStartTime = System.currentTimeMillis();
        sMeasureTag = tag.getClass().getSimpleName();
    }

    /**
     * Logs how much time in ms elapsed since {@link #startMeasure(Object)} was called.
     * @param message Message to be logged before elapsed time.
     */
    public static void stopMeasure(String message) {
        log(sMeasureTag, message + " " + (System.currentTimeMillis() - sMeasureStartTime));
    }

    /**
     * Logs message.
     * @param tag Tag that will be used for log message as a class name.
     * @param message Message to be logged.
     */
    public static void log(Object tag, String message) {
        Log.d(tag.getClass().getSimpleName(), message);
    }

    /**
     * Logs message with Object variable values.
     * @param tag Tag that will be used for log message as a class name.
     * @param message Message to be logged before object values.
     * @param objects Object values to be logged, which each logged in new line with toString representation.
     */
    public static void log(Object tag, String message, Object... objects) {
        StringBuilder logMessageBuilder = new StringBuilder(message + "\n");
        for (int i = 0; i < objects.length; i++) {
            logMessageBuilder.append(objects[i].toString() + "\n");
        }
        log(tag, logMessageBuilder.toString());
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message,
                Toast.LENGTH_LONG).show();
    }

}