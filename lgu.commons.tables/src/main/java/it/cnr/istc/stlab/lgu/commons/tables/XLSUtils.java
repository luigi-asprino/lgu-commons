package it.cnr.istc.stlab.lgu.commons.tables;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSUtils {

	public static String[] rowToStringArray(Row row) {
		String[] rowOut = new String[row.getLastCellNum()];
		Iterator<Cell> cells = row.iterator();
		while (cells.hasNext()) {
			Cell currentCell = cells.next();
			if (currentCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
				rowOut[currentCell.getColumnIndex()] = currentCell.getBooleanCellValue() + "";
			} else if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				rowOut[currentCell.getColumnIndex()] = currentCell.getCellFormula();
			} else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				Double d = currentCell.getNumericCellValue();
				rowOut[currentCell.getColumnIndex()] = d.doubleValue() + "";
			} else {
				rowOut[currentCell.getColumnIndex()] = currentCell.getStringCellValue();
			}
		}
		return rowOut;
	}

}
