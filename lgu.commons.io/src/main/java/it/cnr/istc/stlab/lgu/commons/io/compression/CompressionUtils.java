package it.cnr.istc.stlab.lgu.commons.io.compression;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorException;

import it.cnr.istc.stlab.lgu.commons.io.streams.InputStreamFactory;

public class CompressionUtils {

	public static void printFirstLinesOfGZipFile(String file, int numberOfLines)
			throws IOException, CompressorException {
		Reader decoder = new InputStreamReader(InputStreamFactory.getInputStream(file));
		BufferedReader buffered = new BufferedReader(decoder);
		readFirstLinesOfBufferedReader(buffered, numberOfLines);
		buffered.close();
	}

	public static void printFirstLinesOfBZ2File(String file, int numberOfLines)
			throws IOException, CompressorException {
		BufferedReader br2 = new BufferedReader(new InputStreamReader(InputStreamFactory.getInputStream(file)));
		readFirstLinesOfBufferedReader(br2, numberOfLines);
		br2.close();
	}

	public static void readFirstLinesOfBufferedReader(BufferedReader br, int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++) {
			System.out.println(br.readLine());
		}
	}

	public static byte[] compressString(String s) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(baos);

		zos.write(s.getBytes());
		zos.finish();
		zos.flush();

		return baos.toByteArray();
	}

}