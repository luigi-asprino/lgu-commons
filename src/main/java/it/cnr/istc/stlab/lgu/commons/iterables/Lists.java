package it.cnr.istc.stlab.lgu.commons.iterables;

import java.util.ArrayList;
import java.util.List;

public class Lists {

	/**
	 * Split a list provided as input into a number of equal sized chunks.
	 * 
	 * @param inputList the list to be split
	 * @param numberOfParts the number of chunks to create from 
	 * @return
	 */
	public static <T> List<List<T>> splitList(List<T> inputList, int numberOfParts) {
		List<List<T>> result = new ArrayList<>();
		int chunkSize = inputList.size() / numberOfParts;
		for (int i = 0; i < inputList.size(); i += chunkSize) {
			result.add(inputList.subList(i, Math.min(i + chunkSize, inputList.size())));
		}
		return result;
	}

}
