package it.cnr.istc.stlab.lgu.commons.logging;

public class Logger extends org.apache.log4j.Logger {

	private Logger(Class<?> c) {
		super(c.getName());
	}

	static public Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		return new Logger(clazz);
	}

	public void info(String m, Object... f) {
		super.info(String.format(m, f));
	}

	public void trace(String m, Object... f) {
		super.trace(String.format(m, f));
	}

}
