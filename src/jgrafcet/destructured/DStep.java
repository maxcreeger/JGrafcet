package jgrafcet.destructured;

import jgrafcet.engine.actions.IAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class DStep implements DVisitable {

	private final boolean initial;
	public final int num;
	private final IAction[] action;
	@Setter
	private boolean active;

	@Override
	public <T> T accept(DVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
