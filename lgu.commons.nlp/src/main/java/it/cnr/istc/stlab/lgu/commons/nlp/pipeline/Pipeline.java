package it.cnr.istc.stlab.lgu.commons.nlp.pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jsoniter.output.JsonStream;

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;

public interface Pipeline<T> {

	public void annotate(T obj);


	public default void annotate(T obj, String fileOut) {
		annotate(obj, fileOut, false);
	}

	public default void annotate(T obj, String fileOut, boolean prettyPrint) {
		@SuppressWarnings("deprecation")
		ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		annotate(obj);
		try {
			FastBufferedOutputStream fbos = new FastBufferedOutputStream(new FileOutputStream(new File(fileOut)));
			if (!prettyPrint) {
				JsonStream.serialize(obj, fbos);
			} else {
				fbos.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj));
			}
			fbos.flush();
			fbos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
