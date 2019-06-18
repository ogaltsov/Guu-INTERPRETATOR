package model.operator;

import lombok.Data;

import java.util.List;

@Data
public class SubOperator extends Operator {

	private String name;

	public SubOperator(int line, List<String> tokens) {
		super("sub", line, tokens);
	}


	@Override
	public void run() {
//		System.out.println(line+"| "+String.join(" ", tokens));
	}
}
