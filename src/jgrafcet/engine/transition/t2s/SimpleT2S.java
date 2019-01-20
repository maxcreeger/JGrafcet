package jgrafcet.engine.transition.t2s;

import java.util.Set;

import jgrafcet.engine.Step;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleT2S implements LinkT2S {

	private final LinkT2S upstream;
	private final LinkT2S downstream;

	@Override
	public Set<Step> getActivables() {
		return downstream.getActivables();
	}

}
