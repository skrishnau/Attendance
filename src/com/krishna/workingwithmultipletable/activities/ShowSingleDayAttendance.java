package com.krishna.workingwithmultipletable.activities;
import helpers.Constants;
import helpers.CustomDate;
import helpers.DBAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.R;

import entities.EachDay;
import entities.StudentPresence;

/**
 * ----------Used-----   4 ---
 * @Displays  attendance of each student for a particular day (dayIds)
 *
 */

public class ShowSingleDayAttendance extends Activity{
	MyCustomAdapter dataAdapter = null;
	DBAdapter myDb;
	long clsIds=-1,dayIds=-1;
	CheckBox cBox;
	TextView tvclassInfo,tvDate;
	//TextView tvprogram,tvbatch,tvcourse,totalClass;
	
	private List<EachDay> attdList= new ArrayList<EachDay>();
	ArrayList<StudentPresence> spList = new ArrayList<StudentPresence>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_sheet);
		
		Intent intent=getIntent();
		  clsIds = intent.getLongExtra(Constants.EXTRA_MESSAGE_CLASS_ID, -1);
		  dayIds=intent.getLongExtra(Constants.EXTRA_MESSAGE_DAY_ID, -1);
		  
		//  cBox.setOnClickListener(myhandler);

		  Button slideshowButton = (Button)findViewById(R.id.slideshowBt);
		  slideshowButton.setVisibility(View.GONE);
		tvclassInfo=(TextView)findViewById(R.id.tvSheetClassInfo);
		tvDate=(TextView)findViewById(R.id.dateTextview);
		
		if(dayIds>=0 && clsIds>=0){
				Button bt = (Button)findViewById(R.id.saveAttdButton);
				bt.setVisibility(Button.GONE);
		}
		openDB();
