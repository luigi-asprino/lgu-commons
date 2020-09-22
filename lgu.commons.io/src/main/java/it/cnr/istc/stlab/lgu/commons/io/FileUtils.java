package it.cnr.istc.stlab.lgu.commons.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static long countNumberOfLines(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		long result = br.lines().count();
		br.close();
		return result;
	}

}
