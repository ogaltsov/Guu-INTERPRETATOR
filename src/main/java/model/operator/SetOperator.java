package model.operator;

import config.GuuInterpretatorApp;
import lombok.Data;
import model.Program;

import java.util.List;

@Data
public class SetOperator extends Operator {

	private String var;
	private Integer value;

	public SetOperator(int line, List<String> tokens) {
		super("print", line, tokens);
	}


	@Override
	public void run() {
//		System.out.println(line+"| "+String.join(" ", tokens));
		Program.getVariables().put(var, value);
	}
}
