package bhuva.polygonart.Graphics;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by bhuva on 5/11/2016.
 */
public class Generic {
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


}
