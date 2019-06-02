import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuuInterp implements Runnable {

	static Map<String, Procedure> procedures = new HashMap<>();
	static Map<String, Integer> variables = new HashMap<>();
	static LinkedList<String> stackTrace = new LinkedList<>();

	public static void main(String[] args) {
		GuuInterp program = new GuuInterp();

		List<String> lists = new ArrayList<>();

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/ogaltsov/IdeaProjects/untitled/src/main/resources/code"))) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lists.add(line);
			}


		} catch (IOException ex) {
			ex.printStackTrace();
		}

		//достаем токены

		final List<String> tokens = lists.stream()
				.flatMap(line -> Stream.of(line.split("\\s+")))
				.filter(line -> !line.isBlank())
				.collect(Collectors.toList());

		//группируем токены по процедурам

		Set<List<String>> procedures = parseProcedures(tokens);

		//сгрупировываем по по операторам

		//sub name/oper/structure

		List<Procedure> collect = procedures.stream().map(procedure -> {
			Set<List<String>> operators = parseOperators(procedure);

			List<List<String>> opers = new ArrayList<>(operators);
			return createProcedure(opers);

		}).collect(Collectors.toList());

		////////////////////////////////////////////////////////////////

		collect.forEach(p -> program.procedures.put(p.name, p));

		program.run();



		System.out.println();

	}

	private static Set<List<String>> parseProcedures(List<String> tokens) {
		Set<List<String>> procedures = new LinkedHashSet<>();

		List<String> temp = new ArrayList<>();

		for (String token : tokens) {
			if (token.equalsIgnoreCase("sub")) {
				if (!temp.isEmpty()) {
					procedures.add(temp);
					temp = new ArrayList<>();
					temp.add(token);
				} else {
					temp.add(token);
				}
			} else {
				temp.add(token);
			}
		}
		if (!temp.isEmpty()) {
			procedures.add(temp);
		}
		return procedures;
	}

	private static Set<List<String>> parseOperators(List<String> tokens) {
		Set<List<String>> operators = new LinkedHashSet<>();

		List<String> temp = new ArrayList<>();

		for (String token : tokens) {
			if (token.equalsIgnoreCase("sub")
					|| token.equalsIgnoreCase("cal")
					|| token.equalsIgnoreCase("print")
					|| token.equalsIgnoreCase("set")) {
				if (!temp.isEmpty()) {
					operators.add(temp);
					temp = new ArrayList<>();
					temp.add(token);
				} else {
					temp.add(token);
				}
			} else {
				temp.add(token);
			}
		}
		if (!temp.isEmpty()) {
			operators.add(temp);
		}
		return operators;


	}

	private static Procedure createProcedure(List<List<String>> operators) {
		Procedure procedure = new Procedure();


		List<String> header = operators.get(0);
		if ("sub".equalsIgnoreCase(header.get(0))) {
			if (header.size() > 2) {
				throw new IllegalStateException();
			}
			procedure.setName(header.get(1));
		} else {
			throw new IllegalStateException();
		}


		for (List<String> next: operators.subList(1, operators.size())) {
			Operator operator = createOperator(next.get(0), next);
			procedure.getOperators().add(operator);
		}
		return procedure;
	}

	//
	private static Operator createOperator(String name, List<String> tokens) {
		Operator operator;

		switch (name.toLowerCase()) {
			case "cal": {
				CalOperator op = new CalOperator();
				if (tokens.isEmpty() || tokens.size() > 2) {
					throw new IllegalStateException();
				}
				op.functionName = tokens.get(1);
				operator = op;
				break;
			}
			case "set": {
				SetOperator op = new SetOperator();
				if (tokens.isEmpty() || tokens.size() > 3) {
					throw new IllegalStateException();
				}
				try {
					op.var = tokens.get(1);
					op.value = Integer.parseInt(tokens.get(2));
					operator = op;
				} catch (NumberFormatException e) {
					throw new IllegalStateException();
				}
				break;
			}
			case "print": {
				PrintOperator op = new PrintOperator();
				if (tokens.isEmpty() || tokens.size() > 2) {
					throw new IllegalStateException();
				}
				op.var = tokens.get(1);
				operator = op;
				break;
			}
			default:
				throw new IllegalStateException();
		}
		return operator;
	}

	@Override
	public void run() {
		procedures.get("main").run();
	}
}

class Procedure implements Runnable{
	String name;
	List<Operator> operators = new ArrayList<>();

	public void setName(String name) {
		this.name = name;
	}

	public List<Operator> getOperators() {
		return operators;
	}

	@Override
	public void run() {
		GuuInterp.stackTrace.add(name);
		operators.forEach(Operator::run);
		GuuInterp.stackTrace.removeLast();
	}
}

interface Runnable {
	void run();
}

abstract class Operator implements Runnable {
	Integer line;
	String type;

	public Operator(String type, Integer line) {
		this.type = type;
		this.line = line;
	}


}

class CalOperator extends Operator {
	List<String> tokens = new ArrayList<>();
	String functionName;

	public CalOperator() {
		super("cal", 0);
	}

	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public void run() {

		Procedure procedure = GuuInterp.procedures.get(functionName);
		if (procedure == null) {
			throw new IllegalStateException();
		}
		procedure.run();
	}
}

class PrintOperator extends Operator {
	List<String> tokens = new ArrayList<>();
	String var;

	public PrintOperator() {
		super("print", 0);
	}

	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public void run() {
		System.out.println(GuuInterp.variables.get(var));
	}
}

class SetOperator extends Operator {
	List<String> tokens = new ArrayList<>();
	String var;
	Integer value;

	public SetOperator() {
		super("print", 0);
	}

	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public void run() {
		GuuInterp.variables.put(var, value);
	}
}


