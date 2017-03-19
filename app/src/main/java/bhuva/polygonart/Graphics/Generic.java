package bhuva.polygonart.Graphics;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by bhuva on 5/11/2016.
 */
public class Generic {
    private final static String TAG = "Generic";
    public static double dist(PointF a, PointF b){
        return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y, 2));
    }

    public static double dist(Point a, Point b){
        return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y, 2));
    }

    public static double relDist(PointF a, PointF b){
        return Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2);
    }

    public static double relDist(Point a, Point b){
        return Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2);
    }

    public static boolean doVectorsIntersect(PointF a_startPos, PointF a_dir, PointF b_startPos, PointF b_dir){
        //input are vectors
        float u, v;
        PointF ad = a_dir, as = a_startPos, bd = b_dir, bs = b_startPos;
        try{
            float denom =  (ad.x*bd.y - ad.y*bd.x);
            if(denom!=0) {
                u = (as.y * bd.x + bd.y * bs.x - bs.y * bd.x - bd.y * as.x) / denom;
                if(u > 0) {
                    v = (as.x + ad.x * u - bs.x) / bd.x; //??? what if bd.x is 0? is this correct?
                    if (v > 0) {
                        return true;
                    }
                }
            }
        }catch (ArithmeticException ae){
            //divide by zero
            Log.e(TAG+".VecIntersect", ae.getMessage() );
        }
        return false;
    }

}
