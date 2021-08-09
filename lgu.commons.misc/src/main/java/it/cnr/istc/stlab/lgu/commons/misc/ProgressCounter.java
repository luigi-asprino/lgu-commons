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
	private long t0;
	private org.slf4j.Logger slf4jLogger;
	private boolean printRate;

	public ProgressCounter() {
		check = 10000;
		absolute = true;
		init();
	}

	public ProgressCounter(long until) {
		check = until / steps;
		if (check == 0) {
			check = 1;
			steps = 100 / until;
		}
		init();
	}

	public ProgressCounter setPrefix(String p) {
		this.prefix = p;
		return this;
	}

	public void setCheckpoint(long check) {
		if (check > 0) {
			this.check = check;
		}
	}

	private void init() {
		format = NumberFormat.getInstance();
		format.setGroupingUsed(true);
		t0 = System.currentTimeMillis();
	}

	public ProgressCounter setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public ProgressCounter setSLF4jLogger(org.slf4j.Logger logger) {
		this.slf4jLogger = logger;
		return this;
	}

	public long currentValue() {
		return progress.get();
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

		String mPrint = prefix == null ? m + " " + attachRate() : prefix + " " + m + " " + attachRate();

		if (logger != null) {
			logger.info(mPrint);
		} else if (slf4jLogger != null) {
			slf4jLogger.info(mPrint);
		} else {
			System.out.println(mPrint);
		}
	}

	public long value() {
		return progress.longValue();
	}

	public void setPrintRate(boolean printRate) {
		this.printRate = printRate;
	}

	private String attachRate() {
		if (printRate) {
			long t1 = System.currentTimeMillis();
			long elapsed = t1 - t0;
			double timePerItem = ((double) elapsed) / ((double) progress.longValue());
			return timePerItem + "ms per item";
		}
		return "";
	}

}
