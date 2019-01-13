package jgrafcet.engine.transition.t2s;

import java.util.Set;
import java.util.stream.Collectors;

import jgrafcet.engine.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AndDivergenceT2S implements LinkT2S {

	protected final LinkT2S source;
	protected final Set<? extends LinkT2S> downstreamSteps;

	/*
	                     + downstream Transition
	                     | 
	         +===========+===========+
	         |           |           |
	    [upstream1] [upstream2] [upstream3]
	    
	 */

	@Override
	public Set<Step> getActivables() {
		return downstreamSteps.stream()
								.flatMap(link -> link.getActivables()
														.stream())
								.collect(Collectors.toSet());
	}

}
