package jgrafcet.engine.transition.t2s;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jgrafcet.engine.Step;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleT2Step implements LinkT2S {

	private final List<LinkT2S> upstream = new ArrayList<>();
	private final Step downstream;

	public void addUpstreamLink(LinkT2S link) {
		upstream.add(link);
	}

	@Override
	public Set<Step> getActivables() {
		Set<Step> result = new HashSet<>();
		result.add(downstream);
		return result;
	}

}
