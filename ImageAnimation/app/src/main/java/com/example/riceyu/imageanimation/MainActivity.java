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
import android.view.Display;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {
    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();

    // Position and Acceleration values for drawing the shapes
    public static int accelX;
    public static int accelY;
    public int xMid, yMid;
    private int screenWidth, screenHeight;
    public int radius = 100;

    public Paint p;

    private Sensor Accelerometer;
    private SensorManager sensorManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get width and height of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        xMid = screenWidth/2;
        yMid = screenHeight/2;

        // Create new drawable View
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
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

    public class CustomDrawableView extends View {
        static final int width = 50;
        static final int height = 50;

        public CustomDrawableView(Context context) {
            super(context);
            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xff74AC23);
            mDrawable.setBounds(accelX, accelY, accelX + width, accelY + height);
        }

        protected void onDraw(Canvas canvas) {
            // RectF oval = new RectF(accelX, accelY, accelX + width, accelY + height );

            // Create new paint object and selecting color
            p = new Paint();
            p.setColor(Color.WHITE);

            // Selecting what to draw

            //drawCloud(canvas, xMid, yMid, radius + accelY/2);
            drawSquares(canvas, xMid, yMid, radius + accelY/2);

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