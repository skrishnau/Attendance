package com.krishna.workingwithmultipletable.activities;

import helpers.Constants;
import helpers.CustomDate;
import helpers.DBAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.R;

import entities.EachDay;
import entities.Student;
import files.read.write.ReportGenerateThread;
import files.read.write.StudentInfoExportThread;

/** @Uses ------Used----------2----------
 * @ViewAvgAttendance 
 * Displays a list of Students with percent of attendance
 *@layout R.layout.all_students_info
 *@list layout == R.layout.view_students_list_layout
 */

public class ViewStudents extends Activity implements OnItemSelectedListener{

	int itemId;
	DBAdapter myDb;
	long clsIds, dayIds, dealWithStd;
	long editId,editDate;
	String classNameForFile="File1";
	Student dealWithStudent;
	//displayWise = student_wise or days_wise
	static int displayWise = 1;
	static int clickTimes_genReportBt = 0;
	final static String[] optionsArray = new String[]{"Generate Report","Export student info to file"};
	

	private String[] Countries;
	String allData = "";
	// ============================================================
	TextView tvClassInfo, totalClasses, tvP;
	EditText alertcrn, alertname;
//	Button genReportButton;
	ListView list;
	// =============================================================
	static boolean percentOrDaysState= true;
	//--------------------------------------------------
	private List<Student> stdList = new ArrayList<Student>();
	private List<EachDay> attdList = new ArrayList<EachDay>();
//	int selectedSpinner;
	List<Long> attendaneDayIds =new ArrayList<Long>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.total_attendance);

		Intent intent = getIntent();
		clsIds = intent.getLongExtra(Constants.EXTRA_MESSAGE_CLASS_ID, -1);
		// dayIds=intent.getLongExtra(ViewAttendanceDay.EXTRA_MESSAGE_DAY_ID,
		// -1);
		alertcrn = (EditText) findViewById(R.id.etAlertCrn);
		alertname = (EditText) findViewById(R.id.etAlertName);
		tvClassInfo = (TextView) findViewById(R.id.tvClassInfo);
		totalClasses = (TextView) findViewById(R.id.totalClasses);
		tvP = (TextView)findViewById(R.id.tvP);
		
		list = (ListView) findViewById(R.id.avgAttdlv);
		list.getBottom();
		
		Countries = new String[] { "Edit", "Delete" };
		Arrays.sort(Countries);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				R.layout.listitem, Countries);
//		list.setAdapter(adapter);
		registerForContextMenu(list);
		// ====================functions call ==================//
		openDB();
		
		populateClassData();
		
		RegisterListView();
		RegisterListClick();
		RegisterTextView();
		RegisterButtonOnTouch();
		RegisterGenerateReportButton();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		clickTimes_genReportBt = 0;
		super.onResume();
		RegisterListView();
		RegisterListClick();
		if(totalClassDays()<=1){
			totalClasses.setText("Total " + totalClassDays() + " class hour.");
		}else{
			totalClasses.setText("Total " + totalClassDays() + " class hours.");
		}
		
