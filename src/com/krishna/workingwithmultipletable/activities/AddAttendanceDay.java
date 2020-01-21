package com.krishna.workingwithmultipletable.activities;





/**
 * @Uses not used
 * @see http
 */
public class AddAttendanceDay {
/*
	DBAdapter myDb;
	String clsId,program,batch,course;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_all_attendance_dates);
		
		openDB();
		populateListViewFromDB();
		registerListClickCallback();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		closeDB();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		populateListViewFromDB();
	}
	
	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}
	private void closeDB() {
		myDb.close();
	}

	 
	 * UI Button Callbacks
	 
	public void onClick_AddRecord(View v) {
		
        Intent intent = new Intent(AddAttendanceDay.this,
                AttendanceSheet.class);

         Bundle bundle = new Bundle();
        bundle.putString("_id", clsId);
        bundle.putString("batch", batch);
        bundle.putString("program", program);
        bundle.putString("course", course);
        intent.putExtras(bundle);
        startActivity(intent);
		displayToastForId(idInDB);
		displayToastForId(idInDB1);
		//populateListViewFromDB();
	}

	
	public void onClick_ClearAll(View v) {
		myDb.deleteAll();
		populateListViewFromDB();
	}


	private void populateListViewFromDB() {
		//SimpleCursorAdapter mAdapter;
		Cursor cursor = myDb.getAllRows(DBAdapter.ATTENDANCE_DAY_TABLE, DBAdapter.ALL_ATTENDANCE_DAY_KEYS);
		
		// Allow activity to manage lifetime of the cursor.
		// DEPRECATED! Runs on the UI thread, OK for small/short queries.
		startManagingCursor(cursor);
		
		 // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

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

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
       
        ListView myList = (ListView) findViewById(R.id.lvAttdDays);
        myList.setAdapter(myCursorAdapter);
    }

	
	private void registerListClickCallback() {
		ListView myList = (ListView) findViewById(R.id.lvClass);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, 
					int position, long idInDB) {

				updateItemForId(idInDB);
				displayToastForId(idInDB);
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
			
			Classes classes = new Classes(idInDB,batch,program,course);
			updateClass(classes);
			
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
		
		myDb.updateRow(DBAdapter.CLASS_TABLE,initialValues,idDB);
	}
	private void displayToastForId(long idInDB) {
		Cursor cursor = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, idInDB);
		if (cursor.moveToFirst()) {
			long idDB = cursor.getLong(DBAdapter.COL_ROWID);
			int batch = cursor.getInt(DBAdapter.COL_BATCH);
			String program = cursor.getString(DBAdapter.COL_PROGRAM);
			String course = cursor.getString(DBAdapter.COL_COURSE);
			
			String message = "ID: " + idDB + "\n" 
					+ "Batch: " + batch + "\n"
					+ "program: " + program + "\n"
					+ "course: " + course;
			Toast.makeText(AddAttendanceDay.this, message, Toast.LENGTH_LONG).show();
		}
		cursor.close();
	}
*/
}










