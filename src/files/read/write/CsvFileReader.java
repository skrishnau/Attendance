package files.read.write;

import helpers.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvFileReader {

	// static int currentPosition=0;
	// static int crnPosition=0;
	// static int namePosition = 0;

	private List<NameAndCrn> nameAndCrnList;
	private List<NameAndCrnAsString> nameAndCrnAsStringList;
	private File f;
	int returnType;

	public CsvFileReader(File f) {
		super();
		this.f = f;
//		returnType = CustomFileReader.RETURN_AS_INT;

	}

	// ------------1---starting position---------------//
	// ------------its overloaded because: for returning batch as string rather
	// than integer-----------//
	// this function returns crn as integers of two : batch and roll
	public List<NameAndCrn> ReadFile() {
		nameAndCrnList = new ArrayList<NameAndCrn>();
		// File f = new
		// File("C:/Users/grey/Desktop/AndroidFiles_MinorProject/computer_three_space.csv");
		try {

			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				StringBuilder sbLine = new StringBuilder();
				int columnNumber = 0;
				for (int pos = 0; pos < line.length(); pos++) {
					char ch = line.charAt(pos);

					if (ch == '-') {
						// boolean isCrnAndNameFound = CheckForCrn(line, pos,
						// columnNumber);
						if (CheckForCrn(line, pos, columnNumber)) {
							// pos= currentPosition+1;
							pos = line.length() - 1;
						}
					}

				}// end for
					// / System.out.println();
				lineNumber++;
			}// end while
		} catch (IOException ex) {
			Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return nameAndCrnList;
	}

	
	//==============================================================================//
	/*
	 * overloaded Returns crn as string
	 */
	//these below three functions occur after each other sequentially.
	//the first function is called from AddStudent Activity
	public List<NameAndCrnAsString> ImportStudent(int returnType) {
		this.returnType = returnType;
		if (returnType == CustomFileReader.RETURN_AS_STRING) {
			nameAndCrnAsStringList = new ArrayList<NameAndCrnAsString>();
			// File f = new
			// File("C:/Users/grey/Desktop/AndroidFiles_MinorProject/computer_three_space.csv");
			try {

				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line;
				int lineNumber = 0;

				while ((line = reader.readLine()) != null) {
//					String[] outerToken = line.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
//					StringBuilder sbLine = new StringBuilder();
					int columnNumber = 0;
					for (int pos = 0; pos < line.length(); pos++) {
						char ch = line.charAt(pos);
						if (ch == '-') {
							// boolean isCrnAndNameFound = CheckForCrn(line,
							// pos, columnNumber);
							if (CheckForCrn(line, pos, columnNumber)) {
								// pos= currentPosition+1;
								pos = line.length() - 1;
							}
						}
					}// end for 
					// / System.out.println();
					lineNumber++;
				}// end while
			} catch (IOException ex) {
				Logger.getLogger(CsvFileReader.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			return nameAndCrnAsStringList;
		} else {
			return null;
		}
	}
	private boolean CheckForCrn(String line, int dashPosition, int columnNumber) {
		StringBuilder sbRoll = new StringBuilder();
		int start;
		int tempPos;
		boolean isCrn = true;
		for (start = dashPosition + 1; start < line.length(); start++) {
			char ch = line.charAt(start);
			if (ch == ',') {
				break;
			}
			if (!Character.isDigit(ch)) {
				isCrn = false;
				break;
			}
			sbRoll.append(ch);
		}

		String roll = sbRoll.toString().trim();
		boolean isBatch = true;
		if (roll.length() == 3 && isCrn) {
			sbRoll = new StringBuilder();
			for (int i = dashPosition - 1; i >= 0; i--) {
				char ch = line.charAt(i);
				if (ch == ',') {
					break;
				}
				if (!Character.isDigit(ch)) {
					isBatch = false;
					break;
				}
				sbRoll.append(ch);
			}
			if (isBatch) {
				String batch = sbRoll.reverse().toString().trim();
				// // System.out.print(batch + "-" + roll);
				// currentPosition = start;

				// -----------crn is found so lets add it to the list--------//

				String name = CheckForName(start, line, columnNumber);
				
				if (returnType == CustomFileReader.RETURN_AS_STRING) {
					if (Integer.parseInt(batch) < 100 && batch.length()<3) {
						batch = "0" + batch + "-" + roll;
					} else if (Integer.parseInt(batch) >= 1000 || batch.length()>3) {
						char[] charArray = { batch.charAt(batch.length() - 3),
								batch.charAt(batch.length() - 2),
								batch.charAt(batch.length() - 1) };
						
						batch = new String(charArray) + "-" + roll;
					}else{
						batch= batch+"-"+roll;
					}
					nameAndCrnAsStringList.add(new NameAndCrnAsString(batch, name));
				} else {
					CRN crn = new CRN(Integer.parseInt(batch),
							Integer.parseInt(roll));
					nameAndCrnList.add(new NameAndCrn(crn, name));
				}
				return (true);
			}
			// find out accuracy of roll
		}
		return false;
	}
	// overloaded
	private String CheckForName(int start, String line, int columnNumber) {
		int currentPos = start + 1;
		// / int nameColumn = 0;
		StringBuilder sb = new StringBuilder();
		for (; currentPos < line.length(); currentPos++) {
			char ch = line.charAt(currentPos);
			if (ch == ',' && sb.toString().trim().equals("")) {
				// // System.out.print(" - ");
				columnNumber++;
				continue;
			} else if (ch == ',') {
				break;
			}
			sb.append(ch);
		}
		String name = sb.toString().trim();
		// // System.out.println("\t\t"+name);
		// / nameColumn = columnNumber;
		return name;
	}
	//==============================================================================//
	
	//===============================================================================//
	//these below three functions occur after each other sequentially.
	//the first function is called from AddClass Activity
	public List<NameAndCrnAsString> ImportClass() {
		
		String[] ProgramArray = Constants.PROGRAMS_ARRAY;
		nameAndCrnAsStringList = new ArrayList<NameAndCrnAsString>();
		List<String> BatchArray = Constants.BatchArray();
		
		
		nameAndCrnAsStringList.add(new NameAndCrnAsString("7", ""));
		nameAndCrnAsStringList.add(new NameAndCrnAsString("8", ""));
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				String[] lineArray = line.replaceAll("[,+\\s+]+", " ")
						.split("[\\s+]|[,]");
				int columnNumber = 0;
				for (String word : lineArray) {
					for (int i = 0; i < ProgramArray.length; i++) {
						if (ProgramArray[i].equalsIgnoreCase(word)) {
							nameAndCrnAsStringList.set(0,
									new NameAndCrnAsString(String.valueOf(i),
											word));
							break;
						}
					}
					for (int i = 0; i < BatchArray.size(); i++) {
						if (BatchArray.get(i).equalsIgnoreCase(word)) {
							nameAndCrnAsStringList.set(1,
									new NameAndCrnAsString(String.valueOf(i),
											word));
							break;
						}
					}
					if (word.contains("-")) {
						if (columnNumber < lineArray.length - 1) {
							GetCrn(word, columnNumber,
									lineArray[columnNumber + 1]);
						}
					}
					columnNumber++;
				}
				lineNumber++;
			}
		} catch (IOException ex) {
			Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return nameAndCrnAsStringList;
		
	}
	private String GetCrn(String word, int columnNumber, String nextWord){
		for (int pos = 0; pos < word.length(); pos++) {
			char ch = word.charAt(pos);
			if (ch == '-') {
				if (CheckCrn(word, nextWord, pos, columnNumber)) {
					pos = word.length() - 1;
				}
			}
		}
		
		
		return null;
	}
	private boolean CheckCrn(String word, String nextWord, int dashPosition, int columnNumber) {
		StringBuilder sbRoll = new StringBuilder();
		int start;
		int tempPos;
		boolean isCrn = true;
		for (start = dashPosition + 1; start < word.length(); start++) {
			char ch = word.charAt(start);
			if (!Character.isDigit(ch)) {
				isCrn = false;
				break;
			}
			sbRoll.append(ch);
		}

		String roll = sbRoll.toString().trim();
		boolean isBatch = true;
		if (roll.length() == 3 && isCrn) {
			sbRoll = new StringBuilder();
			for (int i = dashPosition - 1; i >= 0; i--) {
				char ch = word.charAt(i);
				if (!Character.isDigit(ch)) {
					isBatch = false;
					break;
				}
				sbRoll.append(ch);
			}
			if (isBatch) {
				String batch = sbRoll.reverse().toString().trim();
				// // System.out.print(batch + "-" + roll);
				// currentPosition = start;

				// -----------crn is found so lets add it to the list--------//

				String name = nextWord;
					if (Integer.parseInt(batch) < 100 && batch.length()<3) {
						batch = "0" + batch + "-" + roll;
					} else if (Integer.parseInt(batch) >= 1000 || batch.length()>3) {
						char[] charArray = { batch.charAt(batch.length() - 3),
								batch.charAt(batch.length() - 2),
								batch.charAt(batch.length() - 1) };
						
						batch = new String(charArray) + "-" + roll;
					}else{
						batch= batch+"-"+roll;
					}
					nameAndCrnAsStringList.add(new NameAndCrnAsString(batch, name));
				
				return (true);
			}
			// find out accuracy of roll
		}
		return false;
	}
	//===================================================================================//
	
	private List<String> SeparateTheCells(String line) {
		StringBuilder sb = new StringBuilder();
		List<String> rowDataList = new ArrayList<String>();
		for (int pos = 0; pos < line.length(); pos++) {
			char ch = line.charAt(pos);
			sb.append(ch);
			// last element will not be taken if we dont use (pos ==
			// (line.length() - 1))
			if (ch == ',') {
				sb.deleteCharAt(sb.length() - 1);
				rowDataList.add(sb.toString().trim());
				sb = new StringBuilder();
			} else if (pos == (line.length() - 1)) {
				rowDataList.add(sb.toString().trim());
			}
		}
		return rowDataList;
	}
	private void CalculateAccuracy() {
		// if 5 of the crn s lie on the same column number then its a valid
		// column for crn and all crn lie on same column
	}

}
