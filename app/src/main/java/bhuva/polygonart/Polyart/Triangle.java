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
        /*
        PointF a = new PointF(0, 1);
        PointF b = new PointF(-0.75f, -0.75f);
        PointF c = new PointF(0.75f, -0.75f);
        return new Triangle(a,b,c);
        */
        Triangle t = new Triangle();
        t.vertices = t.getVerticesOnUnitCircleRotated(3, (float)((new Random().nextFloat())*(Math.PI/2.0f)));
        return t;
    }

    public Triangle(){
        super(3);
    }
    /*
    public Triangle(PointF a, PointF b, PointF c){
        super(3);
        vertices.set(0, a);
        vertices.set(1, b);
        vertices.set(2, c);
        computeCircumcenter();
    }

    public Triangle(PointF a,PointF b, PointF c, PointF cc){
        super(3);
        vertices = new PointF[3];
        vertices[0] = a;
        vertices[1] = b;
        vertices[2] = c;
        circumcenter = new PointF(0,0);
        circumcenter.x = cc.x;
        circumcenter.y = cc.y;
    }
    */

    public String string(){
        String s = "";
        for(int i=0; i<3; i++){
            s += "v"+i+": "+vertices.get(i).x+", "+vertices.get(i).y+"; ";
        }
        s+= "cc: "+ccenter.x+", "+ccenter.y;
        return s;
    }

    /*
    public void translate(float x, float y){
        for(int i=0;i<3; i++){
            vertices[i].x += x;
            vertices[i].y += y;
        }
        computeCircumcenter();
    }
    public void scale(int s){
        for(int i=0;i<3; i++){
            vertices[i].x *= s;
            vertices[i].y *= s;
        }
        computeCircumcenter();
    }
    public Vector<PointF> getVerticesOfNearestSide(PointF p){
        Vector<PointF> verts = new Vector<PointF>();
        int ver1=0,ver2=1;
        //Vec2D cp = new Vec2D(circumcenter, p);
        //cp.norm();
        //float maxDotProd = 0;
        double minDist = 9999999;
        for(int i=0; i<3; i++){
            PointF mid = new PointF((vertices[i].x + vertices[(i+1)%3].x)/2,(vertices[i].y + vertices[(i+1)%3].y)/2);
            //Vec2D cMid = new Vec2D(circumcenter, mid);
            //cMid.norm();
            //float dotProd = Vec2D.dotProduct(cMid,cp);
            double dist = Generic.relDist(mid, p);
            //if(dotProd > maxDotProd){
            if(dist < minDist) {
                ver1 = i;
                ver2 = (i+1)%3;
                //maxDotProd = dotProd;
                minDist = dist;
            }
        }
        verts.add(vertices[ver1]);
        verts.add(vertices[ver2]);
        verts.add(vertices[(ver2 + 1) % 3]); //farthest vertex
        return verts;
    }
    public boolean contains(PointF p){
        float denom = ((vertices[1].y - vertices[2].y)*(vertices[0].x - vertices[2].x) +
                (vertices[2].x - vertices[1].x)*(vertices[0].y - vertices[2].y));
        float a = ((vertices[1].y - vertices[2].y)*(p.x - vertices[2].x) +
                (vertices[2].x - vertices[1].x)*(p.y - vertices[2].y))/denom;
        float b = ((vertices[2].y - vertices[0].y)*(p.x - vertices[2].x) +
                (vertices[0].x - vertices[2].x)*(p.y - vertices[2].y))/denom;
        float c = 1 - a - b;
        return 0 <= a && a <= 1 && 0 <= b && b <= 1 && 0 <= c && c <= 1;

    }
    */

    public PointF getVertA(){
        return vertices.get(0);
    }
    public PointF getVertB(){
        return vertices.get(1);
    }
    public PointF getVertC(){
        return vertices.get(2);
    }

    /*
    private void computeCircumcenter(){
        float slopeAB = (vertices[1].y-vertices[0].y)/(vertices[1].x-vertices[0].x);
        float slopeAC = (vertices[2].y-vertices[0].y)/(vertices[2].x-vertices[0].x);
        float slopeBC = (vertices[2].y-vertices[1].y)/(vertices[2].x-vertices[1].x);

        float m1 = 1; //slope AB
        float m2 = 1; //slope AC
        PointF p1 = new PointF();
        PointF p2 = new PointF();

        if(slopeAB != Float.NaN && slopeAB!=0 && slopeAC != Float.NaN && slopeAC!=0 ){
            m1 = slopeAB; m2 = slopeAC;
            p1 = new PointF( (vertices[1].x+vertices[0].x)/2 ,(vertices[1].y+vertices[0].y)/2 ); //mid point on AB
            p2 = new PointF( (vertices[2].x+vertices[0].x)/2 ,(vertices[2].y+vertices[0].y)/2 ); //mid point on AC
            Log.d(TAG,"cc using ab ac");
        }else if(slopeAC != Float.NaN && slopeAC!=0 && slopeBC != Float.NaN && slopeBC!=0 ){
            m1 = slopeAC; m2 = slopeBC;
            p1 = new PointF( (vertices[2].x+vertices[0].x)/2 ,(vertices[2].y+vertices[0].y)/2 ); //mid point on AC
            p2 = new PointF( (vertices[2].x+vertices[1].x)/2 ,(vertices[2].y+vertices[1].y)/2 ); //mid point on BC
            Log.d(TAG,"cc using ac bc");
        }else{
            m1 = slopeAB; m2 = slopeBC;
            p1 = new PointF( (vertices[1].x+vertices[0].x)/2 ,(vertices[1].y+vertices[0].y)/2 ); //mid point on AB
            p2 = new PointF( (vertices[2].x+vertices[1].x)/2 ,(vertices[2].y+vertices[1].y)/2 ); //mid point on BC
            Log.d(TAG,"cc using ab bc");
        }
        Log.d(TAG,"slopes:"+m1+", "+m2);
        m1 = -1.0f/m1; //perpendicular slope of AB
        m2 = -1.0f/m2; //perpendicular slope of AC
        //solving y - y1 = m(x-x1) for p1 and p2
        float factor = 1.0f/(-m1+m2);
        float d1 = -p1.y+m1*p1.x;
        float d2 = -p2.y+m2*p2.x;
        float x = factor * (-1*d1 + d2);
        float y = factor * ( -1*m2*d1 + m1*d2 );
        Log.d(TAG, "x:"+x+", y:"+y);
        circumcenter.x = x;
        circumcenter.y = y;
    }
    */
    /*private void computeCentroid(){
        float x = 0, y = 0;
        for(int i=0; i<3; i++){
            x += vertices[i].x;
            y += vertices[i].y;
        }
        centroid.x = x/3;
        centroid.y = y/3;
    }*/
}
