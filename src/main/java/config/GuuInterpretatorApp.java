package config;


import exception.CompilationException;
import model.Program;
import model.procedure.Procedure;
import service.DebugService;
import service.ProgramInterpretatorService;

import java.util.Map;

public class GuuInterpretatorApp {

	public static void main(String[] args) throws CompilationException {

		ProgramInterpretatorService interpretatorService = new ProgramInterpretatorService();

		interpretatorService.compileProgram(args[0]);

		Map<String, Procedure> procedureMap = Program.getProcedures();

		Procedure mainProcedure = procedureMap.get("main");

		execute(args[1], mainProcedure);
	}

	public static void execute(String executeMode, Procedure mainProcedure) {
		if ("debug".equalsIgnoreCase(executeMode)) {
			DebugService debugService = new DebugService();
			debugService.debugProcedure(mainProcedure);
		} else {
			mainProcedure.run();
		}
	}


}








