package com.krishna.workingwithmultipletable;

import helpers.Constants;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.krishna.workingwithmultipletable.activities.AddClass;
import com.krishna.workingwithmultipletable.activities.ViewBatch;
import com.krishna.workingwithmultipletable.activities.ViewClass;

public class FirstScreen extends Activity {

	Button button;
//	public static String  EXTRA_MESSAGE="com.krishna.workingwithmultipletable.firstScreen";
//	int valueToPass=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_screen);
	}
	@Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
	
	public void Classes(View v){
		showClass(1);
	}
	
	
	public void Students(View v){
		Intent intent = new Intent(FirstScreen.this,
                ViewBatch.class);
		startActivity(intent);
	}
	
	public void TakeAttendance(View v){
		showClass(3);
	}
	
	private void showClass(int i) {
		Intent intent = new Intent(FirstScreen.this,
                ViewClass.class);
        intent.putExtra(Constants.EXTRA_MESSAGE_FROM_FIRST_SCREEN, i);
        
        startActivity(intent);
		
	}
	
/*	
public void AddClass(View v) {
		
        Intent intent = new Intent(FirstScreen.this,
                AddClass.class);
		
		startActivity(intent);
	}
	public void TakeAttd(View v){
		valueToPass=1;
        Intent intent = new Intent(FirstScreen.this,
                ViewClass.class);
        intent.putExtra(EXTRA_MESSAGE, valueToPass);
        
        startActivity(intent);
	}
	public void EditRecords(View v){
		valueToPass=2;
        Intent intent = new Intent(FirstScreen.this,
                ViewClass.class);
        intent.putExtra(EXTRA_MESSAGE, valueToPass);
        startActivity(intent);
	}
	public void ViewReports(View v) throws IOException{
		valueToPass=3;
        Intent intent = new Intent(FirstScreen.this,
                ViewClass.class);
       // fileWrite();
        intent.putExtra(EXTRA_MESSAGE, valueToPass);
        startActivity(intent);
	}
	public  void fileWrite()throws IOException {
		String filename = "myfile.csv";
		String string = "Hello world!";
		FileOutputStream outputStream;
		
		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	public void Sett(View v){
		valueToPass=4;
        Intent intent = new Intent(FirstScreen.this,
                ViewClass.class);
        intent.putExtra(EXTRA_MESSAGE, valueToPass);
        startActivity(intent);
	}
	*/
	
	
}
