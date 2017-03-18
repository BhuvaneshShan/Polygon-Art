package bhuva.polygonart;

import android.app.DialogFragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import bhuva.polygonart.Polyart.PolyartMgr;

public class MainActivity extends AppCompatActivity implements BrushSizeSelectorDialog.BrushSelectionListener, ColorPickerDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFullScreenMode();
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tools, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        enableFullScreenMode();
    }

    public void enableFullScreenMode(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onClickCreateNewFile(View view) {
        PolyartMgr.clearAll();
        DrawingView drawingView = (DrawingView)findViewById(R.id.simpleDrawingView1);
        drawingView.invalidate();
    }

    public void onClickBrushSize(View view) {
        BrushSizeSelectorDialog dialog = new BrushSizeSelectorDialog();
        dialog.show(getFragmentManager(), "Brush Size Selector");
        //int newSize = 100;
        //PolyartMgr.selectBrushSize(newSize);
    }

    public void onClickColorSelector(View view) {
        int oldColor = PolyartMgr.getCurColor();
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setColor(oldColor)
                .setShowAlphaSlider(false)
                .show(this);
    }

    public void onClickDone(View view) {
    }

    @Override
    public void onSetBrushSize(int size){
        Utils.Log("onSetBrushSize called!",3);
        PolyartMgr.selectBrushSize(size);
    }

    @Override
    public void onCancel(DialogFragment dialog){
        Utils.Log("onCancel called!",3);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        enableFullScreenMode();
        Utils.Log("onColorSelected called!",3);
        PolyartMgr.selectColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        enableFullScreenMode();
        Utils.Log("dialog dismissed color called!",3);
    }
}
