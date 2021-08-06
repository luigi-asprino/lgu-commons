package it.cnr.istc.stlab.lgu.commons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

public class FileUtils {

	public enum Format {
		TTL, NT, NQ, GZ, BZ2, UNKNOWN
	}

	public enum CompressionFormat {
		GZ, BZ2, NO_COMPRESSION
	}

	public final static String[] COMPRESSION_EXTENSIONS = new String[] { "gz", "bz2" };

	public static CompressionFormat getCompressionFormat(String file) {
		if (FilenameUtils.isExtension(file, COMPRESSION_EXTENSIONS)) {
			if (FilenameUtils.getExtension(file).equalsIgnoreCase("bz2")) {
				return CompressionFormat.BZ2;
			} else if (FilenameUtils.getExtension(file).equalsIgnoreCase("gz")) {
				return CompressionFormat.GZ;
			}
		}
		return CompressionFormat.NO_COMPRESSION;
	}

	public static Format getFormat(String file) {

		if (FilenameUtils.getExtension(file).equalsIgnoreCase("ttl")) {
			return Format.TTL;
		} else if (FilenameUtils.getExtension(file).equalsIgnoreCase("nt")) {
			return Format.NT;
		} else if (FilenameUtils.getExtension(file).equalsIgnoreCase("nq")) {
			return Format.NQ;
		}
		return Format.UNKNOWN;
	}

	public static long countNumberOfLines(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		long result = br.lines().count();
		br.close();
		return result;
	}

	public static long countNumberOfLines(Reader r) throws IOException {
		BufferedReader br = new BufferedReader(r);
		long result = br.lines().count();
		br.close();
		return result;
	}

	public static String readFile(String filename) {
		return readFile(filename, false);
	}

	public static String readFile(String filename, boolean keepCR) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				if (keepCR)
					sb.append('\n');
				line = br.readLine();

			}
			result = sb.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<String> readFileToList(String filename) {
		List<String> result = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String toTextFile(String input, String filePath) throws IOException {

		File f = new File(filePath);
		Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f));

		outputStreamWriter.write(input);
		outputStreamWriter.flush();
		outputStreamWriter.close();

		return f.getAbsolutePath();

	}

	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}

	public static List<String> getFilesUnderTreeRec(String filePath) {
		List<String> result = new ArrayList<String>();

		File f = new File(filePath);
		for (File child : f.listFiles()) {
			if (child.isDirectory()) {
				result.addAll(getFilesUnderTreeRec(child.getAbsolutePath()));
			} else {
				result.add(child.getAbsolutePath());
			}
		}

		return result;
	}

	public static List<String> readFileToListString(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			List<String> result = br.lines().collect(Collectors.toList());
			br.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
