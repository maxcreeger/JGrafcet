package jgrafcet.destructured;

public class DDivergence implements DVisitable {

	@Override
	public <T> T accept(DVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
