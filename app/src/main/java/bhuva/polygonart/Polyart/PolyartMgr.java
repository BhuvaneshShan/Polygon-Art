package bhuva.polygonart.Polyart;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Debug;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import bhuva.polygonart.Common.Pair;
import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.Graphics.Vec2D;
import bhuva.polygonart.UI.SelectionCircle;

/**
 * Created by bhuva on 5/8/2016.
 */
public class PolyartMgr {

    private static ArrayList<Triangle> triangles;
    private static int triCount = 0;
    private static boolean triangleCreationInProgress = false;

    private Paint paint;
    private static int curColor = Color.MAGENTA;
    private static int curBrushSize = 50;

    private NearestNeighbor nearestNeighborMgr;
    private Vector<SelectionCircle> selCircles;

    private Random rnd = new Random();
    private int VERTEX_COUNT = 3;
    private Point screenDim = new Point();
    private GestureDetector gestureDetector;

    private enum Mode{CreationMode, EditingMode};
    private Mode DrawMode;

    private String TAG = "MGR";

    public PolyartMgr(Context context){
        triangles = new ArrayList<Triangle>();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(screenDim);
        Log.d(TAG,"Dim:"+screenDim.x+", "+screenDim.y);
        nearestNeighborMgr = new NearestNeighbor(screenDim);
        setupPaint();
        DrawMode = Mode.CreationMode;
        selCircles = new Vector<>();
        //gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /*private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d(TAG, "Double Tapped at: (" + x + "," + y + ")");

            return true;
        }
    }*/

    public void onTouchEvent(MotionEvent event){
        float curX = event.getX();
        float curY = event.getY();
        if( isWithinScreenDim(curX,curY)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_OUTSIDE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (DrawMode == Mode.CreationMode) {
                        //debugSelCircles(curX, curY);
                        Triangle t = createNewTriangle(curX, curY);
                        addTriangle(t);
                    } else if (DrawMode == Mode.EditingMode) {
                        //editing of triangle
                        Log.d(TAG, "In Editing mode");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (DrawMode == Mode.CreationMode) {
                        if (!triangleCreationInProgress) {
                            PointF touchPoint = new PointF(curX, curY);
                            Triangle temp = triangles.get(triCount - 1);
                            float dist = temp.distTo(touchPoint);
                            if (dist >= 1.4 * curBrushSize) {
                                triangleCreationInProgress = true;
                                //Log.d(TAG, "brush size:" + curBrushSize + " ; dist: " + dist);
                                Triangle t = createNewTriangleWith(triangles.get(triCount - 1), touchPoint);
                                addTriangle(t);
                                triangleCreationInProgress = false;
                            }
                        } else {
                            Log.d(TAG, "Triangle creation in progress");
                        }
                    } else if (DrawMode == Mode.EditingMode) {
                        //editing of triangle
                        Log.d(TAG, "In Editing mode");
                    }
                    break;
            }
        }
    }

    public void drawOnCanvas(Canvas canvas){
        Path path = new Path();
        for (Triangle t : triangles) {
            PointF a = t.getVertA();
            PointF b = t.getVertB();
            PointF c = t.getVertC();

            path.reset();
            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();

            paint.setColor(t.getColor());
            canvas.drawPath(path, paint);
        }

        for(SelectionCircle c : selCircles){
            c.draw(canvas);
        }
    }

    public void addTriangle(Triangle t){
        if(isWithinScreenDim(t.getVertA()) && isWithinScreenDim(t.getVertB()) && isWithinScreenDim(t.getVertC())) {
            triangles.add(t);
            triCount++;
            nearestNeighborMgr.addPoint(t.getVertA());
            nearestNeighborMgr.addPoint(t.getVertB());
            nearestNeighborMgr.addPoint(t.getVertC());
        }
    }

    private boolean isInsideATriangle(float x, float y){
        PointF p = new PointF(x,y);
        for(int i=triangles.size()-1; i>=0; --i){
            Triangle t = triangles.get(i);
            if(t.contains(p)){
                return true;
            }
        }
        return false;
    }

    private Triangle createNewTriangle(float x, float y){
        Triangle t = Triangle.randomUnitTriangle();
        t.scale(curBrushSize);
        t.translate(x, y);
        //t.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        t.setColor(Color.argb(255, Color.red(curColor) + rnd.nextInt(20), Color.green(curColor) + rnd.nextInt(20), Color.blue(curColor) + rnd.nextInt(20)));
        return t;
    }

    private Triangle createNewTriangleWith(Triangle t, PointF touchPoint){
        /**
        double distFirstNear = Generic.relDist(t.getVert(0), touchPoint);
        double distSecNear = distFirstNear;
        PointF firstNear = t.getVert(0);
        PointF secNear = firstNear;
        for(int i=1; i<VERTEX_COUNT; i++){
            double tempDist = Generic.relDist(t.getVert(i), touchPoint);
            if(tempDist < distFirstNear){
                distSecNear = distFirstNear;
                secNear = firstNear;
                distFirstNear = tempDist;
                firstNear = t.getVert(i);
            }else if(tempDist < distSecNear){
                distSecNear = tempDist;
                secNear = t.getVert(i);
            }
        }
        int farthestVertexIndex = 0;
        float farthestDist = 0;
        for(int i=0; i<3; i++){
            float dist = (float)Generic.relDist(t.getVert(i), touchPoint);
            if (dist > farthestDist){
                farthestDist = dist;
                farthestVertexIndex = i;
            }
        }
        Log.d(TAG, "farthest vert: "+farthestVertexIndex + "; far dist:"+farthestDist);
        PointF near1 = t.getVert((farthestVertexIndex+1)%3);
        PointF near2 = t.getVert((farthestVertexIndex+2)%3);
        */

        Vector<PointF> verts = t.getVerticesOfNearestSide(touchPoint); //verts has 0,1 as nearest vertices in terms of side facing and 2 index as the farthest vertex
        Log.d(TAG, "near1:"+verts.get(0).toString()+", near2:"+verts.get(1).toString());

        PointF mid = new PointF((verts.get(0).x+verts.get(1).x)/2.0f, (verts.get(0).y+verts.get(1).y)/2.0f);
        Log.d(TAG, "mid:"+mid.toString());
        Vec2D baseSide = new Vec2D(verts.get(0), verts.get(1));
        Log.d(TAG,"baseSide: "+baseSide.string());
        float adjByHyp = (float)Generic.dist(verts.get(0),verts.get(1))/(2*curBrushSize);
        adjByHyp = adjByHyp>0.75?0.75f:adjByHyp;
        float sinval = (float) Math.sin( Math.acos(adjByHyp));
        Log.d(TAG, "sinval:" + sinval);
        float len = curBrushSize * sinval;
        Log.d(TAG, "len: " + len);
        /*baseSide.norm();
        Log.d(TAG, "after norm: " + baseSide.string());
        baseSide.rotate90();
        Log.d(TAG, "after rotate: " + baseSide.string());
        PointF newVert = baseSide.translatePointBy(mid, len+curBrushSize);
        Log.d(TAG,"new vert: "+newVert.x+", "+newVert.y);
        if( Generic.dist(verts.get(2), newVert) < len+curBrushSize ){
            baseSide.invert();
            Log.d(TAG, "after invert: " + baseSide.string());
            newVert = baseSide.translatePointBy(mid, len + curBrushSize);
        }*/
        Vec2D dir = new Vec2D(t.getCenter(), mid);
        dir.norm();
        PointF newVert = dir.translatePointBy(mid, len+curBrushSize);
        Log.d(TAG,"final new vert: "+newVert.x+", "+newVert.y);
        Triangle newT = new Triangle(verts.get(0), verts.get(1), newVert);
        newT.setColor(Color.argb(255, Color.red(curColor) + rnd.nextInt(2), Color.green(curColor)+rnd.nextInt(2), Color.blue(curColor)+rnd.nextInt(2)));
        //newT.setColor(Color.);
        return newT;
    }

    private void setupPaint(){
        paint = new Paint();
        paint.setColor(curColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public static void clearAll(){
        curBrushSize = 50;
        curColor = Color.RED;
        triangles.clear();
        triangleCreationInProgress =false;
        triCount = 0;
    }

    public static void selectBrushSize(int size){
        curBrushSize = size;
    }

    public static void selectColor(int color){
        curColor = color;
    }

    public static void done(){
        //saving
    }

    private boolean isWithinScreenDim(float x, float y){
        if (x >=0 && x<=screenDim.x && y>=0 && y<=screenDim.y)
            return true;
        return false;
    }

    private boolean isWithinScreenDim(PointF p){
        if (p.x >=0 && p.x<=screenDim.x && p.y>=0 && p.y<=screenDim.y)
            return true;
        return false;
    }

    private void debugSelCircles(float x, float y){
        PointF p = new PointF(x,y);
        Vector<Point> v = nearestNeighborMgr.getNearestNeighbor(p,3,curBrushSize);
        selCircles.clear();
        for(Point nn: v){
            selCircles.add(new SelectionCircle(nn));
        }
    }
}
