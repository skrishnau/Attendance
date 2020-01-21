// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import entities.AttendanceDay;
import entities.Classes;
import entities.Student;


public class DBAdapter {

	private static final String TAG = "DBAdapter";
	
	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	/*
	 * CHANGE 1:
	 */
	// TODO: Setup your fields here:
	public static final String KEY_NAME = "name";
	public static final String KEY_STUDENTNUM = "studentnum";
	public static final String KEY_FAVCOLOUR = "favcolour";
	
	//CLASS TABLE ROW
	public static final String KEY_BATCH = "batch";
	public static final String KEY_PROGRAM = "program";
	public static final String KEY_COURSE = "course";
	
	//STUDENT TABLE ROWS
	public static final String KEY_CRN="crn";
	public static final String KEY_IMAGE ="image"; //----------------------//
	
	//CLASS-STUDENT TABLEL ROWS
	public static final String KEY_CLS_ID="classId";
	public static final String KEY_STD_ID="stdId";
	
	//ATTENDANCE TABEL ROWS
	public static final String KEY_DAY_ID="dayId";
	public static final String KEY_PRESENCE="presence";
	
	//ATTENDANCE_DAY TABLE TOWS
	public static final String KEY_DATE="dates";
	//public static final String KEY_CLS_ID="classId";
	
	
	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	public static final int COL_NAME = 1;
	public static final int COL_STUDENTNUM = 2;
	public static final int COL_FAVCOLOUR = 3;
	
