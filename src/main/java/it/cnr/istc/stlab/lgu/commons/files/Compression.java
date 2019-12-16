package it.cnr.istc.stlab.lgu.commons.files;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class Compression {
	
	public static void printFirstLinesOfGZipFile(String file, int numberOfLines) throws IOException {
		InputStream fileStream = new FileInputStream(file);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		
		for(int i=0;i<numberOfLines;i++) {
			System.out.println(buffered.readLine());
		}
		buffered.close();
	}
	
	public static void main(String[] args) throws IOException {
		printFirstLinesOfGZipFile("/Users/lgu/Dropbox/Lavoro/Projects/Framester/f/Framester_v3/endpoint_v3/conceptnet/5.7.0/conceptnet-assertion-5.7.0.nq.gz", 100);
	}
	
	

}
