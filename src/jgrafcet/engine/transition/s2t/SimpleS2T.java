package jgrafcet.engine.transition.s2t;

import java.util.Set;

import jgrafcet.engine.Step;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleS2T implements LinkS2T {

	private final LinkS2T upstream;
	private final LinkS2T downstream;

	@Override
	public boolean isActive() {
		return upstream.isActive();
	}

	@Override
	public Set<Step> getDeactivables() {
		return upstream.getDeactivables();
	}
}
