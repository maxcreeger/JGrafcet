package jgrafcet.engine.transition.s2t;

import java.util.Set;

import jgrafcet.engine.Step;
import jgrafcet.engine.transition.Activable;

public interface LinkS2T extends Activable {

	public Set<Step> getDeactivables();

}
