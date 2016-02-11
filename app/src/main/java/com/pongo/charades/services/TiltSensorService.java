package com.pongo.charades.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by rsaki on 2/9/2016.
 */
public class TiltSensorService implements SensorEventListener {
    public interface TiltEventListener {
        void onTiltChanged(State oldState, State newState);
    }

    public enum State {
        UPWARDS,
        NEUTRAL,
        DOWNWARDS
    }

    private float[] mGravity;
    private float[] mGeomagnetic;

    private final TiltEventListener mListener;
    private State mState;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    public TiltSensorService(Context context, TiltEventListener listener) {
        mListener = listener;
        mState = State.NEUTRAL;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
            checkTilt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void resume() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void pause() {
        mSensorManager.unregisterListener(this);
    }

    private void checkTilt() {
        float R[] = new float[9];
        float I[] = new float[9];

        if (mGravity == null || mGeomagnetic == null || !SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic))
            return;

        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);  // azimuth, pitch and roll

        float pitch = orientation[1];
        float[] inclineGravity = mGravity.clone();

        // Normalize the accelerometer vector
        double gNorm = Math.sqrt(inclineGravity[0] * inclineGravity[0]
                               + inclineGravity[1] * inclineGravity[1]
                               + inclineGravity[2] * inclineGravity[2]);
        inclineGravity[0] = (float) (inclineGravity[0] / gNorm);
        inclineGravity[1] = (float) (inclineGravity[1] / gNorm);
        inclineGravity[2] = (float) (inclineGravity[2] / gNorm);

        // Check if device is flat on ground or not
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));

        Float objPitch = new Float(pitch);
        Float objZero = new Float(0.0);
        Float objZeroPointTwo = new Float(0.2);
        Float objZeroPointTwoNegative = new Float(-0.2);

        int objPitchZeroResult = objPitch.compareTo(objZero);
        int objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch);
        int objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative);

        if ((objPitchZeroResult > 0 && objPitchZeroPointTwoResult > 0) ||
            (objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult > 0)) {
            switch (mState) {
                case NEUTRAL:
                    if (inclination < 40) changeState(State.UPWARDS);
                    else if (inclination > 140) changeState(State.DOWNWARDS);
                    break;
                case DOWNWARDS:
                    if (inclination < 40) changeState(State.UPWARDS);
                    else if (inclination < 110) changeState(State.NEUTRAL);
                    break;
                case UPWARDS:
                    if (inclination > 70) changeState(State.NEUTRAL);
                    else if (inclination > 140) changeState(State.DOWNWARDS);
                    break;
            }
        }
    }

    private void changeState(State newState) {
        mListener.onTiltChanged(mState, newState);
        mState = newState;
    }
}
