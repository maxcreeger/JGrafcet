package jgrafcet.destructured;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DGrafcet implements DVisitable {

	final Set<DStep> steps = new HashSet<>();
	final Set<DTransition> transitions = new HashSet<>();
	final Set<DConvergence> convergences = new HashSet<>();
	final Set<DDivergence> divergences = new HashSet<>();
	final Map<DConvergence, Set<DStep>> convergenceRequiredSteps = new HashMap<>();
	final Map<DConvergence, DTransition> convergenceTriggerTransition = new HashMap<>();
	final Map<DDivergence, Set<DStep>> divergenceExecutedSteps = new HashMap<>();
	final Map<DDivergence, DTransition> divergenceTriggerTransition = new HashMap<>();
	final Map<DStep, Set<DTransition>> links2t = new HashMap<>();
	final Map<DTransition, Set<DStep>> linkt2s = new HashMap<>();

	public void add(DStep step) {
		steps.add(step);
		links2t.put(step, new HashSet<>());
	}

	public void add(DTransition transition) {
		transitions.add(transition);
		linkt2s.put(transition, new HashSet<>());
	}

	public void link(DStep step, DTransition transition) {
		links2t.get(step)
				.add(transition);
	}

	public void link(DTransition transition, DStep step) {
		linkt2s.get(transition)
				.add(step);
	}

	public void requireAllSteps(DTransition transition, DStep... steps) {
		DConvergence logicGate = new DConvergence();
		Set<DStep> set = new HashSet<>();
		for (DStep step : steps) {
			set.add(step);
		}
		convergences.add(logicGate);
		convergenceTriggerTransition.put(logicGate, transition);
		convergenceRequiredSteps.put(logicGate, set);
	}

	public void executeAllSteps(DTransition transition, DStep... steps) {
		DDivergence logicGate = new DDivergence();
		Set<DStep> set = new HashSet<>();
		for (DStep step : steps) {
			set.add(step);
		}
		divergences.add(logicGate);
		divergenceTriggerTransition.put(logicGate, transition);
		divergenceExecutedSteps.put(logicGate, set);
	}

	@Override
	public <T> T accept(DVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public Set<DStep> getParentSteps(DConvergence conv) {
		return convergenceRequiredSteps.get(conv);
	}

}
