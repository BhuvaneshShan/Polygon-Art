package bhuva.polygonart.Graphics;

import android.graphics.PointF;

/**
 * Created by bhuva on 5/11/2016.
 */
public class Vec2D {
    public float x;
    public float y;
    public Vec2D(){
        x = 0;
        y = 0;
    }
    public Vec2D(Vec2D vec){
        x = vec.x;
        y = vec.y;
    }
    public Vec2D(float tx, float ty){
        x = tx;
        y = ty;
    }
    public Vec2D(PointF a, PointF b){
        x = b.x - a.x;
        y = b.y - a.y;
    }

    public void rotate90(){
        float temp = x;
        x = -y;
        y = temp;
    }

    public void invert(){
        x = -x;
        y = -y;
    }

    public double magnitude(){
        return Math.sqrt(x*x + y*y);
    }

    public void norm(){
        float mag = (float) this.magnitude();
        x = x / mag;
        y = y / mag;
        //if(x<0.000001) x = 0;
        //if(y<0.000001) y = 0;
    }

    public void mult(float factor){
        x = x*factor;
        y = y*factor;
    }

    public PointF translate(PointF p){
        return new PointF(p.x+x, p.y+y);
    }

    public PointF translatePointBy(PointF p, float factor){
        return new PointF(p.x + x*factor, p.y + y*factor);
    }

    public String string(){
        return "x:"+x+", y:"+y;
    }

    public static float dotProduct(Vec2D u, Vec2D v){
        return u.x*v.x + u.y*v.y;
    }
}
