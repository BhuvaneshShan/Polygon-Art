package bhuva.polygonart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.logging.Logger;

import bhuva.polygonart.Polyart.PolyartMgr;

/**
 * Created by bhuva on 2/12/2017.
 */
public class BrushSizeSelectorDialog extends DialogFragment {
    BrushSelectionListener mBrushSelectionListener;

    public interface BrushSelectionListener{
        public void onSetBrushSize(DialogFragment dialog);
        public void onCancel(DialogFragment dialog);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_brush_size_selection, null))
            .setMessage(R.string.select_brush_size)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //PolyartMgr.selectBrushSize(100);
                    mBrushSelectionListener.onSetBrushSize(BrushSizeSelectorDialog.this);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing
                    mBrushSelectionListener.onCancel(BrushSizeSelectorDialog.this);
                }
            });
         // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mBrushSelectionListener = (BrushSelectionListener)activity;
        }catch (Exception e){
            Utils.Log("BRUSH SELECTOR DIALOG:"+e.getMessage(), 5);
        }
    }
}
