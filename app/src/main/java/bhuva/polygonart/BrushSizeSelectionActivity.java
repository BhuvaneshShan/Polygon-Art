package bhuva.polygonart;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import bhuva.polygonart.Polyart.PolyartMgr;

public class BrushSizeSelectionActivity extends AppCompatActivity {
    SeekBar seekBar;
    SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress(PolyartMgr.getCurBrushSize());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Utils.Log("Seekbar:"+ Integer.toString(progress),3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Utils.Log("SEEKBAR CONFIGURED",5);
    }

}
