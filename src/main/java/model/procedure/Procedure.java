package model.procedure;

import config.Runnable;
import lombok.Data;
import model.Program;
import model.operator.Operator;

import java.util.ArrayList;
import java.util.List;

@Data
public class Procedure implements Runnable {

	private String name;
	private List<Operator> operators = new ArrayList<>();

	public void setName(String name) {
		this.name = name;
	}

	public List<Operator> getOperators() {
		return operators;
	}


	@Override
	public void run() {
		Program.getStackTrace().add(name);
		operators.forEach(Operator::run);
		Program.getStackTrace().removeLast();
	}
}
