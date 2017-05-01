package bhuva.polygonart.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import bhuva.polygonart.R;
import bhuva.polygonart.Utils;

/**
 * Created by bhuva on 4/30/2017.
 */

public class CreateNewFileDialog extends DialogFragment {

    NewFileDialogListener mNewFileDialogListener;

    public interface NewFileDialogListener{
        void onNewFile();
        //void showGallery();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_file_dialog, null);
        configureUI(dialogView);
        // Create the AlertDialog object and return it
        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mNewFileDialogListener = (NewFileDialogListener) activity;
        }catch (Exception e){
            Utils.Log("CANVAS SETTING DIALOG:"+e.getMessage(), 5);
        }
    }

    private void configureUI(View dialogView){
        Button newFileButton = (Button) dialogView.findViewById(R.id.newFileButton);
        newFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewFileDialogListener.onNewFile();
                CreateNewFileDialog.this.dismiss();
            }
        });

        Button howToButton = (Button) dialogView.findViewById(R.id.howToButton);
        howToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowToDialog dialog = new HowToDialog();
                dialog.show(CreateNewFileDialog.this.getFragmentManager(), "HOW TO");
            }
        });
        /*
        Button galleryButton = (Button) dialogView.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewFileDialogListener.showGallery();
                CreateNewFileDialog.this.dismiss();
            }
        });*/
    }
}
