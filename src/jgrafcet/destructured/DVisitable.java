package jgrafcet.destructured;

public interface DVisitable {
	<T> T accept(DVisitor<T> visitor);
}
