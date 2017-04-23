package bhuva.polygonart;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileOutputStream;

import bhuva.polygonart.Polyart.PolyartMgr;
import bhuva.polygonart.UI.BrushSizeSelectorDialog;
import bhuva.polygonart.UI.CanvasSettings;

public class MainActivity extends AppCompatActivity implements BrushSizeSelectorDialog.BrushSelectionListener, ColorPickerDialogListener, CanvasSettings.CanvasSettingsListener {

    public static final String PolygonArt = "PolygonArt";

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PolyartMgr.onDeviceRotated(getApplicationContext());
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

    //Buttons clicks
    public void onClickCreateNewFile(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(PolygonArt)
                .setMessage("Would you like to create a new canvas?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PolyartMgr.clearAll();
                        reDrawDrawingView();
                        refreshIcons();
                        enableFullScreenMode();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableFullScreenMode();
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }

    public void onClickBrushSize(View view) {
        BrushSizeSelectorDialog dialog = new BrushSizeSelectorDialog();
        dialog.show(getFragmentManager(), "Brush Size Selector");
    }

    public void onClickColorSelector(View view) {
        int oldColor = PolyartMgr.getCurColor();
        ColorPickerDialog.newBuilder()
                .setDialogId(Utils.COLOR_DIALOG_POLYGON_SELECTOR_ID)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setColor(oldColor)
                .setShowAlphaSlider(false)
                .show(this);
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

    public void onClickCanvasSettings(View view){
        CanvasSettings dialog = new CanvasSettings();
        dialog.show(getFragmentManager(), "Canvas Settings");
    }

    public void onClickDone(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Utils.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            String info = saveAsJpeg(PolyartMgr.getInstance(getApplicationContext()).retrieveBitmap());
            if(info.contains("Error")){
               //show error message
                showErrorDialog(info);
            }else{
                //show share dialog
                showShareDialog(info);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                //Request code from 'Done' button.
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String info = saveAsJpeg(PolyartMgr.getInstance(getApplicationContext()).retrieveBitmap());
                    if (info.contains("Error")) {
                        //show error message
                        showErrorDialog(info);
                    } else {
                        //show share dialog
                        showShareDialog(info);
                    }
                }
                break;

            case Utils.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                //Called from Canvas settings to read image from gallery to set reference image
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    this.startActivityForResult(galleryIntent, Utils.INTENT_RESULT_SELECT_REF_IMG);
                }
                break;
        }
    }

    private void refreshIcons(){
        Button removeButton = (Button) findViewById(R.id.buttonRemove);
        removeButton.setBackgroundResource(R.drawable.ic_remove_poly);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setBackgroundResource(R.drawable.ic_edit_poly);

        if(PolyartMgr.getMode() == PolyartMgr.Mode.RemoveMode) {
            removeButton.setBackgroundResource(R.drawable.ic_remove_poly_clicked);
            Toast.makeText(this, "You can delete polygons now! To start drawing, click Erase button again.", Toast.LENGTH_LONG).show();
        }else if(PolyartMgr.getMode() == PolyartMgr.Mode.EditingMode) {
            editButton.setBackgroundResource(R.drawable.ic_edit_poly_clicked);
            Toast.makeText(this, "You can edit polygons now by touching and dragging control points. To start drawing, click Edit button again. ", Toast.LENGTH_LONG).show();
        }
    }

    private void reDrawDrawingView(){
        DrawingView drawingView = (DrawingView)findViewById(R.id.simpleDrawingView1);
        drawingView.invalidate();
    }

    //Brush dialog listener
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


    //Color Pallette listnere
    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case Utils.COLOR_DIALOG_POLYGON_SELECTOR_ID:
                enableFullScreenMode();
                Utils.Log("onColorSelected called!", 3);
                PolyartMgr.setColor(color);
                break;
            case Utils.COLOR_DIALOG_BACKGROUND_SELECTOR_ID:
                onBackgroundColorSelected(color);
                Utils.Log("bg color selector called!", 3);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        enableFullScreenMode();
        Utils.Log("dialog dismissed color called!",3);
    }


    //Save functions
    private String saveAsJpeg(Bitmap content){
        try {
            File polyartDir = getPolygonArtDir();
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

            return file.getName();
        } catch (Exception e) {
            Utils.Log(e.getMessage(), 5);
            return "Error while saving: "+e.getLocalizedMessage();
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

    private void showShareDialog(final String filename){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Image saved as "+filename+"\n\nShare your creation with your friends!";
        builder.setTitle("Saved!")
                .setIcon(R.drawable.ic_done_save)
                .setMessage(message)
                .setPositiveButton("Share via Twitter", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        shareSavedImage(filename);
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableFullScreenMode();
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void showErrorDialog(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = error;
        builder.setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void shareSavedImage(String filename){
        try {
            File image = new File(getPolygonArtDir(), filename);
            if (image.exists()) {
                //Uri uri = MediaStore.Images.Media.getContentUri(image.getAbsolutePath());
                Uri uri = Uri.parse(image.getAbsolutePath());
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, uri));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.twitter.android");
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I created this using #" + PolygonArt + "App");
                shareIntent.setType("image/*");
                //startActivity(Intent.createChooser(shareIntent, "Share to"));
                startActivity(shareIntent);
            } else {
                Toast.makeText(this, "Error. Image not found!", Toast.LENGTH_SHORT).show();
            }
        }catch (ActivityNotFoundException ae){
            Toast.makeText(this, "Kindly install Twitter to share your image!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteImage(String filename){
        File image = new File(getPolygonArtDir(), filename);
        if(image.exists()){
            image.delete();
        }
    }

    private File getPolygonArtDir(){
        return new File(Environment.getExternalStorageDirectory(), PolygonArt);
    }


    //Canvas Setting Listener
    @Override
    public void onBackgroundColorSelected(int color) {
        PolyartMgr.setReferenceImage(null);
        PolyartMgr.setBackgroundColor(color);
    }

    @Override
    public void onReferenceImageSelected(Bitmap refImage) {
        PolyartMgr.setReferenceImage(refImage);
    }

    @Override
    public void onTranslucencyChanged(int translucency){
        PolyartMgr.setPolygonAlpha(Utils.MAX_ALPHA_OPAQUE - translucency);
        reDrawDrawingView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Utils.Log("Received result!", 3);
            // When an Image is picked
            if (requestCode == Utils.INTENT_RESULT_SELECT_REF_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                Bitmap refImage = BitmapFactory.decodeFile(imgDecodableString);
                onReferenceImageSelected(refImage);
                Utils.Log("Ref iamge set", 3);
            } else {
                Toast.makeText(this, "You haven't picked any image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't fetch image", Toast.LENGTH_LONG).show();
        }

    }
}
