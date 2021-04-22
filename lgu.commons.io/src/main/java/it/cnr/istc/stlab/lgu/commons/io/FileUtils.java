package it.cnr.istc.stlab.lgu.commons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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

}
