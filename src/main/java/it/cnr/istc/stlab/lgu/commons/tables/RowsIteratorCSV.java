package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class RowsIteratorCSV implements RowsIterator {

	private CSVReader reader;

	public RowsIteratorCSV(String file, char separator) throws FileNotFoundException {
		super();
		this.reader = new CSVReader(new FileReader(file), separator);
	}

	private String[] nextRow;

	@Override
	public String[] next() {
		return nextRow;
	}

	@Override
	public boolean hasNext() {
		try {
			return (nextRow = reader.readNext()) != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
