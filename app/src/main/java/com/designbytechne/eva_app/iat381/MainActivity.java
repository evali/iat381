package com.designbytechne.eva_app.iat381;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.DataOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    VideoView vv;

    ToggleButton moveToggleButton;

    static final int PICK_AUDIO_REQUEST = 1;

    public static final int sampleRate = 11025;
    public static final int bufferSizeFactor = 10;

    public AudioRecord audio;
    public int bufferSize;

    public ProgressBar level;
    public Handler handler = new Handler();
    public int lastLevel = 0;
    public DataOutputStream output;

    // Create custom DrawableView
    public static CustomDrawableView myView;
    static ShapeDrawable mDrawable = new ShapeDrawable();

    // Position and Acceleration values for drawing the shapes
    public static int accelX;
    public static int accelY;
    public static int xMid, yMid, xPos, yPos;
    private static int screenWidth, screenHeight;
    public static int radius;

    public static Paint p = new Paint();
    public LinearLayout parent;

    private Sensor Accelerometer;
    private SensorManager sensorManager = null;

    // Media Player and Visualizer
    private MediaPlayer mp;
    private MediaRecorder mRecorder;
    private AudioManager am;
    private int volLevel;
    private Visualizer audioOutput = null;
    private float intensity = 0;
    private int intensityInt;
    private int dbValue;

    private Spinner patternSpinner, motionSpinner, graphicSpinner;

    private GestureDetectorCompat gestureDetect;
    private TextView accelXTextView, levelTextView;
    public static String selectPattern, selectMotion;

    private static final String TAG = "MyActivity";

    LinearLayout.LayoutParams params;

    // =================================================================================
    // onCreate Method
    // =================================================================================

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        Canvas canvas = new Canvas();
        myView = new CustomDrawableView(this);
        myView.setPatternString("Nothing");
        myView.setMotionString("Nothing");
        myView.draw(canvas);
        myView.invalidate();

        accelXTextView = (TextView) findViewById(R.id.accelXTextView);
        levelTextView = (TextView) findViewById((R.id.levelTextView));

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); // 2 pixels height
        ((LinearLayout) findViewById(R.id.topLinearLayout)).addView(new CustomDrawableView(this), params);

        // =========================
        // Dropdown Menu (Spinner)
        // =========================

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        moveToggleButton = (ToggleButton) findViewById(R.id.moveToggleButton);

        // ==================
        // Pattern Spinner
        // ==================
        patternSpinner = (Spinner) findViewById(R.id.patternSpinner);
        patternSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPattern = parent.getItemAtPosition(position).toString();

                if(selectPattern.equals("Circular")){
//                    Toast.makeText(parent.getContext(), "Circular True", Toast.LENGTH_SHORT).show();
                    myView.setPatternString("Circular");
                }
                else if(selectPattern.equals("Square")){
//                    Toast.makeText(parent.getContext(), "Square True", Toast.LENGTH_SHORT).show();
                    myView.setPatternString("Square");
                }
                else if(selectPattern.equals("Wavy")){
//                    Toast.makeText(parent.getContext(), "Wavy True", Toast.LENGTH_SHORT).show();
//                    myView.setPatternString("Spiky");
                }
                else if(selectPattern.equals("Spiky")){
//                    Toast.makeText(parent.getContext(), "Spiky True", Toast.LENGTH_SHORT).show();
                    myView.setPatternString("Spiky");
                }
                else if(selectPattern.equals("Auto")){
//                    Toast.makeText(parent.getContext(), "Auto True", Toast.LENGTH_SHORT).show();
                    myView.setPatternString("Auto");
                }
                else{
//                    Toast.makeText(parent.getContext(), "None", Toast.LENGTH_SHORT).show();
                    myView.setPatternString("Nothing");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pattern_array, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        patternSpinner.setAdapter(adapter); // Apply the adapter to the spinner

        // ==================
        // motionSpinner
        // ==================
        motionSpinner = (Spinner) findViewById(R.id.motionSpinner);
        motionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMotion = parent.getItemAtPosition(position).toString();

                if(selectMotion.equals("Static")){
//                    Toast.makeText(parent.getContext(), "Static True", Toast.LENGTH_SHORT).show();
                    myView.setMotionString("Static");
                }
                else if(selectMotion.equals("Wobble")){
//                    Toast.makeText(parent.getContext(), "Wobble True", Toast.LENGTH_SHORT).show();
                    myView.setMotionString("Wobble");
                }
                else if(selectMotion.equals("Bounce")){
                    myView.setMotionString("Bounce");
                }
                else{
//                    Toast.makeText(parent.getContext(), "None", Toast.LENGTH_SHORT).show();
                    myView.setMotionString("Nothing");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.motion_array, android.R.layout.simple_spinner_dropdown_item);
        motionSpinner.setAdapter(adapter2);

        // ==================
        // graphicSpinner
        // ==================
        graphicSpinner = (Spinner) findViewById(R.id.graphicSpinner);
        graphicSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.graphic_array, android.R.layout.simple_spinner_dropdown_item);
        graphicSpinner.setAdapter(adapter3);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears

        // =========
        // Video
        // =========

        //loop video
        vv = (VideoView) findViewById(R.id.videoView01);

        //Video Loop
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                vv.start(); //need to make transition seamless.
            }
        });

        Uri uri = Uri.parse("http://eva-app.designbytechne.com/videoview1.mp4");

        vv.setVideoURI(uri);
        vv.requestFocus();
