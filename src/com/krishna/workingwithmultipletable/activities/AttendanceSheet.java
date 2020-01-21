package com.krishna.workingwithmultipletable.activities;

import helpers.Constants;
import helpers.CustomDate;
import helpers.DBAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.R;

import entities.EachDay;
import entities.StudentPresence;

/**
 * @Description------------Used------------ Attendance Sheet: This class
 *                                          provides a page with checkboxes to
 *                                          take attendance
 * @layout R.layout.attendancesheet
 * @category attendance sheet
 */
public class AttendanceSheet extends Activity {
	// MyCustomAdapter dataAdapter = null;
	DBAdapter myDb;
	long clsIds, dayIds;
	// public static long previousClassId ;
	int currentPresence = 0;
	public static String EXTRA_MESSAGE = "com.krishna.workingwithmultipletable.classIds";
	TextView tvClassInfo, currentlyPresentStudentsTV;

	ArrayAdapter adapter;
	
	private List<StudentPresence> studentPresenceList = new ArrayList<StudentPresence>();
	private List<Boolean> checkedBoxList = new ArrayList<Boolean>();
	// private List<Images> imageList = new ArrayList<Images>();
	private List<String> imageList = new ArrayList<String>();
	private List<EachDay> totalPresenceOfEachStudentList = new ArrayList<EachDay>();

	private static int slidePosition = 0;
	// ArrayList<StudentPresence> spList = new ArrayList<StudentPresence>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_sheet);

		Intent intent = getIntent();
		clsIds = intent.getLongExtra(Constants.EXTRA_MESSAGE_CLASS_ID, -1);
		dayIds = intent.getLongExtra(Constants.EXTRA_MESSAGE_DAY_ID, -1);

