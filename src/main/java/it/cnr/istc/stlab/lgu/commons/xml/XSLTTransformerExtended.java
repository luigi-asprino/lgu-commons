package it.cnr.istc.stlab.lgu.commons.xml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.io.Files;

public class XSLTTransformerExtended {

	// Usage
	// arg[0] folder or file input
	// arg[1] output folder
	// arg[2-args.length-1] transformations

	public static void main(String[] args) throws IOException, URISyntaxException, TransformerException {

		String input = args[0];
		String output_folder = args[1];

		String[] transform = new String[args.length - 2];
		for (int i = 2; i < args.length; i++) {
			transform[i - 2] = args[i];
		}

		File input_file = new File(input);

		new File(output_folder).mkdirs();
		List<String> fileInputToProcess = new ArrayList<>();
		if (input_file.isDirectory()) {
			for (File f : input_file.listFiles()) {
				if (FilenameUtils.getExtension(f.getAbsolutePath()).matches("rdf|xml")) {
					fileInputToProcess.add(f.getAbsolutePath());
				}
			}
			FileUtils.copyDirectory(input_file, new File(output_folder + "/input"));
		} else {
			fileInputToProcess.add(input);
			Files.copy(input_file, new File(output_folder + "/" + input_file.getName()));
		}

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer[] transformers = new Transformer[transform.length];

		for (int i = 0; i < transformers.length; i++) {
			Source xslt = new StreamSource(new File(transform[i]));
			transformers[i] = factory.newTransformer(xslt);
			transformers[i].setOutputProperty(OutputKeys.INDENT, "yes");
			transformers[i].setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			new File(output_folder + "/" + FilenameUtils.getBaseName(transform[i])).mkdirs();
		}

		new File(output_folder + "/xlst").mkdirs();

		for (int i = 0; i < transformers.length; i++) {
			Transformer transformer = transformers[i];
			for (String f : fileInputToProcess) {
				String outFile = output_folder + "/" + FilenameUtils.getBaseName(transform[i]) + "/"
						+ FilenameUtils.getBaseName(f) + ".xml";
				System.out.println("IN: " + f);
				System.out.println("OUT: " + outFile);
				try {
					Source text = new StreamSource(new File(f));
					transformer.transform(text, new StreamResult(new File(outFile)));
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			Files.copy(new File(transform[i]),
					new File(output_folder + "/xlst/" + FilenameUtils.getName(transform[i])));
		}

	}

}
