package com.example.riceyu.imageanimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.gesture.Gesture;
import static android.view.GestureDetector.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();

    // Position and Acceleration values for drawing the shapes
    public static int accelX;
    public static int accelY;
    public int xMid, yMid, xPos, yPos;
    private int screenWidth, screenHeight;
    public int radius = 100;

    public Paint p = new Paint();
    public LinearLayout parent;
    private MotionEvent simulationEvent;

    private Sensor Accelerometer;
    private SensorManager sensorManager = null;

    private GestureDetectorCompat gestureDetect;

    private static final String TAG = "MyActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = (LinearLayout) findViewById(R.id.parent);

        // Get width and height of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        xMid = screenWidth/2;
        yMid = screenHeight/2;

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Create new drawable View
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);

        // Get Gesture Objects
        gestureDetect = new GestureDetectorCompat(this, this);
        gestureDetect.setOnDoubleTapListener(this);
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = (int) Math.pow(sensorEvent.values[0], 3);
            accelY = (int) Math.pow(sensorEvent.values[1], 3);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {

        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    // =========================
    // Gesture Listener Methods
    // =========================

    @Override
    public boolean onTouchEvent(MotionEvent e){
        xPos = (int) e.getX();
        yPos = (int) e.getY();

        this.gestureDetect.onTouchEvent(e);
        Toast toast = Toast.makeText(getApplication(), "Touched screen", Toast.LENGTH_LONG);
        toast.show();
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        p.setColor(Color.rgb(xPos, yPos, 255));
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG,"Touched: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // p.setColor(Color.BLUE);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        p.setColor(Color.rgb(xPos, yPos, 255));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    // ========================
    // Custom View Class
    // ========================

    public class CustomDrawableView extends View{
        static final int width = 50;
        static final int height = 50;

        public CustomDrawableView(Context context) {
            super(context);
            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xff74AC23);
            mDrawable.setBounds(accelX, accelY, accelX + width, accelY + height);
        }

        protected void onDraw(Canvas canvas) {

            // Create new paint object and selecting color
            // p = new Paint();
            // p.setColor(Color.WHITE);

            // Selecting what to draw
            drawCloud(canvas, xMid - accelX, yMid, radius + accelY/2);
            // drawSquares(canvas, xMid, yMid, radius + accelY/2);

            invalidate();
        }

        // ============
        // Draws Cloud
        // ============
        protected void drawCloud(Canvas canvas, int xMid, int yMid, int radius){

            if (radius <= 1) { return; }
            canvas.drawCircle(xMid, yMid - radius, radius, p); // draw first circle
            drawCloud(canvas, xMid-radius, yMid, radius/2);  // draw circle to the left
            drawCloud(canvas, xMid+radius, yMid, radius/2);  // draw circle to the right
        }

        // ============
        // Draws Squares
        // ============
        protected void drawSquares(Canvas canvas, int xMid, int yMid, int radius){

            if (radius <= 1) { return; }
            canvas.drawRect(xMid, yMid - radius, radius, radius + screenHeight/2, p); // draw first square
            drawSquares(canvas, xMid-radius, yMid, radius/2);  // draw square to the left
            drawSquares(canvas, xMid+radius, yMid, radius/2);  // draw square to the right
        }

    }
}