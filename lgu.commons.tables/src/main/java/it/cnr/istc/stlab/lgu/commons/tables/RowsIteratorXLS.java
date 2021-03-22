package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class RowsIteratorXLS implements RowsIterator {

	private Iterator<Row> rowsIterator;

	public RowsIteratorXLS(String filepath, int numberOfSheet)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet currentSheet = wb.getSheetAt(numberOfSheet);
		this.rowsIterator = currentSheet.iterator();
	}

	@Override
	public boolean hasNext() {
		return this.rowsIterator.hasNext();
	}

	@Override
	public String[] next() {
		return XLSUtils.rowToStringArray(this.rowsIterator.next());
	}

}
