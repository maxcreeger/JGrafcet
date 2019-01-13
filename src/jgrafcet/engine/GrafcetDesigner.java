package jgrafcet.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jgrafcet.engine.actions.IAction;
import jgrafcet.engine.signal.ICondition;
import jgrafcet.engine.transition.Transition;
import jgrafcet.engine.transition.s2t.AndConvergenceS2T;
import jgrafcet.engine.transition.s2t.SimpleS2T;
import jgrafcet.engine.transition.s2t.SimpleS2Transition;
import jgrafcet.engine.transition.s2t.SimpleStep2T;
import jgrafcet.engine.transition.t2s.AndDivergenceT2S;
import jgrafcet.engine.transition.t2s.SimpleT2Step;
import jgrafcet.engine.transition.t2s.SimpleTransition2S;
import lombok.AllArgsConstructor;

public class GrafcetDesigner {

	Map<Integer, StepBlueprint> steps = new HashMap<>();
	Map<Integer, TransitionBlueprint> transitions = new HashMap<>();
	Set<ConvergenceBlueprint> convergences = new HashSet<>();
	Set<DivergenceBlueprint> divergences = new HashSet<>();

	// Set Steps

	public StepBuilderSetAction newStep(int num) {
		return new StepBuilderSetAction(num, false);
	}

	public StepBuilderSetAction newInitialStep(int num) {
		return new StepBuilderSetAction(num, true);
	}

	@AllArgsConstructor
	public class StepBuilderSetAction {
		private final int num;
		private final boolean initial;

		public StepBlueprint withAction(IAction action) {
			if (steps.containsKey(num)) {
				throw new RuntimeException("The Step " + num + " is already defined");
			}
			StepBlueprint step = new StepBlueprint(num, action, initial);
			steps.put(num, step);
			return step;
		}
	}

	@AllArgsConstructor
	public class StepBlueprint {
		private final int num;
		private final IAction action;
		private final boolean initial;

		private Step make() {
			return new Step(num, action, initial);
		}
	}

	// Set transitions

	public TransitionBuilderSetAction newTransition(int num) {
		return new TransitionBuilderSetAction(num);
	}

	@AllArgsConstructor
	public class TransitionBuilderSetAction {

		private final int num;

		public TransitionBlueprint withCondition(ICondition cond) {
			if (transitions.containsKey(num)) {
				throw new RuntimeException("The Transition " + num + " is already defined");
			}
			TransitionBlueprint tran = new TransitionBlueprint(num, cond);
			transitions.put(num, tran);
			return tran;
		}
	}

	@AllArgsConstructor
	public class TransitionBlueprint {
		private final int num;
		private final ICondition condition;

		private Transition make() {
			return new Transition(num, condition);
		}
	}

	// Add simple links

	Set<LinkBlueprint> links = new HashSet<>();

	public StepLinkBlueprint linkStep(StepBlueprint step) {
		return new StepLinkBlueprint(step);
	}

	@AllArgsConstructor
	public class StepLinkBlueprint {
		StepBlueprint step;

		public void to(TransitionBlueprint transition) {
			links.add(new LinkBlueprint(step, transition));
		}
	}

	@AllArgsConstructor
	public class LinkBlueprint {
		StepBlueprint from;
		TransitionBlueprint to;
	}

	// Set divergence synchronization

	public DivergenceBlueprintInput whenTransitionFires(TransitionBlueprint tran) {
		return new DivergenceBlueprintInput(tran);
	}

	@AllArgsConstructor
	public class DivergenceBlueprintInput {
		TransitionBlueprint tran;

		public void activateAll(StepBlueprint... steps) {
			DivergenceBlueprint div = new DivergenceBlueprint(tran, steps);
			divergences.add(div);
		}
	}

	@AllArgsConstructor
	public class DivergenceBlueprint {
		TransitionBlueprint transition;
		StepBlueprint[] steps;
	}

	// Build convergence Synchronization

	public ConvergenceBlueprintInput clearAllSteps(StepBlueprint... steps) {
		return new ConvergenceBlueprintInput(steps);
	}

	@AllArgsConstructor
	public class ConvergenceBlueprintInput {
		StepBlueprint[] steps;

		public void whenFires(TransitionBlueprint transition) {
			ConvergenceBlueprint conv = new ConvergenceBlueprint(steps, transition);
			convergences.add(conv);
		}
	}

	@AllArgsConstructor
	public class ConvergenceBlueprint {
		StepBlueprint[] steps;
		TransitionBlueprint transition;
	}

	// Build

	public Grafcet build() {
		// Build steps & transitions
		Map<StepBlueprint, Step> actualSteps = new HashMap<>();
		Map<TransitionBlueprint, Transition> actualTransitions = new HashMap<>();
		Grafcet grafcet = new Grafcet();
		for (StepBlueprint stepBP : steps.values()) {
			Step step = stepBP.make();
			actualSteps.put(stepBP, step);
			grafcet.add(step);
		}

		for (TransitionBlueprint tranBP : transitions.values()) {
			Transition tran = tranBP.make();
			actualTransitions.put(tranBP, tran);
			grafcet.add(tran);
		}

		// Build Conv & Div
		for (ConvergenceBlueprint convBP : convergences) {
			Set<SimpleStep2T> upstreamS = Stream.of(convBP.steps)
												.map(stepBP -> actualSteps.get(stepBP).downstream)
												.collect(Collectors.toSet());
			SimpleS2Transition downstreamT = actualTransitions.get(convBP.transition).upstream;
			AndConvergenceS2T convergence = new AndConvergenceS2T(upstreamS, downstreamT);
			downstreamT.addUpstreamLink(downstreamT);
			upstreamS.stream()
						.forEach(link -> link.addDownstreamLink(convergence));
			grafcet.add(convergence);
		}
		for (DivergenceBlueprint divBP : divergences) {
			SimpleTransition2S upstreamT = actualTransitions.get(divBP.transition).downstream;
			Set<SimpleT2Step> downstreamS = Stream.of(divBP.steps)
													.map(stepBP -> actualSteps.get(stepBP).upstream)
													.collect(Collectors.toSet());
			AndDivergenceT2S divergence = new AndDivergenceT2S(upstreamT, downstreamS);
			upstreamT.addDownstreamLink(divergence);
			downstreamS.stream()
						.forEach(link -> link.addUpstreamLink(divergence));
			grafcet.add(divergence);
		}

		// Build links
		for (LinkBlueprint linkBP : links) {
			Step step = actualSteps.get(linkBP.from);
			Transition transition = actualTransitions.get(linkBP.to);
			SimpleS2T link = new SimpleS2T(step.downstream, transition.upstream);
			step.downstream.addDownstreamLink(link);
			transition.upstream.addUpstreamLink(link);
		}
		return grafcet;
	}
}
