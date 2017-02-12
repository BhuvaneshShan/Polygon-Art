package bhuva.polygonart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import bhuva.polygonart.Polyart.PolyartMgr;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void onClickCreateNewFile(View view) {
        PolyartMgr.clearAll();
        DrawingView drawingView = (DrawingView)findViewById(R.id.simpleDrawingView1);
        drawingView.invalidate();
    }

    public void onClickBrushSize(View view) {
        int newSize = 100;
        PolyartMgr.selectBrushSize(newSize);
    }

    public void onClickColorSelector(View view) {
        int newColor = Color.BLUE;
        PolyartMgr.selectColor(newColor);
    }

    public void onClickDone(View view) {
    }
}
