package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * This class encloses a set of facilities for reading and writing XLS and XLSX
 * files. It is based on Apache POI library.
 * 
 * @author lgu
 *
 */
public class XLS {

	private String filepath;

	public XLS(String filepath) {
		super();
		this.filepath = filepath;
	}

	public String getFilePath() {
		return filepath;
	}

	public int getNumberOfSheets() throws InvalidFormatException, IOException {
		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		int numberOfSheets = wb.getNumberOfSheets();
		return numberOfSheets;
	}

	public String getNameOfSheet(int numberOfSheet) throws InvalidFormatException, IOException {
		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		return wb.getSheetName(numberOfSheet);
	}

	public List<String[]> getRowsOfSheet(int numberOfSheet) throws Exception {
		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet currentSheet = wb.getSheetAt(numberOfSheet);
		List<String[]> result = new LinkedList<String[]>();
		Iterator<Row> rows = currentSheet.iterator();
		while (rows.hasNext()) {
			Row currentRow = rows.next();
			if (currentRow.getLastCellNum() > 0) {
				String[] rowOut = new String[currentRow.getLastCellNum()];
				Iterator<Cell> cells = currentRow.iterator();
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
				result.add(rowOut);
			}
		}
		return result;
	}

	public List<String> getColumnCellOfSheet(int sheeetNumber, int column) throws Exception {

		List<String> result = new ArrayList<String>();

		List<String[]> rows = getRowsOfSheet(sheeetNumber);
		Iterator<String[]> rowsIterator = rows.iterator();
		while (rowsIterator.hasNext()) {
			String[] currentRow = rowsIterator.next();
			if (column < currentRow.length && currentRow[column] != null && !currentRow[column].equals("")) {
				result.add(currentRow[column]);
			}
		}

		return result;

	}

	public void toHTMLTable(String filenameOut) throws Exception {
		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		int numberOfSheets = wb.getNumberOfSheets();
		List<String> rowsOut = new ArrayList<String>();

		for (int i = 0; i < numberOfSheets; i++) {
			List<String[]> rowsOfSheet = this.getRowsOfSheet(i);
			rowsOut.add("<div class=\"table-responsive\">" + "<table class=\"table table-striped\">"
					+ "<caption style=\"text-align: center\">InsertCaption</caption>" + "<thead>");

			Iterator<String[]> rowsOfSheetIterator = rowsOfSheet.iterator();
			String[] first = rowsOfSheetIterator.next();
			rowsOut.add(toHTMLRow(first, "<th><b>", "</b></th>"));
			rowsOut.add("</thead><tbody>");

			while (rowsOfSheetIterator.hasNext()) {
				String[] currentRow = rowsOfSheetIterator.next();
				rowsOut.add(toHTMLRow(currentRow));
			}

			rowsOut.add("</tbody></table></div>\n\n\n");

		}
		// FileUtilities.createFile(filenameOut, rowsOut);
	}

	private static String toHTMLRow(String[] rowArray) {
		String result = "<tr>";
		for (int i = 0; i < rowArray.length; i++) {
			result = result + "<td>" + rowArray[i] + "</td>";
		}
		result = result + "</tr>";
		return result;
	}

	private static String toHTMLRow(String[] rowArray, String tagOpenElement, String tagCloseElement) {
		String result = "<tr>";
		for (int i = 0; i < rowArray.length; i++) {
			result = result + tagOpenElement + rowArray[i] + tagCloseElement;
		}
		result = result + "</tr>";
		return result;
	}

	public Map<String, List<String[]>> getSheetMap() throws Exception {

		Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();

		InputStream inp = new FileInputStream(filepath);
		Workbook wb = WorkbookFactory.create(inp);
		int numberOfSheets = wb.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++) {
			List<String[]> rowsOfSheet = this.getRowsOfSheet(i);
			result.put(this.getNameOfSheet(i), rowsOfSheet);
		}

		return result;
	}

	public void toSpreadSheet(Map<String, List<String[]>> sheetMap) {

		XSSFWorkbook workBook = new XSSFWorkbook();

		Iterator<String> sheetNameIterator = sheetMap.keySet().iterator();

		while (sheetNameIterator.hasNext()) {
			String currentSheetName = sheetNameIterator.next();
			XSSFSheet sheet = workBook.createSheet(currentSheetName);

			List<String[]> rows = sheetMap.get(currentSheetName);

			Iterator<String[]> rowsIterator = rows.iterator();
			int rowNum = 0;
			while (rowsIterator.hasNext()) {

				String[] currentRowIn = rowsIterator.next();

				XSSFRow currentRowOut = sheet.createRow(rowNum++);

				for (int i = 0; i < currentRowIn.length; i++) {
					try {
						double d = Double.parseDouble(currentRowIn[i]);
						currentRowOut.createCell(i).setCellValue(d);
					} catch (NumberFormatException e) {
						currentRowOut.createCell(i).setCellValue(currentRowIn[i]);
					}
				}

			}
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(this.filepath);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			workBook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toSpreadSheet(List<String[]> rows, boolean s) {

		XSSFWorkbook workBook = new XSSFWorkbook();
		String currentSheetName = "1";
		XSSFSheet sheet = workBook.createSheet(currentSheetName);

		Iterator<String[]> rowsIterator = rows.iterator();
		int rowNum = 0;
		while (rowsIterator.hasNext()) {

			String[] currentRowIn = rowsIterator.next();

			XSSFRow currentRowOut = sheet.createRow(rowNum++);

			for (int i = 0; i < currentRowIn.length; i++) {
				currentRowOut.createCell(i).setCellValue(currentRowIn[i]);
			}
		}

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(this.filepath);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			workBook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toSpreadSheet(List<String[]> rows) {

		XSSFWorkbook workBook = new XSSFWorkbook();
		String currentSheetName = "1";
		XSSFSheet sheet = workBook.createSheet(currentSheetName);

		Iterator<String[]> rowsIterator = rows.iterator();
		int rowNum = 0;
		while (rowsIterator.hasNext()) {

			String[] currentRowIn = rowsIterator.next();

			XSSFRow currentRowOut = sheet.createRow(rowNum++);

			for (int i = 0; i < currentRowIn.length; i++) {
				try {
					double d = Double.parseDouble(currentRowIn[i]);
					currentRowOut.createCell(i).setCellValue(d);
				} catch (Exception e) {
					currentRowOut.createCell(i).setCellValue(currentRowIn[i]);
				}
			}
		}

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(this.filepath);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			workBook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static XLS mergeSingleSheetSpreadsheet(String[] fileNames, String out) throws Exception {
		List<String[]> rows = new ArrayList<String[]>();
		for (int i = 0; i < fileNames.length; i++) {
			XLS current = new XLS(fileNames[i]);
			rows.addAll(current.getRowsOfSheet(0));
		}
		XLS res = new XLS(out);
		res.toSpreadSheet(rows);
		return res;
	}

}