//      vv.start();


        // ==================
        // Media Player
        // ==================
        mp = new MediaPlayer();
//        mp = MediaPlayer.create(getApplicationContext(),R.raw.hello);
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);


        // ==================
        // Audio Detection
        // ==================

        level = (ProgressBar) findViewById(R.id.progressbar_level);
        level.setMax(32676);

        ToggleButton record = (ToggleButton) findViewById(R.id.musicToggleButton);

        //check if the device has mic or proximity sensor
        PackageManager manager = getPackageManager();
        boolean hasMic = manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        boolean hasProximitySensor = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
        //If not, present a message
        if (hasProximitySensor==false && hasMic==false){
            Toast.makeText(this,"Your device does not have a microphone or proximity sensor",Toast.LENGTH_LONG).show();
        }

        record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                        // Begin new intent and get audio file
                        Intent mediaIntent = new Intent(Intent.ACTION_PICK);
                        mediaIntent.setType("audio/*"); //set mime type as per requirement
                        mediaIntent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(mediaIntent, PICK_AUDIO_REQUEST);

                        audio.startRecording();
                        Thread thread = new Thread(new Runnable() {
                            public void run() { readAudioBuffer();}
                        });
//                        createVisualizer();
//                        mp.start();

                        thread.setPriority(Thread.currentThread().getThreadGroup().getMaxPriority());
                        thread.start();
                        handler.removeCallbacks(update);
                        handler.postDelayed(update, 100);

                    } else if (audio != null) {
                        audio.stop();
                        audio.release();
                        audio = null;
                        handler.removeCallbacks(update);
                        mp.stop();

                    }
                } catch (Exception e) {
                    System.out.println("Audio Record failed");
                }
            }
        });

        // ===============================
        // CustomDrawableView objects
        // ===============================

        // Get width and height of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;
        xMid = screenWidth/2;
        yMid = screenHeight/2;

        myView.setXMid(this.xMid);
        myView.setYMid(this.yMid);
        myView.setScreenWidth(this.screenWidth);
        myView.setScreenHeight(this.screenHeight);

        radius = 100;
        myView.setRadius(radius);

        myView.setPatternString("Nothing");
        myView.setMotionString("Nothing");

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);

        // Get Gesture Objects
        gestureDetect = new GestureDetectorCompat(this, this);
        gestureDetect.setOnDoubleTapListener(this);

    } // End of onCreate()


    // =================================================================================
    // On Activity Result
    // =================================================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri audioUri = data.getData();
            Log.d("", "Video URI= " + audioUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mp.setDataSource(getApplicationContext(), audioUri);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mp.prepare();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            }
