package jgrafcet.engine.transition.s2t;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jgrafcet.engine.Step;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleStep2T implements LinkS2T {

	private final Step upstream;
	private final List<LinkS2T> downstream = new ArrayList<>();

	public void addDownstreamLink(LinkS2T link) {
		downstream.add(link);
	}

	@Override
	public boolean isActive() {
		return upstream.isActive();
	}

	@Override
	public Set<Step> getDeactivables() {
		return upstream.getDeactivables();
	}

}
