package bhuva.polygonart;

import android.util.Log;

/**
 * Created by bhuva on 7/4/2016.
 */
public final class Utils {
    private static int PriorityMin = 2;
    public static void Log(String s, int priority){
        if(priority > PriorityMin)
            Log.d("Polyart", s);
    }
}
