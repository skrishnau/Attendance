package files.read.write;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;

public class CustomFileReader {
	public final static int RETURN_AS_STRING = 1;
//	public final static int RETURN_AS_INT = 2;
	
	public List<NameAndCrn> GetNameAndCrn(String folderName, String fileName){
		File fileToRead = createFile(folderName, fileName);
		CsvFileReader csvFile = new CsvFileReader(fileToRead);
		return csvFile.ReadFile();
	}
	public List<NameAndCrnAsString> GetNameAndCrnAsString(String folderName, String fileName){
		File fileToRead = createFile(folderName, fileName);
		CsvFileReader csvFile = new CsvFileReader(fileToRead);
		return csvFile.ImportStudent(RETURN_AS_STRING);
	}
	public List<NameAndCrnAsString> GetNameAndCrnAsString( String path){
//		File fileToRead = createFile(folderName, fileName);
		File fileToRead = new File(path);
		CsvFileReader csvFile = new CsvFileReader(fileToRead);
		return csvFile.ImportStudent(RETURN_AS_STRING);
	}
	public List<NameAndCrnAsString> ImportClass(String folderName, String fileName){
		File fileToRead = createFile(folderName, fileName);
		CsvFileReader csvFile = new CsvFileReader(fileToRead);
		return csvFile.ImportClass();
	}
	public List<NameAndCrnAsString> ImportClass(String filePath){
		File fileToRead = new File(filePath);
		CsvFileReader csvFile = new CsvFileReader(fileToRead);
		return csvFile.ImportClass();
	}
	
	private File createFolderAndFile(String folderName,String fileName) {
//		File arsDirectory = createDirectory("ARS");
		File csv = createFile(folderName,fileName);
//		csv.setWritable(true);
		try {
			FileWriter writer = new FileWriter(csv);
			writer.append("my god ");
			writer.write("May u live long");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Toast.makeText(this, "file created", Toast.LENGTH_LONG);
//		tvFileReadPath.setText("Path:\n"+csv.getPath());
		return csv;
//		OutputStream os = new File(csv);
//		InputStream in = new FileReader(file);
	}

	private File createFile(String folderName, String fileName) {
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
	
}
