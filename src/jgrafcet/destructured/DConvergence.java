package jgrafcet.destructured;

public class DConvergence {

	public <T> T accept(DVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
