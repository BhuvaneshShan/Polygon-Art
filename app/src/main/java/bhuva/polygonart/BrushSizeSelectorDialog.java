package bhuva.polygonart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import java.util.logging.Logger;

import bhuva.polygonart.Polyart.PolyartMgr;

/**
 * Created by bhuva on 2/12/2017.
 */
public class BrushSizeSelectorDialog extends DialogFragment {

    BrushSelectionListener mBrushSelectionListener;
    SeekBar seekBar;
    SurfaceView brushSizeDrawer;

    public interface BrushSelectionListener{
        public void onSetBrushSize(int size);
        public void onCancel(DialogFragment dialog);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_brush_size_selection, null);
        configureUI(dialogView);
        builder.setView(dialogView)
            .setMessage(R.string.select_brush_size)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBrushSelectionListener.onSetBrushSize(seekBar.getProgress());
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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

    private void configureUI(View view){
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress(PolyartMgr.getCurBrushSize());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Utils.Log("Seekbar: " + Integer.toString(progress), 3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brushSizeDrawer = (SurfaceView) view.findViewById(R.id.brushSizeDrawer);

        Utils.Log("UI CONFIGURED",5);
    }
}
