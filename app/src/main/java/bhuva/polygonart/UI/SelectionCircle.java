package bhuva.polygonart.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by bhuva on 6/5/2016.
 */
public class SelectionCircle {
    PointF centre = new PointF();
    float radius;
    int color = Color.BLUE;
    final String TAG = "SelectionCircle";
    final int defaultRadius = 10;
    Paint fillPaint;
    Paint strokePaint;
    public SelectionCircle(float x, float y, float rad){
        centre.x = x;
        centre.y = y;
        radius = rad;
        setupPaint();
    }
    public SelectionCircle(float x, float y){
        centre.x = x;
        centre.y = y;
        radius = defaultRadius;
        setupPaint();
    }
    public SelectionCircle(Point p){
        centre.x = p.x;
        centre.y = p.y;
        radius = defaultRadius;
        setupPaint();
    }
    public SelectionCircle(PointF p){
        centre.x = p.x;
        centre.y = p.y;
        radius = defaultRadius;
        setupPaint();
    }
    public SelectionCircle(PointF p, int tcolor){
        centre.x = p.x;
        centre.y = p.y;
        radius = defaultRadius;
        color = tcolor;
        setupPaint();
    }
    public void translate(float x, float y){
        centre.x += x;
        centre.y += y;
    }
    public void draw(Canvas canvas){
        canvas.drawCircle(centre.x, centre.y, radius, fillPaint);
        canvas.drawCircle(centre.x, centre.y, radius, strokePaint);
    }
    private void setupPaint(){
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(color);
        fillPaint.setStrokeWidth(1);
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Paint.Style.STROKE);
    }
}
