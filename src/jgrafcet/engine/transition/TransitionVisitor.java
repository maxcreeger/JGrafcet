package jgrafcet.engine.transition;

import jgrafcet.engine.transition.s2t.AndConvergenceS2T;
import jgrafcet.engine.transition.s2t.LinkS2T;
import jgrafcet.engine.transition.t2s.AndDivergenceT2S;
import jgrafcet.engine.transition.t2s.LinkT2S;

public interface TransitionVisitor<T> {

	T visit(AndConvergenceS2T andConv);

	T visit(Transition transition);

	T visit(AndDivergenceT2S andDiv);

	T visit(LinkS2T s2t);

	T visit(LinkT2S t2s);
}