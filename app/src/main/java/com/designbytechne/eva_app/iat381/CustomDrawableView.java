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
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class CustomDrawableView extends View{

    // Position and Acceleration values for drawing the shapes
    private static int accelX;
    private static int accelY;
    private static int xMid, yMid, xPos, yPos, dx, dy, getXMid, getYMid, xMove, yMove, bounceX, bounceY;
    private static int screenWidth, screenHeight;
    private static int radius;
    private static int colorR, colorB;

    private static int level;
    private static int stroke;

    public static boolean circular, square;

    private static String pattern;
    private static String patternString;
    private static String motion;
    private static String motionString;
    private static String BGString;

    private Paint p;
    private int paintAlpha = 255;
    private Point a, b, c;

    // Random Numbers
    private String LOG;
    private int r1;
    private int delay = 3000;
    private Handler h = new Handler();
    Timer timer = new Timer();
    int interval = 3000; // One second

    static ShapeDrawable mDrawable = new ShapeDrawable();

    // ==================================================================================
    // CustomDrawableView Constructor
    // ==================================================================================

    public CustomDrawableView(Context context) {
        super(context);

        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(accelX, accelY, accelX + screenWidth, accelY + screenHeight);

        pattern = getPatternString();

        xMove = 355;
        dx = 1;
        dy = 10;

//        colorR = 100;
//        colorB = 255;
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

    public void setScreenHeight(int height){
        this.screenHeight = height;
    }
    public void setScreenWidth(int width){
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
    public void setBGString(String s){this.BGString = s;}


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
    public String getBGString() {return this.BGString; }


    // ==================================================================================
    // onDraw
    // ==================================================================================

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        // Setting paint colour
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
//        p.setColor(Color.rgb(getXPos(), getLevel() * 2, 255));
        p.setColor(Color.rgb(colorR, getLevel() * 2, colorB));
        p.setAlpha(paintAlpha);
        p.setStrokeWidth(getStroke()/3);

        getXMid = getXMid();
        getYMid = getYMid() - getRadius()*2;

        int getLevel = getLevel();

        // Drawing triangle
        a = new Point(100 + getXMid - getXMid/4, 200 + getYMid/2 - getYMid/5);
        b = new Point(200 + getXMid - getXMid/4, 400 + getYMid/2 - getYMid/5);
        c = new Point(0 + getXMid - getXMid/4, 400 + getYMid/2 - getYMid/5);

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
            bounceX += dx;

            if(bounceX <= 0){dx = -(dx);}
            if(bounceX >= 1000){dx = -(dx);}

            yPos = (int) Math.round(300 * Math.sin(Math.toDegrees(bounceX/4)));
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
            drawSpikes(canvas, xMove , getYMid/2 + (getAccelX()/3), getRadius() + getLevel()/2);
        }
        else if(getPatternString().equals("Auto")){

            // Creates new random number every 1000 milliseconds
            h.postDelayed(new Runnable(){
                @Override
                public void run(){
                    Random r = new Random();
                    r1 = r.nextInt(3 - 0 + 1) + 0;
                    Log.d(LOG, "r1: " + r1);

                    invalidate();
//                    h.postDelayed(this, delay);
                }
            }, delay);

            if(r1 == 0){
                drawCloud(canvas, xMove, getYMid + (getAccelX()/3), getRadius() + getLevel()/8);
                Toast.makeText(getContext(), "Circles #: " + r1, Toast.LENGTH_SHORT).show();
            }
            if(r1 == 1){
                drawSquares(canvas, xMove, getYMid + (getAccelX()/3), getRadius()/2 + getLevel()/2);
                Toast.makeText(getContext(), "Squares #: " + r1, Toast.LENGTH_SHORT).show();
            }
            if(r1 == 2){
                Toast.makeText(getContext(), "Wavy #: " + r1, Toast.LENGTH_SHORT).show();
            }
            if(r1 == 3){
                drawSpikes(canvas, xMove, getYMid + (getAccelX()/3), getRadius() + getLevel()/2);
                Toast.makeText(getContext(), "Spiky #: " + r1, Toast.LENGTH_SHORT).show();
            }
        }
        else{
//            Toast.makeText(getContext(), "No Drawing", Toast.LENGTH_SHORT).show();
        }

        // ========================
        // Selecting Pattern
        // ========================

        if(getBGString().equals("Dark")) {
            this.setBackgroundColor(Color.rgb(0,0,0));
//            Toast.makeText(getContext(), "Dark Selected", Toast.LENGTH_SHORT).show();
            colorR = 100;
            colorB = 255;
        }
        else if(getBGString().equals("Bright")) {
            this.setBackgroundColor(Color.rgb(255,208,115));
//            Toast.makeText(getContext(), "Bright Selected", Toast.LENGTH_SHORT).show();
            colorR = 140;
            colorB = 199;
        }
        else if(getBGString().equals("Colorful")) {
            this.setBackgroundColor(Color.rgb(56,60,232));
//            Toast.makeText(getContext(), "Colorful Selected", Toast.LENGTH_SHORT).show();
            colorR = 209;
            colorB = 177;
        }
        else{
//            Toast.makeText(getContext(), "No Color Selected", Toast.LENGTH_SHORT).show();
        }

        invalidate();
    }

    // ====================
    // Draws Cloud
    // ====================

    protected void drawCloud(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
//        paintAlpha = radius/6;
        canvas.drawCircle(xMid , yMid + yPos/6, radius, p); // draw first circle

        drawCloud(canvas, xMid-getLevel()*2, yMid+getLevel()*2 + yPos/8, radius/3);  // draw circle to the left
        drawCloud(canvas, xMid+getLevel()*2, yMid-getLevel()*2 + yPos/8, radius/3);
        drawCloud(canvas, xMid-getLevel()*2 + yPos/8, yMid-getLevel()*2, radius/3);
        drawCloud(canvas, xMid+getLevel()*2 + yPos/8, yMid+getLevel()*2, radius/3);
    }

    // ====================
    // Draws Squares
    // ====================
    protected void drawSquares(Canvas canvas, int xMid, int yMid, int radius){
        if (radius <= 1) { return; }
//        paintAlpha = radius/2;
        canvas.drawRect(xMid - radius*3, yMid - radius*3, xMid + radius*3, yMid + radius*3, p); // draw first square
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

        path.moveTo(a.x, a.y );
        path.lineTo(a.x, a.y + getLevel() );
        path.lineTo(b.x - xMid - getLevel(), b.y + yMid/2 + getLevel()  );
        path.lineTo(c.x + xMid + getLevel(), c.y + yMid/2 + getLevel()  );
        path.close();
        canvas.drawPath(path, p);

        drawSpikes(canvas, xMid/2, yMid/2, radius/2);
//        drawSpikes(canvas, -xMid/2, yMid/2, radius/2);


//        drawSpikes(canvas, xMid+(getLevel()), yMid-(getLevel()), radius/2);
    }
}