package com.krishna.workingwithmultipletable.activities;

import helpers.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityStarter extends Activity{
		public ActivityStarter(Context context, long classId){
			Intent intent = new Intent(context,
	                ViewStudents.class);
			intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, classId);
			startActivity(intent);
		}

}
