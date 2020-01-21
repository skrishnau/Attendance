package com.krishna.workingwithmultipletable.activities;

import helpers.Constants;
import helpers.DBAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.krishna.workingwithmultipletable.R;

import entities.Student;
import files.read.write.CustomFileReader;
import files.read.write.NameAndCrnAsString;

/**
 * @status--------------4---------Used
 * @description
 * Displays a view to Add Students
 * @layout  R.layout.addstudent
 * 
 * @listlayout R.layout.namencrnshow
 * 
 */

public class AddStudent extends Activity{
	
	private static final int ACTION_FILE_CHOOSE = 1;
	private static final int ACTION_PICK_IMAGE = 2;
	private static final int ACTION_TAKE_PICTURE = 3;
	DBAdapter myDb;
	int itemId;
	long clsIds,dayIds,dealWithStd;
	public static String EXTRA_MESSAGE="com.krishna.workingwithmultipletable.classIds";
	TextView tvClassInfo;
	EditText etname,etcrn;
	String allData="";
	Student dealWithStudent;
	private List<Student> stdList = new ArrayList<Student>();
	int crnFront;
	ListView list;
	private String[] menuArray;
//	public boolean rememberImageOption = false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_student);
		
		Intent intent=getIntent();
		  clsIds = intent.getLongExtra(Constants.EXTRA_MESSAGE_CLASS_ID, -1);
		  //dayIds=intent.getLongExtra(ViewAttendanceDay.EXTRA_MESSAGE_DAY_ID, -1);
//		  totalClass=(TextView)findViewById(R.id.tClassDays);
		 // tvprogram=(TextView)findViewById(R.id.tvProgramt);
		 // tvcourse=(TextView)findViewById(R.id.tvCourset);
		  tvClassInfo=(TextView)findViewById(R.id.tvStdClassInfo);
//		  tvpercent=(TextView)findViewById(R.id.percent);
//		  tvpercent.setVisibility(TextView.INVISIBLE);
		etname=(EditText)findViewById(R.id.etName);
		etcrn=(EditText)findViewById(R.id.etCrn);
		//to get crn in first create
		list = (ListView) findViewById(R.id.allStdlv);
		 
		 list.getBottom();
		 registerForContextMenu(list);
		 menuArray = new String[] {"Edit","Delete"};
		    Arrays.sort(menuArray);
		openDB();
		populateClassData();
		populateStudentList();
		displayListView();
		registerClickCallback();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		populateClassData();
		populateStudentList();
		displayListView();
//		registerClickCallback();
	}
	
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
		Constants.IMAGE_OPTION = -1;
//		rememberImageOption = false;
		finish();
	    super.onBackPressed();
	    
        
	}
	

	public void ImportStudents(View v){
		Intent intent = new Intent(AddStudent.this, FileChooser.class);
		startActivityForResult(intent, ACTION_FILE_CHOOSE);
		
		//--
	
		/*List<NameAndCrn> nameAndCrnList = fileReader.GetNameAndCrn("attd_recrd_sys_1", "computer_no_space_with_marks.csv");
		List<CharSequence> array = new ArrayList<CharSequence>();
		
		for(NameAndCrn nac : nameAndCrnList){

			array.add(nac.getCrn().getBatch()+
			"-"+nac.getCrn().getRoll()+
			"\t"+nac.getName()+"");
		}*/
		
		//for string return
		/*CustomFileReader fileReader = new CustomFileReader();
		 * List<NameAndCrnAsString> nameAndCrnList = 
		  				fileReader.GetNameAndCrnAsString("attd_recrd_sys_1", "computer_no_space_with_marks.csv");
		InflateFileData(nameAndCrnList);*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String path;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {

			case ACTION_PICK_IMAGE:
//				Uri selectedImage = data.getData();
				path = getPath(data.getData());
//				displayToast(path);
				ExtractStudentInfo(path);
	            
				break;
			case ACTION_FILE_CHOOSE:
				 path = data
						.getStringExtra(FileChooser.RETURN_FILE_PATH_EXTRA);
				CustomFileReader fileReader = new CustomFileReader();
				List<NameAndCrnAsString> nameAndCrnList = fileReader
						.GetNameAndCrnAsString(path);
				InflateFileData(nameAndCrnList);
				break;
			case ACTION_TAKE_PICTURE:
				/*path = getPath(data.getData());
				displayToast(path);
				ExtractStudentInfo(path);*/	
				Bitmap photo = (Bitmap) data.getExtras().get("data"); 
//		        imageView.setImageBitmap(photo);
//		        knop.setVisibility(Button.VISIBLE);


		        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
		        Uri tempUri = getImageUri(getApplicationContext(), photo);

		        // CALL THIS METHOD TO GET THE ACTUAL PATH
//		        File finalFile = new File(getRealPathFromURI(tempUri));
		        File finalFile = new File(getPath(tempUri));
//		        displayToast(getPath(tempUri));
//		        System.out.println(mImageCaptureUri);
				break;
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    String path = Images.Media.insertImage(inContext.getContentResolver(), 
	    		inImage, "Titlw", null);
	    return Uri.parse(path);
	}
	
	private String getPath(Uri uri){
		String filePath=null;
	    String[] filePathColumn = {MediaColumns.DATA};

	    Cursor cursors = this.getContentResolver().query(uri, filePathColumn, null, null, null);
	    cursors.moveToFirst();

	    int columnIndex = cursors.getColumnIndex(filePathColumn[0]);
	    if(columnIndex!=-1){
	    	filePath = cursors.getString(columnIndex);
//	    	displayToast("File Path: "+filePath);
	    }else {
	    	displayToast("File Path cannot be extracted");
	    }
	    return filePath;
	}
