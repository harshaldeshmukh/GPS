package com.example.hp.gps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

public class LocationTracker extends AppCompatActivity implements SensorEventListener {

    Button btnShowLocation,save;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;
    TextView lat,longitudes,alltitudesss,accuracyss,tvBearing,tvtime,tvDirection;
    ProgressBar progressBar;
    SQLiteHelperMapping sqLiteHelperMapping;
    EditText etlocation;
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    Sensor accelerometer;

    Sensor magnetometer;

    TextView tvHeading;
    String ts;
    String dateStr;
    public static String DB_FILEPATH = "/data/data/com.example.hp.gps/databases/gps.db";
    Button view;
    String todate;
    String id;
    Button btnviewUpdate,saveimage;
    ImageView camera,imgPreview;
    String string_lat;
    String string_long;
    String string_acc;
    String timeStamp;
    TextView indicate;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "GPS";
    private Uri fileUri;
    RelativeLayout camera_rl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);
        sqLiteHelperMapping = new SQLiteHelperMapping(this);
        view=(Button)findViewById(R.id.view);
        viewAll();

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }



//        try {
//            sqLiteHelperMapping.importDatabase(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //exportDB();
       // importDB();

       Long tsLong = System.currentTimeMillis()/1000;
         ts = tsLong.toString();
//
//        getDate(tsLong);
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)tsLong*1000);
       // Toast.makeText(LocationTracker.this, sdf.format(currenTimeZone), Toast.LENGTH_SHORT).show();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        todate= dateFormat.format(currentdate());

        btnShowLocation = (Button) findViewById(R.id.button);
        save=(Button)findViewById(R.id.save);

        lat=(TextView)findViewById(R.id.lattitude);
        longitudes=(TextView)findViewById(R.id.longitude);
        alltitudesss=(TextView)findViewById(R.id.alltitude);
        accuracyss=(TextView)findViewById(R.id.accuracy);
        tvBearing=(TextView)findViewById(R.id.tvBearing);
        tvtime=(TextView)findViewById(R.id.tvtime);
        tvtime.setText(sdf.format(currenTimeZone));
        tvDirection=(TextView)findViewById(R.id.  tvDirection);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        etlocation=(EditText)findViewById(R.id.etlocation) ;

        tvHeading = (TextView) findViewById(R.id.tvHeading);

        camera=(ImageView)findViewById(R.id.camera);
        imgPreview=(ImageView)findViewById(R.id.imgPreview);
        indicate=(TextView)findViewById(R.id.indicate);
     //   btnviewUpdate= (Button)findViewById(R.id.button_update);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                progressBar.setVisibility(View.VISIBLE);
                gps = new GPSTracker(LocationTracker.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    double acc= gps.getAccuracy();
                    double alltitude= gps.getAltitude();
                    double bearing= gps.getBearing();
                    double time= gps.getTimestamp();
                    String provider=gps.getProvoder();
                     string_lat = Double.toString(latitude);
                    string_long = Double.toString(longitude);
                     string_acc = Double.toString(acc);
                    String string_allti = Double.toString(alltitude);
                    String string_bearing = Double.toString(bearing);
                    String string_time = Double.toString(time);

                    if(string_lat.length()>0){
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    lat.setText(string_lat);
                    longitudes.setText(string_long);
                    alltitudesss.setText(string_allti);
                    accuracyss.setText(string_acc);
                    tvBearing.setText(string_bearing);
                 //   tvtime.setText(dateStr);

                    // \n is for new line
//                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
//                            + latitude + "\nLong: " + longitude + "\nAcc: " + acc + "\nAltitude: " + alltitude + "\nprovider: " + provider, + Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat.getText().toString().length() ==0  || lat.getText().toString().equals("0.0")){
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(LocationTracker.this);
                    alertDialogBuilder.setMessage("Latitude and Longitude should not be empty or 0.0 ");
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    boolean isInserted = sqLiteHelperMapping.insertData(etlocation.getText().toString(),lat.getText().toString(),
                            longitudes.getText().toString(),
                            accuracyss.getText().toString(),alltitudesss.getText().toString(),tvBearing.getText().toString(),tvtime.getText().toString(),
                            tvDirection.getText().toString(),"NULL");
                    if(isInserted == true) {
                        exportDB();
                        Toast.makeText(LocationTracker.this, "Data Inserted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LocationTracker.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });



        //////Camera
        camera_rl=(RelativeLayout)findViewById(R.id.camera_rl);
//        camera_rl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                captureImage();
//            }
//        });



       camera.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(string_lat==null){
                   final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(LocationTracker.this);
                   alertDialogBuilder.setMessage("Please GET Current location First");
                   alertDialogBuilder.setPositiveButton("OK",
                           new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface arg0, int arg1) {
                                   // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();


                               }
                           });



                   android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                   alertDialog.show();
               }else {
                  captureImage();
               }


           }
       });
        saveimage=(Button)findViewById(R.id.saveimage);



    }
    private void captureImage() {

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

    // start the image capture Intent
    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //  previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {
        try {

            indicate.setVisibility(View.INVISIBLE);
            saveimage.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);

           final Bitmap bm = ProcessingBitmap();
             timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            File exportDir = new File(Environment.getExternalStorageDirectory()+ "/GPS/IMAGES", "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

         final   String pathLink = Environment.getExternalStorageDirectory()+"/GPS/IMAGES" + File.separator +  timeStamp+".jpg";
            saveimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isInserted = sqLiteHelperMapping.insertData(etlocation.getText().toString(),lat.getText().toString(),
                            longitudes.getText().toString(),
                            accuracyss.getText().toString(),alltitudesss.getText().toString(),tvBearing.getText().toString(),tvtime.getText().toString(),
                            tvDirection.getText().toString(),timeStamp+".jpg");
                    if(isInserted == true) {
                        exportDB();
                        Toast.makeText(LocationTracker.this, "Data Inserted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LocationTracker.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                    storeImage(bm, pathLink);
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private Bitmap ProcessingBitmap() {
        Bitmap bm1 = null;
        Bitmap newBitmap = null;
        try {
            // Toast.makeText(MainActivity.this, pickedImage.getPath(), Toast.LENGTH_LONG).show();
            bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
            int w = bm1.getWidth();
            int h = bm1.getHeight();
            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);


            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bm1, 0, 0, null);
            Paint paint = new Paint();
            Paint mPaint = new Paint();
            mPaint.setAlpha(0);
            //paint.setAntiAlias(true);

            mPaint.setColor(Color.parseColor("#488AC7"));
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(0);

            //Drawing the Top rectangle
            Path mPath = new Path();
            RectF mRectF = new RectF(0, 0, w, 30);

            mPath.addRect(mRectF, Path.Direction.CCW);
            canvas.drawPath(mPath, mPaint);

            //Drawing the Bottom rectangle
            RectF mRectF1 = new RectF(0, h - 30, w, h);
            Path mPath1 = new Path();
            mPath1.addRect(mRectF1, Path.Direction.CCW);
            canvas.drawPath(mPath1, mPaint);

            paint.setColor(Color.WHITE);
            paint.setAlpha(255);
            paint.setTextSize(15);

            canvas.drawText( "LAT: " + string_lat  +"    LONG : " +string_long + "  Acc :" + string_acc   , 15, 16, paint);

            canvas.drawText("IMG: " +timeStamp+".jpg     "+"  /HANDY GPS ", 15, h - 4, paint);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newBitmap;
    }

    private void storeImage(Bitmap mBitmap, String path) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"Saving image...",Toast.LENGTH_SHORT).show();
        OutputStream fOut = null;
        File file = new File(path);
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        Toast.makeText(getApplicationContext(),"Image Stored Sucessfully",Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }








    public void viewAll() {
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Cursor res = sqLiteHelperMapping.getAllData();
//                        if(res.getCount() == 0) {
//                            // show message
//                            showMessage("Error","Nothing found");
//                            return;
//                        }
//
//                        StringBuffer buffer = new StringBuffer();
//                        while (res.moveToNext()) {
//                            buffer.append("Id :"+ res.getString(0)+"\n");
//                             id=res.getString(0);
//                            buffer.append("Location Name :"+ res.getString(1)+"\n");
//                            buffer.append("Latitude :"+ res.getString(2)+"\n");
//                            buffer.append("Longitude :"+ res.getString(3)+"\n");
//                            buffer.append("Accuracy :"+ res.getString(4)+"\n");
//                            buffer.append("Altitude :"+ res.getString(5)+"\n\n\n");
//                        }
//
//                        // Show all data
//                        showMessage("Data",buffer.toString());
//                        CustomDialogClass cdd=new CustomDialogClass(LocationTracker.this);
//                        cdd.show();

                        Intent i= new Intent(getApplicationContext(),ActivityTable.class);
                        startActivity(i);
                    }
                }
        );
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

