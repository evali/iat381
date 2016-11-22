package com.designbytechne.eva_app.iat381;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

public class CustomDrawableView extends View{

    // Position and Acceleration values for drawing the shapes
    private static int accelX;
    private static int accelY;
    private static int xMid, yMid, xPos, yPos, dx, dy, getXMid, getYMid, xMove, yMove, gravityX, gravityY;
    private static int screenWidth, screenHeight;
    private static int radius;

    private static int level;
    private static int stroke;

    public static boolean circular, square;

    private static String pattern;
    private static String patternString;
    private static String motion;
    private static String motionString;

    private Paint p;
    private int paintAlpha = 255;
    private Point a, b, c;

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

        xMove = 325;
        dx = 1;
        dy = 10;

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
    public void setStroke(int stroke) {this.stroke = stroke;}

    public void setCircular(Boolean b) {this.circular = b; }
    public void setSquare(Boolean b) {this.square = b; }

    public void setPatternString(String s){this.patternString = s;}
    public void setMotionString(String s){this.motionString = s;}


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
    public int getStroke() {return this.stroke; }

    public boolean getCircular() { return this.circular; }
    public boolean getSquare() { return this.square; }

    public String getPatternString() {return this.patternString; }
    public String getMotionString() {return this.motionString; }


    // ==================================================================================
    // onDraw
    // ==================================================================================

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Setting paint colour
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.rgb(getXPos(), getLevel() * 2, 255));
        p.setAlpha(paintAlpha);
        p.setStrokeWidth(getStroke()/3);

        getXMid = getXMid();
        getYMid = getYMid() - getRadius()*2;

        gravityX = (int) 0.1;
        gravityY = (int) 0.5;

        int getLevel = getLevel();

        // Drawing triangle
        a = new Point(0, 0);
        b = new Point(0, 100);
        c = new Point(87, 50);

        // ========================
        // Selecting Movement
        // ========================

        if(getMotionString().equals("Static")) {
            // Do Nothing
        }
        else if(getMotionString().equals("Wobble")){
            xMove += dx;

            if(xMove <= 0){dx = -(dx);}
            if(xMove >= 600){dx = -(dx);}
        }
        else if(getMotionString().equals("Bounce")){
            xMove += dx;

            if(xMove <= 0){dx = -(dx);}
            if(xMove >= 600){dx = -(dx);}

            yPos = (int) Math.round(120 * Math.sin(Math.toDegrees(xMove/5)));
        }
        else{
//            Toast.makeText(getContext(), "No Motion", Toast.LENGTH_SHORT).show();
        }

        // ========================
        // Selecting Pattern
        // ========================
        // Toast.makeText(getContext(), "Pattern " + pattern, Toast.LENGTH_SHORT).show();

        if(getPatternString().equals("Circular")) {
            drawCloud(canvas, xMove, getYMid + (getAccelX()/3), getRadius() + getLevel()/8);
        }
        else if(getPatternString().equals("Square")){
            drawSquares(canvas, xMove, getYMid + (getAccelX()/3), getRadius()/2 + getLevel()/2);
        }
        else if(getPatternString().equals("Spiky")){
            drawSpikes(canvas, xMove, getYMid + (getAccelX()/3), getRadius() + getLevel()/2);
        }
        else{
//            Toast.makeText(getContext(), "No Drawing", Toast.LENGTH_SHORT).show();
        }

        invalidate();
    }

    // ====================
    // Draws Cloud
    // ====================

    protected void drawCloud(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
//        paintAlpha = radius/6;
        canvas.drawCircle(xMid , yMid, radius, p); // draw first circle

        drawCloud(canvas, xMid-getLevel(), yMid+getLevel() + yPos/8, radius/3);  // draw circle to the left
        drawCloud(canvas, xMid+getLevel(), yMid-getLevel() + yPos/8, radius/3);
        drawCloud(canvas, xMid-getLevel()+ yPos/8, yMid-getLevel(), radius/3);
        drawCloud(canvas, xMid+getLevel()+ yPos/8, yMid+getLevel(), radius/3);

//       ========================

//        drawCloud(canvas, xMid-radius-accelY, yMid+radius+accelY, radius/3);  // draw circle to the left
//        drawCloud(canvas, xMid+radius+accelY, yMid-radius-accelY, radius/3);  // draw circle to the right
//        drawCloud(canvas, xMid-radius-accelY, yMid-radius-accelY, radius/3);  // draw circle to the left
//        drawCloud(canvas, xMid+radius+accelY, yMid+radius+accelY, radius/3);  // draw circle to the right
    }

    // ====================
    // Draws Squares
    // ====================
    protected void drawSquares(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
//        paintAlpha = radius/2;
        canvas.drawRect(xMid - radius*3, yMid - radius*3, xMid + radius*3, yMid + radius*3, p); // draw first square
//        canvas.drawRect(xMid, yMid + radius, radius, radius + screenHeight/2, p); // draw first square
        drawSquares(canvas, xMid-(accelX/3), yMid+(accelX/6), radius/2);  // draw square to the left
        drawSquares(canvas, xMid+(accelX/3), yMid-(accelX/6), radius/2);  // draw square to the right
    }

    // ====================
    // Draws Spikes
    // ====================
    protected void drawSpikes(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
//        paintAlpha = radius/2;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x + xMid, b.y + yMid);
        path.lineTo(c.x + xMid, c.y +yMid);
        path.lineTo(a.x +xMid, a.y +yMid + radius/3);
        path.close();
        canvas.drawPath(path, p);
        drawSpikes(canvas, xMid-(radius-accelX)/3, (yMid+accelX)/3, radius/2);
        drawSpikes(canvas, xMid+(radius+accelX)/3, (yMid-accelX)/3, radius/2);
    }

}