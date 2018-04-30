package com.example.hp.gps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by MRSAC on 17/04/18.
 */

public class SQLiteHelperMapping extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "gps.db";
    public static final String TABLE_NAME = "gps_mapp_tables";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "LOCATION_NAME";
    public static final String COL_3 = "LAT";
    public static final String COL_4 = "LANG";
    public static final String COL_5 = "ACCURACY";
    public static final String COL_6 = "ALLTITUDE";
    public static final String COL_7 = "BEARING";
    public static final String COL_8 = "TIMESTAMP";
    public static final String COL_9 = "DIRECTION";
    public static final String COL_10 = "IMG";


    public SQLiteHelperMapping(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,LOCATION_NAME TEXT,LAT TEXT,LANG TEXT," +
                "ACCURACY TEXT,ALLTITUDE TEXT,BEARING TEXT,TIMESTAMP TEXT,DIRECTION TEXT,IMG TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String location_name,String lat, String lang, String accuracy, String alltitude,String bearing,String timestamp,String direction,String img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, location_name);
        contentValues.put(COL_3, lat);
        contentValues.put(COL_4, lang);
        contentValues.put(COL_5, accuracy);
        contentValues.put(COL_6, alltitude);
        contentValues.put(COL_7, bearing);
        contentValues.put(COL_8, timestamp);
        contentValues.put(COL_9, direction);
        contentValues.put(COL_10, img);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<GpsModel> retrieveSpacecrafts()
    {
        ArrayList<GpsModel> spacecrafts=new ArrayList<>();

        String[] columns={COL_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_7,COL_8,COL_9,COL_10};

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            //db = helper.getWritableDatabase();
            Cursor c=db.query(TABLE_NAME,columns,null,null,null,null,null);

            GpsModel s;

            if(c != null)
            {
                while (c.moveToNext())
                {
                    String location_id=c.getString(0);
                    String location_name=c.getString(1);
                    String lat=c.getString(2);
                    String lang=c.getString(3);
                    String accuracy=c.getString(4);
                    String alltutude=c.getString(5);
                    String bearing=c.getString(6);

                    String timestamp=c.getString(7);
                    String direction=c.getString(8);
                    String img=c.getString(9);
                  //  String s_destination=c.getString(3);



                    s=new GpsModel();
                    s.setLocationname(location_name);
                    s.setLat(lat);
                    s.setLang(lang);
                    s.setAccuracy(accuracy);
                    s.setAlltitude(alltutude);
                    s.setBearing(bearing);
                    s.setTimestamp(timestamp);
                    s.setDirection(direction);
                    s.setLocation_id(location_id );
                    s.setImg(img );

                    spacecrafts.add(s);
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }


        return spacecrafts;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM gps_mapp_tables ", null);

        return res;

    }

    public boolean updateData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);

        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public static String DB_FILEPATH = "/data/data/com.example.hp.gps/databases/gps.db";

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will
        // commit the created empty database to internal storage.
      //  close();
        File newDb = new File(dbPath,"abc.rar");
        //newDb.getParentFile().createNewFile();
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper
            // will cache it and mark it as created.
            getWritableDatabase().close();
            backupDatabase();
            return true;
        }
        return false;
    }

    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public void backupDatabase() throws IOException {

        if (isSDCardWriteable()) {
            // Open your local db as the input stream
            String inFileName = DB_FILEPATH;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/syntaxionary";
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
        }
    }

    private boolean isSDCardWriteable() {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }


}
