package bhuva.polygonart;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

import static android.graphics.Color.*;

/**
 * Created by bhuva on 7/4/2016.
 */
public final class Utils {
    private static int PriorityMin = 2;
    private static Random random = new Random();

    public static final int COLOR_DIALOG_POLYGON_SELECTOR_ID = 1;
    public static final int COLOR_DIALOG_BACKGROUND_SELECTOR_ID = 2;

    public static final int INTENT_RESULT_SELECT_REF_IMG = 1;

    public static void Log(String s, int priority){
        if(priority > PriorityMin)
            Log.d("Polyart", s);
    }

    public static int getAVariation(int colorVal){
        int r = red(colorVal);
        int g = green(colorVal);
        int b = blue(colorVal);
        float[] hsv = new float[3] ;
        RGBToHSV(r,g,b,hsv);
        hsv[1] += ((random.nextFloat()*2)-1)*0.15;
        hsv[1] = hsv[1]>1?1:hsv[1];
        hsv[1] = hsv[1]<0?0:hsv[1];
        hsv[2] += ((random.nextFloat()*2)-1)*0.15;
        hsv[2] = hsv[2]>1?1:hsv[2];
        hsv[2] = hsv[2]<0?0:hsv[2];
        return HSVToColor(hsv);
    }

    public static int getColorFromHex(String hexColor){
        return Color.parseColor(hexColor);
    }
}
