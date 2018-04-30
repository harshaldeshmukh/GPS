package com.example.hp.gps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DemoActivity extends  AppCompatActivity implements SensorEventListener {



    Float azimut;



    public class CustomDrawableView extends View {

        Paint paint = new Paint();

        public CustomDrawableView(Context context) {

            super(context);

            paint.setColor(0xff00ff00);

            paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth(2);

            paint.setAntiAlias(true);



        };



        protected void onDraw(Canvas canvas) {



            int width = getWidth();

            int height = getHeight();

            int centerx = width/2;

            int centery = height/2;

            canvas.drawLine(centerx, 0, centerx, height, paint);

            canvas.drawLine(0, centery, width, centery, paint);

            // Rotate the canvas with the azimut

            if (azimut != null)

                canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);

            paint.setColor(0xffffffff);

            canvas.drawLine(centerx, -1000, centerx, +1000, paint);

            canvas.drawLine(-1000, centery, 1000, centery, paint);



            canvas.drawText("N", centerx+1, centery-10, paint);

            canvas.drawText("S", centerx-1, centery+15, paint);



            paint.setColor(0xff00ff00);

        }

    }



    CustomDrawableView mCustomDrawableView;

    private SensorManager mSensorManager;

    Sensor accelerometer;

    Sensor magnetometer;



    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        mCustomDrawableView = new CustomDrawableView(this);

        setContentView(mCustomDrawableView);    // Register the sensor listeners

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }



    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);



    }



    protected void onPause() {

        super.onPause();

        mSensorManager.unregisterListener(this);

    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }



    float[] mGravity;

    float[] mGeomagnetic;



    public void onSensorChanged(SensorEvent event) {



        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){



            Log.v("ACCELEROMETER","ACCELEROMETER");



            mGravity = event.values;

        }



        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){



            Log.v("MAGNETIC_FIELD","MAGNETIC_FIELD");

            mGeomagnetic = event.values;



        }



        if (mGravity != null && mGeomagnetic != null) {



            float first[] = new float[9];

            float second[] = new float[9];



            boolean success = SensorManager.getRotationMatrix(first, second, mGravity, mGeomagnetic);

            if (success) {

                float orientation[] = new float[3];

                SensorManager.getOrientation(first, orientation);

                azimut = orientation[0]; // orientation contains: azimut, pitch and roll

            }

        }

        mCustomDrawableView.invalidate();

    }

}
