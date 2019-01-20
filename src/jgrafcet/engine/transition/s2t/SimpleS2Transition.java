package jgrafcet.engine.transition.s2t;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jgrafcet.engine.Step;
import jgrafcet.engine.transition.Transition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleS2Transition implements LinkS2T {

	private final List<LinkS2T> upstream = new ArrayList<>();
	private final Transition downstream;

	public void addUpstreamLink(LinkS2T link) {
		upstream.add(link);
	}

	@Override
	public boolean isActive() {
		return upstream.stream()
						.anyMatch(LinkS2T::isActive);
	}

	@Override
	public Set<Step> getDeactivables() {
		return upstream.stream()
						.flatMap(link -> link.getDeactivables()
												.stream())
						.collect(Collectors.toSet());
	}

}
