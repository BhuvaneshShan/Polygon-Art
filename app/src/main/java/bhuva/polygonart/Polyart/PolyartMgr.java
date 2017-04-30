package bhuva.polygonart.Polyart;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Debug;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import bhuva.polygonart.Common.Pair;
import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.Graphics.Vec2D;
import bhuva.polygonart.UI.SelectionCircle;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 5/8/2016.
 */
public class PolyartMgr {

    public static PolyartMgr polyartMgr=null;

    private static List<Polygon> polygons;

    private static boolean polygonCreationInProgress = false;
    private static boolean polygonRemovalInProgress = false;
    private static boolean isMotionDownEventActive = false;
    private static boolean polygonEditingInProgress = false;

    private static String DefaultColor = "0xFF03A9F4"; //Light Blue
    private static int defaultColor = Color.BLUE;
    private static int curColor = defaultColor;
    private static int curBrushSize = 50;
    private static int curPolygonSides = 3;

    private static int backgroundColor = Color.WHITE;
    private static Bitmap referenceImage=null;
    private static Rect refImgSrc = null;
    private static Rect refImgDst = null;

    private static int polygonAlpha = Utils.MAX_ALPHA_OPAQUE;

    private static PolygonEditor polygonEditor;
    private static UndoManager undoManager = new UndoManager();
    private Paint paint;
    private static Point screenDim = new Point();

    public enum Mode{CreationMode, EditingMode, RemoveMode};
    private static Mode curMode;

    private static final String TAG = "POLYART_MGR";

    public static PolyartMgr getInstance(Context context){
        if(polyartMgr == null){
            polyartMgr = new PolyartMgr(context);
        }
        return polyartMgr;
    }

    public PolyartMgr(Context context){
        polygons = new ArrayList<Polygon>();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(screenDim);
        Log.d(TAG,"Dim:"+screenDim.x+", "+screenDim.y);
        setupPaint();
        clearAll();
        curMode = Mode.CreationMode;
    }

    public void onTouchEvent(MotionEvent event){
        float curX = event.getX();
        float curY = event.getY();
        if( isWithinScreenDim(curX,curY)) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_OUTSIDE:
                    break;

                case MotionEvent.ACTION_DOWN:
                    isMotionDownEventActive = true;
                    if (curMode == Mode.CreationMode) {
                        createNew(curX, curY, false);
                    } else if (curMode == Mode.EditingMode) {
                        //editing of triangle
                        Log.d(TAG, "In Editing mode");
                        selectPolygonOrVertexAt(curX, curY);
                    } else if(curMode == Mode.RemoveMode){
                        removePolygonAt(curX, curY);
                    }
                    isMotionDownEventActive = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (curMode == Mode.CreationMode && !isMotionDownEventActive) {
                        if (!polygonCreationInProgress) {
                            createNew(curX, curY, true);
                        }
                    } else if (curMode == Mode.EditingMode) {
                        //editing of triangle
                        if(!polygonEditingInProgress){
                            transformPolygonOrVertexAt(curX, curY);
                        }

                    } else if(curMode == Mode.RemoveMode){
                        if(!polygonRemovalInProgress){
                            removePolygonAt(curX, curY);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if(curMode == Mode.EditingMode){
                        //release values? needed?
                    }
                    break;
            }
        }
    }

