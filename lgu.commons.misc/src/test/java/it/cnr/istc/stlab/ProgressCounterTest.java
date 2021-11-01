package it.cnr.istc.stlab ;

import org.junit.Test;

import it.cnr.istc.stlab.lgu.commons.misc.ProgressCounter;

public class ProgressCounterTest{
	
	@Test
	public void test1() {
		ProgressCounter pc = new ProgressCounter(0);
		pc.increase();
		pc.increase();
	}
}