//		displayToast("class: "+clsIds+"\tday: "+dayIds);
		populateClassData();
		populateListView();
		displayListView();
	}
		
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
		finish();
	    super.onBackPressed();
	    
        
	}
	////////
	private void populateClassData() {
		
		Cursor dateCursor=myDb.getRow(DBAdapter.ATTENDANCE_DAY_TABLE,
										DBAdapter.ALL_ATTENDANCE_DAY_KEYS, 
											dayIds);
		if(dateCursor.moveToFirst()){
			SimpleDateFormat dateFormat = new SimpleDateFormat(
	                "yyyy-MM-dd  hh:mm a ", Locale.getDefault());
			long dateInInteger=dateCursor.getLong(DBAdapter.COL_DATE);
	        Date date = new Date(dateInInteger);
	        tvDate.setText( dateFormat.format(date));
//		int dateInInteger=dateCursor.getInt(DBAdapter.COL_DATE);
//		GregorianCalendar gc = new GregorianCalendar();
//		Date date = new Date(dateInInteger);
//		gc.setTime(date);
//		tvDate.setText(""+gc.get(Calendar.YEAR)+"-"+gc.get(Calendar.MONTH)+"-"+gc.get(Calendar.DAY_OF_MONTH));
		}
		dateCursor.close();
		
		Cursor c=myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, clsIds);
		if(c.moveToFirst()){
			int batch=c.getInt(DBAdapter.COL_BATCH);
			String program=c.getString(DBAdapter.COL_PROGRAM);
			String course=c.getString(DBAdapter.COL_COURSE);
			String message = String.valueOf(batch)+" / "+program+" / "+course;
			tvclassInfo.setText(message);		
		}
		c.close();
}
			////////////
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
		
		private void populateListView(){
			int totalStudentsPresent = 0;
			TextView currentlyPresentStudentsTv = (TextView)findViewById(R.id.currentlyPresentStudentsTV);
			  //Array list of countries
			attdList.clear();
			Cursor stdclcursor = myDb.getAscStd(clsIds);
			   
			  if(stdclcursor.moveToFirst()){
				  	do{
					 		long stdsId = stdclcursor.getLong(0);
							StudentPresence object=new StudentPresence();
							EachDay eachDay=new EachDay();
							
							
								String scrn=stdclcursor.getString(DBAdapter.COL_STD_CRN);
								String sname=stdclcursor.getString(DBAdapter.COL_STD_NAME);

								object.setCrn(scrn);
								object.setName(sname);
								object.set_id(stdsId);
								////////////
								if(clsIds>=0 && dayIds<0){
									object.setSelected(false);
							   }
							   if(dayIds>=0 && clsIds>=0){
								   boolean present=getOneDayPresence(stdsId);
								   object.setSelected(present);
								   if(present){
									   totalStudentsPresent++;
								   }
							   }
								//////////////
								
								
								eachDay.set_id(stdsId);
								eachDay.setPresence(addAttendance(stdsId));
							
							attdList.add(eachDay);
							spList.add(object);
							
				}while(stdclcursor.moveToNext());
				  stdclcursor.close();
				 
			  }
			  if(totalStudentsPresent<=1){
				  currentlyPresentStudentsTv.setText(String.valueOf(totalStudentsPresent)+" student present.");
			  }else{
				  currentlyPresentStudentsTv.setText(String.valueOf(totalStudentsPresent)+" students present.");
			  }
			
		}
		
		private boolean getOneDayPresence(long stdid) {
			String[] presenceRow=new String[] {DBAdapter.KEY_DAY_ID,DBAdapter.KEY_PRESENCE};
			boolean presence=false;
			Cursor c=myDb.getRows(DBAdapter.ATTENDANCE_TABLE, presenceRow,
									DBAdapter.KEY_STD_ID, stdid
									);
			if(c.moveToFirst()){
				do{
					
					if(c.getLong(0)==dayIds){
						if(c.getString(1).equals("P")){
							presence=true;
							break;
						}
					}
				}while(c.moveToNext());
			}
			c.close();
			return presence;
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
			  //create an ArrayAdaptar from the String Array
			  dataAdapter = new MyCustomAdapter(this,
					  			R.layout.attendance_list_layout, 
					  			spList);
			  ListView listView = (ListView) findViewById(R.id.lvSheet);
			  listView.setAdapter(dataAdapter);


			 listView.setOnItemClickListener(new OnItemClickListener() {
			   public void onItemClick(AdapterView<?> parent, View view,
			     int position, long id) {
			    // When clicked, show a toast with the TextView text
			    StudentPresence country = (StudentPresence) parent.getItemAtPosition(position);
			    
			   /* Toast.makeText(getApplicationContext(),
			      "Clicked on Row: " + country.getName(), 
			      Toast.LENGTH_LONG).show();*/
			    
			   }
			  });

			 }
		private class MyCustomAdapter extends ArrayAdapter<StudentPresence> {

			  private ArrayList<StudentPresence> innerspList;
			  public ArrayList<StudentPresence> checkedList =new ArrayList<StudentPresence>();
			 
			  public MyCustomAdapter(Context context, int textViewResourceId, 
			    ArrayList<StudentPresence> countryList) {
			   super(context, textViewResourceId, countryList);
			   this.innerspList = new ArrayList<StudentPresence>();
			   this.innerspList.addAll(countryList);
			   checkedList.clear();
			  }

			  private class ViewHolder {
			   TextView tvcrn;
			   TextView tvname;
			   CheckBox cbattd;
			  }

			  
			  @Override
			  public View getView(int position, View convertView, ViewGroup parent) {

			   ViewHolder holder = null;
			   Log.v("ConvertView", String.valueOf(position));

			   if (convertView == null) {
				   			LayoutInflater vi = (LayoutInflater)getSystemService(
						    Context.LAYOUT_INFLATER_SERVICE);
				   			convertView = vi.inflate(R.layout.attendance_list_layout, null);

			   holder = new ViewHolder();
			   holder.tvcrn = (TextView) convertView.findViewById(R.id.tvSheetCrn);
			   holder.tvname = (TextView) convertView.findViewById(R.id.tvSheetName);
			   holder.cbattd = (CheckBox) convertView.findViewById(R.id.tvSheetAttd);
			   cBox = (CheckBox) convertView.findViewById(R.id.item_cb_layout);
				convertView.setTag(holder);

				holder.cbattd.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						displayToast("Sorry! you can't change value here.");
						CheckBox cb = (CheckBox) v;
						if (cb.isChecked()) {
							cb.setChecked(false);
						} else {
							cb.setChecked(true);
						}
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			   //////////////////////////////////it prints the students name in the list
			   EachDay eachDay=attdList.get(position);
			   if(clsIds>=0 && dayIds<0){
					   StudentPresence sps = spList.get(position);
					   holder.tvcrn.setText(" " +  sps.getCrn() + " ");
					   holder.tvname.setText(sps.getName());
					   holder.cbattd.setChecked(false);
					   holder.cbattd.setText(""+String.valueOf(eachDay.getPresence())); 
					   holder.cbattd.setTag(sps);
			   
			   }
			   if(dayIds>=0 && clsIds>=0){
				   StudentPresence sps = spList.get(position);
				   holder.tvcrn.setText(" " +  sps.getCrn() + " ");
				   holder.tvname.setText(sps.getName());
				   holder.cbattd.setChecked(sps.isSelected());
				   holder.cbattd.setText("  ");
				   holder.cbattd.setTag(sps);
			   }
			   
			   return convertView;

			  }

			 }
		////////////////////////////////
		/*myhandler.setOnClickListener( new View.OnClickListener() {  
		     public void onClick(View v) {  
		      displayToast("Sorry! You can not change value");
		     }  
		     
		    });  */
		

//////////////////////////////////////////////
		public void saveAttd(View v) {
		   // responseText.append("The following were selected...\n");
			ContentValues attdDayValues = new ContentValues();
		     attdDayValues.put(DBAdapter.KEY_CLS_ID, clsIds);
		     attdDayValues.put(DBAdapter.KEY_DATE, CustomDate.GetCurrentDateTimeInMilliSecond());
		     
		     long attdDayId=myDb.insertRow(DBAdapter.ATTENDANCE_DAY_TABLE, attdDayValues);
			
		    for(int i=0;i<spList.size();i++){
		     StudentPresence sps = spList.get(i);
		     		     
		     ContentValues initialValues = new ContentValues();
		     initialValues.put(DBAdapter.KEY_DAY_ID, attdDayId);
		     initialValues.put(DBAdapter.KEY_STD_ID, sps.get_id());
		     initialValues.put(DBAdapter.KEY_PRESENCE, sps.getPresence());
				
				// Insert it into the database.
				long newClsId = myDb.insertRow(DBAdapter.ATTENDANCE_TABLE, initialValues);
				if( newClsId==-1){
					displayToast("Sorry!!! The datas couldn't be saved.");
				}else{displayToast("Save successful!!");}
		 }
		    Intent intent = new Intent(ShowSingleDayAttendance.this,ShowAllAttendanceDates.class);
		    intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
		    startActivity(intent);
		}
/////////////////////////////////////////////////		

		
		
	// Display an entire recordset to the screen.
		
	/*	private void displayClassSet(Cursor cursor) {
			
			String message = "";
			
			if (cursor.moveToFirst()) {
				do {
					// Process the data:
					int id = cursor.getInt(DBAdapter.COL_ROWID);
					int batch = cursor.getInt(DBAdapter.COL_BATCH);
					String program = cursor.getString(DBAdapter.COL_PROGRAM);
					String course = cursor.getString(DBAdapter.COL_COURSE);
					
					// Append data to the message:
					message += "id=" + id
							   +", batch=" + batch
							   +", program=" + program
							   +", course=" + course
							   +"\n";
				} while(cursor.moveToNext());
			}
					cursor.close();
					displayToast(message);
				}*/
		private void displayToast(String message) {
			Toast.makeText(ShowSingleDayAttendance.this, message,
                    Toast.LENGTH_SHORT).show();
	        
		}
		
}