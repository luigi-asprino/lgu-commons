package it.cnr.istc.stlab.lgu.commons.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	private final static ObjectMapper mapper = new ObjectMapper(); // can use static singleton, inject: just make sure
																	// to reuse!

	public static ObjectMapper getObjectMapper() {
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper;
	}

}
