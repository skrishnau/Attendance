package com.krishna.workingwithmultipletable.activities;

import helpers.Constants;
import helpers.DBAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.FirstScreen;
import com.krishna.workingwithmultipletable.R;

import entities.Classes;

/**
 * -------1------USED------ Displays all current Classes
 * 
 * @main_layout R.layout.showclasses
 * @list_layout R.layout.class_layout
 * 
 */
public class ViewClass extends Activity {

	int itemId;
	long editId;
	Classes dealWithClass;
	static boolean clickOfButtonForOnTouch = false;

	// String valueThruActivity="";
	public static int dataFromFirstScreen = 0;

	public static String EXTRA_MESSAGE = "com.krishna.workingwithmultipletable.classIds";
	public static String EXTRA_DATA = "com.krishna.workingwithmultiple.data";
	// public int countForExit=0;
	DBAdapter myDb;
	private String[] menuArray;
	TextView tvtotal;
	// List<Long> classIdList= new ArrayList<Long>();
	private List<Classes> classList = new ArrayList<Classes>();
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_layout);

		Intent intent = getIntent();
		dataFromFirstScreen = intent.getIntExtra(Constants.EXTRA_MESSAGE_FROM_FIRST_SCREEN, -1);

		// countForExit=0;
		tvtotal = (TextView) findViewById(R.id.totalRecord);
		
		if (Constants.isExternalPresent()) {
			File temp = new File(Constants.ROOT_DIRECTORY, Constants.STUDENTS_IMAGE_DIRECTORY);
			if (!temp.exists()) {
				temp.mkdirs();
			}
			temp = new File(Constants.ROOT_DIRECTORY, Constants.STUDENTS_LIST_DIRECTORY);
			if (!temp.exists()) {
				temp.mkdirs();
			}
		}
		
