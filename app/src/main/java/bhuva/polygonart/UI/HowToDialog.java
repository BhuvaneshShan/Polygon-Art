package bhuva.polygonart.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import bhuva.polygonart.R;

/**
 * Created by bhuva on 4/30/2017.
 */

public class HowToDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.how_to_dialog, null);
        // Create the AlertDialog object and return it
        builder.setView(dialogView);
        return builder.create();
    }
}
