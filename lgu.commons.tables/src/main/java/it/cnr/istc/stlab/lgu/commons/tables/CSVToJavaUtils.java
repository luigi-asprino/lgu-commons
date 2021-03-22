package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CSVToJavaUtils {

	//TODO test!
	public static RowsIterator getRowsIterator(String fileCSVPath, char separator) throws FileNotFoundException {

		CSVReader reader = new CSVReader(new FileReader(fileCSVPath), separator);
		RowsIterator result = new RowsIterator() {

			private String[] nextRow;

			@Override
			public String[] next() {
				return nextRow;
			}

			@Override
			public boolean hasNext() {
				try {
					boolean hasNext = (nextRow = reader.readNext()) != null;
					if (!hasNext)
						reader.close();
					return hasNext;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		};

		return result;

	}

	public static List<String[]> toStringMatrix(String fileCSVPath, char separator) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			CSVReader reader = new CSVReader(new FileReader(fileCSVPath), separator);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				result.add(nextLine);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return result;
	}

	public static List<String[]> toStringMatrix(String fileCSVPath, char separator, char quote) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			CSVReader reader = new CSVReader(new FileReader(fileCSVPath), separator, quote);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				result.add(nextLine);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return result;
	}

	public static void toCSVFile(String fileCSVPath, List<String[]> rows, char separator) {

		try {
			CSVWriter csvw = new CSVWriter(new FileWriter(new File(fileCSVPath)), separator,
					CSVWriter.NO_QUOTE_CHARACTER);
			csvw.writeAll(rows);
			csvw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void toCSVFile(String fileCSVPath, List<String[]> rows, char separator, boolean quote) {

		try {
			if (!quote) {
				CSVWriter csvw = new CSVWriter(new FileWriter(new File(fileCSVPath)), separator,
						CSVWriter.NO_QUOTE_CHARACTER);
				csvw.writeAll(rows);
				csvw.close();
			} else {
				CSVWriter csvw = new CSVWriter(new FileWriter(new File(fileCSVPath)), separator,
						CSVWriter.DEFAULT_QUOTE_CHARACTER);
				csvw.writeAll(rows);
				csvw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
