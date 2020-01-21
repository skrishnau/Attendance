package helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.os.Environment;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.activities.ViewStudents;

public class Constants {

	public static final String[] MONTH_IN_STRING =  new String[]{"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
	public static final String[] PROGRAMS_ARRAY = new String[]{"Architecture", 
		"Civil", "Civil n Rural", "Computer", "Electrical", "Electrical n Electronics", "Electronics"};
	public static final String[] BATCH_ARRAY = new String[]{};
	
	public static List<String> BatchArray (){
		GregorianCalendar gc = new GregorianCalendar();
		int year = gc.get(Calendar.YEAR);
		List<String> BatchArray = new ArrayList<String>();
		for (int x = year - 7; x < year + 3; x++) {
			BatchArray.add(String.valueOf(x));
		}
		return BatchArray;
	}
	
	public static int themeId;
	public static final String[] THEMES = new String[]{"default","black","blue","green","orange","pink"};
	public static  String[] OPTIONS_FOR_STUDENT_LIST = new String[]{
		"View day-wise",
		"Generate Report", 
		"Export Students info to file"};
	public static String[] OPTIONS_FOR_GETTING_IMAGE = new String[]{
		"Import From Gallery",
		"Capture From Camera",
		"Don't Save Image"
	};
	
	public static String STUDENTS_IMAGE_DIRECTORY = "ARS/StudentsInfo/Images";
	public static String STUDENTS_LIST_DIRECTORY = "ARS/StudentsInfo/List";
	public static String REPORTS_DIRECTORY = "ARS/Reports";
	
	
	public static final int STUDENT_WISE = 1;
	public static final int DAY_WISE =2;
	
	public static int IMAGE_OPTION 	= -1;
	
	public static boolean canGetPathFromCamera = true;
	public static boolean canGetPathFromGallery = true;
	
	
	public static String EXTRA_MESSAGE_CLASS_ID="com.krishna.workingwithmultipletable.classIds";
	public static String EXTRA_MESSAGE_DAY_ID="com.krishna.workingwithmultipletable.dayIds";
	public static String EXTRA_MESSAGE_STUDENT_ID="com.krishna.workingwithmultipletable.studentIds";
	public static String EXTRA_MESSAGE_FROM_FIRST_SCREEN="com.krishna.workingwithmultipletable.valueFromFirstScreen";
	
	
	public static final File ROOT_DIRECTORY = Environment.getExternalStoragePublicDirectory("");
	
	public static boolean isExternalPresent(){
		String env = Environment.getExternalStorageState();
		if (env.equals(Environment.MEDIA_UNMOUNTED) ||
				env.equals(Environment.MEDIA_REMOVED)
				) {
			return false;
		}else if(env.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
			return false;
		}else 
			return true;
	}
	
	public static String ImageFileName(String name, String crn){
		String[] fullName = name.split(" ");
		String[] rolls = crn.split("-");
		return fullName[0].toLowerCase() + "" + rolls[0]
				+ "" + rolls[1] + ".jpg";
		
	}
	
	public void SetTheme(){
		
	}
}
