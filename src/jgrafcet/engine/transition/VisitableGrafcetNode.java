package jgrafcet.engine.transition;


public interface VisitableGrafcetNode {

	<T> T accept(TransitionVisitor<T> visitor);
}