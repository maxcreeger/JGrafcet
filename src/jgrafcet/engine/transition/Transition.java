package jgrafcet.engine.transition;

import java.util.Set;

import jgrafcet.engine.Step;
import jgrafcet.engine.signal.ICondition;
import jgrafcet.engine.transition.s2t.SimpleS2Transition;
import jgrafcet.engine.transition.t2s.SimpleTransition2S;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Transition implements Fireable, VisitableGrafcetNode {

	@Getter
	private final int num;
	public final SimpleS2Transition upstream = new SimpleS2Transition(this);
	public final ICondition condition;
	public final SimpleTransition2S downstream = new SimpleTransition2S(this);

	@Override
	public TransitionOutcome fire() {
		if (upstream.isActive() && condition.evaluate()) {
			Set<Step> deactivatedSteps = upstream.getDeactivables();
			Set<Step> activatedSteps = downstream.getActivables();
			TransitionOutcome result = new TransitionOutcome(true, activatedSteps, deactivatedSteps);
			result.merge(result);
			return result;
		}
		return TransitionOutcome.failed();
	}

	@Override
	public <T> T accept(TransitionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Transition " + num + " (" + condition + ")";
	}
}
