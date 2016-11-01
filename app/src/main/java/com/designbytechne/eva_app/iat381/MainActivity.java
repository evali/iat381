package com.designbytechne.eva_app.iat381;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity{

    VideoView vv;

    ToggleButton moveToggleButton;

    public static final int sampleRate = 11025;
    public static final int bufferSizeFactor = 10;

    public AudioRecord audio;
    public int bufferSize;

    public ProgressBar level;

    public Handler handler = new Handler();

    public int lastLevel = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });

//        musicToggleButton = (ToggleButton) findViewById(R.id.musicToggleButton);
        moveToggleButton = (ToggleButton) findViewById(R.id.moveToggleButton);

        Spinner patternSpinner = (Spinner) findViewById(R.id.patternSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pattern_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        patternSpinner.setAdapter(adapter);

        Spinner themeSpinner = (Spinner) findViewById(R.id.themeSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.theme_array, android.R.layout.simple_spinner_item);
        themeSpinner.setAdapter(adapter2);

        Spinner graphicSpinner = (Spinner) findViewById(R.id.graphicSpinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.graphic_array, android.R.layout.simple_spinner_item);
        graphicSpinner.setAdapter(adapter3);

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
//        vv.start();




        //audio detection
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
                // TODO Auto-generated method stub

                try {
                    if (isChecked) {


                        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT) * bufferSizeFactor;

                        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                        audio.startRecording();

                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                readAudioBuffer();
                            }
                        });

                        thread.setPriority(Thread.currentThread().getThreadGroup().getMaxPriority());

                        thread.start();

                        handler.removeCallbacks(update);
                        handler.postDelayed(update, 25);

                    } else if (audio != null) {
                        audio.stop();
                        audio.release();
                        audio = null;
                        handler.removeCallbacks(update);
                    }
                }catch (Exception e) {
                    System.out.println("Audio Record failed");
                }

            }
        });

    }

    public void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult;

            do {

                bufferReadResult = audio.read(buffer, 0, bufferSize);

                for (int i = 0; i < bufferReadResult; i++){

                    if (buffer[i] > lastLevel) {
                        lastLevel = buffer[i];
                    }

                }

            } while (bufferReadResult > 0 && audio.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);

            if (audio != null) {
                audio.release();
                audio = null;
                handler.removeCallbacks(update);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Runnable update = new Runnable() {

        public void run() {

            MainActivity.this.level.setProgress(lastLevel);

            lastLevel *= .5;

            handler.postAtTime(this, SystemClock.uptimeMillis() + 500);

        }

    };

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

}
