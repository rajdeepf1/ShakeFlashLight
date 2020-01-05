package com.rajdeepsingh.shake_flashlight1;

/**
 * Created by RAJDEEP SINGH on 16-02-2017.
 */

public interface AccelerometerListener {

    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);

}
