package bhuva.polygonart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhuva on 5/6/2016.
 */
public class SimpleDrawingView extends View {
    private int paintColor = Color.BLUE;
    private Paint drawPaint;
    private int strokeWidth = 5;
    private List<Point> circlePoints;
    private Path path = new Path();
    //Bitmap mField = null;
    public SimpleDrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        circlePoints = new ArrayList<Point>();
    }
    /*
    public void init() {
        mField = new Bitmap();
        Canvas c = new Canvas(mField);
        c.drawRect();
    }
    */
    private void setupPaint(){
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas){
        /* (Point p:circlePoints) {
            canvas.drawCircle(p.x, p.y, 5, drawPaint);
        }*/
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();
        //circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }
}
