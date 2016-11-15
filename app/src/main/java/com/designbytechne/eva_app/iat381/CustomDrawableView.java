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

import java.util.Random;

public class CustomDrawableView extends View{

    // Position and Acceleration values for drawing the shapes
    private static int accelX;
    private static int accelY;
    private static int xMid, yMid, xPos, yPos;
    private static int screenWidth, screenHeight;
    private static int radius;

    private static int level;

    public static boolean circular, square;

    private static String pattern, theme;
    private static String patternString, themeString;

    private Paint p;

    static ShapeDrawable mDrawable = new ShapeDrawable();

    // ==================================================================================
    // CustomDrawableView Constructor
    // ==================================================================================

    public CustomDrawableView(Context context) {
        super(context);
//        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mInflater.inflate(R.layout.activity_main, this, true);

        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(accelX, accelY, accelX + screenWidth, accelY + screenHeight);

        pattern = getPatternString();
        theme = getThemeString();

//         p = new Paint();
//         p.setColor(Color.WHITE);
    }

    // ==================================================================================
    // Set Methods
    // ==================================================================================

    public void setAccelX(int accelX){
        this.accelX = accelX;
    }
    public void setAccelY(int accelY){
        this.accelY = accelY;
    }
    public void setXPos(int xPos){
        this.xPos = xPos;
    }
    public void setYPos(int yPos){
        this.yPos = yPos;
    }
    public void setXMid(int xMid){
        this.xMid = xMid;
    }
    public void setYMid(int yMid){
        this.yMid = yMid;
    }

    public void setHeight(int height){
        this.screenHeight = height;
    }
    public void setWidth(int width){
        this.screenWidth = width;
    }
    public void setRadius(int radius){
        this.radius = radius;
    }
    public void setPaint(Paint paint) {this.p = paint;}

    public void setLevel(int level) {this.level = level;}

    public void setCircular(Boolean b) {this.circular = b; }
    public void setSquare(Boolean b) {this.square = b; }

    public void setPatternString(String s){this.patternString = s;}
    public void setThemeString(String s){this.themeString = s;}


    // ==================================================================================
    // Get Methods
    // ==================================================================================

    public int getAccelX(){
        return this.accelX;
    }
    public int getAccelY(){
        return this.accelY;
    }
    public int getXMid(){
        return this.xMid;
    }
    public int getYMid(){
        return this.yMid;
    }
    public int getXPos(){
        return this.xPos;
    }
    public int getYPos(){
        return this.yPos;
    }

    public int getScreenWidth(){
        return this.screenWidth;
    }
    public int getScreenHeight(){
        return this.screenHeight;
    }
    public int getRadius(){
        return this.radius;
    }
    public Paint getPaint(){
        return this.p;
    }

    public int getLevel() {return this.level; }

    public boolean getCircular() { return this.circular; }
    public boolean getSquare() { return this.square; }

    public String getPatternString() {return this.patternString; }
    public String getThemeString() {return this.themeString; }


    // ==================================================================================
    // onDraw
    // ==================================================================================

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Setting paint colour
        p = new Paint();
        p.setColor(Color.rgb(getXPos(), getYPos(), 255));

//        Selecting color based on theme
        if(getThemeString().equals("Bright")){
            p.setColor(Color.rgb(getXPos(), getYPos(), 255));
            Toast.makeText(getContext(), "Theme is bright", Toast.LENGTH_SHORT).show();
        }
        else if(getThemeString().equals("Dark")) {
            p.setColor(Color.rgb(getXPos() + getYPos(), getXPos() + getYPos(), getXPos() + getYPos()));
            Toast.makeText(getContext(), "Theme is Dark", Toast.LENGTH_SHORT).show();
        }
        else if(getThemeString().equals("Colorful")){
            Random rand = new Random();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            p.setColor(Color.rgb(r,g,b));
            Toast.makeText(getContext(), "Theme is Colorful", Toast.LENGTH_SHORT).show();

        }
        // Selecting what to draw
//        drawCloud(canvas, getXMid(), getYMid() + (getAccelY() / 3), 100 + getLevel()/3 );
        Toast.makeText(getContext(), "Pattern " + pattern, Toast.LENGTH_SHORT).show();

        if(getPatternString().equals("Circular")) {
            drawCloud(canvas, getXMid(), getYMid() + (getAccelY()/3), getRadius() + getLevel() / 2);
        }
        else if(getPatternString().equals("Square")){
            drawSquares(canvas, getXMid(), getYMid() + (getAccelY()/3), getRadius()/4 + getLevel()/2);
        }
        else{
            Toast.makeText(getContext(), "No Drawing", Toast.LENGTH_SHORT).show();
        }

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