package bhuva.polygonart.Polyart;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import bhuva.polygonart.Graphics.Generic;

/**
 * Created by bhuva on 3/18/2017.
 */

public class Polygon {
    private String TAG = "POLYGON";
    private List<PointF> vertices;
    private PointF ccenter = new PointF(0.0f, 0.0f);
    private int sides;

    private int color = Color.RED;

    private PointF translatedBy = new PointF(0.0f,0.0f);
    private float scaledBy = 1;

    public Polygon(int sides_count){
        if(sides_count > 2) {
            sides = sides_count;
            ccenter = new PointF(0.0f, 0.0f);
            vertices = getVerticesOnUnitCircle(sides);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public static List<PointF> getVerticesOnUnitCircle(int count){
        List<PointF> verts = new ArrayList<>(count);
        for(int i=0; i<count; i++){
            verts.get(i).x = (float) Math.cos(2 * Math.PI * i/(float)count);
            verts.get(i).y = (float) Math.sin(2 * Math.PI * i/(float)count);
        }
        return verts;
    }

    public void setColor(int col){
        color = col;
    }

    public void translate(PointF translationValues){
        translatedBy = translationValues;
        translateCCenter();
        for(int i=0 ; i<sides; i++){
            vertices.get(i).x += translatedBy.x;
            vertices.get(i).y += translatedBy.y;
        }
    }

    public void scale(int value){
        moveToOrigin();
        scaledBy = value;
        for(int i=0; i<sides; i++){
            vertices.get(i).x *= value;
            vertices.get(i).y *= value;
        }
        translate(translatedBy);
    }

    public List<PointF> getNearestSide(PointF p){
        List<PointF> verts = new ArrayList<PointF>(2);
        float distMin = Float.MAX_VALUE;
        for(int i=0; i<sides; i++){
            float distA = (float) Generic.relDist(p,vertices.get(i));
            float distB = (float) Generic.relDist(p, vertices.get((i+1)%sides));
            if(distA+distB < distMin){
                verts.set(0,vertices.get(i));
                verts.set(1,vertices.get((i+1)%sides));
            }
        }
        return verts;
    }

    public boolean contains(PointF point){
        boolean inside = false;
        //first fast check if inside circum circle
        inside = Generic.relDist(point, ccenter) < Math.pow(scaledBy, 2);
        if(!inside) {
            return false;
        }else{
            //point in polygon test by ray casting
            List<PointF> sideVectors = new ArrayList<PointF>(sides);
            for(int i=0; i<sides; i++){
                float iv =  vertices.get((i+1)%sides).x - vertices.get(i).x;
                float jv =  vertices.get((i+1)%sides).y - vertices.get(i).y;
                sideVectors.set(i, new PointF(iv, jv));
            }
            //checking ray intersection with sides;
            int intersections = 0;
            for(int i=0; i<sides; i++){
                if(Generic.doVectorsIntersect(new PointF(0,0), point, vertices.get(i), sideVectors.get(i))){
                    intersections++;
                }
            }
            if((intersections & 1) == 1){
                //even intersections so inside polygon
                return true;
            }else{
                return false;
            }
        }
    }

    private void moveToOrigin(){
        ccenter.x -= translatedBy.x;
        ccenter.y -= translatedBy.y;
        for(int i=0 ; i<sides; i++){
            vertices.get(i).x -= translatedBy.x;
            vertices.get(i).y -= translatedBy.y;
        }
    }

    private void translateCCenter(){
        ccenter.x += translatedBy.x;
        ccenter.y += translatedBy.y;
    }
}