//            mp = MediaPlayer.create(getApplicationContext(), audioUri);

            createVisualizer();
            mp.start();
        }
    }

    // =================================================================================
    // Audio Methods
    // =================================================================================

    public void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];
            int bufferReadResult;

            do {
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sum = 0;
                for (int i = 0; i < bufferReadResult; i++){
//                    output.writeShort(buffer [i]);
                    sum += buffer[i];
//                   sum += buffer [i] * buffer [i];

                    if (buffer[i] > lastLevel) {lastLevel = buffer[i];}
                }
                lastLevel = (int) Math.abs((sum / bufferReadResult));

            } while (bufferReadResult > 0 && audio.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);

            if (audio != null) {
                audio.release();
                audio = null;
                handler.removeCallbacks(update);
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    public Runnable update = new Runnable() {

        public void run() {
            MainActivity.this.level.setProgress(lastLevel);
//            lastLevel *= .01;
            int updateLastlevel = (int) Math.round(lastLevel) * 2;
//            int updateLastlevel = (int) Math.round(lastLevel);

//            myView.setLevel(updateLastlevel);
//            levelTextView.setText("Level: " + lastLevel);
            handler.postAtTime(this, SystemClock.uptimeMillis() + 0);
        }
    };

    // =================================================================================
    // Saved Instance State
    // =================================================================================

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("SaveState", "onSaveInstanceState called");

        //save current counter value in bundle key - value
        outState.putInt("LEVEL_VALUE", lastLevel);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("SaveState", "onRestoreInstanceState called");

        //retrieve current counter value from bundle based on key
        lastLevel = savedInstanceState.getInt("LEVEL_VALUE");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent= new Intent(this, Setting.class);
            startActivity(intent);
        }

        if (id == R.id.videos) {
//            Intent intent= new Intent(this, Videos.class);
//            startActivity(intent);
            Intent intent = new Intent(this, Noise.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    // =================================================================================
    // Sensor Changed Methods
    // =================================================================================

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] g = sensorEvent.values.clone();

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = (int) Math.pow(g[0], 3);
            accelY = (int) Math.pow(g[1], 3);

            myView.setAccelX(this.accelX);
            myView.setAccelY(this.accelY);
            accelXTextView.setText("AccelX: " + accelX);
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

    // ==================================================================================
    // Gesture Listener Methods
    // =================================================================================

    @Override
    public boolean onTouchEvent(MotionEvent e){
        xPos = (int) e.getX();
        yPos = (int) e.getY();
        myView.setXPos(this.xPos);
        myView.setYPos(this.yPos);

        this.gestureDetect.onTouchEvent(e);
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
    public void onLongPress(MotionEvent e) { }

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


    // ==================================================================================
    // Audio Visualizer
    // ==================================================================================

    private void createVisualizer(){
        int rate = Visualizer.getMaxCaptureRate();
        int audio = mp.getAudioSessionId();

        audioOutput = new Visualizer(audio); // get output audio stream
        audioOutput.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
        {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//                intensity = ((float) bytes[0] + 128f);
//                intensityInt = Math.round(intensity);
//                levelTextView.setText("Intensity: " + intensityInt);
//                myView.setLevel(intensityInt);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, final byte[] bytes, int samplingRate) {
                for (int i = 0; i < bytes.length / 2; i++) {

                    byte rfk = bytes[2 * i];
                    byte ifk = bytes[2 * i + 1];
                    float magnitude = (float) (Math.sqrt(rfk * rfk) + Math.sqrt(ifk * ifk));
                    dbValue = (int) (20 * Math.log10(magnitude/32767));
                }

//                levelTextView.setText("Db : " + dbValue);
//                myView.setLevel(dbValue);

                int freq = Math.abs(bytes[0] );
                levelTextView.setText("Freq : " + freq);
                myView.setLevel(freq);
                myView.setStroke(freq);
            }
        };

//        audioOutput.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate(), false, true);
        audioOutput.setDataCaptureListener(captureListener, 20000, false, true);
        audioOutput.setEnabled(true);

//        Log.d("rate", String.valueOf(Visualizer.getMaxCaptureRate()));
    }




    // =================================================================================
    // Spinner button listener
    // =================================================================================

//    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        // An item was selected. You can retrieve the selected item using
//
//        String graphicString = parent.getItemAtPosition(pos).toString();
//
//        if(graphicString == "Circular"){
//            Toast.makeText(parent.getContext(), "Circular True", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//
//    // get the selected dropdown list value
//    public void addListenerOnButton() {
//
//        patternSpinner = (Spinner) findViewById(R.id.patternSpinner);
//        themeSpinner = (Spinner) findViewById(R.id.themeSpinner);
//        graphicSpinner = (Spinner) findViewById(R.id.graphicSpinner);
//        // btnSubmit = (Button) findViewById(R.id.btnSubmit);
//
//        patternSpinner.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                if( String.valueOf(patternSpinner.getSelectedItem()) == "Spiky"){
//                    // Toast.makeText(getApplication(), "Spiky Select", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
//    }


}
