package com.krishna.workingwithmultipletable.activities;

import java.io.File;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

import helpers.Constants;
import helpers.CustomDate;
import helpers.DBAdapter;

import com.krishna.workingwithmultipletable.R;
import com.krishna.workingwithmultipletable.activities.AttendanceSheet.ImageDisplayEntities;

import entities.Student;
import entities.StudentPresence;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleStudentReportActivity extends Activity {

	List<String> MonthList = new ArrayList<String>();
	List<CharSequence> PresenceList = new ArrayList<CharSequence>();
	List<CharSequence> DateList = new ArrayList<CharSequence>();
	List<String> MonthTotalList = new ArrayList<String>();
	int totalClassDays = 0;
	String[] monthArray = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
			"June", "July", "Aug", "Sep", "Oct", "Nov", "Dec" };
	int previousMonth = 0;
	int nextMonth = 13;
	
	long clsId,stdId;
	
	DBAdapter myDb;
	
	Student student;
	String imagePath = "";
	
	TextView studentInfo,inDays, inPercent, classInfo;
	ListView list;
	ImageView image;
	
	int totalClassesHeld=0;
	int totalPresentDays = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_student_report_layout);
		Intent intent = getIntent();
		clsId = intent.getLongExtra(Constants.EXTRA_MESSAGE_CLASS_ID, -1);
		stdId = intent.getLongExtra(Constants.EXTRA_MESSAGE_STUDENT_ID, -1);
		  list = (ListView)findViewById(R.id.lvDateNPresence);
		  image = (ImageView)findViewById(R.id.ivSingleStudent);
		 studentInfo = (TextView)findViewById(R.id.tvStudentInfo);
		 inDays = (TextView)findViewById(R.id.tvTotalPresentInDays);
		 inPercent = (TextView)findViewById(R.id.tvTotalPresentInPercent);
		 classInfo = (TextView)findViewById(R.id.tvSingleStdClassInfo);
//		 totalDaysTV = (TextView)findViewById(R.id.tvTotalClassDaysSingle);
		 
