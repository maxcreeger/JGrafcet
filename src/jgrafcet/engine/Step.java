package jgrafcet.engine;

import java.util.HashSet;
import java.util.Set;

import jgrafcet.engine.actions.IAction;
import jgrafcet.engine.transition.Activable;
import jgrafcet.engine.transition.s2t.LinkS2T;
import jgrafcet.engine.transition.s2t.SimpleStep2T;
import jgrafcet.engine.transition.t2s.LinkT2S;
import jgrafcet.engine.transition.t2s.SimpleT2Step;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Step implements Activable, LinkS2T, LinkT2S {

	private final boolean initial;
	@Setter
	private boolean active;
	public final int num;
	private final IAction action;
	SimpleT2Step upstream = new SimpleT2Step(this);
	SimpleStep2T downstream = new SimpleStep2T(this);

	public Step(int num, IAction action, boolean initial) {
		this.num = num;
		this.initial = initial;
		this.active = initial;
		this.action = action;
	}

	@Override
	public Set<Step> getActivables() {
		Set<Step> result = new HashSet<>();
		result.add(this);
		return result;
	}

	@Override
	public Set<Step> getDeactivables() {
		Set<Step> set = new HashSet<>();
		set.add(this);
		return set;
	}

	@Override
	public String toString() {
		return (initial ? "[Initial]" : "") + "Step " + num + " (" + action + ")" + (active ? " - active" : "");
	}

	public void perform() {
		if (active) {
			action.perform();
		}
	}

}
