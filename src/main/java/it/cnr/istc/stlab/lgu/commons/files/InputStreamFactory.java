package it.cnr.istc.stlab.lgu.commons.files;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;

public class InputStreamFactory {

	public static InputStream getInputStream(String file) throws CompressorException, IOException {
		String extension = FilenameUtils.getExtension(file);
		FileInputStream fin = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fin);
		if (extension.equalsIgnoreCase("gz")) {
			return new GZIPInputStream(bis);
		} else if (extension.equalsIgnoreCase("bz2")) {
			return new CompressorStreamFactory().createCompressorInputStream(bis);
		}
		return bis;
	}

}
