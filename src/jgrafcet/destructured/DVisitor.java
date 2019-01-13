package jgrafcet.destructured;

public interface DVisitor<T> {

	T visit(DGrafcet grafcet);

	T visit(DStep step);

	T visit(DTransition tran);

	T visit(DDivergence div);

	T visit(DConvergence conv);
}
