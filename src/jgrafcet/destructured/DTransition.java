package jgrafcet.destructured;

import jgrafcet.engine.signal.ICondition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DTransition implements DVisitable {

	@Getter
	private final int num;
	public final ICondition condition;

	@Override
	public <T> T accept(DVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