//		displayToast(this.getClass().toString());
	}

	private void RegisterListView() {
		TextView tvC = (TextView)findViewById(R.id.tvC);
		TextView tvN = (TextView)findViewById(R.id.tvN);
		// ===================== POPULATE LIST VIEW ==============================//
		if (displayWise == Constants.STUDENT_WISE) {
			
			tvN.setVisibility(View.VISIBLE);
			tvC.setText("Crn");
			tvP.setVisibility(View.VISIBLE);
			if (percentOrDaysState) {
				tvP.setText(R.string.in_days_text);
			}else{
				tvP.setText(R.string.in_percent_text);
			}
			attendaneDayIds.clear();
			CreateStudentsList();
			ArrayAdapter<Student> adapter = new StudentListAdapter();
			list.setAdapter(adapter);
/*			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> aview, View view,
						int pos, long id) {
					displayToast("Student wise list view");
				}
			});*/
		} else if (displayWise == Constants.DAY_WISE) {
			
			tvN.setVisibility(View.INVISIBLE);
			
			tvC.setText("Dates");
			
			tvP.setVisibility(View.INVISIBLE);
			stdList.clear();
			attdList.clear();
			attendaneDayIds.clear();
			Cursor cursor = myDb.getRowsDesc(DBAdapter.ATTENDANCE_DAY_TABLE,
					DBAdapter.ALL_ATTENDANCE_DAY_KEYS, DBAdapter.KEY_CLS_ID,
					clsIds);
			List<String> array = new ArrayList<String>();
			if (cursor.moveToFirst()) {
				do {
					long id = cursor.getLong(0);
					long date = cursor.getLong(DBAdapter.COL_DATE);
					array.add(CustomDate.GetFormattedDate(date));

					attendaneDayIds.add(id);
				} while (cursor.moveToNext());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.total_attendance_list_single_tv_layout, array);
//android.R.layout.simple_list_item_1
			list.setAdapter(adapter);
		}
		// =================================================//
		
		//============= Short Click==========================//
		
		/*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				
				if (displayWise == Constants.STUDENT_WISE) {
					
					 * Student student = stdList.get(position); EachDay eachDay
					 * = attdList.get(position); int x = eachDay.getPresence();
					 * int total = totalClassDays(); float presentPercent =
					 * (float) 0.0; if (!(total == 0)) { presentPercent =
					 * (float) (1.0 * eachDay.getPresence() / totalClassDays() *
					 * 100); } // float presentPercent = (float) (1.0 * //
					 * eachDay.getPresence() // / totalClassDays() * 100); //
					 * yourTextView.setText( "Your value is: " + //
					 * ((String.format("%.02f", yourFLoatValue)) + " !"));
					 * 
					 * String message = "SSSSSStudent  \ncrn::" +
					 * student.getCrn() + " \nname " + student.getName() +
					 * "\nTotal Present Days: " + x + "\nPresent Percent: " +
					 * ((String.format("%.02f", presentPercent)) + " %");
					 * 
					 * Toast.makeText(ViewStudentsWithTotalAttendance.this,
					 * message + " student.", Toast.LENGTH_LONG).show();
					 
					displayToast("StudentWise list selected");
				} else if (displayWise == Constants.DAY_WISE) {
					
					displayToast("Day Wise list selected "+position);
				}else if(displayWise == Constants.STUDENT_WISE){
					displayToast("StudentWise List selected"+position);
				}
			}
		});*/
		//======================================================//
		
		//==================Long Click==================//
	
		
		//=========================================//
	}
	private void RegisterListClick() {
		final Context context= this;
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {

				if (displayWise == Constants.STUDENT_WISE) {
					Student student = stdList.get(index);
					dealWithStudent = student;
					dealWithStd = student.get_id();
				} else if (displayWise == Constants.DAY_WISE) {
					long id = attendaneDayIds.get(index);
					Cursor day = myDb.getRow(DBAdapter.ATTENDANCE_DAY_TABLE,
							DBAdapter.ALL_ATTENDANCE_DAY_KEYS, id);
					if (day.moveToFirst()) {
						editDate = day.getLong(DBAdapter.COL_DATE);
						editId = id;
					}
				}
				return false;
			}
		});


		//		ListView listview = (ListView)findViewById(R.id.avgAttdlv);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, 
					int pos, long id) {
				if(displayWise == Constants.DAY_WISE){
					long attdId=attendaneDayIds.get(pos);
					Intent intent=new Intent(ViewStudents.this,
							ShowSingleDayAttendance.class);
					intent.putExtra(Constants.EXTRA_MESSAGE_DAY_ID, attdId);
					intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
					startActivity(intent);
//					displayToast("displaywise "+pos);////
				}else if(displayWise == Constants.STUDENT_WISE){
					/*
					SingleStudentReportThread studentThread = new SingleStudentReportThread(context, getLayoutInflater(), clsIds, stdList.get(pos).get_id());
					studentThread.execute();*/
//					InflateSinlgeStudentReport(stdList.get(pos));
					Intent intent=new Intent(ViewStudents.this,
							SingleStudentReportActivity.class);
					intent.putExtra(Constants.EXTRA_MESSAGE_STUDENT_ID, stdList.get(pos).get_id());
					intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
					startActivity(intent);
					
				}
			}

			

			
		});
	}

	
	private void RegisterGenerateReportButton() {
		Button genReportButton = (Button)findViewById(R.id.genReportBt);
		genReportButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (displayWise == Constants.STUDENT_WISE) {
					Constants.OPTIONS_FOR_STUDENT_LIST[0] = "View Day-wise";
				} else if (displayWise == Constants.DAY_WISE) {
					Constants.OPTIONS_FOR_STUDENT_LIST[0] = "View Student-wise";
				}
				DisplayOptionsMenu();
			}
		});
}

	/*private void setSpinner() {
		Spinner optionsSpinner = (Spinner) findViewById(R.id.optionsSpinner);
		optionsSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_items,optionsArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		optionsSpinner.setAdapter(adapter);
		optionsSpinner.setSelected(false);
		
		
	}*/
	
	// ================================================================//
	//On click of TakeAttandance Button======================
	public void TakeAttandance(View v) {
		displayToast("Tick to take attendance");
		Intent intent = new Intent(ViewStudents.this,
                AttendanceSheet.class);
		intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
       startActivity(intent);
	}
	public void AddNewStudent(View v){
	Intent intent = new Intent(ViewStudents.this,
            AddStudent.class);
    intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID,clsIds);
    startActivity(intent);
}
	private int totalClassDays() {
		// for total class days
		int ClassDays = 0;
		String[] attdKeys = new String[] { DBAdapter.KEY_ROWID };
		Cursor attdDayscursor = myDb.getRows(DBAdapter.ATTENDANCE_DAY_TABLE,
				attdKeys, DBAdapter.KEY_CLS_ID, clsIds);
		ClassDays = attdDayscursor.getCount();
		attdDayscursor.close();
		return ClassDays;
	}
	private void populateClassData() {
		
		Cursor c = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
				clsIds);
		if (c.moveToFirst()) {
			int batch = c.getInt(DBAdapter.COL_BATCH);
			String program = c.getString(DBAdapter.COL_PROGRAM);
			String course = c.getString(DBAdapter.COL_COURSE);
			// tvprogram.setText(program);
			allData = "" + String.valueOf(batch) + " / " + program + " / "
					+ course;
			tvClassInfo.setText("" + allData);
			classNameForFile = String.valueOf(batch)+"_"+program+"_"+course+".csv";
		}
		c.close();
	}
	// /////
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*Intent intent = new Intent(ViewStudentsWithTotalAttendance.this,
                ViewClass.class);
        startActivity(intent);*/
		onDestroy();
		

	}
	
	private void CreateStudentsList() {
		stdList.clear();
		attdList.clear();
		Cursor stdclcursor = myDb.getAscStd(clsIds);

		if (stdclcursor.moveToFirst()) {
			do {
				Student object = new Student();
				EachDay eachDay = new EachDay();
				long sid = stdclcursor.getLong(DBAdapter.COL_ROWID);

				String scrn = stdclcursor.getString(DBAdapter.COL_STD_CRN);
				String sname = stdclcursor.getString(DBAdapter.COL_STD_NAME);

				object.setCrn(scrn);
				object.setName(sname);
				object.set_id(sid);
				eachDay.set_id(sid);
				eachDay.setPresence(addAttendance(sid));
//				eachDay.setPresence(1);
//				System.out.println("attandance days"+ eachDay.getPresence());
				stdList.add(object);
				// stdclcursor.close();
				attdList.add(eachDay);
			} while (stdclcursor.moveToNext());

		}
		stdclcursor.close();
	}

	private int addAttendance(long stdId) {
		int attddays = 0;
		Cursor attdCursor = myDb.getRows(DBAdapter.ATTENDANCE_TABLE,
				DBAdapter.ALL_ATTENDANCE_KEYS, DBAdapter.KEY_STD_ID, stdId);
		if (attdCursor.moveToFirst()) {
			do {
				if (attdCursor.getString(DBAdapter.COL_PRESENCE).equals("P")) {
					attddays += 1;
				}
			} while (attdCursor.moveToNext());

		}
		attdCursor.close();
		return attddays;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	private void closeDB() {
		myDb.close();
	}

	private void DisplayOptionsMenu() {		
		LayoutInflater li = getLayoutInflater();
		final View v = li.inflate(R.layout.listviewonly, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final ListView listview = (ListView)v.findViewById(R.id.classListFromAddClass);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.options_menu_textview_layout,Constants.OPTIONS_FOR_STUDENT_LIST);
		adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
		
		listview.setAdapter(adapter);
		builder.setView(v);		
		builder.show();

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				String message = Constants.OPTIONS_FOR_STUDENT_LIST[position]; 
				switch(position){
				case 0:
					if(displayWise==Constants.STUDENT_WISE){
						//student wise is displayed so display attendance days
						displayWise=Constants.DAY_WISE;						
					}
					else if(displayWise==Constants.DAY_WISE){
						displayWise = Constants.STUDENT_WISE;
					}
					Intent intent = new Intent(ViewStudents.this,
			                ViewStudents.class);
					intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
					startActivity(intent);
					finish();
					break;
					
				case 1:
					clickTimes_genReportBt++;
					if (Constants.isExternalPresent()) {
						ReportGenerateThread genReport = new ReportGenerateThread(
								ViewStudents.this, clsIds, classNameForFile);
						genReport.execute(classNameForFile);
					} else {
						displayToast("External Storage Not Found");
					}
					break;
					
				case 2:
					
					StudentInfoExportThread exportThread = new StudentInfoExportThread(ViewStudents.this, clsIds, classNameForFile);
					exportThread.execute(classNameForFile);
					break;
					
				default:
					break;
				}
//		        finish();
//				Toast.makeText(ViewStudentsWithTotalAttendance.this, message, Toast.LENGTH_LONG).show();
			}

		});
}
	
	private void RegisterTextView() {
		
		
		tvP.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					tvP.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_small_drawable));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					tvP.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_small_drawable));
				}
				return false;
			}
		});

		tvP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// true means display in percent, false means display in days
				if (percentOrDaysState) {
					RegisterListView();
					percentOrDaysState = false;
					tvP.setText(R.string.in_percent_text);
//					tvP.setBackgroundColor(Color.BLACK);
				} else {
					RegisterListView();
					percentOrDaysState = true;
					tvP.setText(R.string.in_days_text);
//					tvP.setBackgroundColor(color.smokeDark);
				}
			}
		});

	}

	
	private void RegisterButtonOnTouch() {	
		
		final Button takeAttdBt = (Button)findViewById(R.id.takeAttendanceButton);
		takeAttdBt.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					takeAttdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					takeAttdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});
		
		final Button addNewStdBt = (Button)findViewById(R.id.addNewStudentButton);
		addNewStdBt.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					addNewStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					addNewStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});
		
		final Button genBt = (Button)findViewById(R.id.genReportBt);
		genBt.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					genBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					genBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});
		
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		if (displayWise == Constants.STUDENT_WISE) {
			menu.setHeaderTitle("" + dealWithStudent.getCrn() + "     "
					+ dealWithStudent.getName());
		} else if (displayWise == Constants.DAY_WISE) {
			menu.setHeaderTitle("" + editDate);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		// displayToast("this is from onContextItemSelected"+menuItemIndex);
		itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_edit:
			if(displayWise==Constants.STUDENT_WISE){
				menuEditStudent();
			}else if(displayWise == Constants.DAY_WISE){
				menuEditDays();
			}
			// /////
			return true;
			// //////////////////////////////////////////////////////////
		case R.id.menu_delete:
			if (displayWise == Constants.STUDENT_WISE) {
				menuDeleteStudent();
			}else if(displayWise==Constants.DAY_WISE){
				menuDeleteDays();
			}

			return true;
		default:

			return super.onContextItemSelected(item);
		}

	}

