package it.cnr.istc.stlab.lgu.commons.iterations;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public class ProgressCounter {

	private AtomicLong progress = new AtomicLong(0L);
	private long steps = 10;
	private long check;
	private boolean absolute = false;
	private NumberFormat format;
	private Logger logger;

	public ProgressCounter() {
		check = 10000;
		absolute = true;
		init();
	}

	public ProgressCounter(long until) {
		check = until / steps;
		init();
	}

	private void init() {
		format = NumberFormat.getInstance();
		format.setGroupingUsed(true);
	}

	public ProgressCounter setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public void increase() {
		if (progress.incrementAndGet() % check == 0) {
			if (!absolute) {
				long perc = (progress.longValue() / check) * steps;
				if (perc == 100) {
					logMessage(perc + "%");
				} else {
					logMessage(perc + "% ");
				}
			} else {
				logMessage(format.format(progress) + " ");
			}
		}
	}

	private void logMessage(String m) {
		if (logger != null) {
			logger.info(m);
		} else {
			System.out.println(m);
		}
	}

}
