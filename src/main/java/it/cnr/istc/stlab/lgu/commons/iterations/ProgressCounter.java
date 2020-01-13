package it.cnr.istc.stlab.lgu.commons.iterations;

import java.util.concurrent.atomic.AtomicLong;

public class ProgressCounter {

	private AtomicLong progress = new AtomicLong(0L);
	private long steps = 10;
	private long check;
	private boolean absolute = false;

	public ProgressCounter() {
		check = 10000;
		absolute = true;
	}

	public ProgressCounter(long until) {
		check = until / steps;
	}

	public void increase() {
		if (progress.incrementAndGet() % check == 0) {
			if (!absolute) {
				long perc = (progress.longValue() / check) * steps;
				if (perc == 100) {
					System.out.println(perc + "%");
				} else {
					System.out.print(perc + "% ");
				}
			} else {
				System.out.println(progress + " ");
			}
		}
	}

}
