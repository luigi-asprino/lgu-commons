package it.cnr.istc.stlab.lgu.commons.files;

import java.net.URL;

public class File extends java.io.File {

	public File(String pathname) {
		super(pathname);
		this.filepath = pathname;
	}

	public File(String pathname, URL url) {
		super(pathname);
		this.filepath = pathname;
		this.url = url;
	}

	private static final long serialVersionUID = 1L;
	private String filepath;
	private URL url;
	private Format format;
	private CompressionFormat compressionFormat;

	public enum Format {
		TTL, NT
	}

	public enum CompressionFormat {
		GZ, BZ2
	}

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
