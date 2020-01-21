package com.krishna.workingwithmultipletable.activities;

import entities.Classes;
import files.read.write.CustomFileReader;
import files.read.write.NameAndCrnAsString;
import helpers.DBAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.R;
public class AddClass extends Activity implements OnItemSelectedListener {

	private static final int FILE_CHOOSER_ID = 1;
	DBAdapter myDb;
	EditText  etcourse;
	Spinner pSpinner, bSpinner, cSpinner;

	static boolean isImport = false;
	
	String selectedProgram = "", selectedCourse = "", selectedBatch = "0";

	static List<CharSequence> array = new ArrayList<CharSequence>();
	
	static List<NameAndCrnAsString> ncList;
//	static int programSpinnerValue = 0;
//	static int batchSpinnerValue = 7;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_class);
		// etprogram=(EditText)findViewById(R.id.etProgram);
		etcourse = (EditText) findViewById(R.id.etCourse);
//		etbatch = (EditText) findViewById(R.id.etBatch);
		openDB();

		setSpinner();
		PopulateListView();
		RegisterClickCallBack();
		
	}


	private void PopulateListView() {
		final ListView lv = (ListView) findViewById(R.id.stdInAddClassLv);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(AddClass.this, R.layout.import_small_textview, array); //R.layout.import_list_items
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		lv.setAdapter(adapter);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == FILE_CHOOSER_ID){
			if(resultCode == RESULT_OK){
				String path = data.getStringExtra(FileChooser.RETURN_FILE_PATH_EXTRA);
				CustomFileReader fileReader = new CustomFileReader();
				 
				InflateFileData(fileReader.ImportClass(path));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	private void RegisterClickCallBack() {
		final Button importClassBt = (Button)findViewById(R.id.importClassButton);
		importClassBt.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					importClassBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					importClassBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});
		final Button saveClassBt = (Button)findViewById(R.id.saveClassButton);
		saveClassBt.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					saveClassBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					saveClassBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				}
				
				return false;
			}
		});
		
	}

	private void setSpinner() {
		
		//============ProgramSpinner===================//
		Spinner programspinner = (Spinner) findViewById(R.id.program_spinner);
		programspinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> programAdapter = ArrayAdapter
				.createFromResource(this, R.array.program_array,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		programAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		programspinner.setAdapter(programAdapter);
//		programspinner.setSelection(programSpinnerValue);
		
		//=============BatchSpinner===================//
		GregorianCalendar gc = new GregorianCalendar();
		int year = gc.get(Calendar.YEAR);
		List<CharSequence> array = new ArrayList();
		
		for (int x = year - 7; x < year + 3; x++) {
			array.add(String.valueOf(x));
		}
//		for(CharSequence c : array){
////			System.out.println(c);
//		}
//		List<CharSequence> yearArray = new ArrayList();
		Spinner batchspinner = (Spinner) findViewById(R.id.batch_spinner);
		batchspinner.setOnItemSelectedListener(this);
		
		/**
		 * the spinner_items layout is a TextView Layout and is used to create a view for 
		 * default text of spinner not the contents to display after 
		 * spinner is selected
		 */
		ArrayAdapter<CharSequence> batchAdapter = new ArrayAdapter<CharSequence>
				(this, android.R.layout.simple_spinner_item,array);
		// Specify the layout to use when the list of choices appears
		batchAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		batchspinner.setAdapter(batchAdapter);
		batchspinner.setSelection(7);
//		batchspinner.setSelected(false);
		/**
		 * // Spinner batchspinner = (Spinner) findViewById(R.id.batch_spinner);
		// batchspinner.setOnItemSelectedListener(this);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		SpinnerAdapter sa = new ArrayAdapter<String>(this, 0);
		//
		
		//
		
		// ArrayAdapter<CharSequence> adp = new SpinnerAdapter();
*/
		
		
	}

	public void importClass(View v) {
		
		Intent intent = new Intent(AddClass.this,FileChooser.class);
		startActivityForResult(intent, FILE_CHOOSER_ID);
		
		/*CustomFileReader fileReader = new CustomFileReader();
		List<NameAndCrnAsString> nameAndCrnList = fileReader
				.ImportClass("attd_recrd_sys_1",
						"computer_no_space_with_marks.csv");

		InflateFileData(nameAndCrnList);*/
	}

	private void InflateFileData(final List<NameAndCrnAsString> nameAndCrnList) {

		array.clear();
		ncList = new ArrayList<NameAndCrnAsString>();
		for (int i = 2; i < nameAndCrnList.size(); i++) {
			NameAndCrnAsString nac = nameAndCrnList.get(i);
			array.add(nac.getCrn() + "\t  " + nac.getName() + "");
			ncList.add(nac);
		}
		LayoutInflater li = getLayoutInflater();
		final View v = li.inflate(R.layout.imported_std_listview, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(String.valueOf(nameAndCrnList.get(1).getName())
				+ " / " + nameAndCrnList.get(0).getName());

		final ListView lv = (ListView) v
				.findViewById(R.id.importedStdListViewlistview);
		final TextView totalStds = (TextView) v
				.findViewById(R.id.totalImportedStdsTV);
		totalStds.setText("Total " + array.size() + " students.");
		/*
		 * The layout import_list_items is a TextView layout and is used to
		 * determine the text of each list elements
		 */
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, R.layout.import_list_items, array);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		lv.setAdapter(adapter);

		builder.setView(v);
		builder.setPositiveButton("Import",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (array.size() != 0) {
							//===================== SET SPINNER SELECTION ========================//
							if (nameAndCrnList.size() >= 1) {
								// program
								if (!(nameAndCrnList.get(0).getName().equals(""))) {
									Spinner batchspinner = (Spinner) findViewById(R.id.program_spinner);
									batchspinner.setSelection(Integer.parseInt(nameAndCrnList
											.get(0).getCrn()));
								}
								// batch
								if (!(nameAndCrnList.get(1).getName().equals(""))) {
									Spinner batchspinner = (Spinner) findViewById(R.id.batch_spinner);
									batchspinner.setSelection(Integer.parseInt(nameAndCrnList
											.get(1).getCrn()));
								}
							}
							//======================================================================//
							//--------------------populate textview ---------------------------------//
							TextView commentTv = (TextView)findViewById(R.id.topCommentOfList);
							commentTv.setText("These Students will be added to the class");
							//============== POPULATE LIST VIEW WITH STUDENT NAME AND CRN ==========//
							final ListView lv = (ListView) findViewById(R.id.stdInAddClassLv);
							ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(AddClass.this, R.layout.import_small_textview, array); //R.layout.import_list_items
							adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
							lv.setAdapter(adapter);
							//=======================================================================//
							
							//======================SAVE DATA TO DATABASE ============================//
							//save button has to be pressed to save class and student information
							isImport = true;
							displayToast("Scroll down to view the students");
							//=======================================================================//
						}

//						onResume();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// v.setVisibility(View.INVISIBLE);

					}
				});
		builder.show();
