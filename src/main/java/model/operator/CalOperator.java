package model.operator;

import config.GuuInterpretatorApp;
import lombok.Data;
import model.Program;
import model.procedure.Procedure;

import java.util.List;

@Data
public class CalOperator extends Operator {

	private String functionName;

	public CalOperator(int line, List<String> tokens) {
		super("cal", line, tokens);
	}


	@Override
	public void run() {

		Procedure procedure = Program.getProcedures().get(functionName);
		if (procedure == null) {
			throw new IllegalStateException();
		}
//		System.out.println(line+"| "+String.join(" ", tokens));
		procedure.run();
	}
}
