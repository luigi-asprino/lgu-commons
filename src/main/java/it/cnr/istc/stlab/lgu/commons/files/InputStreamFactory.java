package it.cnr.istc.stlab.lgu.commons.files;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;

public class InputStreamFactory {

	private static final int BUFFER_SIZE = 64 * 1024;

	public static InputStream getInputStream(String file) throws CompressorException, IOException {
		String extension = FilenameUtils.getExtension(file);
		FileInputStream fin = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fin, BUFFER_SIZE);
		if (extension.equalsIgnoreCase("gz")) {
			return new GZIPInputStream(bis, BUFFER_SIZE);
		} else if (extension.equalsIgnoreCase("bz2")) {
			return new CompressorStreamFactory().createCompressorInputStream(bis);
		}
		return bis;
	}

	public static Stream<String> getInputLineStream(String file) throws CompressorException, IOException {
		InputStream is = getInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE);
		Stream<String> result = br.lines().onClose(() -> {
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return result.parallel();
	}

}