//		View v = this.getApplication().getApplicationContext().get
	}
	
	protected long SaveClassToDatabase() {
		long clsId = -1;
		if ( etcourse.getText().toString().equals("")) {
			/*Toast.makeText(AddClass.this, "Please add values..",
					Toast.LENGTH_LONG).show();*/
			clsId=-2;
		} else {
			int batch = Integer.parseInt(selectedBatch);
			String program = selectedProgram;
			String course = etcourse.getText().toString();
			Classes classes = new Classes(batch, program, course);

			ContentValues initialValues = new ContentValues();
			initialValues.put(DBAdapter.KEY_BATCH, classes.getBatch());
			initialValues.put(DBAdapter.KEY_PROGRAM, classes.getProgram());
			initialValues.put(DBAdapter.KEY_COURSE, classes.getCourse());

			 clsId = myDb
					.insertRow(DBAdapter.CLASS_TABLE, initialValues);
			 if(isImport){
					
					if (clsId != -1 ) {
						boolean saveSuccessful = true;
						for (NameAndCrnAsString nac : ncList) {
							if (SaveStudentToDatabase(clsId, nac.getCrn(),
									nac.getName()) == -1) {
								displayToast("Sorry! Couldnt save data. \nFrom "
										+ nac.getCrn()
										+ "\t"
										+ nac.getName()
										+ "Till bottom.");
								saveSuccessful = false;
								break;
							}
						}
						if (saveSuccessful) {
							// AddFooterToListView();
							displayToast("Save Successful {{+}}.");
						}
					}
			 }
			
		}
		isImport = false;
		ncList = null;
		return clsId;
	}
	private long SaveStudentToDatabase(long clsIds, String crn, String name){
		ContentValues initialValues =new ContentValues();
	    initialValues.put(DBAdapter.KEY_CRN, crn);
	    initialValues.put(DBAdapter.KEY_NAME, name);
	    //save student to db
	    long stdIdm= myDb.insertRow(DBAdapter.STUDENT_TABLE, initialValues);
		if (stdIdm != -1) {
			ContentValues aValues = new ContentValues();
			aValues.put(DBAdapter.KEY_CLS_ID, clsIds);
			aValues.put(DBAdapter.KEY_STD_ID, stdIdm);
			// save student's respective class
			long stdclsIdm = myDb.insertRow(DBAdapter.CLASS_STUDENT_TABLE,
					aValues);
			if(stdclsIdm==-1){
				myDb.deleteRow(DBAdapter.STUDENT_TABLE, stdIdm);
				displayToast("Sorry. Couldn't save. kk");
				return -1;
			}
			return stdIdm;
		}else{
			displayToast("Sorry. Couldn't save. kl");
			return -1;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		programSpinnerValue = 0;
//		batchSpinnerValue = 7;
		finish();
		super.onBackPressed();

	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}
	private void closeDB() {
		myDb.close();
	}

	public void OnClick_saveClass(View v) {
			long newClsId = SaveClassToDatabase();
			//================= for displaying the comment =================//
			if(newClsId == -1){
				displayToast("Sorry, couldn't save.");
			}else if(newClsId==-2){
				displayToast("Please enter values");
			}else{
				displayToast("Save Successful.");
				finish();
			}
//			Cursor cursor = myDb.getRow(DBAdapter.CLASS_TABLE,
//					DBAdapter.ALL_CLASS_KEYS, newClsId);
//
//			displayClassSet(cursor);
			
	}

	// Display an entire recordset to the screen.

	private void displayClassSet(Cursor cursor) {

		String message = "";

		if (cursor.moveToFirst()) {
			do {
				// Process the data:
				int id = cursor.getInt(DBAdapter.COL_ROWID);
				int batch = cursor.getInt(DBAdapter.COL_BATCH);
				String program = cursor.getString(DBAdapter.COL_PROGRAM);
				String course = cursor.getString(DBAdapter.COL_COURSE);

				// Append data to the message:
				message += "id=" + id + ", batch=" + batch + ", program="
						+ program + ", course=" + course + "\n";

			} while (cursor.moveToNext());
		}
		cursor.close();
		displayToast(message + "\n" + "Save Successful!!");
	}

	private void displayToast(String message) {
		Toast.makeText(AddClass.this, message, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		switch(parent.getId()){
		case R.id.batch_spinner:
			selectedBatch = parent.getItemAtPosition(pos).toString();
//			batchSpinnerValue = pos;
			break;
		case R.id.program_spinner:
			selectedProgram = parent.getItemAtPosition(pos).toString();
//			programSpinnerValue = pos;
			break;
		}
//		switch(view.getId()){
//		case R.id.batch_spinner:
//			selectedBatch = parent.getItemAtPosition(pos).toString();
//			break;
//		case R.id.program_spinner:
//			selectedProgram = parent.getItemAtPosition(pos).toString();
//			break;
//		}
//		displayToast("program: "+selectedProgram.toString()+" batch: "+selectedBatch);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		pSpinner.setSelection(0);
		
		

	}

}