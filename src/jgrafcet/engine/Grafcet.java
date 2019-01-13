package jgrafcet.engine;

import java.util.HashSet;
import java.util.Set;

import jgrafcet.engine.transition.Fireable.TransitionOutcome;
import jgrafcet.engine.transition.Transition;
import jgrafcet.engine.transition.s2t.AndConvergenceS2T;
import jgrafcet.engine.transition.t2s.AndDivergenceT2S;
import lombok.Getter;

@Getter
public class Grafcet {

	private final Set<Step> steps = new HashSet<>();
	private final Set<Transition> transitions = new HashSet<>();
	private final Set<Step> active = new HashSet<>();
	private final Set<AndConvergenceS2T> convergences = new HashSet<>();
	private final Set<AndDivergenceT2S> divergences = new HashSet<>();

	public void add(Step... steps) {
		for (Step step : steps) {
			this.steps.add(step);
			if (step.isInitial()) {
				active.add(step);
			}
		}
	}

	public void add(Transition... transitions) {
		for (Transition transition : transitions) {
			this.transitions.add(transition);
		}
	}

	public void add(AndConvergenceS2T convergence) {
		convergences.add(convergence);
	}

	public void add(AndDivergenceT2S divergence) {
		divergences.add(divergence);
	}

	public void iterate() {
		// Execute active Steps operation
		for (Step step : active) {
			step.perform();
		}
		// Compute transitions
		TransitionOutcome outcome = TransitionOutcome.failed();
		for (Transition transition : transitions) {
			TransitionOutcome fireResult = transition.fire();
			if (fireResult.isHasFired()) {
				System.out.println("Firing transition " + transition.getNum() + "!");

				outcome = outcome.merge(fireResult);
			}
		}
		if (outcome.isHasFired()) {
			active.removeAll(outcome.deactivated);
			active.addAll(outcome.activated);
		}
		// turn on/off the relevant steps
		for (Step step : steps) {
			if (active.contains(step)) {
				step.setActive(true);
			} else {
				step.setActive(false);
			}
		}
	}
}
