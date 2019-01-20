package jgrafcet.engine.transition.s2t;

import java.util.Set;
import java.util.stream.Collectors;

import jgrafcet.engine.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AndConvergenceS2T implements LinkS2T {

	protected final Set<? extends LinkS2T> upstreamSteps;
	protected final LinkS2T downstream;

	/*
	    [upstream1] [upstream2] [upstream3]
	         |           |           |
	         +===========+===========+
	                     |
	                     + downstream Transition
	 */

	@Override
	public boolean isActive() {
		return !upstreamSteps.stream()
								.anyMatch(link -> !link.isActive());
	}

	@Override
	public Set<Step> getDeactivables() {
		return upstreamSteps.stream()
							.flatMap(link -> link.getDeactivables()
													.stream())
							.collect(Collectors.toSet());
	}

}
