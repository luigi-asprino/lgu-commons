package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class RowsIteratorCSV implements RowsIterator {

	private CSVReader reader;
	private String[] nextRow;
	private boolean readNextCalled = false;

	public RowsIteratorCSV(String file, char separator) throws FileNotFoundException {
		super();
		this.reader = new CSVReader(new FileReader(file), separator);
	}

	@Override
	public String[] next() {
		if (nextRow == null) {
			if (readNextCalled) {
				return null;
			} else {
				String[] result;
				try {
					result = reader.readNext();
					readNextCalled = true;
					nextRow = reader.readNext();
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				String[] result = nextRow;
				nextRow = reader.readNext();
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	@Override
	public boolean hasNext() {
		try {

			if (nextRow == null && !readNextCalled) {
				if (readNextCalled) {
					return false;
				} else {
					nextRow = reader.readNext();
					readNextCalled = true;
					return nextRow != null;
				}
			} else {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
