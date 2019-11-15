package it.cnr.istc.stlab.lgu.commons.iterations;

public class ProgressCounter {

	private long progress;
	private long steps = 10;
	private long check;

	public ProgressCounter(long until) {
		check = until / steps;
	}

	public void increase() {
		progress++;
		if (progress % check == 0) {
			long perc = (progress / check) * steps;
			if (perc == 100) {
				System.out.println(perc + "%");
			} else {
				System.out.print(perc + "% ");
			}
		}
	}

}
