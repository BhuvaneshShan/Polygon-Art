package bhuva.polygonart.Polyart;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.Random;
import java.util.Vector;

import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.Graphics.Vec2D;

/**
 * Created by bhuva on 5/8/2016.
 */
public class Triangle extends Polygon{
    private String TAG = "TRIANGLE";

    public static Triangle randomUnitTriangle(){
        Triangle t = new Triangle();
        t.vertices = t.getVerticesOnUnitCircleRotated(3, (float)((new Random().nextFloat())*(Math.PI/2.0f)));
        return t;
    }

    public Triangle(){
        super(3);
    }

    public String string(){
        String s = "";
        for(int i=0; i<3; i++){
            s += "v"+i+": "+vertices.get(i).x+", "+vertices.get(i).y+"; ";
        }
        s+= "cc: "+ccenter.x+", "+ccenter.y;
        return s;
    }

    public PointF getVertA(){
        return vertices.get(0);
    }
    public PointF getVertB(){
        return vertices.get(1);
    }
    public PointF getVertC(){
        return vertices.get(2);
    }
}
