package com.designbytechne.eva_app.iat381;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Eva on 2016-10-31.
 */

public class Noise extends AppCompatActivity {

    private static final int sampleRate = 11025;
    private static final int bufferSizeFactor = 10;

    private AudioRecord audio;
    private int bufferSize;

    private ProgressBar level;

    private Handler handler = new Handler();

    private int lastLevel = 0;


    private SensorManager mySensorManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noise);


        level = (ProgressBar) findViewById(R.id.progressbar_level);

        level.setMax(32676);

        ToggleButton record = (ToggleButton) findViewById(R.id.togglebutton_record);

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


    private void readAudioBuffer() {

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

    private Runnable update = new Runnable() {

        public void run() {

            Noise.this.level.setProgress(lastLevel);

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


}