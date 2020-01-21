package entities;

import android.graphics.Bitmap;

public class ImageDisplayEntities{

	public Bitmap bitmap;
	public StudentPresence student;
	public String fileName;
	public ImageDisplayEntities(Bitmap bitmap, StudentPresence student,
			String fileName) {
		super();
		this.bitmap = bitmap;
		this.student = student;
		this.fileName = fileName;
	}

	
}