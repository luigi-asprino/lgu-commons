package it.cnr.istc.stlab.lgu.commons.server;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	public static final String BASE_URI = "http://localhost:8080/";

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static HttpServer startServer() {
		final ResourceConfig rc = new ResourceConfig().packages("it.cnr.istc.stlab.lgu.commons.server");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	public static void main(String[] args) throws IOException {
		final HttpServer server = startServer();
		logger.info("Jersey app started with WADL available at {} ", BASE_URI);
		logger.info("Hit enter to stop it");
		System.in.read();
		server.shutdown();
	}
}