    public void drawOnCanvas(Canvas canvas){
        //drawing bg
        if(referenceImage==null) {
            canvas.drawColor(backgroundColor);
        }else{
            canvas.drawBitmap(referenceImage, refImgSrc, refImgDst, null);
        }

        Path path = new Path();

        //draw the Polygons
        Utils.Log("polyalph:"+polygonAlpha, 3);

        for (Polygon t : polygons) {

            if(t.isVisible()) {
                path.reset();
                path = t.draw(path);
                path.close();

                paint.setColor(t.getColor());
                paint.setAlpha(polygonAlpha);
                canvas.drawPath(path, paint);
            }
        }

        //draw editing mode points
        /*
        if(curMode == Mode.EditingMode && polygonEditor!=null && polygonEditor.isPolygonSelected()
                && polygons.get(polygonEditor.getSelectedPolygonId()).isVisible()){
            polygonEditor.drawEditingPoints(canvas);
        }*/
        //For Edit mode
        if(curMode == Mode.EditingMode && polygonEditor!=null && polygonEditor.isPolygonSelected()){
            //draw polygon
            Polygon t = polygonEditor.getSelectedPolygon();
            path.reset();
            path = t.draw(path);
            path.close();

            paint.setColor(t.getColor());
            paint.setAlpha(polygonAlpha);
            canvas.drawPath(path, paint);

            //draw editing points
            polygonEditor.drawEditingPoints(canvas);
        }

    }

    public void createNew(float x, float y, boolean appending){
        PointF touchPoint = new PointF(x, y);
        if(!appending) {
            Polygon polygon = new Polygon(curPolygonSides, touchPoint, curBrushSize, curColor, true);
            polygons.add(polygon);
            undoManager.addStateInCreateMode(polygons, polygons.size()-1);
        }else{
            if(polygons.size()>0) {
                Polygon neighbor = polygons.get(polygons.size() - 1);
                float dist = neighbor.distToCCenter(touchPoint);
                if (dist > 1.5 * curBrushSize && dist < 20 * curBrushSize) {
                    polygonCreationInProgress = true;
                    Polygon generated = generateNeighbor(polygons.get(polygons.size() - 1), touchPoint);
                    polygons.add(generated);
                    undoManager.addStateInCreateMode(polygons, polygons.size()-1);
                    polygonCreationInProgress = false;
                }
            }
        }
    }

    public void removePolygonAt(float x, float y){
        polygonRemovalInProgress = true;
        PointF touchPoint = new PointF(x, y);
        for(int i=polygons.size()-1; i>=0; i--){
            Polygon p = polygons.get(i);
            if( p.contains(touchPoint) && p.isVisible()){
                undoManager.addState(polygons, i);
                p.setVisible(false);
                break;
            }
        }
        polygonRemovalInProgress = false;
    }

    public void selectPolygonOrVertexAt(float x, float y){
        polygonEditingInProgress = true;
        PointF touchPoint = new PointF(x, y);
        boolean canAddUndoState = true;
        //check if cur polygon is transformed
        if(polygonEditor == null){
            polygonEditor = new PolygonEditor(touchPoint, polygons);
        }else if(!polygonEditor.isPolygonSelected() || !polygonEditor.isSelectedPolygonValid()){
            polygonEditor = new PolygonEditor(touchPoint, polygons);
        } else{
            int oldPolyId = polygonEditor.getSelectedPolygonId();
            polygonEditor.selectVertexBasedOnTouchPoint(touchPoint);
            if(!polygonEditor.isVertexSelected()){
                polygonEditor.applyTransformedPolygonTo(polygons);
                polygonEditor = new PolygonEditor(touchPoint, polygons);
            }
            canAddUndoState = (polygonEditor.getSelectedPolygonId() != oldPolyId);
        }
        if(polygonEditor.isSelectedPolygonValid() && canAddUndoState){
            undoManager.addStateInEditMode(polygons, polygonEditor.getSelectedPolygonId());
        }
        polygonEditingInProgress = false;
    }

    public void transformPolygonOrVertexAt(float x, float y){
        polygonEditingInProgress = true;
        if(polygonEditor!=null){
            if(polygonEditor.isPolygonSelected() && polygonEditor.isVertexSelected()) {
                polygonEditor.transformSelectedPointTo(new PointF(x, y));
            }
        }
        polygonEditingInProgress = false;
    }

    private Polygon generateNeighbor(Polygon t, PointF touchPoint){
        List<PointF> axisPoints = t.getVerticesOfNearestSide(touchPoint);
        Polygon polygon = Polygon.generateNeighbor(t, axisPoints);
        return polygon;
    }

