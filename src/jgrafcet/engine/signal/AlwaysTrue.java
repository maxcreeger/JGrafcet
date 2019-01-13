package jgrafcet.engine.signal;

public class AlwaysTrue implements ICondition {

	@Override
	public boolean evaluate() {
		return true;
	}

	@Override
	public String toString() {
		return "TRUE!";
	}

}
