package bhuva.polygonart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import bhuva.polygonart.Common.Pair;
import bhuva.polygonart.Polyart.PolyartMgr;

/**
 * Created by bhuva on 5/7/2016.
 */
public class DrawingView extends View {

    private PolyartMgr polyartMgr;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        polyartMgr = PolyartMgr.getInstance(context);
        setupDebugPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        polyartMgr.drawOnCanvas(canvas);
        //canvas.drawPath(debugPath, debugPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        polyartMgr.onTouchEvent(event);
        //debugTouch(event);
        postInvalidate();
        return true;
    }

    Path debugPath = new Path();
    Paint debugPaint;

    private void debugTouch(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();
        //circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                debugPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                debugPath.lineTo(touchX, touchY);
                break;
            default:
                break;
        }
    }
    private void setupDebugPaint(){
        debugPaint = new Paint();
        debugPaint.setColor(Color.BLUE);
        debugPaint.setAntiAlias(true);
        debugPaint.setStrokeWidth(5);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setStrokeJoin(Paint.Join.ROUND);
        debugPaint.setStrokeCap(Paint.Cap.ROUND);
    }

}
