package it.cnr.istc.stlab.lgu.commons.tables;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

public class RowsIteratorGoogleSheets implements RowsIterator {

	private static Logger logger = LoggerFactory.getLogger(RowsIterator.class);

	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public Iterator<List<Object>> iterator;

	public RowsIteratorGoogleSheets(String spreadsheetId, String range) throws GeneralSecurityException, IOException {

		logger.trace("Creating new rows iterator for: {} {}", spreadsheetId, range);

		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleUtils.getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();

		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		iterator = response.getValues().iterator();

	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String[] next() {
		List<Object> rowList = iterator.next();
		String[] result = new String[rowList.size()];
		for (int i = 0; i < rowList.size(); i++) {
			result[i] = rowList.get(i).toString();
		}
		return result;
	}

}