		tvClassInfo = (TextView) findViewById(R.id.tvSheetClassInfo);
		currentlyPresentStudentsTV = (TextView) findViewById(R.id.currentlyPresentStudentsTV);
		slidePosition = 0;
		openDB();
		adapter = new AttendanceListAdapter();
		populateClassData();
		 populateListView();
		 displayListView();
		 RegisterClickCallBack();
	}

	private void RegisterClickCallBack() {
		final Button saveAttdBt = (Button) findViewById(R.id.saveAttdButton);
		saveAttdBt.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					saveAttdBt.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.orange_drawable));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					saveAttdBt.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.blue_drawable));
				}
				return false;
			}
		});

	}

	private void populateClassData() {
		// totalClass.setText("Total classes:"+totalClassDays());
		Cursor c = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
				clsIds);
		String classInfo = "";
		if (c.moveToFirst()) {
			int batch = c.getInt(DBAdapter.COL_BATCH);
			String program = c.getString(DBAdapter.COL_PROGRAM);
			String course = c.getString(DBAdapter.COL_COURSE);
			classInfo = String.valueOf(batch) + " / " + program + " / "
					+ course;
			tvClassInfo.setText(classInfo);
		}
		c.close();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// previousClassId = clsIds;
		if (currentPresence > 0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setMessage("Do you want to save?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									saveAttd(null);
								}
							})
					.setNeutralButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									finish();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub

								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}

		// ViewClass.valueThruActivity=1;
		else {
			finish();
			super.onBackPressed();
		}

	}

	private void populateListView() {

		// Array list of countries
		totalPresenceOfEachStudentList.clear();
		String[] classrow = new String[] { DBAdapter.KEY_STD_ID };
		// ////////////////
		Cursor stdclcursor = myDb.getAscStd(clsIds);

		if (stdclcursor.moveToFirst()) {
			do {
				long stdsId = stdclcursor.getLong(0);
				StudentPresence object = new StudentPresence();
				EachDay eachDay = new EachDay();
				String imagePath = "";
				Cursor stdcursor = myDb.getRow(DBAdapter.STUDENT_TABLE,
						DBAdapter.ALL_STUDENT_KEYS, stdsId);
				if (stdcursor.moveToFirst()) {
					String scrn = stdcursor.getString(DBAdapter.COL_STD_CRN);
					String sname = stdcursor.getString(DBAdapter.COL_STD_NAME);
					imagePath = stdcursor.getString(DBAdapter.COL_STD_IMAGE);
					
					
					object.setCrn(scrn);
					object.setName(sname);
					object.set_id(stdsId);
					object.setSelected(false);

					
					eachDay.set_id(stdsId);
					eachDay.setPresence(addAttendance(stdsId));
				}
				totalPresenceOfEachStudentList.add(eachDay);
				// spList.add(object);
				checkedBoxList.add(false);
				studentPresenceList.add(object);
				imageList.add(imagePath);
				stdcursor.close();
			} while (stdclcursor.moveToNext());
			stdclcursor.close();
			// //////////////
		}

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

	private void displayListView() {
		// create an ArrayAdaptar from the String Array
		/*
		 * dataAdapter = new MyCustomAdapter(this,
		 * R.layout.attendance_sheet_list_layout, spList);
		 */
		final Context thisContext = this;
//		ArrayAdapter adapter = new AttendanceListAdapter();
		ListView listView = (ListView) findViewById(R.id.lvSheet);
		listView.setAdapter(adapter);
		
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				displayToast("student this selected " + pos);

			}
		});

		/*
		 * listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		 * { public void onItemClick(AdapterView<?> parent, View view, int
		 * position, long id) { // When clicked, show a toast with the TextView
		 * text StudentPresence country = (StudentPresence)
		 * parent.getItemAtPosition(position);
		 * displayToast("Student Selected "+position); } });
		 */

	}

	// ======================= ATTENDANCE LIST ADAPTER
	// ===============================//
	
	CheckBox checkBox;
	private class ViewHolder {
		TextView tvcrn;
		TextView tvname;
		CheckBox cbattd;
	}
	private class AttendanceListAdapter extends ArrayAdapter<StudentPresence> {

		public AttendanceListAdapter() {
			super(AttendanceSheet.this, R.layout.attendance_list_layout,
					studentPresenceList);
		}

		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			final int pos = position;
			View itemView = convertView;
			 ViewHolder holder = null;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.attendance_list_layout, parent, false);
				holder = new ViewHolder();
				holder.cbattd = (CheckBox) itemView
						.findViewById(R.id.tvSheetAttd);
				holder.tvcrn = (TextView) itemView
						.findViewById(R.id.tvSheetCrn);
				holder.tvname = (TextView) itemView
						.findViewById(R.id.tvSheetName);
				itemView.setTag(holder);
				holder.cbattd.setChecked(false);
				// if(previousClassId==clsIds){
				// holder.cbattd.setChecked(checkedBoxList.get(position));
				// }else{
				// holder.cbattd.setChecked(false);
				// }
			} else {
				holder = (ViewHolder) itemView.getTag();
				// displayToast("crn: "+holder.tvcrn.getText().toString()+" --name: "+holder.tvname.toString());
			}
			StudentPresence sp = studentPresenceList.get(position);

			final EachDay eachDay = totalPresenceOfEachStudentList
					.get(position);
			// TextView tvcrn = (TextView)
			// itemView.findViewById(R.id.item_crn_layout);
			holder.tvcrn.setText(sp.getCrn());
			// TextView tvname = (TextView)
			// itemView.findViewById(R.id.item_name_layout);
			holder.tvname.setText(sp.getName());
			// CheckBox cbPresence = (CheckBox)
			// itemView.findViewById(R.id.item_cb_layout);
			holder.cbattd.setChecked(checkedBoxList.get(position));
			holder.cbattd.setText("" + String.valueOf(eachDay.getPresence()));
			holder.cbattd.setTag(sp);
			
			
			holder.tvcrn.setBackgroundColor(Color.LTGRAY);
			holder.tvcrn.setTextColor(Color.BLACK);
			holder.tvname.setBackgroundColor(Color.LTGRAY);
			holder.tvname.setTextColor(Color.BLACK);
			
			final ViewHolder hol =holder;
			holder.tvcrn.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					setTouchColor(hol,event);
					return false;
				}
			});
			
			holder.tvname.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					setTouchColor(hol, event);
					return false;
				}
			});
			
			holder.cbattd.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					checkBox = (CheckBox) v;
					
					// sps holds the address of the item of the list
					// we set tag to cbAttd just before setOnClickListener
					StudentPresence sps = (StudentPresence) cb.getTag();
					if (cb.isChecked()) {
						cb.setChecked(true);
						currentPresence++;
						// this changes the value in the list coz sps carries
						// address of the item in the list
						sps.setSelected(true);
						int x = Integer.parseInt(cb.getText().toString()) + 1;
						cb.setText("" + String.valueOf(x));
						checkedBoxList.set(pos, true);
						eachDay.setPresence(eachDay.getPresence() + 1);
					} else {
						cb.setChecked(false);
						currentPresence--;
						// this changes the value in the list coz sps carries
						// address of the item in the list
						sps.setSelected(false);
						int x = Integer.parseInt(cb.getText().toString()) - 1;
						cb.setText("" + String.valueOf(x));
						checkedBoxList.set(pos, false);
						eachDay.setPresence(eachDay.getPresence() - 1);

					}
					if (currentPresence <= 1) {
						currentlyPresentStudentsTV.setText(String
								.valueOf(currentPresence) + " student present.");
					} else {
						currentlyPresentStudentsTV.setText(String
								.valueOf(currentPresence)
								+ " students present.");
					}

				}
			});

			holder.tvcrn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					ImageDisplayEntities entities = GetImage(pos);
					if(entities!= null)
						DisplayImage(entities.bitmap, entities.student, entities.fileName,  pos);
					else
						displayToast("Image could not be extracted");
				}
			});

			holder.tvname.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TextView cb = (TextView) v;
					// imageList.get(pos);
					
					 ImageDisplayEntities entities = GetImage(pos);
					 if(entities!=null)
						 DisplayImage(entities.bitmap, entities.student, entities.fileName, pos);
					 else
						 displayToast("Image could not be extracted");
				}
			});

			return itemView;
		}
	}
	
	/*void checkChanged(boolean isChecked, int pos) {

		final EachDay eachDay = totalPresenceOfEachStudentList.get(pos);
		StudentPresence sps = (StudentPresence) checkBox.getTag();
		if (isChecked) {
//			checkBox.setChecked(true);
			currentPresence++;
			// this changes the value in the list coz sps carries
			// address of the item in the list
			sps.setSelected(true);
			int x = Integer.parseInt(checkBox.getText().toString()) + 1;
//			checkBox.setText("" + String.valueOf(x));
			checkedBoxList.set(pos, true);
			eachDay.setPresence(eachDay.getPresence() + 1);
		} else {
//			checkBox.setChecked(false);
			currentPresence--;
			// this changes the value in the list coz sps carries
			// address of the item in the list
			sps.setSelected(false);
			int x = Integer.parseInt(checkBox.getText().toString()) - 1;
//			checkBox.setText("" + String.valueOf(x));
			checkedBoxList.set(pos, false);
			eachDay.setPresence(eachDay.getPresence() - 1);

		}
		if (currentPresence <= 1) {
			currentlyPresentStudentsTV.setText(String.valueOf(currentPresence)
					+ " student present.");
		} else {
			currentlyPresentStudentsTV.setText(String.valueOf(currentPresence)
					+ " students present.");
		}

	}*/
	
	private void setTouchColor(ViewHolder hol, MotionEvent event){
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			hol.tvcrn.setBackgroundDrawable(getResources()
//					.getDrawable(R.drawable.orange_drawable));
			hol.tvcrn.setBackgroundColor(Color.BLACK);
			hol.tvcrn.setTextColor(Color.WHITE);
			hol.tvname.setBackgroundColor(Color.BLACK);
			hol.tvname.setTextColor(Color.WHITE);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
//			hol.tvcrn.setBackgroundDrawable(getResources()
//					.getDrawable(R.drawable.blue_drawable));
			hol.tvcrn.setBackgroundColor(Color.LTGRAY);
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.LTGRAY);
			hol.tvname.setTextColor(Color.BLACK);
		}else if (event.getAction() == MotionEvent.ACTION_SCROLL){
			hol.tvcrn.setBackgroundColor(Color.LTGRAY);
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.LTGRAY);
			hol.tvname.setTextColor(Color.BLACK);
		}else if (event.getAction() == MotionEvent.ACTION_MOVE){
			hol.tvcrn.setBackgroundColor(Color.LTGRAY);		
			hol.tvcrn.setTextColor(Color.BLACK);
			hol.tvname.setBackgroundColor(Color.LTGRAY);
			hol.tvname.setTextColor(Color.BLACK);
		}
	}
	/**
	 * 
	 * @param Bitmap, StudentPresence, FileName
	 *
	 */
	public class ImageDisplayEntities{
		
		public ImageDisplayEntities(Bitmap bitmap, StudentPresence student,
				String fileName) {
			super();
			this.bitmap = bitmap;
			this.student = student;
			this.fileName = fileName;
		}
		public Bitmap bitmap;
		public StudentPresence student;
		public String fileName;
		
	}
	private ImageDisplayEntities GetImage(int pos) {
		String imagePath = imageList.get(pos);
		if (imagePath == null || imagePath == "") {
//			displayToast("image Path is null");
			if (isExternalPresent()) {
				File root = Constants.ROOT_DIRECTORY;
				File temp = new File(root, Constants.STUDENTS_IMAGE_DIRECTORY);
				if (!temp.exists()) {
//					displayToast("Directory Not Found: "+ temp);
					return null;
				} else {
					StudentPresence student = studentPresenceList.get(pos);
					String fileName = Constants.ImageFileName(student.getName(), student.getCrn());
					File file = new File(temp, fileName);;
					
					
					if (file.exists()) {
						// read the bitmap
						Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
						return( new ImageDisplayEntities(bmp,student,fileName));
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
				StudentPresence student = studentPresenceList.get(pos);
//				DisplayImage(bmp, student, imagePath);
				return( new ImageDisplayEntities(bmp,student,imagePath));
			}
		}
		return null;
	}

	private void DisplayImage(Bitmap bm, StudentPresence student, 
			String name, final int pos) {
		LayoutInflater li = getLayoutInflater();
		final View v = li.inflate(R.layout.image_view, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		RadioGroup  rGroup = (RadioGroup) v.findViewById(R.id.radioGroupAttd);
		ImageView imageview = (ImageView) v.findViewById(R.id.stdImageView);
		TextView imagetv = (TextView) v.findViewById(R.id.imageTextView);
		imagetv.setText(student.getCrn() + " " + student.getName() +"\n"+name);
		imageview.setImageBitmap(bm);
		
		builder.setView(v);
		final AlertDialog alert = builder.create();
		
		rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup rad, int id) {
//				displayToast(""+pos);
				switch(id){
				case R.id.radio0:
//					checkChanged(true, listPosition);
//					alert.cancel();
					/*currentPresence++;
					StudentPresence sps = studentPresenceList.get(listPosition);
					final EachDay eachDay = totalPresenceOfEachStudentList
							.get(listPosition);
//					cb.setChecked(true);
//					currentPresence++;
					// this changes the value in the list coz sps carries
					// address of the item in the list
					sps.setSelected(true);
					studentPresenceList.set(listPosition, sps);
//					int x = Integer.parseInt(cb.getText().toString()) + 1;
//					cb.setText("" + String.valueOf(x));
					checkedBoxList.set(pos, true);
					eachDay.setPresence(eachDay.getPresence() + 1);
					totalPresenceOfEachStudentList.set(listPosition, eachDay);*/
					
					displayToast("Present");
					break;
				case R.id.radio1:
					currentPresence++;
					StudentPresence sps = studentPresenceList.get(pos);
					final EachDay eachDay = totalPresenceOfEachStudentList
							.get(pos);
					// this changes the value in the list coz sps carries
					// address of the item in the list
					sps.setSelected(true);
					studentPresenceList.set(pos, sps);
//					int x = Integer.parseInt(cb.getText().toString()) + 1;
//					cb.setText("" + String.valueOf(x));
					checkedBoxList.set(pos, true);
					
					eachDay.setPresence(eachDay.getPresence() + 1);
					totalPresenceOfEachStudentList.set(pos, eachDay);
					
					adapter.notifyDataSetChanged();
					displayToast("Absent "+checkedBoxList.get(0));
					
					alert.cancel();
					break;
				case R.id.radio2:
//					checkChanged(false, listPosition);
					alert.cancel();
//					alert.dismiss();
					displayToast("Late");
					break;
					
				}
			}
		});
		
		alert.show();
		
		// arsImages/batch_program/shreeku010308
		
//		builder.show();
	}
	

	private boolean isExternalPresent() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED) || 
				Environment.getExternalStorageState() == Environment.MEDIA_REMOVED) {
			Toast.makeText(
					AttendanceSheet.this,
					"SD card Unmounted. Please insert a memory card",
					Toast.LENGTH_LONG).show();
			return false;
		}else{
			return true;
		}
	}


	// ==================================================================================//

	/*
	 * private class MyCustomAdapter extends ArrayAdapter<StudentPresence> {
	 * 
	 * private ArrayList<StudentPresence> innerspList; public
	 * ArrayList<StudentPresence> checkedList = new
	 * ArrayList<StudentPresence>();
	 * 
	 * public MyCustomAdapter(Context context, int textViewResourceId,
	 * ArrayList<StudentPresence> countryList) { super(context,
	 * textViewResourceId, countryList); this.innerspList = new
	 * ArrayList<StudentPresence>(); this.innerspList.addAll(countryList);
	 * checkedList.clear(); currentPresence = 0; }
	 * 
	 * private class ViewHolder { TextView tvcrn; TextView tvname; CheckBox
	 * cbattd; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) {
	 * 
	 * ViewHolder holder = null; Log.v("ConvertView", String.valueOf(position));
	 * 
	 * if (convertView == null) { LayoutInflater vi = (LayoutInflater)
	 * getSystemService(Context.LAYOUT_INFLATER_SERVICE); convertView =
	 * vi.inflate(R.layout.attendance_sheet_list_layout, null);
	 * 
	 * holder = new ViewHolder(); holder.tvcrn = (TextView) convertView
	 * .findViewById(R.id.item_crn_layout); holder.tvname = (TextView)
	 * convertView .findViewById(R.id.item_name_layout); holder.cbattd =
	 * (CheckBox) convertView .findViewById(R.id.item_cb_layout);
	 * convertView.setTag(holder); holder.cbattd.setChecked(false);
	 * holder.cbattd.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { CheckBox cb = (CheckBox) v;
	 * if(cb.isChecked()){ // cb.setChecked(true); }else { //
	 * cb.setChecked(false); } } });
	 * 
	 * } else { holder = (ViewHolder) convertView.getTag(); } EachDay eachDay =
	 * attdList.get(position); if (clsIds >= 0 && dayIds < 0) { StudentPresence
	 * sps = spList.get(position); holder.tvcrn.setText(" " + sps.getCrn() +
	 * " "); holder.tvname.setText(sps.getName());
	 * holder.cbattd.setChecked(false); holder.cbattd.setText("" +
	 * String.valueOf(eachDay.getPresence())); holder.cbattd.setTag(sps);
	 * 
	 * } // ================== ON CLICK LISTENER
	 * ================================// holder.cbattd.setOnClickListener(new
	 * View.OnClickListener() { public void onClick(View v) { CheckBox cb =
	 * (CheckBox) v;
	 * 
	 * StudentPresence sp = (StudentPresence) cb.getTag();
	 * sp.setSelected(cb.isChecked()); if (cb.isChecked()) {
	 * cb.setChecked(true); int x = Integer.parseInt(cb.getText().toString()) +
	 * 1; cb.setText("" + String.valueOf(x)); sp.setSelected(true); //
	 * tempChecked = true; currentPresence++; } else { cb.setChecked(false); //
	 * tempChecked = false; int x = Integer.parseInt(cb.getText().toString()) -
	 * 1; cb.setText("" + String.valueOf(x)); sp.setSelected(false);
	 * currentPresence--; } if(currentPresence<=1){
	 * currentlyPresentStudentsTV.setText
	 * (String.valueOf(currentPresence)+" student present."); }else{
	 * currentlyPresentStudentsTV
	 * .setText(String.valueOf(currentPresence)+" students present."); }
	 * checkedList.add(sp); } });
	 * 
	 * // ===========================================================// // it
	 * prints the students name in the list EachDay eachDay =
	 * attdList.get(position); if (clsIds >= 0 && dayIds < 0) { StudentPresence
	 * sps = spList.get(position); holder.tvcrn.setText(" " + sps.getCrn() +
	 * " "); holder.tvname.setText(sps.getName());
	 * holder.cbattd.setChecked(false); holder.cbattd.setText("" +
	 * String.valueOf(eachDay.getPresence())); holder.cbattd.setTag(sps);
	 * 
	 * } if (dayIds >= 0 && clsIds >= 0) { StudentPresence sps =
	 * spList.get(position); holder.tvcrn.setText(" " + sps.getCrn() + " ");
	 * holder.tvname.setText(sps.getName());
	 * holder.cbattd.setChecked(sps.isSelected()); holder.cbattd.setText(" ");
	 * holder.cbattd.setTag(sps); } return convertView; } }
	 */

	

	// ////////////////////////////////////////////
	public void saveAttd(View v) {
		// responseText.append("The following were selected...\n");
		Cursor stdclcursor = myDb.getAscStd(clsIds);
		if (stdclcursor.getCount() > 0) {
			ContentValues attdDayValues = new ContentValues();
			attdDayValues.put(DBAdapter.KEY_CLS_ID, clsIds);
			attdDayValues.put(DBAdapter.KEY_DATE,
					CustomDate.GetCurrentDateTimeInMilliSecond());

			long attdDayId = myDb.insertRow(DBAdapter.ATTENDANCE_DAY_TABLE,
					attdDayValues);

			for (int i = 0; i < studentPresenceList.size() - 1; i++) {
				StudentPresence sps = studentPresenceList.get(i);

				ContentValues initialValues = new ContentValues();
				initialValues.put(DBAdapter.KEY_DAY_ID, attdDayId);
				initialValues.put(DBAdapter.KEY_STD_ID, sps.get_id());
				initialValues.put(DBAdapter.KEY_PRESENCE, sps.getPresence());

				// Insert it into the database.
				myDb.insertRow(DBAdapter.ATTENDANCE_TABLE, initialValues);
				/*
				 * if( newClsId==-1){
				 * //displayToast("Sorry!!! The datas couldn't be saved.");
				 * break; }else{displayToast("Save successful!!");}
				 */
			}
			StudentPresence sps = studentPresenceList.get(studentPresenceList
					.size() - 1);
			ContentValues initialValues = new ContentValues();
			initialValues.put(DBAdapter.KEY_DAY_ID, attdDayId);
			initialValues.put(DBAdapter.KEY_STD_ID, sps.get_id());
			initialValues.put(DBAdapter.KEY_PRESENCE, sps.getPresence());
			long newClsId = myDb.insertRow(DBAdapter.ATTENDANCE_TABLE,
					initialValues);

			if (newClsId == -1) {
				displayToast("Sorry!!! The datas couldn't be saved.");
			} else {
				displayToast("Save successful.");
			}
			/*
			 * Intent intent = new
			 * Intent(AttendanceSheet.this,ShowAllAttendanceDates.class);
			 * intent.putExtra(EXTRA_MESSAGE, clsIds); startActivity(intent);
			 */
			finish();
		} else {
			displayToast("No Students to take Attendance...");
		}
	}
	//position till current attendance taken
	//position of current attendance
	//save to the global variable
	
	public void slideShow(View view){
		Intent intent = new Intent(AttendanceSheet.this,Slide.class);
		startActivity(intent);
		
		displayToast("Not available yet.");
		
				/*	final CheckBox cb = (CheckBox)findViewById(R.id.cbRememberMyChoice);
			
			LayoutInflater li = getLayoutInflater();
			final View v = li.inflate(R.layout.slideshow_layout, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final ImageView present = (ImageView)v.findViewById(R.id.presentImageSlideshow);
			final ImageView absent = (ImageView)v.findViewById(R.id.absentImageSlideshow);
			final ImageView late = (ImageView)v.findViewById(R.id.lateImageSlideshow);
			final ImageView studentImage = (ImageView)v.findViewById(R.id.stdImageSlideshow);
			
			ImageDisplayEntities entities = GetImage(slidePosition);
			if(entities!=null){
				studentImage.setImageBitmap(entities.bitmap);
			}
			present.setOnClickListener(new View.OnClickListener() {
				
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public void onClick(View arg0) {
					//change image to bordered
					
					present.setImageResource(R.drawable.present_select);
					absent.setImageResource(R.drawable.absent);
					late.setImageResource(R.drawable.late);
					studentImage.setScaleX((float)0.5);
					studentImage.setX((float)0.5);
					checkedBoxList.set(slidePosition, true);
					//redraw list
					slidePosition++;
					ImageDisplayEntities entities = GetImage(slidePosition);
					if(entities!= null){
						studentImage.setImageBitmap(entities.bitmap);
						present.setImageResource(R.drawable.present);
					}
//					Animation animation = new Animation();
//					studentImage.setAnimation(animation);
					
					ChangeStudentInSlide();
					
				}
			});
			absent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//change image to bordered
					absent.setImageResource(R.drawable.absent_select);
					present.setImageResource(R.drawable.present);
					late.setImageResource(R.drawable.late);
					ChangeStudentInSlide();
				}
			});
			late.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//change image to bordered
					late.setImageResource(R.drawable.late_select);
					present.setImageResource(R.drawable.present);
					absent.setImageResource(R.drawable.absent);
					
					ChangeStudentInSlide();
				}
			});
			
			builder.setView(v);		
			builder.show();
			*/
			
	}
	private void ChangeStudentInSlide(){
		
	}
	private void displayToast(String message) {
		Toast.makeText(AttendanceSheet.this, message, Toast.LENGTH_SHORT).show();

	}

}