//////////////////////////////////////////////////////
	private void menuDeleteStudent() {
		// TODO Auto-generated method stub
		AlertDialog.Builder delbuilder = new AlertDialog.Builder(this);
		delbuilder
				.setMessage("Are you sure to delete?")
				.setPositiveButton("Yes!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// FIRE ZE MISSILES!
								myDb.deleteRow(DBAdapter.STUDENT_TABLE,
										dealWithStudent.get_id());
								String[] attdRow = new String[] { DBAdapter.KEY_ROWID };

								myDb.deleteRows(DBAdapter.ATTENDANCE_TABLE,
										DBAdapter.ALL_ATTENDANCE_KEYS,
										DBAdapter.KEY_STD_ID,
										dealWithStudent.get_id());
								myDb.deleteRows(DBAdapter.CLASS_STUDENT_TABLE,
										DBAdapter.ALL_CLASS_STUDENT_KEYS,
										DBAdapter.KEY_STD_ID,
										dealWithStudent.get_id());
								displayToast("Successfully deleted.");
								onResume();
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

	private void menuEditStudent() {
		LayoutInflater li = getLayoutInflater();
		View v = li.inflate(R.layout.editalert, null);
		// View v=getLayoutInflater().inflate(R.layout.editalert, null, false);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter the values...");
		final EditText etscrn = (EditText) v.findViewById(R.id.etAlertCrn);
		etscrn.setText(dealWithStudent.getCrn());
		final EditText etsname = (EditText) v.findViewById(R.id.etAlertName);
		etsname.setText(dealWithStudent.getName());
		builder.setView(v);
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ContentValues newValues = new ContentValues();
						newValues.put(DBAdapter.KEY_CRN, etscrn.getText()
								.toString());
						newValues.put(DBAdapter.KEY_NAME, etsname.getText()
								.toString());
						myDb.updateRow(DBAdapter.STUDENT_TABLE, newValues,
								dealWithStudent.get_id());

						onResume();
					}

				});
		builder.show();

	}

	private void menuDeleteDays() {
		// TODO Auto-generated method stub
		AlertDialog.Builder delbuilder = new AlertDialog.Builder(this);
		delbuilder
				.setMessage("Are you sure to delete?")
				.setPositiveButton("Yes!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// FIRE ZE MISSILES!
								myDb.deleteRows(DBAdapter.ATTENDANCE_DAY_TABLE,
										DBAdapter.ALL_ATTENDANCE_DAY_KEYS,
										DBAdapter.KEY_ROWID, editId);
								String[] attdRow = new String[] { DBAdapter.KEY_ROWID };

								myDb.deleteRows(DBAdapter.ATTENDANCE_TABLE,
										DBAdapter.ALL_ATTENDANCE_KEYS,
										DBAdapter.KEY_DAY_ID, editId);

								displayToast("Attendance Successfully deleted.");
								onResume();
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

	private void menuEditDays() {
		LayoutInflater li = getLayoutInflater();
		View v = li.inflate(R.layout.edit_attd, null);
		// View v=getLayoutInflater().inflate(R.layout.editalert, null, false);
		int year, months, days, hour, min, sec;

		year = CustomDate.GetYear(editDate);
		months = CustomDate.GetMonth(editDate);
		days = CustomDate.GetDay(editDate);
		hour = CustomDate.GetHour(editDate);
		min = CustomDate.GetMinute(editDate);
		sec = CustomDate.GetSecond(editDate);

		// displayToast(""+year+" month "+months+"  "+ days+"   ");

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter the values...");

		final EditText etyear = (EditText) v.findViewById(R.id.etYear);
		final EditText etmonth = (EditText) v.findViewById(R.id.etMonth);
		final EditText etday = (EditText) v.findViewById(R.id.etDay);
		final EditText ethour = (EditText) v.findViewById(R.id.etHour);
		final EditText etmin = (EditText) v.findViewById(R.id.etMinute);
		final EditText etsec = (EditText) v.findViewById(R.id.etSecond);

		etyear.setText(String.valueOf(year));
		etmonth.setText(String.valueOf(months));
		etday.setText(String.valueOf(days));
		ethour.setText(String.valueOf(hour));
		etmin.setText(String.valueOf(min));
		etsec.setText(String.valueOf(sec));
		// String finaldate=year+"-"+months+"-"+days+" "+hour+":"+min+"";

		builder.setView(v);
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int year = Integer
								.parseInt(etyear.getText().toString());
						int months = Integer.parseInt(etmonth.getText()
								.toString());
						int days = Integer.parseInt(etday.getText().toString());
						int hour = Integer
								.parseInt(ethour.getText().toString());
						int minute = Integer.parseInt(etmin.getText()
								.toString());
						int second = Integer.parseInt(etsec.getText()
								.toString());
						// check the date using any regular expression
						long finaldate = CustomDate.CreateDateInLong(year,
								months, days, hour, minute, second);
						ContentValues newValues = new ContentValues();
						newValues.put(DBAdapter.KEY_CLS_ID, clsIds);
						newValues.put(DBAdapter.KEY_DATE, finaldate);
						if (myDb.updateRow(DBAdapter.ATTENDANCE_DAY_TABLE,
								newValues, editId)) {
							displayToast("saved Successfully!!");
						}
						onResume();
					}

				});
		builder.show();

	}
