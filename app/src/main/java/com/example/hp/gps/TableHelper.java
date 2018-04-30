package com.example.hp.gps;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by MRSAC on 18/04/18.
 */

public class TableHelper {
    Context c;
    private String[] spaceProbeHeaders={"ID","LOCATION","LAT","LANG","ACCURACY","ALTITUDE","BEARING","TIMESTAMP","DIRECTION"};
    private String[][] spaceProbes;

    //CONSTRUCTOR
    public TableHelper(Context c) {
        this.c = c;
    }

    //RETURN TABLE HEADERS
    public String[] getSpaceProbeHeaders()
    {
        return spaceProbeHeaders;
    }

    //RETURN TABLE ROWS
    public  String[][] getSpaceProbes()
    {
        ArrayList<GpsModel> spacecrafts=new SQLiteHelperMapping(c).retrieveSpacecrafts();
        GpsModel s;

        spaceProbes= new String[spacecrafts.size()][9];

        for (int i=0;i<spacecrafts.size();i++) {

            s=spacecrafts.get(i);
            spaceProbes[i][0]=s.getLocation_id();
            spaceProbes[i][1]=s.getLocationname();
            spaceProbes[i][2]=s.getLat();
            spaceProbes[i][3]=s.getLang();

            spaceProbes[i][4]=s.getAccuracy();
            spaceProbes[i][5]=s.getAlltitude();
            spaceProbes[i][6]=s.getBearing();

            spaceProbes[i][7]=s.getTimestamp();
            spaceProbes[i][8]=s.getDirection();
           // spaceProbes[i][8]=s.getLang();
        }

        return spaceProbes;

    }
}
