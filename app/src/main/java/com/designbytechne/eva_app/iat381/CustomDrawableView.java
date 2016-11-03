package com.designbytechne.eva_app.iat381;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by riceyu on 2016-11-01.
 */

public class CustomDrawableView extends View{

    SensorActivity mySA;

    // Position and Acceleration values for drawing the shapes
    private static int accelX;
    private static int accelY;
    private static int xMid, yMid, xPos, yPos;
    private static int screenWidth, screenHeight;
    public  int radius;

    private Paint p;

    CustomDrawableView mCustomDrawableView;
    static ShapeDrawable mDrawable = new ShapeDrawable();

    public CustomDrawableView(Context context) {
        super(context);
//        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mInflater.inflate(R.layout.activity_main, this, true);

        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(accelX, accelY, accelX + screenWidth, accelY + screenHeight);

    }

    protected void onDraw(Canvas canvas) {
        // Selecting what to draw

        drawCloud(canvas, 250, 500, 300);
        // canvas.drawCircle(50,50,50, p);
        Toast.makeText(getContext(),"radius " + radius,Toast.LENGTH_LONG).show();

        // Toast.makeText(getApplication(), "Volume: " + intensityInt, Toast.LENGTH_LONG).show();

//        drawCloud(canvas, 250, 250, mRadius);
//        drawCloud(canvas, xMid, yMid+(accelY/3), radius + accelY/2);
//        drawSquares(canvas, mySA.getXMid(), mySA.getYMid(), mySA.getRadius() + mySA.getAccelY()/2);
        invalidate();
    }

    // ====================
    // Draws Cloud
    // ====================
    protected void drawCloud(Canvas canvas, int xMid, int yMid, int radius){

        if (radius <= 1) { return; }

        canvas.drawCircle(xMid, yMid - radius, radius, p); // draw first circle
        drawCloud(canvas, xMid-radius-accelY, yMid+accelY, radius/2);  // draw circle to the left
        drawCloud(canvas, xMid+radius+accelY, yMid-accelY, radius/2);  // draw circle to the right
    }

    // ====================
    // Draws Squares
    // ====================
    protected void drawSquares(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
        canvas.drawRect(xMid, yMid - radius, radius, radius + screenHeight/2, p); // draw first square
        drawSquares(canvas, xMid-radius, yMid, radius/2);  // draw square to the left
        drawSquares(canvas, xMid+radius, yMid, radius/2);  // draw square to the right
    }

}