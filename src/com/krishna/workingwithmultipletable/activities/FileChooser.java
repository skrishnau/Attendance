package com.krishna.workingwithmultipletable.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.krishna.workingwithmultipletable.R;


public class FileChooser extends Activity{

	public static final String RETURN_FILE_PATH_EXTRA = "com.krishna.returnfilepathextra";
	ListView list;
	List<MyFile> listOfFiles;
	TextView folderNameTv;
	// File previousFile = null;
	// File currentFile = null;

	List<File> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_chooser_layout);
		list = (ListView) findViewById(R.id.filelv);
		folderNameTv = (TextView) findViewById(R.id.folderNametv);
		listOfFiles = new ArrayList<MyFile>();
		history = new ArrayList<File>();
//		ActionBar bar = this.getActionBar();
//		bar.setTitle("Choose .csv file");
	}

	@Override
	public void onBackPressed() {
		if (history.size() > 1) {
			history.remove(history.size() - 1);
			onResume(history.get(history.size() - 1));
		} else {
			finish();
		}

		// super.onBackPressed();
	}

	@Override
	protected void onResume() {
		history.clear();
		super.onResume();
		File file = getRootDirectory();
		history.add(file);
		PopulateFileArray(file);
		PopulateFileListView();
		RegisterListClick();
	}

	protected void onResume(File file) {
		// super.onResume();
		PopulateFileArray(file);
		PopulateFileListView();

		RegisterListClick();
	}

	private File getRootDirectory() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath());
		return file;
	}

	private void PopulateFileArray(File file) {
		listOfFiles.clear();
		folderNameTv.setText(file.getPath().toString()+"/");
		File[] fileList = file.listFiles();
		//===========================
		String[] nameArray = new String[fileList.length];
		File[] tempFileArray = new File[fileList.length];
//		Arrays.sort(fileList);
		for(int i = 0; i<fileList.length; i++){
			nameArray[i] = fileList[i].getName();
		}
		Arrays.sort(nameArray);
		for(int i = 0 ; i<nameArray.length; i++){
			for(int j = 0; j<fileList.length; j++){
				if(nameArray[i].equalsIgnoreCase(fileList[j].getName().toString())){
					tempFileArray[i] = fileList[j];
					break;
				}
			}
		}
		fileList = tempFileArray;
		//=============================
		for (int i = 0; i < fileList.length; i++) {
			boolean isFile = false;
			if (fileList[i].isFile()) {
				isFile = true;
			}

			String name = fileList[i].getName();

			if (!name.startsWith(".")) {
				if (isFile) {
					if (name.endsWith(".csv")) {
						listOfFiles.add(new MyFile(fileList[i],  isFile));
					}
				} else {
					listOfFiles.add(new MyFile(fileList[i], isFile));
				}
			}
		}

	}

	private void PopulateFileListView() {
		ArrayAdapter<MyFile> adapter = new FileAdapter();
		list.setAdapter(adapter);

	}

	private void RegisterListClick() {
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				File file = listOfFiles.get(position).getFile();
				if (file.getName().toString().endsWith(".csv")) {
					Intent returnIntent = new Intent();
					returnIntent.putExtra(RETURN_FILE_PATH_EXTRA,file.getPath());
					setResult(RESULT_OK,returnIntent);
					finish();
				} else {
					history.add(file);
					onResume(file);
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class FileAdapter extends ArrayAdapter<MyFile> {

		public FileAdapter() {
//			super(FileChooser.this, android.R.layout.simple_gallery_item,
//					listOfFiles);
			super(FileChooser.this, R.layout.file_chooser_list_layout,
					listOfFiles);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.file_chooser_list_layout, parent, false);
			}
			TextView tv = (TextView) itemView.findViewById(R.id.tvFileName);
			ImageView im = (ImageView)itemView.findViewById(R.id.imFileImage);
			
			MyFile myFile = listOfFiles.get(position);
			String name = myFile.getFile().getName().toString();
			if (!myFile.isFile()) {
				im.setBackgroundResource(R.drawable.folder_image);
			}else{
				im.setBackgroundResource(R.drawable.excel_logo);
			}
			tv.setText(name);

			return itemView;
		}

	}

	private class MyFile {
		private File file;
//		private Images image;
		private boolean isFile;// true for file false for folder

		public MyFile(File file, boolean isFile) {
			super();
			this.file = file;
			this.isFile = isFile;
//			this.image = image;
		}

		public File getFile() {
			return file;
		}


		public boolean isFile() {
			return isFile;
		}

	}
}
