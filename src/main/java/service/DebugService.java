package service;

import model.DebugCommand;
import model.Program;
import model.operator.CalOperator;
import model.operator.Operator;
import model.procedure.Procedure;

import java.util.Iterator;
import java.util.Scanner;

public class DebugService {

	public static final String CODE_LINE_FORMAT = "[%s | %s]";

	public void debugProcedure(Procedure procedure) {
		Program.getStackTrace().add(procedure.getName());

		Iterator<Operator> operatorsIndex = procedure.getOperators().iterator();

		Operator tempOperator;

		if (operatorsIndex.hasNext()) {
			Operator operator = operatorsIndex.next();
			tempOperator = operator;
			System.out.println(String.format(CODE_LINE_FORMAT, operator.getLine(), String.join(" ", operator.getTokens())));
		} else {
			return;
		}


		boolean switcher = true;
		while (switcher) {
			switch (readInput()) {
				case IN: {

					if (tempOperator instanceof CalOperator) {

						Procedure linkedProcedure = Program.getProcedures().get(((CalOperator) tempOperator).getFunctionName());
						debugProcedure(linkedProcedure);
						break;
					}
				}
				case NEXT: {
					tempOperator.run();
					if (operatorsIndex.hasNext()) {
						tempOperator = operatorsIndex.next();
					} else {
						switcher = false;
						break;
					}
					System.out.println(String.format(CODE_LINE_FORMAT, tempOperator.getLine(), String.join(" ", tempOperator.getTokens())));
					break;
				}
				case VARIABLES: {
					System.out.println(Program.getVariables());
					System.out.println(String.format(CODE_LINE_FORMAT, tempOperator.getLine(), String.join(" ", tempOperator.getTokens())));
					break;
				}
				case STACKTRACE: {
					System.out.println(String.join("->", Program.getStackTrace()));
					System.out.println(String.format(CODE_LINE_FORMAT, tempOperator.getLine(), String.join(" ", tempOperator.getTokens())));
					break;
				}

			}
		}
		Program.getStackTrace().removeLast();
	}

	public DebugCommand readInput() {

		Scanner scanner = new Scanner(System.in);

		System.out.println(
				"=====================================================\n" +
				"| Write command\n" +
				"=====================================================\n" +
				"| 1 - next | 2 - in | 3 -variables | 4 - stacktrace |\n" +
				"=====================================================\n"
		);
		int command = scanner.nextInt();

		if (command < 1 || command > 4) {
			throw new IllegalArgumentException();
		}

		return DebugCommand.value(command);
	}
}
