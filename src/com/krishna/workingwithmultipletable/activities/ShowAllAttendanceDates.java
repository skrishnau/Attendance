package com.krishna.workingwithmultipletable.activities;





/**
 * 
 * -------3----Not USED---------
 * Displays lists of Attandance dates with time --------
 * 
 * @layout R.layout.attendancedays
 * 
 * 
 */
public class ShowAllAttendanceDates  {
	/*DBAdapter myDb;
	String program,batch,course;
	long editDate;
	long clsIds,editId;
	List<Long> attdIds =new ArrayList<Long>();
	ListView listForLong;
	TextView tvtotal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent=getIntent();
		clsIds = intent.getLongExtra(ViewClass.EXTRA_MESSAGE, -1);
		
		setContentView(R.layout.show_all_attendance_dates);
		tvtotal=(TextView)findViewById(R.id.tvTotalClsDays);
		listForLong=(ListView)findViewById(R.id.lvAttdDays);
		openDB();
		populateClassInfo();
		populateListViewFromDB();
		registerListClickCallback();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openDB();
		populateListViewFromDB();
		registerListClickCallback();
	}
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
		finish();
	    super.onBackPressed();
	    
        //finish();
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

	 
	 * UI Button Callbacks
	 
	public void addAttendanceDay(View v) {
		
        Intent intent = new Intent(ShowAllAttendanceDates.this,
                AttendanceSheet.class);
        intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID,clsIds);
        startActivity(intent);
	}
	public void addStudentADay(View v) {
		
        Intent intent = new Intent(ShowAllAttendanceDates.this,
                AddStudent.class);
        intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID,clsIds);
        startActivity(intent);
		
	}
	public void viewStds(View v){
		Intent intent = new Intent (ShowAllAttendanceDates.this,ViewAllStudents.class);
		intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID,clsIds);
		startActivity(intent);
	}
	
		private int totalClassDays(){
			 //for total class days
			int ClassDays=0;
			String[] attdKeys= new String[]{DBAdapter.KEY_ROWID};
			Cursor attdDayscursor = myDb.getRows(DBAdapter.ATTENDANCE_DAY_TABLE,
											  attdKeys,
											  DBAdapter.KEY_CLS_ID, 
											  clsIds);
			ClassDays=attdDayscursor.getCount();
			 attdDayscursor.close();
			 return ClassDays;
		}
	
	private void populateClassInfo() {
		 tvtotal.setText("Total Class Days:  "+totalClassDays()+" days");
		 Cursor cursor = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, clsIds);
		
		 tvbatch.setText(cursor.getInt(DBAdapter.COL_BATCH));
		 tvcourse.setText(cursor.getString(DBAdapter.COL_COURSE));
		 tvprogram.setText(cursor.getString(DBAdapter.COL_PROGRAM));
		 cursor.close();
	}
	private void populateListViewFromDB() {
			
		Cursor cursor = myDb.getRowsDesc(DBAdapter.ATTENDANCE_DAY_TABLE, 
									DBAdapter.ALL_ATTENDANCE_DAY_KEYS,
									DBAdapter.KEY_CLS_ID,
									clsIds);
		List<String> array = new ArrayList<String>();
		if(cursor.moveToFirst()){
			do{
				long id=cursor.getLong(0);
				long date = cursor.getLong(DBAdapter.COL_DATE);
				array.add(CustomDate.GetFormattedDate(date));
				
				attdIds.add(id);
			}while(cursor.moveToNext());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
		ListView myList = (ListView) findViewById(R.id.lvAttdDays);
		myList.setAdapter(adapter);
		
		
		cursor.moveToFirst();
		startManagingCursor(cursor);
        String[] fromColumns = {DBAdapter.KEY_DATE};
        int[] toViews = {android.R.id.text1}; 
        
        SimpleCursorAdapter myCursorAdapter = 
				new SimpleCursorAdapter(
						this,		// Context
						android.R.layout.simple_list_item_1,	// Row layout template
						cursor,					// cursor (set of DB records to map)
						fromColumns,			// DB Column names
						toViews				// View IDs to put information in
						);
        
       
        
        
        myList.setAdapter(myCursorAdapter);
        //
        
        registerForContextMenu(myList);
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    int index, long arg3) {

//                ListView myList = (ListView)findViewById(R.id.allStdlv);
                //Student student=stdList.get(index);
                long id=attdIds.get(index);
                Cursor day=myDb.getRow(DBAdapter.ATTENDANCE_DAY_TABLE, 
                							DBAdapter.ALL_ATTENDANCE_DAY_KEYS, id);
                if(day.moveToFirst()){
                	editDate=day.getLong(DBAdapter.COL_DATE);
                	editId=id;
                }
                	
//                editId=arg3;
                //displayToast(	"got long clicked !!");
               // alertcrn.setText(dealWithStudent.getCrn());
			//	alertname.setText(dealWithStudent.getName());
				//alertname.setTextColor(Color.BLUE);
                dealWithStudent=student;
                dealWithStd=student.get_id();
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
	    menu.setHeaderTitle(""+editDate);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    int menuItemIndex = item.getItemId();
	    //displayToast("this is from onContextItemSelected"+menuItemIndex);
	   // itemId=item.getItemId();
	    switch (menuItemIndex) {
	        case R.id.menu_edit:

					menuEdit();
	        	///////
	            return true;
	////////////////////////////////////////////////////////////
	        case R.id.menu_delete:
	        	
	        		menuDelete();

	            return true;
	        default:
	        	
	            return super.onContextItemSelected(item);
	    }
	        
	}
	
	//////////////////
	private void menuDelete() {
		// TODO Auto-generated method stub
		AlertDialog.Builder delbuilder = new AlertDialog.Builder(this);
        delbuilder.setMessage("Are you sure to delete?")
               .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                	   myDb.deleteRows(DBAdapter.ATTENDANCE_DAY_TABLE, 
                			   			DBAdapter.ALL_ATTENDANCE_DAY_KEYS,
                			   			DBAdapter.KEY_ROWID, editId);
                	   String[] attdRow=new String[] {DBAdapter.KEY_ROWID};
                	
                	   myDb.deleteRows(DBAdapter.ATTENDANCE_TABLE,
                			   			DBAdapter.ALL_ATTENDANCE_KEYS, 
                			   			DBAdapter.KEY_DAY_ID, 
                			   			editId);
                	 
                	  displayToast("Attendance Successfully deleted.");
                	   onResume();
                   }
               })
               .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        delbuilder.show();
        // Create the AlertDialog object and return it
	}

	private void menuEdit() {
		LayoutInflater li = getLayoutInflater();
		View v = li.inflate(R.layout.edit_attd, null);
		//View v=getLayoutInflater().inflate(R.layout.editalert, null, false);
		int year,months,days,hour,min,sec;
		
		year= CustomDate.GetYear(editDate);
		months= CustomDate.GetMonth(editDate);
		days=CustomDate.GetDay(editDate);	
		hour=CustomDate.GetHour(editDate);	
		min=CustomDate.GetMinute(editDate);
		sec=CustomDate.GetSecond(editDate);
		
		//displayToast(""+year+" month "+months+"  "+ days+"   ");
		
		final AlertDialog.Builder builder = new AlertDialog.Builder( this );
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
//		String finaldate=year+"-"+months+"-"+days+" "+hour+":"+min+"";
		
		builder.setView(v);
		builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
				int year=Integer.parseInt(etyear.getText().toString());
				int months=Integer.parseInt(etmonth.getText().toString());
				int days=Integer.parseInt(etday.getText().toString());
				int hour=Integer.parseInt(ethour.getText().toString());
				int minute=Integer.parseInt(etmin.getText().toString());
				int second=Integer.parseInt(etsec.getText().toString());
				//check the date using any regular expression
				long finaldate=CustomDate.CreateDateInLong(year, months, days, hour, minute, second);
				ContentValues newValues = new ContentValues();
					newValues.put(DBAdapter.KEY_CLS_ID, clsIds);
					newValues.put(DBAdapter.KEY_DATE, finaldate);
					if(myDb.updateRow(DBAdapter.ATTENDANCE_DAY_TABLE,
							newValues, editId)){
						displayToast("saved Successfully!!");
					}
					onResume();
			}

			});			    
	    builder.show();
		
	}
	//////////////////////
	
	
	private void registerListClickCallback() {
		ListView myList = (ListView) findViewById(R.id.lvAttdDays);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, 
					int position, long idInDB) {
				long id=attdIds.get(position);
				displayToast("u clicked on :"+position+ " and id: "+String.valueOf(id));
				
				//displayToastForId(idInDB);
				Intent intent=new Intent(ShowAllAttendanceDates.this,ShowSingleDayAttendance.class);
				intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
				
				intent.putExtra(Constants.EXTRA_MESSAGE_DAY_ID, id);
				startActivity(intent);  //this is the id of the aatendance_day
				//search the attendance table for this id and get all students in this 
				//attendance day
			}
		});
	}


	
	private void updateItemForId(long idInDB) {
		Cursor cursor = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, idInDB);
		if (cursor.moveToFirst()) {
			long idDB = cursor.getLong(DBAdapter.COL_ROWID);
			
			int batch = cursor.getInt(DBAdapter.COL_BATCH);
			String program = cursor.getString(DBAdapter.COL_PROGRAM);
			String course = cursor.getString(DBAdapter.COL_COURSE);
			
			//Classes classes = new Classes(idInDB,batch,program,course);
			//updateClass(classes);
			
		}
		cursor.close();
		populateListViewFromDB();		
	}
	
	private void updateClass(Classes classes) {
		// TODO Auto-generated method stub
		long idDB = classes.get_id();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DBAdapter.KEY_BATCH, classes.getBatch());
		initialValues.put(DBAdapter.KEY_PROGRAM, classes.getProgram());
		initialValues.put(DBAdapter.KEY_COURSE, classes.getCourse());
		
		myDb.updateRow(DBAdapter.ATTENDANCE_DAY_TABLE,initialValues,idDB);
	}
	private void displayToastForId(long idInDB) {
		Cursor cursor = myDb.getRow(DBAdapter.ATTENDANCE_DAY_TABLE, DBAdapter.ALL_ATTENDANCE_DAY_KEYS, idInDB);
		if (cursor.moveToFirst()) {
			long idDB = cursor.getLong(DBAdapter.COL_ROWID);
			long idate = cursor.getLong(DBAdapter.COL_DATE);
			long cls = cursor.getLong(DBAdapter.COL_CLS_ID);
			
			
			String message = "ID: " + idDB + "\n" 
					+ "DATE ID: " + idate + "\n"
					+ "course ID: " + cls;
			Toast.makeText(ShowAllAttendanceDates.this, message, Toast.LENGTH_LONG).show();
		}
		cursor.close();
	}

	private void displayToast(String message) {
		Toast.makeText(ShowAllAttendanceDates.this, message,
              Toast.LENGTH_LONG).show();
  }*/
}