/**
 * The test functions for extracting file path are at last on this page
 * May be needed later
 * */
	private void InflateFileData(final List<NameAndCrnAsString> nameAndCrnList) {
		final List<NameAndCrnAsString> ncList = new ArrayList<NameAndCrnAsString>();
		final List<CharSequence> array = new ArrayList<CharSequence>();
		// =============== REMOVE THE STUDENTS THAT ARE ALREADY IN THE CLASS ===============//
		for (NameAndCrnAsString nac : nameAndCrnList) {
			String nacCrnString = nac.getCrn().toString().trim();

			for (int i = 0; i < stdList.size(); i++) {
				String stdListCrnString = stdList.get(i).getCrn().toString()
						.trim();
				int rollNac = Integer.parseInt(nacCrnString.substring(
						nacCrnString.length() - 3, nacCrnString.length()));
				int len = stdListCrnString.length();
				int rollStdList = Integer.parseInt(stdListCrnString.substring(
						len - 3, len));
				if (rollNac == rollStdList) {
					break;
				}
				if (rollNac != rollStdList && i == stdList.size() - 1) {
					ncList.add(nac);
					array.add(nac.getCrn() + "\t  " + nac.getName() + "");
				}
			}
			if (stdList.size() <= 0) {
				ncList.add(nac);
				array.add(nac.getCrn() + "\t  " + nac.getName() + "");
			}
		}
		// ==================================================================================//
		LayoutInflater li = getLayoutInflater();
		final View v = li.inflate(R.layout.imported_std_listview, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Topic");

		final ListView lv = (ListView) v
				.findViewById(R.id.importedStdListViewlistview);
		final TextView totalStds = (TextView) v
				.findViewById(R.id.totalImportedStdsTV);
		totalStds.setText("Total " + array.size() + " students.");
		/*
		 * The layout import_list_items is a TextView layout and is used to
		 * determine the text of each list elements
		 */
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, R.layout.import_list_items, array);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		lv.setAdapter(adapter);

		builder.setView(v);
		builder.setPositiveButton("Import",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (array.size() != 0) {
							boolean saveSuccessful = true;
							for (NameAndCrnAsString nac : ncList) {
								if (SaveStudentToDatabase(nac.getCrn(),
										nac.getName(), null) == -1) {
									displayToast("Sorry! Couldnt save data. \nFrom "
											+ nac.getCrn()
											+ "\t"
											+ nac.getName() + "Till bottom.");
									saveSuccessful = false;
									break;
								}
							}
							if (saveSuccessful) {
								// AddFooterToListView();
								displayToast("Save Successful.");
							}
						}
						onResume();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// v.setVisibility(View.INVISIBLE);

					}
				});
		builder.show();
	}
	
	public void SaveStdInfo(View v){
		String name=etname.getText().toString();
	    String crn=etcrn.getText().toString();
	    //////////check if empty
		if (name.equals("") || crn.equals("")) {
			displayToast("Enter vaues...");
		} else if (crn.length() == 7) {
			if (Constants.IMAGE_OPTION<0 ) {
				ShowAlertDialogForImage();
			} else {
				switch (Constants.IMAGE_OPTION) {
				case 0:
					//directly open galary
					openGallery();
					break;
				case 1:
					// directly open camera
					openCamera();
					break;
				case 2:
					ExtractStudentInfo(null);
					// do not choose now
					
					break;
				default:
					break;
				}
			}
		}
//		    File root = Environment.getExternalStoragePublicDirectory("");
//			File temp = new File(root, "/DCIM/Camera/pillow.jpg");
//	    	long savedId = SaveStudentToDatabase(crn,name, temp.getPath());
		    
//		    if(savedId!=-1)
//		    	displayToastForId(savedId);
		    
		/*    String constCrn=crn.substring(0, 4);
		    String incCrn=crn.substring(4, 7);
		    incCrn=incCrn.trim();
		    
		    int rollInc=Integer.valueOf(incCrn);
		    rollInc+=1;
		    etcrn.setText(""+constCrn+""+rollInc);
		    etname.setText("");
		    
		    SetDefaultTextForCrnET();
		    
		    onResume();*/
	  /*  }else{
	    	displayToast("Invalid!\n Must be of form: 010-301");
	    }*/		   
	  }
	
	private void ShowAlertDialogForImage() {
		
		
		LayoutInflater li = getLayoutInflater();
		final View v = li.inflate(R.layout.listview_with_checkbox, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final ListView listview = (ListView)v.findViewById(R.id.chooseImageOptionLV);
		final CheckBox cb = (CheckBox)v.findViewById(R.id.cbRememberMyChoice);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.options_menu_textview_layout,
				Constants.OPTIONS_FOR_GETTING_IMAGE);
		adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
		
		listview.setAdapter(adapter);
		builder.setView(v);
		final AlertDialog alert = builder.show();
		alert.show();
//		builder.show();
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(cb.isChecked()){
					Constants.IMAGE_OPTION = position;
				}else{
					Constants.IMAGE_OPTION = -1;
				}
				String message = Constants.OPTIONS_FOR_GETTING_IMAGE[position];
				switch(position){
				case 0:
					openGallery();
					alert.cancel();
					break;
				case 1:
//					openCamera();
					Constants.IMAGE_OPTION = -1;
					displayToast("Not available.");
//					alert.cancel();
					break;
				case 2:
					ExtractStudentInfo(null);
					alert.cancel();
//					onResume();
					break;
				default:
					onResume();
					break;
				}
			}

		});

		/*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				String message = Constants.OPTIONS_FOR_STUDENT_LIST[position]; 
				switch(position){
				case 0:
					//give picture directory to choose
					Intent intent = new Intent(AddStudent.this,
			                ViewStudents.class);
					intent.putExtra(Constants.EXTRA_MESSAGE_CLASS_ID, clsIds);
					startActivity(intent);
					finish();
					break;
					
				case 1:
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
						Toast.makeText(
								AddStudent.this,
								"SD card Unmounted. Please insert a memory card",
								Toast.LENGTH_LONG).show();
					}else if(Environment.getExternalStorageState() == Environment.MEDIA_REMOVED){
						Toast.makeText(
								AddStudent.this,
								"SD card Removed. Please insert a memory card",
								Toast.LENGTH_LONG).show();
					}else if(Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
						
					}else {
							FileWriterThread genReport = new FileWriterThread(ViewStudents.this,clsIds, classNameForFile);
							genReport.execute(classNameForFile);
						
					}
					break;				
				default:
					break;
				}
				if(cb.isChecked()){
					rememberImageOption = true;
					imageOption = position;
				}
			}

		});*/

	}
	

	private void openCamera() {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Image File name");
		Uri mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		startActivityForResult(intentPicture,ACTION_TAKE_PICTURE);
	}
	
	private void openGallery(){
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), ACTION_PICK_IMAGE);
	}

	private void ExtractStudentInfo(String path){
		String name=etname.getText().toString();
	    String crn=etcrn.getText().toString();
	    //////////check if empty
	    if(name.equals("") || crn.equals("")){
	    	displayToast("Enter vaues...");
	    }else if(crn.length()==7){
	    	if(SaveStudentToDatabase(crn,name, path)!=-1){
//	    		displayToast("crn"+crn+"Name: "+name+" Image Path: "+path);
	    		displayToast("Image Path:\n\n"+path);
	    	}
	    }
	    onResume();
	}
	private long SaveStudentToDatabase(String crn, String name, String imagePath){
		ContentValues initialValues =new ContentValues();
	    initialValues.put(DBAdapter.KEY_CRN, crn);
	    initialValues.put(DBAdapter.KEY_NAME, name);
	    if(imagePath!=null || imagePath!="")
	    	initialValues.put(DBAdapter.KEY_IMAGE, imagePath);
	    //save student to db
	    long stdIdm= myDb.insertRow(DBAdapter.STUDENT_TABLE, initialValues);
		if (stdIdm != -1) {
			ContentValues aValues = new ContentValues();
			aValues.put(DBAdapter.KEY_CLS_ID, clsIds);
			aValues.put(DBAdapter.KEY_STD_ID, stdIdm);
			// save student's respective class
			long stdclsIdm = myDb.insertRow(DBAdapter.CLASS_STUDENT_TABLE,
					aValues);
			if(stdclsIdm==-1){
				myDb.deleteRow(DBAdapter.STUDENT_TABLE, stdIdm);
				displayToast("Sorry. Couldn't save.");
				return -1;
			}
			return stdIdm;
		}else{
			displayToast("Sorry. Couldn't save.");
			return -1;
		}
	}
	
	private void SetDefaultTextForCrnET(){
		Cursor stdclcursor = myDb.getAscStd(clsIds);
		if(stdclcursor.moveToFirst()){
			stdclcursor.moveToLast();
			long sid=stdclcursor.getLong(DBAdapter.COL_ROWID);
			
			String scrn=stdclcursor.getString(DBAdapter.COL_STD_CRN);
			String sname=stdclcursor.getString(DBAdapter.COL_STD_NAME);
			
			String constCrn=scrn.substring(0, 4);
		    String incCrn=scrn.substring(4, 7);
		    incCrn=incCrn.trim();
		    
		    int rollInc=Integer.valueOf(incCrn);
		    rollInc+=1;
		    etcrn.setText(""+constCrn+""+rollInc);
		    etname.setText("");
		}else if(clsIds>-1){
			Cursor c=myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, clsIds);
			int x=c.getInt(DBAdapter.COL_BATCH);
			crnFront=x%1000;
			if(crnFront<100){
				etcrn.setText("0"+String.valueOf(crnFront)+"-");
			}else{
				etcrn.setText(String.valueOf(crnFront)+"-");
			}
		}
	}
	
	private void populateClassData() {
					
					SetDefaultTextForCrnET();
//			totalClass.setText("Total classes:"+totalClassDays()+" days");
			
			Cursor c=myDb.getRow(DBAdapter.CLASS_TABLE, DBAdapter.ALL_CLASS_KEYS, clsIds);
			if(c.moveToFirst()){
				int batch=c.getInt(DBAdapter.COL_BATCH);
				String program=c.getString(DBAdapter.COL_PROGRAM);
				String course=c.getString(DBAdapter.COL_COURSE);
				allData=""+String.valueOf(batch)+" / "+program+" / "+course;
			//	tvprogram.setText(program);
				tvClassInfo.setText(""+allData);
			//	tvcourse.setText(course);	
				SetDefaultTextForCrnET();
				
			}
			c.close();
	}

		/*private void AddFooterToListView() {
			LayoutInflater li = getLayoutInflater();
			View v = li.inflate(R.layout.single_text_view_list_layout, null);
			TextView tv = (TextView)v.findViewById(R.id.singleTextView);
			tv.setText("Total " +stdList.size()+" students.");
			
			list.removeFooterView(v);
			list.addFooterView(v,null,false);
		}*/


		///////////
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
		///////////
		private void populateStudentList() {
			stdList.clear();
			List<Long> longList=new ArrayList<Long>();
			//Cursor stdclcursor=myDb.getRows(DBAdapter.CLASS_STUDENT_TABLE, DBAdapter.ALL_CLASS_STUDENT_KEYS, clsIds);
			String[] someKeys= new String[]{DBAdapter.KEY_STD_ID};
			//////////////////////
			Cursor stdclcursor = myDb.getAscStd(clsIds);
			
			 if(stdclcursor.moveToFirst()){
				 do{
				 	Student object=new Student();
				 	
					long sid=stdclcursor.getLong(DBAdapter.COL_ROWID);
				
					String scrn=stdclcursor.getString(DBAdapter.COL_STD_CRN);
					String sname=stdclcursor.getString(DBAdapter.COL_STD_NAME);
				
					object.setCrn(scrn);
					object.setName(sname);
					object.set_id(sid);
					
					stdList.add(object);
					
				 }while(stdclcursor.moveToNext());
			
			 }stdclcursor.close();
			
			
			 
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
		
		private void displayListView() {
			
			ArrayAdapter<Student> adapter = new MyListAdapter();
			//ListView list = (ListView) findViewById(R.id.allStdlv);
			
			
			list.setAdapter(adapter);	
			
			list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

	            public boolean onItemLongClick(AdapterView<?> arg0, View v,
	                    int index, long arg3) {

	                ListView myList = (ListView)findViewById(R.id.allStdlv);
	                Student student=stdList.get(index);
	               /* displayToast(	String.valueOf(index)+"\n"+
        							student.get_id()+"\n"+
        							student.getCrn()+"\n"+
        							student.getName()
        							);*/
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
		    menu.setHeaderTitle(""+dealWithStudent.getCrn()+"     "+
		    					dealWithStudent.getName());
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {
//		    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//		    int menuItemIndex = item.getItemId();
		   // displayToast("this is from onContextItemSelected"+menuItemIndex);
		    itemId=item.getItemId();
		    switch (itemId) {
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


		private void menuDelete() {
			// TODO Auto-generated method stub
			AlertDialog.Builder delbuilder = new AlertDialog.Builder(this);
            delbuilder.setMessage("Are you sure to delete?")
                   .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // FIRE ZE MISSILES!
                    	   myDb.deleteRow(DBAdapter.STUDENT_TABLE, dealWithStudent.get_id());
//                    	   String[] attdRow=new String[] {DBAdapter.KEY_ROWID};
                    	
                    	   myDb.deleteRows(DBAdapter.ATTENDANCE_TABLE,
                    			   			DBAdapter.ALL_ATTENDANCE_KEYS, 
                    			   			DBAdapter.KEY_STD_ID, 
                    			   			dealWithStudent.get_id());
                    	  myDb.deleteRows(DBAdapter.CLASS_STUDENT_TABLE,
                    			  DBAdapter.ALL_CLASS_STUDENT_KEYS,
                    			  DBAdapter.KEY_STD_ID,
                    			  dealWithStudent.get_id());
                    	  displayToast("Successfully deleted.");
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
			View v = li.inflate(R.layout.editalert, null);
			//View v=getLayoutInflater().inflate(R.layout.editalert, null, false);
			final AlertDialog.Builder builder = new AlertDialog.Builder( this );
			builder.setTitle("Enter the values...");
			final EditText etscrn = (EditText) v.findViewById(R.id.etAlertCrn);
			etscrn.setText(dealWithStudent.getCrn());
			final EditText etsname = (EditText) v.findViewById(R.id.etAlertName);
			etsname.setText(dealWithStudent.getName());
			builder.setView(v);
			builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					ContentValues newValues = new ContentValues();
						newValues.put(DBAdapter.KEY_CRN, etscrn.getText().toString());
						newValues.put(DBAdapter.KEY_NAME, etsname.getText().toString());
						myDb.updateRow(DBAdapter.STUDENT_TABLE, newValues, dealWithStudent.get_id());
						
						onResume();
				}

				});			    
		    builder.show();
			
		}
		
		private void registerClickCallback() {
			ListView list = (ListView) findViewById(R.id.allStdlv);
//			AddFooterToListView();
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					Student student = stdList.get(position);
					String message = "Student Id:: " + id
									+"\ncrn::"+student.getCrn()
									+ " \nname " + student.getName(); 
					Toast.makeText(AddStudent.this, message, Toast.LENGTH_LONG).show();
				}
			});
			final Button importStdBt = (Button)findViewById(R.id.importStudentButton);
			importStdBt.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						importStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
					}else if(event.getAction()==MotionEvent.ACTION_UP){
						importStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
					}
					return false;
				}
			});
			final Button saveStdBt = (Button)findViewById(R.id.SaveStudentButton);
			saveStdBt.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						saveStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_drawable));
					}else if(event.getAction()==MotionEvent.ACTION_UP){
						saveStdBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_drawable));
					}
					return false;
				}
			});
		
		}
		
		private class MyListAdapter extends ArrayAdapter<Student> {
			public MyListAdapter() {
				super(AddStudent.this, R.layout.add_student_list_layout, stdList);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// Make sure we have a view to work with (may have been given null)
				View itemView = convertView;
				if (itemView == null) {
					itemView = getLayoutInflater().inflate(R.layout.add_student_list_layout, parent, false);
				}
				
				// Find the student to work with.
				Student student = stdList.get(position);
				
				// Crn:
				TextView tvcrn = (TextView) itemView.findViewById(R.id.tvShowCrn);
				tvcrn.setText(student.getCrn());
				// name:
				TextView tvname = (TextView) itemView.findViewById(R.id.tvShowName);
				tvname.setText(student.getName());
				
				return itemView;
			}				
		}

		private void displayToast(String message) {
    		Toast.makeText(AddStudent.this, message,
                  Toast.LENGTH_SHORT).show();
      }    
	
	/*	private String getDateTime() {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			Date date = new Date();
			return dateFormat.format(date);
		}*/
    	
		/*private void displayToastForId(long idInDB) {
			Cursor cursor = myDb.getRow(DBAdapter.STUDENT_TABLE, DBAdapter.ALL_STUDENT_KEYS, idInDB);
			if (cursor.moveToFirst()) {
				long idDB = cursor.getLong(DBAdapter.COL_ROWID);
				String crn= cursor.getString(DBAdapter.COL_STD_CRN);
				String name = cursor.getString(DBAdapter.COL_STD_NAME);
				
				
				String message = "ID: " + idDB + "\n" 
						+ "CRN: " + crn + "\n"
						+ "name:: : " + name ;
				displayToast(message);
			}
			cursor.close();
		}*/

	
	//==========================================================================================//	
	//================== The below codes are for extracting file Path from Uri =================//
	//============================================================================================//
		/*String imagePath = 	getRealPathFromURI(selectedImage);
		ExtractStudentInfo(imagePath);
		displayToast("path: "+imagePath+" \nencoded path: ");*/

		//--
		
		/*String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
        
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
        
        // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            
        ExtractStudentInfo(realPath);
         displayToast("real Path: "+realPath);
        */
		/*
		String fileName = getFileNameByUri(this, data.getData());
		
		ExtractStudentInfo(fileName);
		displayToast(fileName);*/
		/*displayToast(selectedImage.getPath());
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		displayToast(""+cursor.getCount()+" column: "+cursor.getColumnCount());*/
		
	/*	
		
       if(cursor.moveToFirst()){
    	   do{
    		   int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA); 
    		   displayToast("Required column: "+ idx);
    		   for(int x = 0; x<cursor.getColumnCount(); x++){
    			   displayToast(cursor.getString(x));
    		   }
    		   
    	   }while(cursor.moveToNext());
       }*/
