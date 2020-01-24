package it.cnr.istc.stlab.lgu.commons.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessUtils {

	public static void executeCommand(String[] command) throws IOException, InterruptedException {
		executeCommand(String.join(" ", command));
	}

	public static void executeCommand(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);

		Runnable rerr = () -> {
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String buff = null;
			try {
				Thread.sleep(1000);
				while ((buff = err.readLine()) != null) {
					System.err.println(buff);
					Thread.sleep(100);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		};

		Runnable rin = () -> {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String buff = null;
			try {
				Thread.sleep(1000);
				while ((buff = in.readLine()) != null) {
					System.out.println(buff);
					Thread.sleep(100);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		};

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(rin);
		executorService.execute(rerr);
		Field f;
		try {
			f = p.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			System.out.println("PID: " + f.get(p));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		p.waitFor();
		System.out.println("Done");
		// FIXME Do not wait the termination of the process and its children this seems
		// to be needed for short processes only
		// OR there is no problem and it is only the delayed loggers that make believe
		// that the process is still running (TODO CHECK)
		executorService.shutdown();
	}

}
