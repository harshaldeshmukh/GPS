package com.example.hp.gps;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper myDb;
    EditText etname,etEmail,etpassword ,etcontact;
    Button btnAddData;
    RelativeLayout rl_skip;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new SQLiteHelper(this);

        etname = (EditText)findViewById(R.id.etname);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etpassword = (EditText)findViewById(R.id.etpassword);
        etcontact = (EditText)findViewById(R.id.etcontact);
        btnAddData = (Button)findViewById(R.id.btnRedister);
      final  String contact = etcontact.getText().toString();

        rl_skip=(RelativeLayout)findViewById(R.id.rl_skip);
        rl_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),LocationTracker.class);
                startActivity(i);
            }
        });




//     Button   btncheck =(Button)findViewById(R.id.btncheck);
//        btncheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Cursor res = myDb.getAllData(contact);
////                if(res.getCount() == 0) {
////                    Toast.makeText(MainActivity.this,"Data Present",Toast.LENGTH_LONG).show();
////                    return;
////                }
//
//                Intent i = new Intent(getApplicationContext(),LocationTracker.class);
//                startActivity(i);
//            }
//        });


        AddData();
//        viewAll();
//        UpdateData();
//        DeleteData();
    }



    public  void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(etEmail.getText().toString(),
                                etname.getText().toString(),
                                etpassword.getText().toString(),etcontact.getText().toString() );
                        if(isInserted == true)
                            Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

//    public void viewAll() {
//        btnviewAll.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Cursor res = myDb.getAllData();
//                        if(res.getCount() == 0) {
//                            // show message
//                            showMessage("Error","Nothing found");
//                            return;
//                        }
//
//                        StringBuffer buffer = new StringBuffer();
//                        while (res.moveToNext()) {
//                            buffer.append("Id :"+ res.getString(0)+"\n");
//                            buffer.append("Name :"+ res.getString(1)+"\n");
//                            buffer.append("Surname :"+ res.getString(2)+"\n");
//                            buffer.append("Marks :"+ res.getString(3)+"\n\n");
//                        }
//
//                        // Show all data
//                        showMessage("Data",buffer.toString());
//                    }
//                }
//        );
//    }

//    public void showMessage(String title,String Message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        builder.show();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}

