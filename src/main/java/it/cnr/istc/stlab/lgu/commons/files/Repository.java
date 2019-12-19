package it.cnr.istc.stlab.lgu.commons.files;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository {

	private static Logger logger = LoggerFactory.getLogger(Repository.class);

	private String folder;
	private List<File> files;

	public Repository(String folder, List<File> files) {
		super();
		this.folder = folder;
		this.files = files;
	}

	public void synchronize() throws IOException {
		logger.info("Synchronizing repository");
		File folderFile = new File(folder);
		folderFile.mkdirs();
		Set<String> filenames = new HashSet<>();
		for (java.io.File f : folderFile.listFiles()) {
			filenames.add(f.getAbsolutePath());
		}

		for (File f : files) {
			/* TODO
			 * 
			 * check last remote last update 
			 * check file dimension
			 * 
			 */
			if (!filenames.contains(f.getAbsolutePath())) {
				logger.info("Downloading file from {}", f.getUrl().toString());
				FileUtils.copyURLToFile(f.getUrl(), f);
			} else {
				logger.info("{} already downloaded!", f.getUrl().toString());
			}
		}
		logger.info("End");
	}

	public static List<File> createListOfFilesFromURLs(List<String> urls, String baseFolder)
			throws MalformedURLException {
		List<File> result = new ArrayList<>();
		for (String url : urls) {
			URL u = new URL(url);
			String filename = FilenameUtils.getName(u.getFile());
			result.add(new File(baseFolder + "/" + filename, u));
		}
		return result;
	}

	public static void main(String[] args) throws IOException {

		String f = "/Users/lgu/Desktop/csk_repo";
		List<File> files = createListOfFilesFromURLs(
				it.cnr.istc.stlab.lgu.commons.files.FileUtils.readFileToListString(f + "/links"), f);
		Repository r = new Repository(f, files);
		r.synchronize();

	}

}
