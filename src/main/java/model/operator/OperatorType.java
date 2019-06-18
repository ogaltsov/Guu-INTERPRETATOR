package model.operator;

public enum OperatorType {

	SUB("sub", 2),
	CAL("cal", 2),
	PRINT("print", 2),
	SET("set", 3),
	UNKNOWN("unknown", 0);

	private final String name;
	private final int size;

	OperatorType(String name, int size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public static OperatorType value(String name) {
		OperatorType operatorType;
		try {
			operatorType = OperatorType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			operatorType = OperatorType.UNKNOWN;
		}
		return operatorType;
	}
}
