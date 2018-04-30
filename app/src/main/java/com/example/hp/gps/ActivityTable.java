package com.example.hp.gps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ActivityTable extends AppCompatActivity {
    TableView<String[]> tb;
    TableHelper tableHelper;
    private String[][] spaceProbes;
    EditText nameEditText;
    Button saveBtn;
    SQLiteHelperMapping sqLiteHelperMapping;
    AppBarLayout appBarLayout;
    Button updated,delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        sqLiteHelperMapping = new SQLiteHelperMapping(this);

        tableHelper=new TableHelper(this);
        tb = (TableView<String[]>) findViewById(R.id.tableView);
        tb.setColumnCount(9);
       // ArrayList<GpsModel> spacecrafts=new SQLiteHelperMapping(getApplicationContext()).retrieveSpacecrafts();
       // tableHelper.getSpaceProbes();
        tb.setHeaderBackgroundColor(Color.parseColor("#FFFFFF"));
        appBarLayout=(AppBarLayout)findViewById(R.id.appBar);

updated=(Button)findViewById(R.id.updated);
delete=(Button)findViewById(R.id.delete);


        tb.setHeaderAdapter(new SimpleTableHeaderAdapter(ActivityTable.this,tableHelper.getSpaceProbeHeaders()));
        tb.setDataAdapter(new SimpleTableDataAdapter(ActivityTable.this, tableHelper.getSpaceProbes()));

        final int selectedItemBackgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
//        final SelectionHelper<GpsModel> selectionHelper = new SelectionHelper<>(selectedItemBackgroundColor, tableView);
//
//        selectionHelper.setSelectionEnabled(true);
        tb.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {

                //Toast.makeText(ActivityTable.this, ((String[])clickedData)[0], Toast.LENGTH_SHORT).show();
              final  String id=((String[])clickedData)[0];
                appBarLayout.setVisibility(View.VISIBLE);



                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityTable.this);
                        alertDialogBuilder.setMessage("Are you sure to Delete  record of this ID : " + id);
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                        Integer deletedRows = sqLiteHelperMapping.deleteData(id);
                                        if(deletedRows > 0) {
                                            Toast.makeText(ActivityTable.this, "Data Deleted", Toast.LENGTH_LONG).show();
                                            exportDB();
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(ActivityTable.this,"Data not Deleted",Toast.LENGTH_LONG).show();}

                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appBarLayout.setVisibility(View.GONE);
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


                    }
                });

                //displayDialog(id);
                updated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityTable.this);
                        alertDialogBuilder.setMessage("Are you sure to updated ID : " + id);
                                alertDialogBuilder.setPositiveButton("yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                               // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                                displayDialog(id);
                                            }
                                        });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appBarLayout.setVisibility(View.GONE);
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
        });
    }


    private void displayDialog( final String id)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_layout, null);
        final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        final EditText etEmail = alertLayout.findViewById(R.id.et_email);
        Button update = alertLayout.findViewById(R.id.saveBtn);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Update Location");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = etUsername.getText().toString();
                String pass = etEmail.getText().toString();

                boolean isUpdate = sqLiteHelperMapping.updateData(id,etUsername.getText().toString());
                if(isUpdate == true){
                    Toast.makeText(ActivityTable.this,"Data Update",Toast.LENGTH_LONG).show();
                    exportDB();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ActivityTable.this, "Data not Updated", Toast.LENGTH_LONG).show();
                }


                //Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
            }
        });
       AlertDialog dialog = alert.create();
       dialog.show();
    }

    private void exportDB() {

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
            Cursor curCSV = db.rawQuery("SELECT * FROM gps_mapp_table", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7),curCSV.getString(8)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }





}
