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

public class StudentInfoExportThread extends AsyncTask<String,String,String>{
	
//	String f;
	ProgressDialog dialog;
	
	DBAdapter myDb;
	Context context;
	long classId;
	long dayId;
	int TotalPresentDays =0;
	String fileName;
	String studentsListFolder;
	String studentsImageFolder;
	FileWriter writer;
	List<EachDay> eachDayList= new ArrayList<EachDay>();
	List<StudentPresence> spList = new ArrayList<StudentPresence>();
	
	int dialogPercentCount = 0;
	
	public StudentInfoExportThread(Context context, long classId, String fileName) {
		super();
		this.context = context;
		this.classId = classId;
		this.fileName = fileName;
		this.studentsListFolder = Constants.STUDENTS_LIST_DIRECTORY;
		this.studentsImageFolder = Constants.STUDENTS_IMAGE_DIRECTORY;
		
		File file = CreateFile(studentsListFolder, fileName);
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
		
		// super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... fileName) {
		
		Cursor classCursor = myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS,
				classId);
		if (classCursor.moveToFirst()) {
			int batch = classCursor.getInt(DBAdapter.COL_BATCH);
			String program = classCursor.getString(DBAdapter.COL_PROGRAM);
			String course = classCursor.getString(DBAdapter.COL_COURSE);
			String heading = "Batch," + String.valueOf(batch) + 
							"\nProgram," + program + ",\nSubject, " + course+",";
			writeLine(heading);
		}
		classCursor.close();
		writeLine();
		write("CRN,Name");
		writeLine();
		writeLine();
		Cursor studentCursor = myDb.getAscStd(classId);
		int studentCount= studentCursor.getCount();
		//
		if(studentCursor.moveToFirst()){
			dialogPercentCount = 100- dialogPercentCount;
			float ratio = (float)(1.0* dialogPercentCount)/studentCount;

			float ratioCounter = (float) 0.0;
			do {
				ratioCounter += ratio;
				String scrn=studentCursor.getString(DBAdapter.COL_STD_CRN);
				String sname=studentCursor.getString(DBAdapter.COL_STD_NAME);
				write(scrn+","+sname);
				publishProgress(String.valueOf((int) ratioCounter),scrn,sname);
				writeLine();
				ratioCounter-=((int)ratioCounter);
			}while(studentCursor.moveToNext());
			writeLine();
		}//student cursor loop
		
		studentCursor.close();
		closeDB();
		closeWriter();
		dialog.dismiss();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		
		 Intent intent = new Intent(context,
	                ViewStudents.class);
			intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, classId);
		 context.startActivity(intent);
//		 dialog.dismiss();
		 Toast.makeText(context,
				 "Info is exported to:\n\nsdcard/"+studentsListFolder+"/"+fileName, Toast.LENGTH_LONG).show();
		 ((Activity) context).finish();
     
		// super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(String... values) {
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
	
	private File CreateFile(String studentsListFolder, String fileName) {
		File root = Constants.ROOT_DIRECTORY;
		File temp = new File(root, studentsListFolder);
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
	//============================== FUNCTIONS END ==============================//
	//===========================================================================//
	//===========================================================================//
	
}