	//class
	public static final int COL_BATCH = 1;
	public static final int COL_PROGRAM = 2;
	public static final int COL_COURSE = 3;
	//student
	public static final int COL_STD_CRN=1;
	public static final int COL_STD_NAME=2;
	public static final int COL_STD_IMAGE = 3;//----------------------------//
	//attendance_day
	public static final int COL_CLS_ID=1;
	public static final int COL_DATE=2;
	//attendance
	public static final int COL_DAY_ID=1;
	public static final	int COL_STD_ID=2;
	public static final int COL_PRESENCE=3;
	
	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_NAME, KEY_STUDENTNUM, KEY_FAVCOLOUR};
	
	public static final String[] ALL_CLASS_KEYS = 
			new String[] {KEY_ROWID, KEY_BATCH, KEY_PROGRAM, KEY_COURSE};
	public static final String[] ALL_STUDENT_KEYS = 
			new String[] {KEY_ROWID, KEY_CRN, KEY_NAME, KEY_IMAGE};
	//---------------------------//
	public static final String[] ALL_CLASS_STUDENT_KEYS =
			new String[] {KEY_ROWID,KEY_CLS_ID,KEY_STD_ID};
	public static final String[] ALL_ATTENDANCE_KEYS =
			new String[] {KEY_ROWID,KEY_DAY_ID,KEY_STD_ID,KEY_PRESENCE};
	public static final String[] ALL_ATTENDANCE_DAY_KEYS =
			new String[] {KEY_ROWID,KEY_CLS_ID,KEY_DATE};
	
	
	
	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "MyDb";
	public static final String DATABASE_TABLE = "mainTable";
	
	public static final String CLASS_TABLE = "classTable";
	public static final String STUDENT_TABLE = "studentTable";
	public static final String CLASS_STUDENT_TABLE="classStudentTable";
	public static final String ATTENDANCE_TABLE="attendanceTable";
	public static final String ATTENDANCE_DAY_TABLE="attendanceDayTable";
	
	
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 34;	

	
/*	private static final String DATABASE_CREATE_SQL = 
			"create table " + DATABASE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			
			
			 * CHANGE 2:
			 
			// TODO: Place your fields here!
			// + KEY_{...} + " {type} not null"
			//	- Key is the column name you created above.
			//	- {type} is one of: text, integer, real, blob
			//		(http://www.sqlite.org/datatype3.html)
			//  - "not null" means it is a required field (must be given a value).
			// NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
			+ KEY_NAME + " text not null, "
			+ KEY_STUDENTNUM + " integer not null, "
			+ KEY_FAVCOLOUR + " string not null"
			
			// Rest  of creation:
			+ ");";*/
	
	private static final String CREATE_CLASS_TABLE = 
			"create table " + CLASS_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_BATCH + " integer not null, "
			+ KEY_PROGRAM + " text not null, "
			+ KEY_COURSE + " text not null "
			+ ");";
	private static final String CREATE_STUDENT_TABLE = 
			"create table " + STUDENT_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_CRN + " text not null, "
			+ KEY_NAME + " text not null, "
			+ KEY_IMAGE + " text"
			+ ");";
	private static final String CREATE_CLASS_STUDENT_TABLE = 
			"create table " + CLASS_STUDENT_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_CLS_ID + " integer not null, "
			+ KEY_STD_ID + " integer not null "			
			+ ");";
	private static final String CREATE_ATTENDANCE_TABLE = 
			"create table " + ATTENDANCE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_DAY_ID + " integer not null, "
			+ KEY_STD_ID + " integer not null, "	
			+ KEY_PRESENCE + " text  "
			+ ");";
	private static final String CREATE_ATTENDANCE_DAY_TABLE = 
			"create table " + ATTENDANCE_DAY_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_CLS_ID + " integer not null, "
			+ KEY_DATE + " integer not null "
			+ ");";

	
	
	
	// Context of application who uses us.
	private final Context context;
	
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	
	// Add a new set of values to the database.
	public long insertRow(String TABLE_NAME, ContentValues initialValues) {
		return db.insert(TABLE_NAME, null, initialValues);
	}
	
	//Add class values
	public long insertClass(Classes classes) {
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_BATCH, classes.getBatch());
		initialValues.put(KEY_PROGRAM, classes.getProgram());
		initialValues.put(KEY_COURSE, classes.getCourse());
		
		// Insert it into the database.
		return db.insert(CLASS_TABLE, null, initialValues);
	}
	//Add student values
	public long insertStudent(Student student){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CRN, student.getCrn());
		initialValues.put(KEY_PROGRAM, student.getName());
				
		return db.insert(STUDENT_TABLE, null, initialValues);
	}
	
	public long insertClassStudent(long cls_id,long std_id) {
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CLS_ID, cls_id);
		initialValues.put(KEY_STD_ID, std_id);
		
		return db.insert(CLASS_STUDENT_TABLE, null, initialValues);
	}
	public long insertAttendanceDay(AttendanceDay aDay) {
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CLS_ID,aDay.getClsId() );
		initialValues.put(KEY_DATE,aDay.getDaysS() );
	
		return db.insert(ATTENDANCE_DAY_TABLE, null, initialValues);
	}
	public long insertAttendance(long day_id,long std_id) {
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DAY_ID, day_id);
		initialValues.put(KEY_STD_ID, std_id);
		
		
		return db.insert(ATTENDANCE_TABLE, null, initialValues);
	}
	// end of insert functions
	
	
	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(String TABLE_NAME ,  long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(TABLE_NAME, where, null) != 0;
		
	}
	
	public void deleteAll(String TABLE_NAME,String[] ALL_TABLE_KEYS) {
		Cursor c = getAllRows( TABLE_NAME, ALL_TABLE_KEYS);
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(TABLE_NAME,c.getLong((int) rowId));				
			} while (c.moveToNext());
		}
		c.close();
	}
	public void deleteRows(String TABLE_NAME,
							String[] ALL_TABLE_KEYS,
							String WHERE_COLUMN_NAME,
							long WHERE_COLUMN_VALUE) {
		//Cursor c = getAllRows( TABLE_NAME, ALL_TABLE_KEYS);
		Cursor cursor = getRows(TABLE_NAME, ALL_TABLE_KEYS, 
								WHERE_COLUMN_NAME, WHERE_COLUMN_VALUE);
		long rowId = cursor.getColumnIndexOrThrow(KEY_ROWID);
		if (cursor.moveToFirst()) {
			do {
				deleteRow(TABLE_NAME,cursor.getLong((int) rowId));				
			} while (cursor.moveToNext());
		}
		cursor.close();
	}
	
	// Return all data in the database.
	public Cursor getAllRows(String TABLE_NAME,String[] ALL_TABLE_KEYS) {
		String where = null;
		Cursor c = 	db.query(true, TABLE_NAME, ALL_TABLE_KEYS, 
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	// Get a specific row (by rowId)

	public Cursor getRow(String TABLE_NAME,String[] ALL_TABLE_KEYS,long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	public Cursor getRows(String TABLE_NAME,
						  String[] ALL_TABLE_KEYS,
						  String WHERE_COLUMN_NAME,
						  long WHERE_COLUMN_VALUE) {
		String where = WHERE_COLUMN_NAME + "=" + WHERE_COLUMN_VALUE;
		Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	public Cursor getRows(String TABLE_NAME,
			  String[] ALL_TABLE_KEYS,
			  String WHERE_COLUMN_NAME,
			  long WHERE_COLUMN_VALUE,
			  String WHERE_COLUMN_NAME_2,
			  String WHERE_COLUMN_VALUE_2) {
			String where = "("+WHERE_COLUMN_NAME + " = " + WHERE_COLUMN_VALUE+
								" AND " +
						    WHERE_COLUMN_NAME_2+" = "+WHERE_COLUMN_VALUE_2+")";
			Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
					where, null, null, null, null, null);
				if (c != null) {
					c.moveToFirst();
				}
				return c;
		}
	
	public Cursor getRows(String TABLE_NAME,
			  String[] ALL_TABLE_KEYS,
			  String WHERE_COLUMN_NAME,
			  long WHERE_COLUMN_VALUE,
			  String WHERE_COLUMN_NAME_2,
			  long WHERE_COLUMN_VALUE_2) {
			String where = "("+WHERE_COLUMN_NAME + " = " + WHERE_COLUMN_VALUE+
								" AND " +
						    WHERE_COLUMN_NAME_2+" = "+WHERE_COLUMN_VALUE_2+")";
			Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
					where, null, null, null, null, null);
				if (c != null) {
					c.moveToFirst();
				}
				return c;
		}
	
	public Cursor getRowsDesc(String TABLE_NAME,
			  String[] ALL_TABLE_KEYS,
			  String WHERE_COLUMN_NAME,
			  long WHERE_COLUMN_VALUE) {
			String where = WHERE_COLUMN_NAME + "=" + WHERE_COLUMN_VALUE;
			String orderBy=KEY_ROWID+" "+"DESC";
				/*Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
									where, null, null, null, null, null);*/
				Cursor c=db.query(false, TABLE_NAME, ALL_TABLE_KEYS,
									where, null, null, null, orderBy, null);
				if (c != null) {
					c.moveToFirst();
				}
				return c;
}
	public Cursor getAscStd(long clsIds){
			String getStdInAsc= "SELECT * FROM " +STUDENT_TABLE  +
					" WHERE "+KEY_ROWID+" IN (SELECT "+KEY_STD_ID+
					" FROM "+CLASS_STUDENT_TABLE+" WHERE "+
					KEY_CLS_ID+"="+clsIds+" )" +
			" ORDER BY "+KEY_CRN+" ASC;";
			Cursor cursor=db.rawQuery(getStdInAsc,null);
			if(cursor!=null){
				cursor.moveToFirst();
			}
			return cursor;
	}
//SELECT * FROM COMPANY WHERE ID IN (SELECT ID FROM COMPANY WHERE SALARY > 45000) ;
	public Cursor getRowsOrderBy(String TABLE_NAME,
			  String[] ALL_TABLE_KEYS,
			  String WHERE_COLUMN_NAME,
			  long WHERE_COLUMN_VALUE,
			  String ORDER_BY_COLUMN_NAME,
			  String ORDER_TYPE) {
			String where = WHERE_COLUMN_NAME + "=" + WHERE_COLUMN_VALUE;
			String orderBy=ORDER_BY_COLUMN_NAME+" "+ORDER_TYPE;
				/*Cursor c = 	db.query(false, TABLE_NAME, ALL_TABLE_KEYS, 
									where, null, null, null, null, null);*/
				Cursor c=db.query(false, TABLE_NAME, ALL_TABLE_KEYS,
									where, null, null, null, orderBy, null);
				if (c != null) {
					c.moveToFirst();
				}
				return c;
}

	public Cursor getSpecificStudents(long clsIds){
	 String sql="SELECT * FROM "+DBAdapter.STUDENT_TABLE+" where (SELECT "
			   +DBAdapter.KEY_STD_ID +" FROM "+DBAdapter.CLASS_STUDENT_TABLE+" WHERE ("
			   +DBAdapter.CLASS_STUDENT_TABLE+"="+clsIds+"))";
	   
	    Cursor rawq= db.rawQuery (sql,null); 
	    return rawq;
	}
// Change an existing row to be equal to new data.
	public boolean updateRow(String TABLE_NAME, ContentValues newValues,long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		// Insert it into the database.
		return db.update(TABLE_NAME, newValues, where, null) != 0;
	}
	
	
	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			//_db.execSQL(DATABASE_CREATE_SQL);
			
			_db.execSQL(CREATE_CLASS_TABLE);
			_db.execSQL(CREATE_STUDENT_TABLE);
			_db.execSQL(CREATE_CLASS_STUDENT_TABLE);
			_db.execSQL(CREATE_ATTENDANCE_TABLE);
			_db.execSQL(CREATE_ATTENDANCE_DAY_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
			
			// Destroy old database:
			//_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			
			_db.execSQL("DROP TABLE IF EXISTS " + CLASS_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + STUDENT_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + CLASS_STUDENT_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_DAY_TABLE);
			
			// Recreate new database:
			onCreate(_db);
		}
	}
}
