package it.cnr.istc.stlab.lgu.commons.misc;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public class ProgressCounter {

	private AtomicLong progress = new AtomicLong(0L);
	private long steps = 10;
	private long check;
	private boolean absolute = false;
	private String prefix;
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

	public ProgressCounter setPrefix(String p) {
		this.prefix = p;
		return this;
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

		String mPrint = prefix == null ? m : prefix + " " + m;

		if (logger != null) {
			logger.info(mPrint);
		} else {
			System.out.println(mPrint);
		}
	}
	
	public long value() {
		return progress.longValue();
	}

}
