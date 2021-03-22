package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnSelector {

	private static Logger logger = LoggerFactory.getLogger(ColumnSelector.class);

	public static void main(String[] args) throws IOException {

		divide("/Users/lgu/Desktop/FDistinctions/classinstance_features.tsv", "/Users/lgu/Desktop/FDistinctions/",
				new String[] { "class", "instance" }, 1, new int[] { 0, 1, 9 }, true);
		divide("/Users/lgu/Desktop/FDistinctions/physicalObject_features.tsv", "/Users/lgu/Desktop/FDistinctions/",
				new String[] { "non_physical_object", "physical_object" }, 1, new int[] { 0, 1, 9 }, true);

	}

	public static void divide(String inputFile, String outputFolder, String[] classes, int classColumn,
			int[] colsToCopy, boolean skipFirst) throws IOException {

		Map<String, FileOutputStream> fosMap = new HashMap<>();
		for (int i = 0; i < classes.length; i++) {
			fosMap.put(classes[i], new FileOutputStream(
					new File(outputFolder + "/" + FilenameUtils.getBaseName(inputFile) + "_" + classes[i] + ".tsv")));
		}

		BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)));
		if (skipFirst) {
			br.readLine();
		}
		int lineNumber = 0;
		String line;
		while ((line = br.readLine()) != null) {

			if (lineNumber > 0 && lineNumber % 1000 == 0) {
				logger.info("Line number {}", lineNumber);
			}
			String[] row = line.split("\t");
			for (int i = 0; i < colsToCopy.length - 1; i++) {
				if (colsToCopy[i] < row.length)
					fosMap.get(row[classColumn]).write(row[colsToCopy[i]].getBytes());
				fosMap.get(row[classColumn]).write("\t".getBytes());
			}
			if (colsToCopy[colsToCopy.length - 1] < row.length)
				fosMap.get(row[classColumn]).write(row[colsToCopy[colsToCopy.length - 1]].getBytes());
			fosMap.get(row[classColumn]).write("\n".getBytes());
		}
		br.close();
		for (int i = 0; i < classes.length; i++) {
			fosMap.get(classes[i]).close();
		}
	}

	public static void pickerRandom(String inputFile, int numberOfLines) {

	}

}
