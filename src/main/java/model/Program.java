package model;

import config.Runnable;
import lombok.Getter;
import model.procedure.Procedure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Program implements Runnable {


	@Getter
	private static Map<String, Procedure> procedures = new HashMap<>();
	@Getter
	private static Map<String, Integer> variables = new HashMap<>();
	@Getter
	private static LinkedList<String> stackTrace = new LinkedList<>();


	@Override
	public void run() {
		Program.getProcedures().get("main").run();
//		Procedure main = GuuInterpretatorApp.getProcedures().get("main");
	}

}
