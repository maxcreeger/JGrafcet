package jgrafcet.engine.actions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoAction implements IAction {

	private final String name;

	@Override
	public void perform() {
		System.out.println("performing " + name);
	}

	@Override
	public String toString() {
		return "No Action!" + name;
	}

}
