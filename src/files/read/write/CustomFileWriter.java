package files.read.write;

import helpers.CustomDate;
import helpers.DBAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import entities.EachDay;
import entities.StudentPresence;

public class CustomFileWriter {
	
	DBAdapter myDb;
	Context context;
	long classId;
	long dayId;
	int TotalPresentDays =0;
	String folderName, fileName;
	FileWriter writer;
	List<EachDay> eachDayList= new ArrayList<EachDay>();
	List<StudentPresence> spList = new ArrayList<StudentPresence>();
	
	String[]  monthArray = new String[]{
		"Jan","Feb","Mar","Apr","May","June","July"	,"Aug","Sep","Oct","Nov","Dec"
	};
	
	public CustomFileWriter(Context context, long classId, String fileName) {
		super();
		this.context = context;
		this.classId = classId;
		this.fileName = fileName;
		this.folderName = "ARS";
		
		File file = CreateFile(folderName, fileName);
		try {
			this.writer = new FileWriter(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		openDB();
	}
	
	private void openDB() {
		myDb = new DBAdapter(context);
		myDb.open();
	}

	private void closeDB() {
		myDb.close();
	}

	private void append(String s){
		try {
			writer.append(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(){
		append(",");
	}
	
	public void write(String s) {
		append(s+",");
	}

	public void writeLine(){
		append("\n");
	}
	
	public void writeLine(String s){
		append(s+"\n");
		
	}
	
	public void closeWriter(){
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private File CreateFile(String folderName, String fileName) {
		File root = Environment.getExternalStoragePublicDirectory("");
		File temp = new File(root, folderName);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		File file = new File(temp, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	
	public void GenerateReport(){
		//class Record; batch program subject
//		writeLine("inside Generate Report");
		writeLine("Attendance Report");
		Cursor classCursor = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
				classId);
		if (classCursor.moveToFirst()) {
			int batch = classCursor.getInt(DBAdapter.COL_BATCH);
			String program = classCursor.getString(DBAdapter.COL_PROGRAM);
			String course = classCursor.getString(DBAdapter.COL_COURSE);
			// tvprogram.setText(program);
			String heading = "Batch," + String.valueOf(batch) + 
							"\nProgram," + program + ",\nSubject, " + course+",";
			writeLine(heading);
			
			
		}
		classCursor.close();
		writeLine();
		
		//month
		Cursor attendanceDayCursor = myDb.getRows(DBAdapter.ATTENDANCE_DAY_TABLE,
						new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_DATE},
						DBAdapter.KEY_CLS_ID, classId);
		writeLine("Total "+attendanceDayCursor.getCount()+" class hours held.");
		writeLine("Report Generated on "+ CustomDate.GetCurrentDateTimeInString());
		writeLine();
		write();
		write();
		if(attendanceDayCursor.moveToFirst()){
			int previousMonth=-1;
			do{
				
			long milliSeconds = attendanceDayCursor.getLong(1);
			int thisMonth = CustomDate.GetMonth(milliSeconds);
			
			if(previousMonth!=thisMonth){
				write(monthArray[thisMonth]);
				previousMonth=thisMonth;
			}else{
				write();
			}
			}while(attendanceDayCursor.moveToNext());
		} 
		write("Total, % ,");
		
		//next month
		writeLine();
		//crn name and date header
		write("CRN,Name");
		
		if (attendanceDayCursor.moveToFirst()) {
			do {
				long milliSeconds = attendanceDayCursor.getLong(1);
				int day = CustomDate.GetDay(milliSeconds);
				if(day==1 || day%10==1){
					write(String.valueOf(day)+" st");
				}else if(day==2 || day%10 == 2){
					write(String.valueOf(day)+" nd");
				}else if(day==3 || day%10 == 3){
					write(String.valueOf(day)+" rd");
				}else{
					write(String.valueOf(day)+" th");
				}
				
			} while (attendanceDayCursor.moveToNext());
		}
		writeLine();
		
		//student crn and name
		//and their presence
		Cursor studentCursor = myDb.getAscStd(classId);
		if(studentCursor.moveToFirst()){
			do{
				long stdId = studentCursor.getLong(0);
				String scrn=studentCursor.getString(DBAdapter.COL_STD_CRN);
				String sname=studentCursor.getString(DBAdapter.COL_STD_NAME);
				write(scrn+","+sname);
				
				int totalPresentDaysForAStudent = 0;
				if(attendanceDayCursor.moveToFirst()){
					do{
						//here we deal with each day of the class
						long attdDayId = attendanceDayCursor.getLong(0);
						long date = attendanceDayCursor.getLong(1);
						
						String[] rowsToSelect = new String[]{DBAdapter.KEY_ROWID,DBAdapter.KEY_PRESENCE};
						Cursor attdCursor= myDb.getRows(DBAdapter.ATTENDANCE_TABLE, 
								rowsToSelect, 
								DBAdapter.KEY_STD_ID,
								stdId ,
								DBAdapter.KEY_DAY_ID,
								attdDayId
								);
						if(attdCursor.moveToFirst()){
							if(attdCursor.getString(1).equalsIgnoreCase("P")){
								write("P");
								totalPresentDaysForAStudent++;
							}else{
								write("A");
							}
						}else{
							write("A");
						}
						attdCursor.close();
					}while(attendanceDayCursor.moveToNext());
					write(String.valueOf(totalPresentDaysForAStudent));
					float presentPercent = (float)1.0*totalPresentDaysForAStudent/attendanceDayCursor.getCount()*100;
					write(String.format("%.02f", presentPercent)+" %");
//					write(String.format(%0.2f, args)((float)((1.0*totalPresentDaysForAStudent)/(attendanceDayCursor.getCount())*100)));
				}
//					write(String.valueOf(GetTotalPresentDaysForAStudent(stdId)));
				TotalPresentDays=0;
				writeLine();
			}while(studentCursor.moveToNext());
			writeLine();
		}//student cursor loop
		studentCursor.close();
		attendanceDayCursor.close();
		closeDB();
		closeWriter();
	}
	
	
	private void GetStudentsList(long classId2) {
		Cursor studentCursor = myDb.getAscStd(classId);
		
	}

	private void populateListView(){
		
		  //Array list of countries
		eachDayList.clear();
		//gives list of students in ascending order of crn for given classId
		Cursor studentCursor = myDb.getAscStd(classId);
		   
		  if(studentCursor.moveToFirst()){
			  	do{
				 		long id = studentCursor.getLong(0);
						StudentPresence studentpresence=new StudentPresence();
						EachDay eachDay=new EachDay();
						
						
							String scrn=studentCursor.getString(DBAdapter.COL_STD_CRN);
							String sname=studentCursor.getString(DBAdapter.COL_STD_NAME);

							studentpresence.setCrn(scrn);
							studentpresence.setName(sname);
							studentpresence.set_id(id);
							////////////
							if(classId>=0 && dayId<0){
								studentpresence.setSelected(false);
						   }
						   if(dayId>=0 && classId>=0){
							   boolean present=IsStudentPresentOnTheDay(id, dayId);
							   studentpresence.setSelected(present);
						   }
							//////////////
							
							
							eachDay.set_id(id);
							eachDay.setPresence(GetTotalPresentDaysForAStudent(id));
						
						eachDayList.add(eachDay);
						spList.add(studentpresence);
						
			}while(studentCursor.moveToNext());
			  studentCursor.close();
			 
		  }
		
	}
	
	private boolean IsStudentPresentOnTheDay(long stdId, long dayId) {
		String[] presenceRow=new String[] {DBAdapter.KEY_DAY_ID,DBAdapter.KEY_PRESENCE};
		boolean presence=false;
		Cursor c=myDb.getRows(DBAdapter.ATTENDANCE_TABLE, presenceRow,
								DBAdapter.KEY_STD_ID, stdId
								);
		if(c.moveToFirst()){
			do{
				if(c.getLong(0)==dayId){
					if(c.getString(1).equals("P")){
						presence=true;
						TotalPresentDays++;
					}
				}
			}while(c.moveToNext());
		}
		c.close();
		return presence;
	}
	
	private int GetTotalPresentDaysForAStudent(long stdId){
		int attddays=0;
	Cursor attdCursor= myDb.getRows(DBAdapter.ATTENDANCE_TABLE, 
				DBAdapter.ALL_ATTENDANCE_KEYS, 
				DBAdapter.KEY_STD_ID,
				stdId 
				);
	if(attdCursor.moveToFirst()){
		do{
			if(attdCursor.getString(DBAdapter.COL_PRESENCE).equals("P")){
				attddays+=1;
			}
		}while(attdCursor.moveToNext());
		
	}
	attdCursor.close();
	return attddays;
	}
	
	/**
	 * @description Returns (classid and date ) of attendance of given clsId
	 * @param clsId
	 */
	private void GetAttendanceDaysForAClass(long clsId) {
		
		Cursor cursor = myDb
				.getRows(DBAdapter.ATTENDANCE_DAY_TABLE,
						DBAdapter.ALL_ATTENDANCE_DAY_KEYS,
						DBAdapter.KEY_CLS_ID, clsId);
		if (cursor.moveToFirst()) {
			do {
				long id = cursor.getLong(0);
//				attdIds.add(id);
			} while (cursor.moveToNext());
		}
		cursor.moveToFirst();
	}
	
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}


}