//		 this.setFinishOnTouchOutside(true);
		 openDB();
		 
		 CreateListNGetTotal();
		 PopulateStudentData();
		 populateClassData();
		 PopulateListView();
		 PopulateAttendanceData();
		
		
	}
	private void populateClassData() {
		
		Cursor c = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
				clsId);
		String allData ="";
		if (c.moveToFirst()) {
			int batch = c.getInt(DBAdapter.COL_BATCH);
			String program = c.getString(DBAdapter.COL_PROGRAM);
			String course = c.getString(DBAdapter.COL_COURSE);
			// tvprogram.setText(program);
			allData = "" + String.valueOf(batch) + " / " + program + " / "
					+ course;
			classInfo.setText("" + allData);
		}
		c.close();
	}
	
	private void PopulateStudentData() {
		
		Cursor cursor = myDb.getRow(DBAdapter.STUDENT_TABLE, DBAdapter.ALL_STUDENT_KEYS, stdId);
		String crn="";
		String name="";
		if(cursor.moveToFirst()){
			 crn = cursor.getString(DBAdapter.COL_STD_CRN);
			 name = cursor.getString(DBAdapter.COL_STD_NAME);
			 imagePath = cursor.getString(DBAdapter.COL_STD_IMAGE);
			student = new Student(crn, name);
		}
		studentInfo.setText(name +"\n"+crn);
		
		ImageDisplayEntities entities = GetImage();
		TextView imagetv = (TextView)findViewById(R.id.imageNametv);
		if(entities!= null){
			image.setImageBitmap(entities.bitmap);
			Toast.makeText(this, entities.fileName, Toast.LENGTH_LONG);
			imagetv.setText(Constants.ImageFileName(name, crn));
		}
		else
			imagetv.setText("Image could not be extracted");
	}
	
	private void PopulateAttendanceData() {
		inDays.setText("Present Days:  "+ String.valueOf(totalPresentDays) + "  /"+totalClassDays);
		float presentPercent =
				 (float)1.0*totalPresentDays/totalClassesHeld*100;
		inPercent.setText("Present Percent:  "+String.format("%.02f", presentPercent)+" %");
//		totalDaysTV.setText("Total Classes Held:  "+ totalClassDays);
	}

	private void PopulateListView() {
		ArrayAdapter<CharSequence>adapter = new SingleStudentListAdapter();
		View footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.single_text_view, null, false);
        TextView tv = (TextView)footerView.findViewById(R.id.single_TV);
        tv.setText("OverAll presence  "+totalPresentDays+ "  days");
		list.addFooterView(footerView);
		list.setAdapter(adapter);
		
	}
	public class ImageDisplayEntities{

		public Bitmap bitmap;
		public Student student;
		public String fileName;
		public ImageDisplayEntities(Bitmap bitmap, Student student,
				String fileName) {
//			super();
			this.bitmap = bitmap;
			this.student = student;
			this.fileName = fileName;
		}

		
	}
	private ImageDisplayEntities GetImage() {
//		String imagePath = imageList.get(pos);
		if (imagePath == null || imagePath == "") {
//			displayToast("image Path is null");
			if (Constants.isExternalPresent()) {
				File root = Constants.ROOT_DIRECTORY;
				File temp = new File(root, Constants.STUDENTS_IMAGE_DIRECTORY);
				if (!temp.exists()) {
//					displayToast("Directory Not Found: "+ temp);
					return null;
				} else {
//					StudentPresence student = studentPresenceList.get(pos);
					String fileName = Constants.ImageFileName(student.getName(), student.getCrn());
					File file = new File(temp, fileName);;
					
					
					if (file.exists()) {
						// read the bitmap
						Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
						Toast.makeText(this, fileName, Toast.LENGTH_LONG);
						return( new ImageDisplayEntities(bmp, student, fileName));
						
//						DisplayImage(bmp, student, fileName);
					}else{
//						displayToast("Image Not Found ! "+temp+"\n"+file.getPath());
						return null;
					}
				}
			}

		} else if(imagePath.length()>0){
//			File file = new File(imagePath);
			if (new File(imagePath).exists()) {
				Bitmap bmp = BitmapFactory.decodeFile(imagePath);
//				StudentPresence student = studentPresenceList.get(pos);
//				DisplayImage(bmp, student, imagePath);
				return( new ImageDisplayEntities(bmp,student,new File(imagePath).getName()));
			}
		}
		return null;
	}
	
	private void CreateListNGetTotal() {

//		openDB();
		MonthTotalList.clear();
		Cursor attendanceDayCursor = myDb.getRows(
				DBAdapter.ATTENDANCE_DAY_TABLE, new String[] {
						DBAdapter.KEY_ROWID, DBAdapter.KEY_DATE },
				DBAdapter.KEY_CLS_ID, clsId);
		totalClassesHeld = attendanceDayCursor.getCount();
		int totalPresentDaysForAStudent = 0;
		if (attendanceDayCursor.moveToFirst()) {
//			boolean isMonthChanged = false;
			int monthlyTotal = 0;
			int previousMonth = -1;
			do {
				long date = attendanceDayCursor.getLong(1);
				int thisMonth = CustomDate.GetMonth(date);
				int thisDay = CustomDate.GetDay(date);
				if (previousMonth != thisMonth) {
					// write(monthArray[thisMonth]);
					MonthList.add(monthArray[thisMonth]);
					previousMonth = thisMonth;

					if (totalClassDays == 0) {
						// MonthTotalList.add("");

					} else {
						MonthTotalList.add(String.valueOf(monthlyTotal));
					}
					monthlyTotal = 0;

				} else {
					MonthList.add("");
					MonthTotalList.add("");
				}
				// here we deal with each day of the class
				long attdDayId = attendanceDayCursor.getLong(0);
				CharSequence status = "";
				String[] rowsToSelect = new String[] { DBAdapter.KEY_ROWID,
						DBAdapter.KEY_PRESENCE };
				Cursor attdCursor = myDb.getRows(DBAdapter.ATTENDANCE_TABLE,
						rowsToSelect, DBAdapter.KEY_STD_ID, stdId,
						DBAdapter.KEY_DAY_ID, attdDayId);
				if (attdCursor.moveToFirst()) {
					if (attdCursor.getString(1).equalsIgnoreCase("P")) {
						status = "P";
						monthlyTotal++;
						totalPresentDaysForAStudent++;
					} else {
						status = "A";
					}
				} else {
					status = "A";
				}
				
//				int day = CustomDate.GetDay(milliSeconds);
				String fDay = String.valueOf(thisDay);
				if(thisDay==1 || thisDay%10==1){
					fDay+=" st";
				}else if(thisDay==2 || thisDay%10 == 2){
					fDay+=" nd";
				}else if(thisDay==3 || thisDay%10 == 3){
					fDay+=" rd";
				}else{
					fDay+=" th";
				}
				
				DateList.add(fDay);
				PresenceList.add(status);
				// if(attdCountForIncrement==attendanceCount/2){
				// publishProgress(1);
				// }
				attdCursor.close();

				/*
				 * if(isMonthChanged){
				 * MonthTotalList.add(String.valueOf(monthlyTotal));
				 * monthlyTotal = 0; isMonthChanged = false; }else
				 * MonthTotalList.add("");
				 */

				if (totalClassDays == totalClassesHeld - 1
						|| totalClassDays == totalClassesHeld) {
					MonthTotalList.add(String.valueOf(monthlyTotal));
					/*displayToast(String.valueOf(monthlyTotal) + "   "
							+ MonthTotalList.get(MonthTotalList.size() - 1));
					displayToast(" List Postition "
							+ (MonthTotalList.size() - 1));*/
					// MonthTotalList.add(String.valueOf(monthlyTotal));
					// MonthTotalList.add(String.valueOf(monthlyTotal));
				}
				 
				// write(String.format("%.02f", presentPercent)+" %");
				totalClassDays++;
			} while (attendanceDayCursor.moveToNext());

		}
		totalPresentDays = totalPresentDaysForAStudent;
		attendanceDayCursor.close();
		// return totalPresentDaysForAStudent;
	}

	private class SingleStudentListAdapter extends ArrayAdapter<CharSequence> {
		public SingleStudentListAdapter( ) {
			super(SingleStudentReportActivity.this,
					R.layout.single_student_attendance_list_layout,
					PresenceList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.single_student_attendance_list_layout, parent,
						false);
			}
			TextView datetv = (TextView) itemView
					.findViewById(R.id.dayText);
			TextView totalText = (TextView) itemView
					.findViewById(R.id.totalText);
			TextView monthtv = (TextView) itemView
					.findViewById(R.id.singleMonthTV);
			TextView statustv = (TextView) itemView.findViewById(R.id.singleDayTV);
			TextView totaltv1 = (TextView) itemView
					.findViewById(R.id.SingleTotalTV1);
			TextView totaltv = (TextView) itemView
					.findViewById(R.id.SingleTotalTV);
			
			monthtv.setVisibility(View.GONE);
			totaltv1.setVisibility(View.GONE);
			totaltv.setVisibility(View.GONE);
			totalText.setVisibility(View.GONE);
//			dayText.setVisibility(View.GONE);

			String thisMonth = MonthList.get(position);
			String thisMonthTotal = MonthTotalList.get(position);
			statustv.setText(PresenceList.get(position));
			
			
			datetv.setText(DateList.get(position));
			
			if (!thisMonth.equalsIgnoreCase("")) {
				monthtv.setVisibility(View.VISIBLE);
				monthtv.setText(thisMonth);
			}
			if (!thisMonthTotal.equalsIgnoreCase("")) {
				totalText.setVisibility(View.VISIBLE);
				totaltv.setVisibility(View.VISIBLE);
				totaltv.setText(thisMonthTotal);
			}
			return itemView;
		}
	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	private void closeDB() {
		myDb.close();
	}

}
