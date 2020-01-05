package com.rajdeepsingh.shake_flashlight1;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Build;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity implements AccelerometerListener {

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;
    MediaPlayer mp;

    ImageButton btnSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSwitch = (ImageButton)findViewById(R.id.btnSwitch);

        isFlashOn=false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);

        }
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {

            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            android.app.AlertDialog builder = alert.create();
            builder.show();
            return;
        }
        getCamera();
        toggleButtonImage();
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void  onShake(float force) {

        if (isFlashOn) {
                turnOffFlash();
                 toggleButtonImage();


            } else {
                turnOnFlash();
                toggleButtonImage();

            }

    }


    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {

            }
        }
    }

    private void  turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null ) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // play sound
            playSound();

            // changing button/switch image
            toggleButtonImage();
        }

    }
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }



    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();

    }



    @Override
    public void onResume() {
        super.onResume();
     //   Toast.makeText(getBaseContext(), "onResume Accelerometer Started", Toast.LENGTH_SHORT).show();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
        else
        {
            Toast.makeText(getBaseContext(), "Accelerometer Not Supported on Your Device",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

          //  Toast.makeText(getBaseContext(), "onStop Accelerometer Stoped", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        turnOffFlash();
        Log.i("Sensor", "Service  destroy");

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

          //  Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stoped", Toast.LENGTH_SHORT).show();
        }

    }

    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.flashlight);
        }else{
            btnSwitch.setImageResource(R.drawable.bulb);
        }
    }

}
