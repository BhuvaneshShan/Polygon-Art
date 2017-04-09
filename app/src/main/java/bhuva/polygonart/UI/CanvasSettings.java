package bhuva.polygonart.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import bhuva.polygonart.Polyart.PolyartMgr;
import bhuva.polygonart.R;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 4/8/2017.
 */

public class CanvasSettings extends DialogFragment{

    private View dialogView = null;
    private CanvasSettingsListener mCanvasSettingsListener;

    public interface CanvasSettingsListener{
        void onBackgroundColorSelected(int color);
        void onReferenceImageSelected(Bitmap refImage);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.canvas_settings, null);
        configureUI(dialogView);
        builder.setView(dialogView)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCanvasSettingsListener = (CanvasSettingsListener) activity;
        }catch (Exception e){
            Utils.Log("CANVAS SETTING DIALOG:"+e.getMessage(), 5);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(dialogView!=null) {
            Button bgColorButton = (Button) dialogView.findViewById(R.id.bgColorButton);
            if (PolyartMgr.isReferenceImageSet()) {
                bgColorButton.setBackgroundColor(Color.WHITE);
            } else {
                bgColorButton.setBackgroundColor(PolyartMgr.getBackgroundColor());
            }
        }
    }

    private void configureUI(final View view){
        Button bgColorButton = (Button) view.findViewById(R.id.bgColorButton);
        if(PolyartMgr.isReferenceImageSet()){
            bgColorButton.setBackgroundColor(Color.WHITE);
        }else {
            bgColorButton.setBackgroundColor(PolyartMgr.getBackgroundColor());
        }
        bgColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogId(Utils.COLOR_DIALOG_BACKGROUND_SELECTOR_ID)
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setShowAlphaSlider(false)
                        .show(getActivity());
            }
        });

        Button refImageButton = (Button) view.findViewById(R.id.refImageButton);
        refImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(galleryIntent, Utils.INTENT_RESULT_SELECT_REF_IMG);
            }
        });
    }
}
