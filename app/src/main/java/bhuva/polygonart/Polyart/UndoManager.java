package bhuva.polygonart.Polyart;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import bhuva.polygonart.Common.Pair;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 4/30/2017.
 */

public class UndoManager {
    List<Pair<Integer, Polygon>> undoList;

    public UndoManager(){
        undoList = new ArrayList<>(50);
    }

    public void addState(List<Polygon> polygons, int index){
        Polygon toAdd = new Polygon(polygons.get(index));
        if(undoList.size()>50){
            undoList.remove(0);
        }
        undoList.add(new Pair(index, toAdd));
    }

    public void addStateInCreateMode(List<Polygon> polygons, int index){
        Polygon toAdd = new Polygon(polygons.get(index));
        toAdd.setVisible(false);
        if(undoList.size()>50){
            undoList.remove(0);
        }
        undoList.add(new Pair(index, toAdd));
    }

    public void addStateInEditMode(List<Polygon> polygons, int index){
        /*if (undoList.size() > 0) {
            Pair<Integer, Polygon> lastAdded = undoList.get(undoList.size() - 1);
            if (lastAdded.getA() == index){
                return;
            }
        }*/
        Polygon toAdd = new Polygon(polygons.get(index));
        toAdd.setVisible(true);
        if(undoList.size()>50){
            undoList.remove(0);
        }
        undoList.add(new Pair(index, toAdd));
    }

    public void apply(List<Polygon> polygons){
        if (undoList.size() > 0) {
            try {
                Pair pair = undoList.get(undoList.size() - 1);
                polygons.set((Integer) pair.getA(), (Polygon) pair.getB());
            } catch (Exception e){
                Log.e("POLYGONART.UNDOMANAGER", e.getLocalizedMessage(), e);
            }finally {
                undoList.remove(undoList.size() - 1);
            }
        }
    }
}
