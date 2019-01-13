package jgrafcet.engine.transition.t2s;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jgrafcet.engine.Step;
import jgrafcet.engine.transition.Transition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleTransition2S implements LinkT2S {

	private final Transition upstream;
	private final List<LinkT2S> downstream = new ArrayList<>();

	public void addDownstreamLink(LinkT2S link) {
		downstream.add(link);
	}

	@Override
	public Set<Step> getActivables() {
		return downstream.stream()
							.flatMap(link -> link.getActivables()
													.stream())
							.collect(Collectors.toSet());
	}

}
