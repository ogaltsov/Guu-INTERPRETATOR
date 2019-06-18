package model;

public enum DebugCommand {


	NEXT(1),
	IN(2),
	VARIABLES(3),
	STACKTRACE(4);

	private final int code;

	DebugCommand(int code) {
		this.code = code;
	}

	public static DebugCommand value(int code) {
		for (DebugCommand debugCommand: DebugCommand.values()) {
			if (debugCommand.code == code) {
				return debugCommand;
			}
		}
		throw new IllegalArgumentException();
	}
}
