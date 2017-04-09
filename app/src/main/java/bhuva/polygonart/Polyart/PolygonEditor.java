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

    private boolean isPolygonSelected = false;
    private boolean isVertexSelected = false;

    private int selectedPointId = -1; // -1 is ccenter of a polygon. 0 to 'polygon.sides' is the vertex of the polygon.

    private int vertexPointColor = Color.BLUE;
    private int ccenterPointColor = Color.GREEN;

    private int maxTouchRadius = 30;
    private final static int CCENTER = -1;

    PolygonEditor(PointF touchPoint, List<Polygon> polygons){
        isPolygonSelected = false;
        for (int i = polygons.size() - 1; i >= 0; i--) {
            Polygon p = polygons.get(i);
            if (p.contains(touchPoint) && p.isVisible()) {
                selPolygonId = i;
                selPolygon = p;
                isPolygonSelected = true;
                break;
            }
        }
    }
    /*PolygonEditor(int id, List<Polygon> polygons){
        selPolygonId = id;
        selPolygon = polygons.get(selPolygonId);
    }*/

    public void drawEditingPoints(Canvas canvas){
        new SelectionCircle(selPolygon.getCCenter(), ccenterPointColor).draw(canvas);
        for(PointF vertex: selPolygon.getVertices()){
            new SelectionCircle(vertex, vertexPointColor).draw(canvas);
        }
    }

    public void selectVertexBasedOnTouchPoint(PointF touchPoint){
        isVertexSelected = false;
        if(isWithinRadius(selPolygon.getCCenter(), touchPoint, maxTouchRadius)){
            selectedPointId = -1; //ccenter is selected
            isVertexSelected = true;
        }else {
            for (int i = 0; i < selPolygon.getSides(); i++) {
                if (isWithinRadius(selPolygon.getVertex(i), touchPoint, maxTouchRadius)) {
                    selectedPointId = i;
                    isVertexSelected = true;
                    break;
                }
            }
        }
    }

    /*public boolean transformSelPolygonBy(PointF touchPoint){
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
    }*/

    public void transformSelectedPointTo(PointF touchPoint){
        if(selectedPointId == CCENTER ){
            translateSelPolgon(touchPoint);
        }else{
            selPolygon.setVertex(selectedPointId, touchPoint);
        }
    }

    public boolean isPolygonSelected() {
        return isPolygonSelected;
    }

    public void setPolygonSelected(boolean polygonSelected) {
        isPolygonSelected = polygonSelected;
    }

    public boolean isVertexSelected() {
        return isVertexSelected;
    }

    public void setVertexSelected(boolean vertexSelected) {
        isVertexSelected = vertexSelected;
    }

    public void setPolygonColor(int color){
        selPolygon.setColor(color);
    }

    private boolean isWithinRadius(PointF anchor, PointF touchPoint, float radius){
        return (Generic.relDist(anchor, touchPoint) <= radius*radius);
    }

    private void translateSelPolgon(PointF touchPoint){
        float x = touchPoint.x - selPolygon.getCCenter().x;
        float y = touchPoint.y - selPolygon.getCCenter().y;
        PointF translationFactor = new PointF(x,y);
        selPolygon.translate(translationFactor);
    }

    public boolean isSelectedPolygonValid(){
        return isPolygonSelected && selPolygon.isVisible();
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
