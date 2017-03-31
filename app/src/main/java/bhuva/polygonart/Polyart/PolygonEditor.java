package bhuva.polygonart.Polyart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.UI.SelectionCircle;

/**
 * Created by bhuva on 3/30/2017.
 */

public class PolygonEditor {
    private Polygon selPolygon; //selected polygon
    private int selPolygonId;

    private int vertexPointColor = Color.BLUE;
    private int ccenterPointColor = Color.GREEN;

    private int maxTouchRadius = 20;

    PolygonEditor(int id, List<Polygon> polygons){
        selPolygonId = id;
        selPolygon = polygons.get(selPolygonId);
    }

    public void drawEditingPoints(Canvas canvas){
        new SelectionCircle(selPolygon.getCCenter(), ccenterPointColor).draw(canvas);
        for(PointF vertex: selPolygon.getVertices()){
            new SelectionCircle(vertex, vertexPointColor).draw(canvas);
        }
    }

    public boolean transformSelPolygonBy(PointF touchPoint){
        if(isWithinRadius(selPolygon.getCCenter(), touchPoint, maxTouchRadius)){
            //if ccenter move the polygon
            translateSelPolgon(touchPoint);
            return true;
        }
        for(int i=0; i<selPolygon.getSides(); i++){
            if(isWithinRadius(selPolygon.getVertex(i), touchPoint, maxTouchRadius)) {
                //move the vertex
                selPolygon.setVertex(i, touchPoint);
                return true;
            }
        }
        return false;
    }

    public void setColor(int color){
        selPolygon.setColor(color);
    }

    public boolean isWithinRadius(PointF anchor, PointF touchPoint, float radius){
        return (Generic.relDist(anchor, touchPoint) <= radius*radius);
    }

    private void translateSelPolgon(PointF touchPoint){
        float x = touchPoint.x - selPolygon.getCCenter().x;
        float y = touchPoint.y - selPolygon.getCCenter().y;
        PointF translationFactor = new PointF(x,y);
        selPolygon.translate(translationFactor);
    }

    public boolean isSelPolygonValid(){
        return selPolygon.isVisible();
    }

    /*
    * to be called only if selPolygon has been instantiated

    private void constructEditPoints(){
        editPoints.add(new SelectionCircle(selPolygon.getCCenter(), ccenterPointColor));
        for(int i=0; i<selPolygon.getSides(); i++) {
            editPoints.add(new SelectionCircle(selPolygon.getVertex(i), vertexPointColor));
        }
    }*/
}
