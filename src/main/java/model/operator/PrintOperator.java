package model.operator;

import config.GuuInterpretatorApp;
import lombok.Data;

import java.util.List;

@Data
public class PrintOperator extends Operator {

	private String var;

	public PrintOperator(int line, List<String> tokens) {
		super("print", line, tokens);
	}


	@Override
	public void run() {
		System.out.println(GuuInterpretatorApp.getVariables().get(var));
//		System.out.println(line+"| "+String.join(" ", tokens));

	}
}