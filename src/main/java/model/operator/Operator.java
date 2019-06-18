package model.operator;

import config.Runnable;
import lombok.Data;

import java.util.List;

@Data
public abstract class Operator implements Runnable {
	private Integer line;
	private String type;
	private List<String> tokens;

	public Operator(String type, Integer line, List<String> tokens) {
		this.type = type;
		this.line = line;
		this.tokens = tokens;
	}
}
