package it.cnr.istc.stlab.lgu.commons.files;

import java.net.URL;

import org.apache.commons.io.FilenameUtils;

public class File extends java.io.File {

	public File(String pathname) {
		super(pathname);
		this.filepath = pathname;
		initFormatAndCompressionFormat();
	}

	public File(String pathname, URL url) {
		super(pathname);
		this.filepath = pathname;
		this.url = url;
		initFormatAndCompressionFormat();
	}

	private void initFormatAndCompressionFormat() {
		String f = filepath;
		if (FilenameUtils.isExtension(f, COMPRESSION_EXTENSIONS)) {
			if (FilenameUtils.getExtension(f).equalsIgnoreCase("bz2")) {
				compressionFormat = CompressionFormat.BZ2;
			} else if (FilenameUtils.getExtension(f).equalsIgnoreCase("gz")) {
				compressionFormat = CompressionFormat.GZ;
			}
			f = FilenameUtils.removeExtension(f);
		}

		if (FilenameUtils.getExtension(f).equalsIgnoreCase("ttl")) {
			format = Format.TTL;
		} else if (FilenameUtils.getExtension(f).equalsIgnoreCase("nt")) {
			format = Format.NT;
		} else if (FilenameUtils.getExtension(f).equalsIgnoreCase("nq")) {
			format = Format.NQ;
		}
	}

	private static final long serialVersionUID = 1L;
	private String filepath;
	private URL url;
	private Format format;
	private CompressionFormat compressionFormat;

	public enum Format {
		TTL, NT, NQ, GZ, BZ2
	}

	public enum CompressionFormat {
		GZ, BZ2, NO_COMPRESSION
	}

	public final static String[] COMPRESSION_EXTENSIONS = new String[] { "gz", "bz2" };

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public CompressionFormat getCompressionFormat() {
		return compressionFormat;
	}

	public void setCompressionFormat(CompressionFormat compressionFormat) {
		this.compressionFormat = compressionFormat;
	}

}
