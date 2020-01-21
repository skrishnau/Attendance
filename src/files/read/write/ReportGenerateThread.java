package files.read.write;

import helpers.Constants;
import helpers.CustomDate;
import helpers.DBAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.activities.ViewStudents;

import entities.EachDay;
import entities.StudentPresence;

public class ReportGenerateThread extends AsyncTask<String,String,String>{
	
//	String f;
	ProgressDialog dialog;
	
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
	
	int dialogPercentCount = 0;
	
	public ReportGenerateThread(Context context, long classId, String fileName) {
		super();
		this.context = context;
		this.classId = classId;
		this.fileName = fileName;
		this.folderName = Constants.REPORTS_DIRECTORY;
		
		File file = CreateFile(folderName, fileName);
		try {
			this.writer = new FileWriter(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		openDB();
	}
	
	//============================== ASYNC TASK ======================================//
	//========================= OVERRIDDEN FUNCTIONS ==================================//

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMax(100);
		dialog.show();
		// Toast.makeText(ViewStudents.this,
		// "Wait a second.\nThis may take upto 12 seconds depending on attendance data",
		// Toast.LENGTH_LONG);
		// super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... fileName) {
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
			//1 line
			
		}
		classCursor.close();
		writeLine();
		//
		//month
		Cursor attendanceDayCursor = myDb.getRows(DBAdapter.ATTENDANCE_DAY_TABLE,
						new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_DATE},
						DBAdapter.KEY_CLS_ID, classId);
		writeLine("Total "+attendanceDayCursor.getCount()+" class hours held.");
		writeLine("Report Generated on "+ CustomDate.GetCurrentDateTimeInString());
		writeLine();
		write();
		write();
		//1 line
//		 int attendanceCount =  attendanceDayCursor.getCount();
		 
		//3 line
		if (attendanceDayCursor.moveToFirst()) {
//			int attdCountForIncrement = 0;
			int previousMonth = -1;
			do {

				long milliSeconds = attendanceDayCursor.getLong(1);
				int thisMonth = CustomDate.GetMonth(milliSeconds);
//				attdCountForIncrement++;
				if (previousMonth != thisMonth) {
					write(monthArray[thisMonth]);
					previousMonth = thisMonth;
					
				} else {
					write();
				}
//				if(attdCountForIncrement==attendanceCount/2){
//					publishProgress(1);
			
			} while (attendanceDayCursor.moveToNext());
		} 
		write("Total, % ,");
		
		//next month
		writeLine();
		//crn name and date header
		write("CRN,Name");
//		publishProgress(1);
		if (attendanceDayCursor.moveToFirst()) {
			int attdCountForIncrement = 0;
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
//				if(attdCountForIncrement==attendanceCount/2){
//					publishProgress(1);
//				}
			} while (attendanceDayCursor.moveToNext());
		}
		writeLine();
		//3 line
		
		
		//student crn and name
		//and their presence
		Cursor studentCursor = myDb.getAscStd(classId);
		
		int studentCount= studentCursor.getCount();
		//
		if(studentCursor.moveToFirst()){
//			int incrementerValue = 1;
//			int stdCountForIncrement = 1;
			dialogPercentCount = 100- dialogPercentCount;
			float ratio = (float)(1.0* dialogPercentCount)/studentCount;
//			int tens =0;
//			int ones = 0;
			float ratioCounter = (float) 0.0;
			do {
				ratioCounter += ratio;
				
				long stdId = studentCursor.getLong(0);
				String scrn=studentCursor.getString(DBAdapter.COL_STD_CRN);
				String sname=studentCursor.getString(DBAdapter.COL_STD_NAME);
				write(scrn+","+sname);
				publishProgress(String.valueOf((int) ratioCounter),scrn,sname);
				int totalPresentDaysForAStudent = 0;
				if(attendanceDayCursor.moveToFirst()){
					int attdCountForIncrement = 0;
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
//						if(attdCountForIncrement==attendanceCount/2){
//							publishProgress(1);
//						}
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
				ratioCounter-=((int)ratioCounter);
//				publishProgress(tens,ones);
			}while(studentCursor.moveToNext());
			writeLine();
		}//student cursor loop
		studentCursor.close();
		attendanceDayCursor.close();
		closeDB();
		closeWriter();
		dialog.dismiss();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		
//		clickTimes_genReportBt = 0;
		
		 Intent intent = new Intent(context,
	                ViewStudents.class);
			intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, classId);
		 context.startActivity(intent);
//		 dialog.dismiss();
		 Toast.makeText(context,
				 "Report is generated in\n\nsdcard/"+folderName+"/"+fileName, Toast.LENGTH_LONG).show();
		 ((Activity) context).finish();
//		 ActivityStarter starter = new ActivityStarter(context,classId);
		
	        
		// super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		/*if (values.length > 1)
			if (values[1] > 5) {
				values[0]++;
			}*/
		
		dialog.incrementProgressBy(Integer.parseInt(values[0]));
		dialog.incrementSecondaryProgressBy(1);
		if (values.length >= 2) {
			dialog.setMessage(values[1] + " " + values[2]);
			dialog.setTitle(values[1] + " " + values[2]);
		}
		// super.onProgressUpdate(values);
	}

	public class ActivityStarter extends Activity{
		public ActivityStarter(Context context, long classId){
			Intent intent = new Intent(context,
	                ViewStudents.class);
			intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, classId);
			startActivity(intent);
		}

}
	
	
//===========================================================================//
//===========================================================================//
//========================= OTHER FUNCTIONS ==================================//

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
//		publishProgress(1);
		append("\n");
	}
	
	public void writeLine(String s){
//		publishProgress(1);
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
	//============================== FUNCTIONS END ==============================//
	//===========================================================================//
	//===========================================================================//
	
}