//        setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(),realPath);
    
		
		//--
       /* InputStream imageStream = null;
		try {
			
			imageStream = getContentResolver().openInputStream(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
        imageStream.close();*/
		
		//==========================================================================================//	
		//================== The below codes are functions for extracting file Path from Uri =================//
		//============================================================================================//	
		
    /*    

		private String getRealPathFromURI(Uri contentURI) {
		    String result;
		    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		    if (cursor == null) { // Source is Dropbox or other similar local file path
		        result = contentURI.getPath();
		    } else { 
		        cursor.moveToFirst(); 
		        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		        result = cursor.getString(idx);
		        cursor.close();
		    }
		    return result;
		}
		public static String getFileNameByUri(Context context, Uri uri)
		{
		    String fileName="ARS";//"unknown"==>default fileName
		    Uri filePathUri = uri;
		    String st="";
		    if (uri.getScheme().toString().compareTo("content")==0)
		    {      
		        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		        if (cursor.moveToFirst())
		        {
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
		            filePathUri = Uri.parse(cursor.getString(column_index));
		            
		            for(String s:filePathUri.getPathSegments()){
		            	st.concat(s);
		            }
		            fileName = filePathUri.getLastPathSegment().toString();
		        }
		    }
		    else if (uri.getScheme().compareTo("file")==0)
		    {
		        fileName = filePathUri.getLastPathSegment().toString();
		    }
		    else
		    {
		        fileName = fileName+"_"+filePathUri.getLastPathSegment();
		    }
		    return st.concat("\n"+fileName);
		}
		*/ 
 
}