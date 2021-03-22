package it.cnr.istc.stlab.lgu.commons.semanticweb.files;

import java.io.File;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

public class SWFileUtils {

	public static final String[] QUAD_EXTENSIONS = { "nq" };

	public static final String[] TRIPLE_EXTENSIONS = { "rdf", "ttl", "owl", "nt" };

	public static final String[] ZIP_EXTENSIONS = { "gz", "bz2" };

	public enum Format {
		TTL, NT, NQ, GZ, BZ2
	}

	public enum CompressionFormat {
		GZ, BZ2, NO_COMPRESSION
	}

	public static boolean isRDFExension(String file) {

		return isTripleExension(file) || isQuadExtension(file);
	}

	public static boolean isTripleExension(String file) {

		if (FilenameUtils.isExtension(file, ZIP_EXTENSIONS)) {
			return FilenameUtils.isExtension(FilenameUtils.getBaseName(file), TRIPLE_EXTENSIONS);

		}

		return FilenameUtils.isExtension(file, TRIPLE_EXTENSIONS);

	}

	public static boolean isQuadExtension(String file) {

		if (FilenameUtils.isExtension(file, ZIP_EXTENSIONS)) {
			return FilenameUtils.isExtension(FilenameUtils.getBaseName(file), QUAD_EXTENSIONS);

		}

		return FilenameUtils.isExtension(file, QUAD_EXTENSIONS);

	}

	public static boolean isRDFExension(File file) {
		return isRDFExension(file.getAbsolutePath());
	}

	public static boolean isRDFExension(Path file) {
		return isRDFExension(file.toFile().getAbsolutePath());
	}

	public static Format getSWFormat(String filename) {
		String f = filename;
		if (FilenameUtils.isExtension(f, ZIP_EXTENSIONS)) {
			f = FilenameUtils.removeExtension(f);
		}
		if (FilenameUtils.getExtension(f).equalsIgnoreCase("ttl")) {
			return Format.TTL;
		} else if (FilenameUtils.getExtension(f).equalsIgnoreCase("nt")) {
			return Format.NT;
		} else if (FilenameUtils.getExtension(f).equalsIgnoreCase("nq")) {
			return Format.NQ;
		}
		return null;
	}

}
