package bhuva.polygonart.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import bhuva.polygonart.Polyart.PolyartMgr;
import bhuva.polygonart.R;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 2/12/2017.
 */
public class BrushSizeSelectorDialog extends DialogFragment {

    BrushSelectionListener mBrushSelectionListener;
    SeekBar sizeBar, sidesBar;
    BrushSizeView brushSizeDrawer;

    public interface BrushSelectionListener{
        public void onSetBrushSize(int size);
        public void onSetSidesCount(int count);
        public void onBrushDialogCancel(DialogFragment dialog);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_brush_size_selection, null);
        configureUI(dialogView);
        builder.setView(dialogView)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBrushSelectionListener.onSetBrushSize(sizeBar.getProgress());
                    mBrushSelectionListener.onSetSidesCount(sidesBar.getProgress());
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBrushSelectionListener.onBrushDialogCancel(BrushSizeSelectorDialog.this);
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
            brushSizeDrawer.reDraw(PolyartMgr.getCurBrushSize(), PolyartMgr.getCurPolygonSides()); //to fix the polygon at center
        }catch (Exception e){
            Utils.Log("BRUSH SELECTOR DIALOG:"+e.getMessage(), 5);
        }
    }

    private void configureUI(View view){
        brushSizeDrawer = (BrushSizeView) view.findViewById(R.id.brushSizeDrawer);

        sizeBar = (SeekBar)view.findViewById(R.id.seekBar);
        sizeBar.setMax(Utils.MAX_BRUSH_SIZE);
        sizeBar.setProgress(PolyartMgr.getCurBrushSize());

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < Utils.MIN_BRUSH_SIZE){
                    seekBar.setProgress(Utils.MIN_BRUSH_SIZE);
                }
                if(brushSizeDrawer!=null) {
                    brushSizeDrawer.reDraw(sizeBar.getProgress(), sidesBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sidesBar = (SeekBar) view.findViewById(R.id.sideCountBar);
        sidesBar.setMax(Utils.MAX_SIDES_ALLOWED);
        sidesBar.setProgress(PolyartMgr.getCurPolygonSides());

        sidesBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < Utils.MIN_SIDES_ALLOWED){
                    sidesBar.setProgress(Utils.MIN_SIDES_ALLOWED);
                }
                if(brushSizeDrawer!=null) {
                    brushSizeDrawer.reDraw(sizeBar.getProgress(), sidesBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
}