//    private void importDB() {
//        // TODO Auto-generated method stub
//
//        try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data  = Environment.getDataDirectory();
//
//
//            if (sd.canWrite()) {
//                String  currentDBPath= "//data//" + "com.example.hp.gps"
//                        + "//databases//" + "gps.db";
//                String backupDBPath  = "BackupFolder/gps.db";
//              //  f.createNewFile();
//                File  backupDB= new File(data, currentDBPath);
//                File currentDB  = new File(sd, backupDBPath);
//
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//                Toast.makeText(getBaseContext(), backupDB.toString(),
//                        Toast.LENGTH_LONG).show();
//
//            }
//        } catch (Exception e) {
//
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
//                    .show();
//
//        }
//    }

    private Date currentdate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTime();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }



    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }
    float[] mGravity;

    float[] mGeomagnetic;
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated

        float degree = Math.round(event.values[0]);



        tvDirection.setText( Float.toString(degree) );

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float outR[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];

                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);

                SensorManager.getOrientation(outR, orientation);
                float azimut = orientation[0];

                float degrees = (float) (Math.toDegrees(azimut) + 360) % 360;

                System.out.println("degree " + degree);
                String str = String.valueOf(degrees);
                tvHeading.setText(str);
                tvDirection.setText( Float.toString(degrees) );

            }
        }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void exportDB() {
//        String filename = "myfile";
//        String fileContents = "Hello world!";
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(fileContents.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        sqLiteHelperMapping = new SQLiteHelperMapping(getApplicationContext());

        File exportDir = new File(Environment.getExternalStorageDirectory()+ "/GPS", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "gps.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = sqLiteHelperMapping.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM gps_mapp_tables", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7),curCSV.getString(8),curCSV.getString(9)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }
}
