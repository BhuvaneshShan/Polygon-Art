package bhuva.polygonart.UI;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import bhuva.polygonart.Polyart.PolyartMgr;
import bhuva.polygonart.Polyart.Polygon;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 3/26/2017.
 */

public class BrushSizeView extends View {
    Polygon sample;
    Paint paint;
    PointF center;

    public BrushSizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new PointF(328, 328);
        sample = new Polygon(PolyartMgr.getCurPolygonSides(), center, PolyartMgr.getCurBrushSize() ,PolyartMgr.getCurColor(), false);
        setupPaint();
    }

    public void reDraw(int brushSize, int sidesCount){
        center = new PointF(this.getWidth()/2, this.getHeight()/2);
        sample = new Polygon(sidesCount, center, brushSize ,PolyartMgr.getCurColor(), false);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        Path path = new Path();
        path.reset();
        path = sample.draw(path);
        path.close();

        paint.setColor(sample.getColor());
        canvas.drawPath(path, paint);
    }

    private void setupPaint(){
        paint = new Paint();
        paint.setColor(PolyartMgr.getCurColor());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }
}
