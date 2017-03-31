package bhuva.polygonart;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.DateTimePatternGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Timer;

import bhuva.polygonart.Polyart.PolyartMgr;

public class MainActivity extends AppCompatActivity implements BrushSizeSelectorDialog.BrushSelectionListener, ColorPickerDialogListener {

    public static final String PolygonArt = "PolygonArt";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 147;
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
        reDrawDrawingView();
    }

    public void onClickRemovePoly(View view){
        if(PolyartMgr.getMode() != PolyartMgr.Mode.RemoveMode) {
            PolyartMgr.setMode(PolyartMgr.Mode.RemoveMode);
        } else {
            PolyartMgr.setMode(PolyartMgr.Mode.CreationMode);
        }
        reDrawDrawingView();
        refreshIcons();
    }

    public void onClickEditPoly(View view){
        if(PolyartMgr.getMode() != PolyartMgr.Mode.EditingMode) {
            PolyartMgr.setMode(PolyartMgr.Mode.EditingMode);
        } else {
            PolyartMgr.setMode(PolyartMgr.Mode.CreationMode);
        }
        reDrawDrawingView();
        refreshIcons();
    }

    public void onClickDone(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            saveAsJpeg(PolyartMgr.getInstance(getApplicationContext()).retrieveBitmap());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveAsJpeg(PolyartMgr.getInstance(getApplicationContext()).retrieveBitmap());
                }
                break;
            }
        }
    }

    private void refreshIcons(){
        Button removeButton = (Button) findViewById(R.id.buttonRemove);
        removeButton.setBackgroundResource(R.drawable.ic_remove_poly);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setBackgroundResource(R.drawable.ic_edit_poly);

        if(PolyartMgr.getMode() == PolyartMgr.Mode.RemoveMode) {
            removeButton.setBackgroundResource(R.drawable.ic_remove_poly_clicked);
            Toast.makeText(this, "In Remove mode. Touch polygons to delete! ", Toast.LENGTH_SHORT).show();
        }else if(PolyartMgr.getMode() == PolyartMgr.Mode.EditingMode) {
            editButton.setBackgroundResource(R.drawable.ic_edit_poly_clicked);
            Toast.makeText(this, "In Edit mode. Touch and drag polygons to edit! ", Toast.LENGTH_SHORT).show();
        }
    }

    private void reDrawDrawingView(){
        DrawingView drawingView = (DrawingView)findViewById(R.id.simpleDrawingView1);
        drawingView.invalidate();
    }

    //Brush dialog functions

    public void onClickBrushSize(View view) {
        BrushSizeSelectorDialog dialog = new BrushSizeSelectorDialog();
        dialog.show(getFragmentManager(), "Brush Size Selector");
    }

    @Override
    public void onSetBrushSize(int size){
        enableFullScreenMode();
        Utils.Log("onSetBrushSize called!",2);
        PolyartMgr.setBrushSize(size);
    }

    public void onSetSidesCount(int count){
        Utils.Log("onSetSidesCount called", 2);
        PolyartMgr.setCurPolygonSides(count);
    }

    @Override
    public void onBrushDialogCancel(DialogFragment dialog){
        enableFullScreenMode();
        Utils.Log("Brush Dialog cancel called!",2);
    }

    //Color Pallette functions

    public void onClickColorSelector(View view) {
        int oldColor = PolyartMgr.getCurColor();
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setColor(oldColor)
                .setShowAlphaSlider(false)
                .show(this);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        enableFullScreenMode();
        Utils.Log("onColorSelected called!",3);
        PolyartMgr.setColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        enableFullScreenMode();
        Utils.Log("dialog dismissed color called!",3);
    }

    private void saveAsJpeg(Bitmap content){
        try {
            File polyartDir = new File(Environment.getExternalStorageDirectory(), PolygonArt );
            if (!polyartDir.exists()) {
                polyartDir.mkdirs();
            }

            File file = new File(polyartDir, genFileName());
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream filesOpStream = new FileOutputStream(file);
            content.compress(Bitmap.CompressFormat.JPEG, 100, filesOpStream);
            filesOpStream.flush();
            filesOpStream.close();

            exposeToGallery(file.getAbsolutePath());

            Toast.makeText(getApplicationContext(), "Saved as "+PolygonArt+"/"+file.getName(), Toast.LENGTH_LONG);
        } catch (Exception e) {
            Utils.Log(e.getMessage(), 5);
            Toast.makeText(getApplicationContext(), "Save error:"+e.getLocalizedMessage(), Toast.LENGTH_LONG);
        }
    }

    private String genFileName(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return PolygonArt + "_" + ts+".jpeg";
    }

    private void exposeToGallery(String filePath){
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


}