//		this.studentsListFolder = "ARS/StudentsInfo/StudentsList";
//		this.studentsImageFolder = "ARS/StudentsInfo/StudentsImages";
		
		openDB();
		SetTheme();
		// RegisterListView();
		registerListClickCallback();

		list = (ListView) findViewById(R.id.lvClass);
		// registerForContextMenu(list);
		menuArray = new String[] { "Edit", "Delete" };
		Arrays.sort(menuArray);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	@Override
	protected void onResume() {
		super.onResume();
		RegisterListView();
		registerListClickCallback();
		// SetTheme();

	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		/*
		 * android.os.Process.killProcess(android.os.Process.myPid());
		 * System.exit(1);
		 */
		super.onBackPressed();
	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	private void closeDB() {
		myDb.close();
	}

	private void displayToast(String message) {
		Toast.makeText(ViewClass.this, message, Toast.LENGTH_SHORT).show();
	}

	/*
	 * UI Button Callbacks
	 */
	public void onClick_AddRecord(View v) {

		Intent intent = new Intent(ViewClass.this, AddClass.class);
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		menu.setHeaderTitle("" + dealWithClass.getBatch() + "   "
				+ dealWithClass.getProgram() + "    "
				+ dealWithClass.getCourse());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		// displayToast("this is from onContextItemSelected"+menuItemIndex);
		// itemId=item.getItemId();
		switch (menuItemIndex) {
		case R.id.menu_edit:

			menuEdit();
			// /////
			return true;
			// //////////////////////////////////////////////////////////
		case R.id.menu_delete:

			menuDelete();

			return true;
		default:

			return super.onContextItemSelected(item);
		}

	}

	private void menuDelete() {
		// TODO Auto-generated method stub
		AlertDialog.Builder delbuilder = new AlertDialog.Builder(this);
		delbuilder
				.setMessage("Are you sure to delete?")
				.setPositiveButton("Yes!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// FIRE ZE MISSILES!
								DeleteThread dThread = new DeleteThread(ViewClass.this, myDb, dealWithClass);
								dThread.execute();
							}
						})
				.setNegativeButton("No!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		delbuilder.show();
		// Create the AlertDialog object and return it
	}

	private void menuEdit() {
		LayoutInflater li = getLayoutInflater();
		View v = li.inflate(R.layout.edit_class, null);
		// View v=getLayoutInflater().inflate(R.layout.editalert, null, false);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter the values...");

		final EditText etprogram = (EditText) v.findViewById(R.id.eteProgram);
		final EditText etcourse = (EditText) v.findViewById(R.id.eteCourse);
		final EditText etbatch = (EditText) v.findViewById(R.id.eteBatch);
		etprogram.setText(dealWithClass.getProgram());
		etcourse.setText(dealWithClass.getCourse());
		etbatch.setText("" + String.valueOf(dealWithClass.getBatch()));
		// displayToast("now ready to change");
		builder.setView(v);
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ContentValues newValues = new ContentValues();
						newValues.put(DBAdapter.KEY_BATCH, etbatch.getText()
								.toString());
						newValues.put(DBAdapter.KEY_PROGRAM, etprogram
								.getText().toString());
						newValues.put(DBAdapter.KEY_COURSE, etcourse.getText()
								.toString());
						myDb.updateRow(DBAdapter.CLASS_TABLE, newValues,
								dealWithClass.get_id());

						onResume();
					}

				});
		builder.show();

	}

	private void RegisterListView() {
		classList.clear();
		Cursor cursor = myDb.getAllRows(DBAdapter.CLASS_TABLE,
				DBAdapter.ALL_CLASS_KEYS);
		int classCount = cursor.getCount();
		if (classCount <= 1) {
			tvtotal.setText("Total " + String.valueOf(classCount) + " class.");
		} else {
			tvtotal.setText("Total " + String.valueOf(classCount) + " classes.");
		}

		if (cursor.moveToFirst()) {
			do {
				long _id = cursor.getLong(DBAdapter.COL_ROWID);
				int batch = cursor.getInt(DBAdapter.COL_BATCH);
				String program = cursor.getString(DBAdapter.COL_PROGRAM);
				String course = cursor.getString(DBAdapter.COL_COURSE);
				Classes classes = new Classes(_id, batch, program, course);
				classList.add(classes);
			} while (cursor.moveToNext());
		}
		cursor.close();
		ArrayAdapter<Classes> adapter = new ClassListAdapter();
		
		list.setAdapter(adapter);
		
		registerForContextMenu(list);

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {

				// ListView myList = (ListView)findViewById(R.id.lvClass);
				// displayToast("long item clicked @@");
				Classes classes = classList.get(index);
				editId = classes.get_id();
				dealWithClass = classes;

				return false;
			}
		});

	}

	private class ClassListAdapter extends ArrayAdapter<Classes> {

		public ClassListAdapter() {
			super(ViewClass.this, android.R.layout.simple_list_item_2, classList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
//			list.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						android.R.layout.simple_list_item_2, parent, false);
			}
			TextView tvbatch = (TextView) itemView
					.findViewById(android.R.id.text1);
			if(classList.size()==0 || classList==null ||classList.get(0)==null){
				tvbatch.setText("No classes added yet. Click \"Add Class\" to add new classes");
			}
			// Find all the classes.
			Classes classes = classList.get(position);
			// Batch:
			
			if ((Integer.parseInt(String.valueOf(classes.getBatch())) % 2000) < 100) {
				tvbatch.setText("0" + String.valueOf(classes.getBatch() % 2000));
			} else if ((Integer.parseInt(String.valueOf(classes.getBatch())) % 2000) < 1000) {
				tvbatch.setText(String.valueOf(classes.getBatch()));
			}
			// Program:
			
			tvbatch.append("\t   "+classes.getProgram());
			// subject
			TextView tvsubject = (TextView) itemView
					.findViewById(android.R.id.text2);
			tvsubject.setText(classes.getCourse());

			return itemView;
		}
	}

	/*
	 * private void populateListViewFromDB() { Cursor cursor =
	 * myDb.getAllRows(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS); int
	 * classCount=cursor.getCount();
	 * tvtotal.setText("Total "+String.valueOf(classCount)+" classes.");
	 * if(cursor.moveToFirst()){ do{ // List<Long> classIdList= new
	 * ArrayList<Long>(); //this classIdList should be the variable of whole
	 * class so that we can access from another function
	 * classIdList.add(cursor.getLong(0));
	 * 
	 * }while(cursor.moveToNext()); } cursor.moveToFirst();
	 * startManagingCursor(cursor);
	 * 
	 * // Setup mapping from cursor to view fields: String[] fromFieldNames =
	 * new String[] {DBAdapter.KEY_BATCH, DBAdapter.KEY_PROGRAM,
	 * DBAdapter.KEY_COURSE}; int[] toViewIDs = new int[] {R.id.item_batch,
	 * R.id.item_program, R.id.item_course};
	 * 
	 * // Create adapter to may columns of the DB onto elemesnt in the UI.
	 * SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter( this, //
	 * Context R.layout.class_list_layout, // Row layout template cursor, //
	 * cursor (set of DB records to map) fromFieldNames, // DB Column names
	 * toViewIDs // View IDs to put information in );
	 * 
	 * // Set the adapter for the list view ListView myList = (ListView)
	 * findViewById(R.id.lvClass); myList.setAdapter(myCursorAdapter);
	 * //list.setAdapter(myCursorAdapter); //////////////
	 * registerForContextMenu(myList); myList.setOnItemLongClickListener(new
	 * AdapterView.OnItemLongClickListener() {
	 * 
	 * public boolean onItemLongClick(AdapterView<?> arg0, View v, int index,
	 * long arg3) {
	 * 
	 * ListView myList = (ListView)findViewById(R.id.lvClass); //
	 * displayToast("long item clicked @@"); editId=classIdList.get(index);
	 * 
	 * Cursor cls= myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
	 * editId); if(cls.moveToFirst()){ String
	 * program=cls.getString(DBAdapter.COL_PROGRAM); String course =
	 * cls.getString(DBAdapter.COL_COURSE); int batch
	 * =cls.getInt(DBAdapter.COL_BATCH); dealWithClass=new Classes(editId,batch,
	 * program, course); //displayToast("class detail extractedd"); }
	 * cls.close(); return false; } });
	 * 
	 * 
	 * }
	 */
	
	private void registerListClickCallback() {
		ListView list = (ListView) findViewById(R.id.lvClass);
//list.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long idInDB) {

				// updateItemForId(idInDB);
				displayToastForId(classList.get(position).get_id());
				Intent intent = new Intent(ViewClass.this,
						ViewStudents.class);
				intent.putExtra(EXTRA_MESSAGE, classList.get(position).get_id());
				startActivity(intent);
				
				/*Intent intentAsPerFirstScreen = new Intent(ViewClass.this, ViewStudents.class);
				
				
				switch(dataFromFirstScreen){
				
				case 1:
//					intentAsPerFirstScreen = new Intent(ViewClass.this, ViewStudents.class);
					intentAsPerFirstScreen.putExtra(EXTRA_MESSAGE, classList.get(position).get_id());
					break;
				case 2:
//					intentAsPerFirstScreen = new Intent(ViewClass.this, ViewBatch.class);
//					intentAsPerFirstScreen.putExtra(EXTRA_MESSAGE, 0);
//					ViewBatch.class);
					break;
				case 3:
					
					intentAsPerFirstScreen.putExtra(EXTRA_MESSAGE, classList.get(position).get_id());
					break;
				case 4:
					break;
				case 5:
					break;
					
				}
				startActivity(intentAsPerFirstScreen);*/
				/*
				 * if(ViewClass.valueThruActivity==1){ Intent intent = new
				 * Intent(ViewClass.this, AttendanceSheet.class);
				 * intent.putExtra(EXTRA_MESSAGE,
				 * classList.get(position).get_id()); startActivity(intent);
				 * 
				 * }else if(ViewClass.valueThruActivity==2){ Intent intent = new
				 * Intent(ViewClass.this, ShowAllAttendanceDates.class);
				 * intent.putExtra(EXTRA_MESSAGE,
				 * classList.get(position).get_id()); startActivity(intent);
				 * 
				 * }else if(ViewClass.valueThruActivity==3){ //in
				 * ViewAvgAttendance orginally ViewAllStudents Intent intent =
				 * new
				 * Intent(ViewClass.this,ViewStudentsTotalAttendance.class);
				 * intent.putExtra(EXTRA_MESSAGE,
				 * classList.get(position).get_id()); startActivity(intent); }
				 */
				// Intent intent =new
				// Intent(ViewClass.this,ViewAttendanceDay.class);
				// intent.putExtra(EXTRA_MESSAGE, idInDB);
				// startActivity(intent);
				// ---------------
				// knows where to go from this state
				// checkForTask(idInDB);
			}
		});

		/*
		 * final TextView tvprogram = (TextView)
		 * findViewById(R.id.item_program); final TextView tvsubject =
		 * (TextView) findViewById(R.id.item_course);
		 * 
		 * tvprogram.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent event) {
		 * if(event.getAction()==MotionEvent.ACTION_DOWN){
		 * tvprogram.setBackgroundDrawable
		 * (getResources().getDrawable(R.drawable.orange_drawable)); }else
		 * if(event.getAction()==MotionEvent.ACTION_UP){
		 * tvprogram.setBackgroundDrawable
		 * (getResources().getDrawable(R.drawable.blue_list_drawable)); } return
		 * false; } });
		 * 
		 * tvsubject.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent event) {
		 * if(event.getAction()==MotionEvent.ACTION_DOWN){
		 * tvsubject.setBackgroundDrawable
		 * (getResources().getDrawable(R.drawable.orange_drawable)); }else
		 * if(event.getAction()==MotionEvent.ACTION_UP){
		 * tvsubject.setBackgroundDrawable
		 * (getResources().getDrawable(R.drawable.blue_list_drawable)); } return
		 * false; } });
		 */
	}

	private class DeleteThread extends AsyncTask<String,Integer,String>{

		Context context;
		ProgressDialog dialog;
		DBAdapter myDb;
		Classes dealWithClass;
		int progressCounter=0;
		public DeleteThread(Context context, DBAdapter myDb, Classes dealWithClass){
			this.context = context;
			this.myDb = myDb;
			this.dealWithClass = dealWithClass;
		}
		@Override
		protected String doInBackground(String... param) {
			 
			myDb.deleteRow(DBAdapter.CLASS_TABLE,
					dealWithClass.get_id());
			String[] attdRow = new String[] { DBAdapter.KEY_ROWID };

			myDb.deleteRows(DBAdapter.ATTENDANCE_DAY_TABLE,
					DBAdapter.ALL_ATTENDANCE_DAY_KEYS,
					DBAdapter.KEY_CLS_ID,
					dealWithClass.get_id());
			progressCounter +=1;
			publishProgress(progressCounter);
			Cursor c = myDb.getRows(
					DBAdapter.CLASS_STUDENT_TABLE,
					DBAdapter.ALL_CLASS_STUDENT_KEYS,
					DBAdapter.KEY_CLS_ID,
					dealWithClass.get_id());
			progressCounter+=1;
			publishProgress(progressCounter);
			float ratio = (float)(1.0*96)/c.getCount();
			float ratioCounter = (float)0.0;
			if (c.moveToFirst()) {
				do {
					ratioCounter+=ratio;
					publishProgress((int)ratioCounter);
					ratioCounter -=((int)ratioCounter);
					long stdid = c
							.getLong(DBAdapter.COL_STD_ID);
					myDb.deleteRow(DBAdapter.STUDENT_TABLE,
							stdid);

					myDb.deleteRows(
							DBAdapter.ATTENDANCE_TABLE,
							DBAdapter.ALL_ATTENDANCE_KEYS,
							DBAdapter.KEY_STD_ID, stdid);
				} while (c.moveToNext());
			}
			/*myDb.deleteRows(DBAdapter.CLASS_STUDENT_TABLE,
					DBAdapter.ALL_CLASS_STUDENT_KEYS,
					DBAdapter.KEY_STD_ID,
					dealWithClass.get_id());*/
			publishProgress(2);
//			onResume();
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			displayToast("Successfully deleted.");
			onResume();
//			super.onPostExecute(result);
		}
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(100);
			dialog.show();
//			super.onPreExecute();
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			dialog.incrementProgressBy(values[0]);
			super.onProgressUpdate(values);
		}
		
		
	}
	/*
	 * private void updateItemForId(long idInDB) { Cursor cursor =
	 * myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, idInDB); if
	 * (cursor.moveToFirst()) { long idDB = cursor.getLong(DBAdapter.COL_ROWID);
	 * 
	 * int batch = cursor.getInt(DBAdapter.COL_BATCH); String program =
	 * cursor.getString(DBAdapter.COL_PROGRAM); String course =
	 * cursor.getString(DBAdapter.COL_COURSE);
	 * 
	 * Classes classes = new Classes(idInDB,batch,program,course);
	 * updateClass(classes);
	 * 
	 * } cursor.close(); populateListViewFromDB(); }
	 * 
	 * private void updateClass(Classes classes) { // TODO Auto-generated method
	 * stub long idDB = classes.get_id(); ContentValues initialValues = new
	 * ContentValues(); initialValues.put(DBAdapter.KEY_BATCH,
	 * classes.getBatch()); initialValues.put(DBAdapter.KEY_PROGRAM,
	 * classes.getProgram()); initialValues.put(DBAdapter.KEY_COURSE,
	 * classes.getCourse());
	 * 
	 * myDb.updateRow(DBAdapter.CLASS_TABLE,initialValues,idDB); }
	 */
	private void displayToastForId(long idInDB) {
		Cursor cursor = myDb.getRow(DBAdapter.CLASS_TABLE,
				DBAdapter.ALL_CLASS_KEYS, idInDB);
		if (cursor.moveToFirst()) {
			long idDB = cursor.getLong(DBAdapter.COL_ROWID);
			int batch = cursor.getInt(DBAdapter.COL_BATCH);
			String program = cursor.getString(DBAdapter.COL_PROGRAM);
			String course = cursor.getString(DBAdapter.COL_COURSE);

			String message = "Batch: " + batch + " #\n" + "program: " + program
					+ " #\n" + "Subject: " + course + " #";
			Toast.makeText(ViewClass.this, message, Toast.LENGTH_LONG).show();
		}
		cursor.close();
	}

	public void SetTheme() {
		final Button newClassButton = (Button) findViewById(R.id.addBtnFrmShowCls);
		// newClassButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.colorful_green_button));
		newClassButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					newClassButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.orange_drawable));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					newClassButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});
		newClassButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// newClassButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				onClick_AddRecord(null);
			}
		});
	}
}