    private void setupPaint(){
        paint = new Paint();
        paint.setColor(curColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(Utils.MAX_ALPHA_OPAQUE);
    }

    public static void clearAll(){
        curBrushSize = 50;
        curColor = defaultColor;
        curPolygonSides = 3;
        backgroundColor = Color.WHITE;
        referenceImage = null;

        polygons.clear();

        polygonCreationInProgress =false;
        polygonRemovalInProgress = false;
        isMotionDownEventActive = false;
        polygonEditingInProgress = false;
        polygonEditor = null;
        curMode = Mode.CreationMode;
        undoManager = new UndoManager();
    }

    public static void setBrushSize(int size){
        curBrushSize = size;
    }

    public static void setColor(int color){
        if(curMode == Mode.EditingMode && polygonEditor!=null && polygonEditor.isSelectedPolygonValid()){
            polygonEditor.setPolygonColor(color);
        }else {
            curColor = color;
        }
    }

    public static void setMode(Mode mode){
        if(mode == Mode.RemoveMode && curMode == Mode.EditingMode && polygonEditor!=null && polygonEditor.isSelectedPolygonValid()){
            //a polygon is selected. so delete it since we are moving to remove mode
            //deletes the editing polygon and deinitializes the polygonEditor
            polygonEditor = null;
        }else if(curMode == Mode.EditingMode && polygonEditor!=null && polygonEditor.isSelectedPolygonValid()){
            //moving away from editing mode to creation mode. so save the changes and deinitialize the polygonEditor
            polygonEditor.applyTransformedPolygonTo(polygons);
            polygonEditor = null;
        }
        curMode = mode;
    }

    public static Mode getMode(){
        return curMode;
    }

    public static int getCurPolygonSides() {
        return curPolygonSides;
    }

    public static void setCurPolygonSides(int curPolygonSides) {
        PolyartMgr.curPolygonSides = curPolygonSides;
    }

    public static int getCurBrushSize(){
        return curBrushSize;
    }

    public static int getCurColor(){
        return curColor;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(int backgroundColor) {
        PolyartMgr.backgroundColor = backgroundColor;
    }

    public static void setReferenceImage(Bitmap referenceImage) {
        if(referenceImage!=null) {
            //refImgPosition.x = (screenDim.x - referenceImage.getWidth()) / 2;
            //refImgPosition.y = (screenDim.y - referenceImage.getHeight()) / 2;
            refImgSrc = new Rect(0,0,referenceImage.getWidth(),referenceImage.getHeight());
            float scaleFactor = screenDim.x / (float)referenceImage.getWidth();
            float refImgFinalHeight = referenceImage.getHeight() * scaleFactor;
            float y = screenDim.y/2 - refImgFinalHeight/2;
            refImgDst = new Rect(0, (int)y, screenDim.x, (int)(y+refImgFinalHeight));
        }
        PolyartMgr.referenceImage = referenceImage;
    }

    public static void setPolygonAlpha(int polygonAlpha) {
        PolyartMgr.polygonAlpha = polygonAlpha;
    }

    public static int getPolygonAlpha() {
        return PolyartMgr.polygonAlpha;
    }

    public static boolean isReferenceImageSet(){
        return (referenceImage!=null);
    }

    private boolean isWithinScreenDim(float x, float y){
        if (x >=0 && x<=screenDim.x && y>=0 && y<=screenDim.y)
            return true;
        return false;
    }

    public Bitmap retrieveBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(screenDim.x, screenDim.y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getBackgroundColor());
        drawOnCanvas(canvas);
        return bitmap;
    }

    public static void onDeviceRotated(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(screenDim);
        Log.d(TAG,"Phone rotated new dim:"+screenDim.x+", "+screenDim.y);
    }

    public static void undo(){
        undoManager.apply(polygons);
        polygonEditor = null;
    }

}
