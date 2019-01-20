package jgrafcet.engine.transition;

import java.util.HashSet;
import java.util.Set;

import jgrafcet.engine.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface Fireable {

	TransitionOutcome fire();

	static final TransitionOutcome FAILED_TRANSITION = new TransitionOutcome(false, null, null);

	@AllArgsConstructor
	@Getter
	public static class TransitionOutcome {
		boolean hasFired;
		public final Set<Step> activated;
		public final Set<Step> deactivated;

		public static TransitionOutcome failed() {
			return FAILED_TRANSITION;
		}

		public TransitionOutcome merge(TransitionOutcome other) {
			if (other.hasFired) {
				if (this.hasFired) {
					Set<Step> activated = new HashSet<>();
					Set<Step> deactivated = new HashSet<>();
					activated.addAll(this.activated);
					activated.addAll(other.activated);
					deactivated.addAll(this.deactivated);
					deactivated.addAll(other.deactivated);
					deactivated.removeAll(activated); // if a step becomes activated and also deactivated, then itstays activated
					return new TransitionOutcome(true, activated, deactivated);
				} else {
					return other;
				}
			} else {
				return this;
			}
		}
	}

}
