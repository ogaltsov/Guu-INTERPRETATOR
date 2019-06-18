package config;

import exception.CompilationException;
import lombok.Getter;
import model.operator.CalOperator;
import model.operator.Operator;
import model.operator.OperatorType;
import model.operator.PrintOperator;
import model.operator.SetOperator;
import model.operator.SubOperator;
import model.procedure.Procedure;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GuuInterpretatorApp implements Runnable {

	@Getter
	private static Map<String, Procedure> procedures = new HashMap<>();
	@Getter
	private static Map<String, Integer> variables = new HashMap<>();
	@Getter
	private static LinkedList<String> stackTrace = new LinkedList<>();



	public static void main(String[] args) throws CompilationException {

		GuuInterpretatorApp program = new GuuInterpretatorApp();


		//читаем файл по строкам
		//line per operator
		final Map<Integer, String> lines = program.readCodeFromFile(args[0]);

		//достаем токены
		Map<Integer, List<String>> tokensByNum = lines.entrySet().stream()
				.filter(e -> StringUtils.isNotBlank(e.getValue()))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> Arrays.asList(e.getValue().split("\\s+")),
						(o, n) -> o,
						LinkedHashMap::new
				));


		program.initialValidation(tokensByNum);

		//группируем токены по процедурам
		List<List<Operator>> listsOperators = program.parseOperators(tokensByNum);

		List<Procedure> procedureList = listsOperators.stream()
				.map(program::createProcedure)
				.collect(Collectors.toList());

		//упаковываем процедуры
		procedureList.forEach(p -> GuuInterpretatorApp.getProcedures().put(p.getName(), p));

		//запускаем
		program.run();


		System.out.println();

	}

	private Map<Integer, String> readCodeFromFile(String path) {
		Map<Integer, String> lists = new LinkedHashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

			String line;
			int lineCount = 0;

			while ((line = bufferedReader.readLine()) != null) {
				lists.put(lineCount++, line);
			}

		} catch (IOException ex) {
			throw new IllegalStateException();
		}
		return lists;
	}

	private void initialValidation(Map<Integer, List<String>> lines) throws CompilationException {
		//проверка первой строки
		Map.Entry<Integer, List<String>> firstEntry = lines.entrySet().stream().findFirst().orElseThrow();

		if (firstEntry.getValue().size() < 1 || OperatorType.SUB != OperatorType.value(firstEntry.getValue().get(0))) {
			throw new CompilationException(String.format("%s| %s  - must be sub", firstEntry.getKey(), String.join(" ", firstEntry.getValue())));
		}

		for (Map.Entry<Integer, List<String>> tokens : lines.entrySet()) {
			if (tokens.getValue().size() > 3 || tokens.getValue().size() < 2) {
				throw new CompilationException(String.format("%s| %s  - error", tokens.getKey(), String.join(" ", tokens.getValue())));
			}
		}
}

	private List<List<Operator>> parseOperators(Map<Integer, List<String>> tokensByLine) {

		List<List<Operator>> listOper = new ArrayList<>();

		List<Operator> temp = new ArrayList<>();

		for (Map.Entry<Integer, List<String>> entryToken: tokensByLine.entrySet()) {
			 List<String> operatorsToken = entryToken.getValue();

			if (operatorsToken.size() == 0 || operatorsToken.get(0).isEmpty() || operatorsToken.get(0).matches("\\s+")) {
				continue;
			}

			Operator operator = createOperator(operatorsToken.get(0), operatorsToken, entryToken.getKey());

			if (OperatorType.SUB == OperatorType.value(operator.getType())) {
				if (temp.size() == 0) {
					temp.add(operator);
				} else {
					listOper.add(temp);
					temp = new ArrayList<>();
					temp.add(operator);
				}
			} else {
				temp.add(operator);
			}
		}

		if (temp.size() != 0) {
			listOper.add(temp);
		}
		return listOper;
	}


	private Procedure createProcedure(List<Operator> operators) {
		Procedure procedure = new Procedure();

		Operator header = operators.get(0);

		if (OperatorType.SUB == OperatorType.value(header.getType())) {
			procedure.setName(((SubOperator) header).getName());
		} else {
			throw new IllegalStateException();
		}

		procedure.getOperators().addAll(operators.subList(1, operators.size()));

		return procedure;
	}


	private Operator createOperator(String name, List<String> tokens, int line) {
		Operator operator;

		switch (OperatorType.value(name)) {
			case CAL: {
				CalOperator op = new CalOperator(line, tokens);
				if (tokens.isEmpty() || tokens.size() > OperatorType.CAL.getSize()) {
					throw new IllegalStateException();
				}
				op.setFunctionName(tokens.get(1));
				operator = op;
				break;
			}
			case SET: {
				SetOperator op = new SetOperator(line, tokens);
				if (tokens.isEmpty() || tokens.size() > OperatorType.SET.getSize()) {
					throw new IllegalStateException();
				}
				try {
					op.setVar(tokens.get(1));
					op.setValue(Integer.parseInt(tokens.get(2)));
					operator = op;
				} catch (NumberFormatException e) {
					throw new IllegalStateException();
				}
				break;
			}
			case PRINT: {
				PrintOperator op = new PrintOperator(line, tokens);
				if (tokens.isEmpty() || tokens.size() > OperatorType.PRINT.getSize()) {
					throw new IllegalStateException();
				}
				op.setVar(tokens.get(1));
				operator = op;
				break;
			}
			case SUB: {
				SubOperator op = new SubOperator(line, tokens);
				if (tokens.isEmpty() || tokens.size() > OperatorType.SUB.getSize()) {
					throw new IllegalStateException();
				}
				op.setName(tokens.get(1));
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
		GuuInterpretatorApp.getProcedures().get("main").run();
	}
}








