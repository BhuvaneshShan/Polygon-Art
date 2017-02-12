package bhuva.polygonart.Polyart;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.Vector;

import bhuva.polygonart.Common.Pair;
import bhuva.polygonart.Graphics.Generic;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 5/18/2016.
 */
public class NearestNeighbor {
    int CELL_WIDTH = 10;
    GridEle[][] grid;
    int totalCols, totalRows = 0;

    //A vector to store queried points to sort and return the nearest. each pair consists of point and dist from given point
    Vector<Pair<Point, Double>> sortedPoints = new Vector<>();

    final String TAG = "NearestNeighbor";

    public NearestNeighbor(Point maxSize){
        totalCols = (maxSize.x/CELL_WIDTH) + 1;
        totalRows = (maxSize.y/CELL_WIDTH) + 1;
        grid = new GridEle[totalCols][totalRows];
        for(int i=0; i<totalCols; i++){
            for(int j=0; j<totalRows; j++){
                grid[i][j] = new GridEle();
            }
        }
    }

    public void addPoint(PointF point){
        Log.d(TAG, "Point:"+point.x+", "+point.y);
        int col = (int)point.x/CELL_WIDTH;
        int row = (int)point.y/CELL_WIDTH;
        Log.d(TAG, "col,row:"+col+", "+row);
        Point p = new Point(Math.round(point.x),Math.round(point.y));
        Log.d(TAG, "Adding to grid"+col+", "+row);
        grid[col][row].add(p);
    }

    public void removePoint(PointF point){
        int col = (int)point.x/CELL_WIDTH;
        int row = (int)point.y/CELL_WIDTH;
        Point p = new Point(Math.round(point.x),Math.round(point.y));
        grid[col][row].remove(p);
    }

    //point  - point from which nearest points need to be found
    //count - number of nearest points to be found
    //size - the brush size/ or the radius of the area to be checked for nearest points
    public Vector<Point> getNearestNeighbor(PointF pointf, int count, int size){
        int col = (int)pointf.x/CELL_WIDTH;
        int row = (int)pointf.y/CELL_WIDTH;
        Point point = new Point(Math.round(pointf.x),Math.round(pointf.y));
        int radius = size/CELL_WIDTH;

        sortedPoints.clear();

        Vector<Pair<Point, Double>> queriedPointsInfo = grid[col][row].getNearest(point, count);
        insertIntoSortedPoints(queriedPointsInfo);

        //all square grids around the gridele[col][row]
        for(int rad=1; rad<=radius && queriedPointsInfo.size()<count; rad++){
            int trow = row - rad;
            if(trow >= 0) {
                for (int c = col - rad; c <= col + rad; c++) {
                    if(inLimits(0,c,totalCols-1)) {
                        queriedPointsInfo = grid[c][trow].getNearest(point, count);
                        insertIntoSortedPoints(queriedPointsInfo);
                    }else{
                        Utils.Log("c is outside limits!c:"+c,5);
                    }
                }
            }

            trow = row + rad;
            if(trow < totalRows) {
                for (int c = col - rad; c <= col + rad; c++) {
                    if(inLimits(0,c,totalCols-1)) {
                        queriedPointsInfo = grid[c][trow].getNearest(point, count);
                        insertIntoSortedPoints(queriedPointsInfo);
                    }else{
                        Utils.Log("c is outside limits! c:"+c,5);
                    }
                }
            }

            int tcol = col - rad;
            if(tcol >= 0) {
                for (int r = row - rad; r <= row + rad; r++) {
                    if(inLimits(0,r,totalRows-1)) {
                        queriedPointsInfo = grid[tcol][r].getNearest(point, count);
                        insertIntoSortedPoints(queriedPointsInfo);
                    }else{
                        Utils.Log("r is outside limits! r:"+r,5);
                    }
                }
            }

            tcol = col + rad;
            if(tcol < totalCols) {
                for (int r = row - rad; r <= row + rad; r++) {
                    if(inLimits(0,r,totalRows-1)) {
                        queriedPointsInfo = grid[tcol][r].getNearest(point, count);
                        insertIntoSortedPoints(queriedPointsInfo);
                    }else{
                        Utils.Log("r is outside limits! r:"+r,5);
                    }
                }
            }
        }

        //return count numeber of neighboring points
        Vector<Point> neighbors = new Vector<>();
        count = count>sortedPoints.size()?sortedPoints.size():count;
        for(int i=0;i<count; i++){
            neighbors.add( sortedPoints.get(i).a);
        }
        return neighbors;
    }

    private boolean inLimits(int min, int val, int max){
        if(val >= min && val <= max){
            return true;
        }
        return false;
    }

    private void insertIntoSortedPoints(Vector<Pair<Point, Double>> newList){
        for(Pair<Point, Double> p:newList) {
            for (int i = 0; i < sortedPoints.size(); i++) {
                if (sortedPoints.get(i).b > p.b) {
                    sortedPoints.insertElementAt(p, i);
                    return;
                }
            }
            sortedPoints.add(p);
        }
    }

    private class GridEle {
        //Pair<Point, Integer> represent  a point and its count (number of times a vertex occurs at that point)
        public Vector< Pair<Point, Integer> > ele = new Vector<>();
        //Pair<Point, Double> represent a point and its dist from the given point. this is used as a temp array to sort
        Vector<Pair<Point, Double>> sortedPoints = new Vector<>();

        public Vector<Pair<Point, Double>> getNearest(Point p, int count){
            if(ele.size() > 0){
                sortedPoints.clear();
                //find dist between p and eles and sort and return count number of elements.
                for(Pair<Point, Integer> t:ele){
                    double dist = Generic.relDist(p, t.a);
                    insertIntoSortedPoints(t.a, dist);
                }
                //return count number of points
                if(ele.size()<=count) {
                    return sortedPoints;
                }
                else{
                    return (Vector<Pair<Point, Double>>)sortedPoints.subList(0, count);
                }
            }else{
                return new Vector<>();
            }
        }

        private void insertIntoSortedPoints(Point p, double dist){
            for(int i=0; i < sortedPoints.size(); i++){
                if(sortedPoints.get(i).b > dist){
                    sortedPoints.insertElementAt(new Pair<Point, Double>(p, dist), i);
                    return;
                }
            }
            sortedPoints.add(new Pair<Point, Double>(p, dist));
        }

        public void add(Point p){
            Log.d(TAG,"Adding to NN:"+p.x+", "+p.y);
            for(Pair<Point, Integer> t:ele){
                if(t.a.equals(p)) {
                    t.b++;
                    return;
                }
            }
            //if no such point is present, add a point
            Log.d(TAG,"Adding new point to grid");
            ele.add(new Pair(p,1));
        }
        public void remove(Point p){
            for(int i=ele.size()-1; i>=0; i--){
                if(ele.get(i).a.equals(p)) {
                    ele.get(i).b--;
                    if(ele.get(i).b == 0)
                        ele.remove(i);
                    return;
                }
            }
        }
    }
}