/////////////////////////////////////////////////////
	private class StudentListAdapter extends ArrayAdapter<Student> {

		public StudentListAdapter() {
			super(ViewStudents.this,
					R.layout.view_students_list_layout, stdList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.view_students_list_layout, parent, false);
			}

			TextView tvcrn = (TextView) itemView.findViewById(R.id.tvViewCrn);
			TextView tvname = (TextView) itemView.findViewById(R.id.tvViewName);
			TextView tvpercent = (TextView) itemView.findViewById(R.id.tvViewAttd);
			
			
			
			if(stdList.size()<1 || attdList.size() <1){
//				tvname.setHeight(50);
				tvname.setText("No Students added in this class");
//				tvname.setText("Click on \"Add New Student\" button to add Students");
//				return itemView;
			}
			// Find the student to work with.
			Student student = stdList.get(position);
			EachDay eachDay = attdList.get(position);
			// Crn:
			
			tvcrn.setText(student.getCrn());
			// name:
			tvname.setText(student.getName());
			
			if (percentOrDaysState) {
				int total = totalClassDays();
				float presentPercent = (float) 0.0;
				if (!(total == 0)) {
					presentPercent = (float) (1.0 * eachDay.getPresence()
							/ totalClassDays() * 100);
				}
				tvpercent.setText(""
						+ ((String.format("%.02f", presentPercent)) + " %"));

			} else {
				if(eachDay.getPresence()<=1){
					tvpercent.setText(String.valueOf(eachDay.getPresence())
						+ " day");
				}else{
					tvpercent.setText(String.valueOf(eachDay.getPresence())
							+ " days");
				}
			}
			/*tvcrn.setBackgroundColor(Color.WHITE);
			tvcrn.setTextColor(Color.BLACK);
			tvname.setBackgroundColor(Color.WHITE);
			tvname.setTextColor(Color.BLACK);
			tvpercent.setBackgroundColor(Color.LTGRAY);
			tvpercent.setTextColor(Color.BLACK);*/
			
			final ViewHolder holder = new ViewHolder(tvcrn,tvname,tvpercent);
			
			/*tvcrn.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
//					setTouchColor(holder, event);
					return false;
				}
			});
			
			tvname.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
//					setTouchColor(holder, event);
					return false;
				}
			});*/
			
			tvpercent.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
//					setTouchColor(holder, event);
					return false;
				}
			});
			
			
			return itemView;
		}
	}
	

	private void setTouchColor(ViewHolder hol, MotionEvent event){
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			hol.tvcrn.setBackgroundDrawable(getResources()
//					.getDrawable(R.drawable.orange_drawable));
			hol.tvcrn.setBackgroundColor(Color.BLACK);
			hol.tvcrn.setTextColor(Color.WHITE);
			hol.tvname.setBackgroundColor(Color.BLACK);
			hol.tvname.setTextColor(Color.WHITE);
			hol.tvpercent.setBackgroundColor(Color.BLACK);
			hol.tvpercent.setTextColor(Color.WHITE);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
//			hol.tvcrn.setBackgroundDrawable(getResources()
//					.getDrawable(R.drawable.blue_drawable));
			hol.tvcrn.setBackgroundColor(Color.WHITE);
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.WHITE);
			hol.tvname.setTextColor(Color.BLACK);
			hol.tvpercent.setBackgroundColor(Color.LTGRAY);
			hol.tvpercent.setTextColor(Color.BLACK);
		}else if (event.getAction() == MotionEvent.ACTION_SCROLL){
			hol.tvcrn.setBackgroundColor(Color.WHITE);
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.WHITE);
			hol.tvname.setTextColor(Color.BLACK);
			hol.tvpercent.setBackgroundColor(Color.LTGRAY);
			hol.tvpercent.setTextColor(Color.BLACK);
		}else if (event.getAction() == MotionEvent.ACTION_MOVE){
			hol.tvcrn.setBackgroundColor(Color.WHITE);		
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.WHITE);
			hol.tvname.setTextColor(Color.BLACK);
			hol.tvpercent.setBackgroundColor(Color.LTGRAY);
			hol.tvpercent.setTextColor(Color.BLACK);
		}
	}
	private class ViewHolder {
		TextView tvcrn;
		TextView tvname;
		TextView tvpercent;
		public ViewHolder(TextView c, TextView n, TextView p){
			tvcrn = c;
			tvname = n;
			tvpercent = p;
		}
	}

	/*private void displayToastForId(long idInDB) {
		Cursor cursor = myDb.getRow(DBAdapter.STUDENT_TABLE,
				DBAdapter.ALL_STUDENT_KEYS, idInDB);
		if (cursor.moveToFirst()) {
			long idDB = cursor.getLong(DBAdapter.COL_ROWID);
			String crn = cursor.getString(DBAdapter.COL_STD_CRN);
			String name = cursor.getString(DBAdapter.COL_STD_NAME);

			String message = "ID: " + idDB + "\n" + "CRN: " + crn + "\n"
					+ "name:: : " + name;
			// displayToast(message);
		}
		cursor.close();
	}*/

	private void displayToast(String message) {
		Toast.makeText(ViewStudents.this, message, Toast.LENGTH_SHORT)
				.show();
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
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		displayToast(""+parent.getItemAtPosition(position)+" view: "+view.getId()+" position: "+position+" id:"+id);

		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}
	
	//============== Async Task for generating report from a thread ================//
	/*public  class CreateReport extends AsyncTask<String, Integer, String>{
		String f;
		ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(ViewStudentsWithTotalAttendance.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(100);
			dialog.show();
//			Toast.makeText(ViewStudentsWithTotalAttendance.this, "Wait a second.\nThis may take upto 12 seconds depending on attendance data", Toast.LENGTH_LONG);
//			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... fileName) {
			for(int i = 0; i<100; i++){
				publishProgress(1);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			f = fileName[0];
			CustomFileWriter writer = new CustomFileWriter(ViewStudentsWithTotalAttendance.this, clsIds, fileName[0]);
			writer.GenerateReport();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			clickTimes_genReportBt=0;
//			Toast.makeText(ViewStudentsWithTotalAttendance.this, "Report is generated in\nsdcard/ARS/"+f, Toast.LENGTH_LONG).show();
//			displayToast("Report is generated in\nsdcard/ARS/"+classNameForFile);
//			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			dialog.incrementProgressBy(values[0]);
			
//			super.onProgressUpdate(values);
		}
		
	}
	*/
	/*private void GenerateReport(String fileName) {
		CustomFileWriter writer = new CustomFileWriter(this, clsIds, fileName); //fileName = classNameForFile

//		writer.GenerateReport();
//		writer.closeWriter();
		displayToast("Report is generated in\nsdcard/ARS/"+classNameForFile);
	}*/
	//=============================================================================//
}