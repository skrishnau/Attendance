package helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CustomDate {
	

	public static String GetCurrentDateTimeInString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm a", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static  long GetCurrentDateTimeInMilliSecond() {
		Date date = new Date();
		return date.getTime();
	}

	public static String GetFormattedDate(long milliseconds) {
		String s="";
		/*SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd E hh:mm a ", Locale.getDefault());*/
		Date date = new Date(milliseconds);
		///============================================////
		SimpleDateFormat dayWeekFormat = new SimpleDateFormat(" E ",Locale.getDefault());
		s+= dayWeekFormat.format(date)+"  ";
		
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM",Locale.getDefault());
		String temp = Constants.MONTH_IN_STRING[Integer.parseInt(monthFormat.format(date))];
		s+="  "+temp+" ";
		SimpleDateFormat dayFormat = new SimpleDateFormat(
				"dd", Locale.getDefault());
		s+=dayFormat.format(date)+", ";
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.getDefault());
		s+=yearFormat.format(date)+"  ";
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm  a ",Locale.getDefault());
		s+= " "+timeFormat.format(date);
		//=============================================//
//		return (dateFormat.format(date));
		return s;
	}

	public static int GetYear(long milliseconds){
		Date date = new Date(milliseconds);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc.get(Calendar.YEAR);
	}
	
	/*public static int GetMonth(long milliSeconds){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
		Date date = new Date(milliSeconds);
		return(Integer.parseInt(dateFormat.format(date)));
	}*/
	public static int GetMonth(long milliSeconds){
		Calendar c = new  GregorianCalendar();
		Date date = new Date(milliSeconds);
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	public static int GetDay(long milliSeconds) {
		Calendar c = new GregorianCalendar();
		Date date = new Date(milliSeconds);
		c.setTime(date);
		return c.get(Calendar.DATE);
		
	}
	public static int GetHour(long milliSeconds) {
		Calendar c = new GregorianCalendar();
		Date date = new Date(milliSeconds);
		c.setTime(date);
		return c.get(Calendar.HOUR);
		
	}
	public static int GetMinute(long milliSeconds) {
		Calendar c = new GregorianCalendar();
		Date date = new Date(milliSeconds);
		c.setTime(date);
		return c.get(Calendar.MINUTE);
		
	}
	public static int GetSecond(long milliSeconds) {
		Calendar c = new GregorianCalendar();
		Date date = new Date(milliSeconds);
		c.setTime(date);
		return c.get(Calendar.SECOND);
		
	}
	public static long CreateDateInLong(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, day, hour, minute, second);
		return cal.getTimeInMillis();
		
	}
}
