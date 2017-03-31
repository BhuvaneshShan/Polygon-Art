package bhuva.polygonart.Polyart;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.Graphics.Vec2D;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 3/18/2017.
 */

public class Polygon {
    private String TAG = "POLYGON";
    protected List<PointF> vertices;
    protected PointF ccenter = new PointF(0.0f, 0.0f);
    protected int sides;

    protected int color = PolyartMgr.getCurColor();

    protected float brushSize = 1; //brush size used to create this
    protected boolean visible = true;

    public Polygon(int sides_count){
        if(sides_count > 2) {
            sides = sides_count;
            ccenter = new PointF(0.0f, 0.0f);
            vertices = getVerticesOnUnitCircle(sides);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public Polygon(int sides_count, PointF position, int size){
        if(sides_count > 2) {
            sides = sides_count;
            ccenter = new PointF(0.0f, 0.0f);
            vertices = getVerticesOnUnitCircle(sides);
            scale(size);
            translate(position);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public Polygon(int sides_count, PointF position, int size, boolean randomized){
        if(sides_count > 2) {
            sides = sides_count;
            ccenter = new PointF(0.0f, 0.0f);
            if(randomized){
                vertices = getVerticesOnUnitCircleRotated(sides, (float)((new Random().nextFloat())*(Math.PI/2.0f)));
            }else{
                vertices = getVerticesOnUnitCircle(sides);
            }
            scale(size);
            translate(position);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public Polygon(int sides_count, PointF position, int size, int colorValue, boolean randomized){
        if(sides_count > 2) {
            sides = sides_count;
            ccenter = new PointF(0.0f, 0.0f);
            if(randomized){
                vertices = getVerticesOnUnitCircleRotated(sides, (float)((new Random().nextFloat())*(Math.PI/2.0f)));
            }else{
                vertices = getVerticesOnUnitCircle(sides);
            }
            color = colorValue;
            scale(size);
            translate(position);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public Polygon(Polygon poly){
        vertices = poly.getVertices();
        ccenter = poly.getCCenter();
        sides = poly.getSides();
        color = poly.getColor();
        brushSize = poly.getBrushSize();
    }

    public static Polygon generateNeighbor(Polygon original, Vec2D axis){
        int originalSides = original.getSides();
        List<PointF> originaVerts = original.getVertices();
        List<PointF> axisPoints = new ArrayList<>(2);
        axisPoints.add(new PointF()); axisPoints.add(new PointF());
        float maxDotProd = Float.MIN_VALUE;
        Vec2D axisNorm = new Vec2D(axis);
        axisNorm.norm();
        for(int i=0; i<original.getSides(); i++){
            float iv =  originaVerts.get((i+1)%originalSides).x - originaVerts.get(i).x;
            float jv =  originaVerts.get((i+1)%originalSides).y - originaVerts.get(i).y;
            Vec2D sideVector = new Vec2D(iv, jv);
            sideVector.norm();
            float dotProd = Vec2D.dotProduct( axisNorm ,sideVector);
            if(dotProd > maxDotProd){
                //found the vertices corresponding to axis
                maxDotProd = dotProd;
                axisPoints.set(0,originaVerts.get(i));
                axisPoints.set(1,originaVerts.get((i+1)%originalSides));
            }
        }

        Polygon neighbor = new Polygon(original);

        PointF midAxisPoint = new PointF((axisPoints.get(0).x+axisPoints.get(1).x)/2.0f,
                (axisPoints.get(0).y+axisPoints.get(1).y)/2.0f);
        Vec2D ppAxis = new Vec2D(original.getCCenter(), midAxisPoint);
        float dist = (float) ppAxis.magnitude();
        ppAxis.norm();
        neighbor.setCcenter(ppAxis.translatePointBy(original.getCCenter(),2*dist));

        /*
        List<PointF> newVerts = original.getVertices();
        for(int i=0; i<originalSides; i++){
            Vec2D MidAxisPointToVert = new Vec2D(midAxisPoint, originaVerts.get(i));
            float distFromAxis =  Vec2D.dotProduct(MidAxisPointToVert, ppAxis);
            newVerts.set(i, ppAxis.translatePointBy(originaVerts.get(i), distFromAxis) );
        }
        neighbor.setVertices(newVerts);
        return neighbor;*/
        /*
        List<PointF> verts = original.getVertices();
        for(int i=0; i<originalSides; i++){
            Vec2D midAxisPointToVert = new Vec2D(midAxisPoint, originaVerts.get(i));
            Vec2D midAxisPointToAxisPoint;
            if( Generic.relDist(originaVerts.get(i),axisPoints.get(0)) < Generic.relDist(originaVerts.get(i), axisPoints.get(1))) {
                midAxisPointToAxisPoint = new Vec2D(midAxisPoint, axisPoints.get(0));
            }else{
                midAxisPointToAxisPoint = new Vec2D(midAxisPoint, axisPoints.get(1));
            }
            float theta = (float) Math.acos( Vec2D.dotProduct(midAxisPointToVert,midAxisPointToAxisPoint)/(midAxisPointToAxisPoint.magnitude()*midAxisPointToVert.magnitude()) );
            float distance = (float)( midAxisPointToVert.magnitude() * Math.sin(theta));
            verts.set(i, ppAxis.translatePointBy(originaVerts.get(i), 2*distance));
        }
        neighbor.setVertices(verts);
        return neighbor;
        */
        float param = (axisPoints.get(0).x - neighbor.getCCenter().x)/neighbor.getBrushSize();
        float theta = (float) Math.acos( param );
        Polygon temp = new Polygon(neighbor.getSides());
        temp.setVertices( getVerticesOnUnitCircleRotated(neighbor.getSides(), theta));
        temp.translate(neighbor.getCCenter());
        temp.scale(neighbor.getBrushSize());
        List<PointF> newVerts = temp.getVertices();
        //newVerts.set(0, axisPoints.get(0));
        //newVerts.set(newVerts.size()-1, axisPoints.get(1));
        neighbor.setVertices( newVerts );
        //Polygon p = new Polygon(originalSides, neighbor.getCCenter(), 50);
        //return p;
        //p.vertices = getVerticesOnUnitCircleRotated()
        return neighbor;
    }

    public static Polygon generateNeighbor(Polygon given, List<PointF> twoPoints){
        PointF mid = new PointF((twoPoints.get(0).x+twoPoints.get(1).x)/2.0f, (twoPoints.get(0).y+twoPoints.get(1).y)/2.0f);
        Vec2D baseSide = new Vec2D(twoPoints.get(0),twoPoints.get(1));
        //finding cc
        Vec2D ccMid = new Vec2D(given.getCCenter(), mid);
        ccMid.norm();
        float dist = (float)Generic.dist(given.getCCenter(), mid);
        PointF cc = ccMid.translatePointBy(given.getCCenter(), 2 * dist);
        List<PointF> newVerts = new ArrayList<>(given.getSides());
        /*
        //compliment mirror
        for(int i=0; i<given.getSides(); i++){
            Vec2D vertxMid = new Vec2D(given.getVertex(i), mid);
            vertxMid.norm();
            dist = (float) Generic.dist(given.getVertex(i), mid );
            PointF newVert = vertxMid.translatePointBy(given.getVertex(i), 2 * dist);
            newVerts.add(newVert);
        }
        */

        //moving along normal
        for(int i=0; i<given.getSides(); i++){
            if(given.getVertex(i).equals(twoPoints.get(0)) || given.getVertex(i).equals(twoPoints.get(1))){
                newVerts.add(given.getVertex(i));
            }else{
                Vec2D vertxMid = new Vec2D(given.getVertex(i), mid);
                dist = Vec2D.dotProduct(vertxMid, ccMid);
                PointF newVert = ccMid.translatePointBy(given.getVertex(i), 2*dist);
                newVerts.add(newVert);
            }
        }

        Polygon generated = new Polygon(given);
        generated.setCcenter(cc);
        generated.setVertices(newVerts);
        generated.setColor(Utils.getAVariation(generated.getColor()));
        return generated;
    }

    public static List<PointF> getVerticesOnUnitCircle(int count){
        List<PointF> verts = new ArrayList<>(count);
        for(int i=0; i<count; i++){
            float x = (float) Math.cos(2 * Math.PI * i/(float)count);
            float y = (float) Math.sin(2 * Math.PI * i/(float)count);
            verts.add(new PointF(x,y));
        }
        return verts;
    }

    public static List<PointF> getVerticesOnUnitCircleRotated(int count, float theta){
        List<PointF> verts = new ArrayList<>(count);
        for(int i=0; i<count; i++){
            float x = (float) Math.cos((2 * Math.PI * i/(float)count) + theta);
            float y = (float) Math.sin((2 * Math.PI * i/(float)count) + theta);
            verts.add(new PointF(x,y));
        }
        return verts;
    }

    public void setColor(int col){
        color = col;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void translate(PointF translationFactor){
        ccenter.x += translationFactor.x;
        ccenter.y += translationFactor.y;
        for(int i=0 ; i<sides; i++){
            vertices.get(i).x += translationFactor.x;
            vertices.get(i).y += translationFactor.y;
        }
    }

    public void scale(float value){
        brushSize = value;
        PointF ccenterCopy = new PointF(ccenter.x, ccenter.y);
        moveToOrigin();
        for(int i=0; i<sides; i++){
            vertices.get(i).x *= value;
            vertices.get(i).y *= value;
        }
        translate(ccenterCopy);
    }

    public List<PointF> getVerticesOfNearestSide(PointF p){
        List<PointF> verts = new ArrayList<PointF>(2);
        verts.add(new PointF()); verts.add(new PointF());
        float distMin = Float.MAX_VALUE;
        for(int i=0; i<sides; i++){
            PointF mid = new PointF((vertices.get(i).x+vertices.get((i+1)%sides).x)/2.0f, (vertices.get(i).y+vertices.get((i+1)%sides).y)/2.0f);
            float dist = (float) Generic.relDist(mid, p);
            if(dist < distMin){
                distMin = dist;
                verts.set(0,vertices.get(i));
                verts.set(1,vertices.get((i+1)%sides));
            }
        }
        return verts;
    }

    public boolean contains(PointF point){
        boolean inside = false;
        //first fast check if inside circum circle
        inside = Generic.relDist(point, ccenter) < Math.pow( Generic.dist(ccenter, vertices.get(0)), 2);
        if(!inside) {
            Utils.Log("false circle check",5);
            return false;
        }else{
            //point in polygon test by ray casting
            List<PointF> sideVectors = new ArrayList<PointF>(sides);
            for(int i=0; i<sides; i++){
                float iv =  vertices.get((i+1)%sides).x - vertices.get(i).x;
                float jv =  vertices.get((i+1)%sides).y - vertices.get(i).y;
                sideVectors.add( new PointF(iv, jv));
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
                Utils.Log("true 1",3);
                return true;
            }else{
                intersections = getIntersectionsWithDiffVectorStartPos(point, sideVectors);
                if((intersections & 1) == 1) {
                    Utils.Log("true 2", 3);
                    return true;
                }
                Utils.Log("false intersection check",5);
                return false;
            }
        }
    }

    private void calibrateCCenterBasedOnVertices(){
        float x = 0, y = 0;
        for(PointF vertex: vertices){
            x += vertex.x;
            y += vertex.y;
        }
        x /= vertices.size();
        y /= vertices.size();
        ccenter.x = x;
        ccenter.y = y;
    }

    private int getIntersectionsWithDiffVectorStartPos(PointF point, List<PointF> sideVectors){
        int intersections = 0;
        for(int i=0; i<sides; i++){
            if(Generic.doVectorsIntersect(new PointF(200,0), point, vertices.get(i), sideVectors.get(i))){
                intersections++;
            }
        }
        return intersections;
    }

    public Path draw(Path path){
        path.moveTo(vertices.get(0).x, vertices.get(0).y);
        for(int i=1;i<sides;i++) {
            path.lineTo(vertices.get(i).x, vertices.get(i).y);
        }
        return path;
    }

    public float distToCCenter(PointF p){
        return (float)Math.sqrt(Math.pow(ccenter.x - p.x, 2) + Math.pow(ccenter.y - p.y, 2));
    }

    public PointF getCCenter() {
        return new PointF(ccenter.x, ccenter.y);
    }

    public int getSides() {
        return sides;
    }

    public int getColor() {
        return color;
    }

    public float getBrushSize() {
        return brushSize;
    }

    public List<PointF> getVertices(){
        return new ArrayList<PointF>(vertices);
    }

    public PointF getVertex(int id){ return new PointF(vertices.get(id).x, vertices.get(id).y); }

    public void setVertices(List<PointF> vertices) {
        if(vertices.size() == sides)
            this.vertices = new ArrayList<PointF>(vertices);
        else
            throw new IllegalArgumentException();
    }

    public void setVertex(int index, PointF point){
        if(index < vertices.size()){
            vertices.set(index, new PointF(point.x, point.y));
            calibrateCCenterBasedOnVertices();
        }else{
            throw new IllegalArgumentException();
        }
    }

    public void setCcenter(PointF ccenter) {
        this.ccenter = new PointF(ccenter.x, ccenter.y);
    }

    private void moveToOrigin(){
        for(int i=0 ; i<sides; i++){
            vertices.get(i).x -= ccenter.x;
            vertices.get(i).y -= ccenter.y;
        }
        ccenter.x = 0;
        ccenter.y = 0;
    }
}
