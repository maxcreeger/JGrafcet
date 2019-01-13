package jgrafcet.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import jgrafcet.engine.Grafcet;
import jgrafcet.engine.Step;

public class GrafcetDisplay extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Grafcet grafcet;
	private static final int scale = 20;

	Map<Step, Point> stepsLocation = new HashMap<Step, Point>();

	public GrafcetDisplay(Grafcet grafcet) {
		super();
		this.grafcet = grafcet;
		this.setPreferredSize(new Dimension(800, 600));
		for (Step step : grafcet.getSteps()) {
			int x = (int) (Math.random() * 40);
			int y = (int) (Math.random() * 30);
			stepsLocation.put(step, new Point(x, y));
		}
	}

	public void setLocation(Step step, int i, int j) {
		stepsLocation.put(step, new Point(i, j));
	}
	/*

	@Override
	public void paint(Graphics g) {
		for (Entry<Step, Point> entry : stepsLocation.entrySet()) {
			if (entry.getKey()
						.isActive()) {
				g.setColor(Color.yellow);
				g.fillRect(scale * entry.getValue().x - scale / 2, scale * entry.getValue().y - scale / 2, scale, scale);
				g.setColor(Color.black);
			}
			g.drawRect(scale * entry.getValue().x - scale / 2, scale * entry.getValue().y - scale / 2, scale, scale);
			g.drawString(String.valueOf(entry.getKey()
												.getNum()), scale * entry.getValue().x, scale * entry.getValue().y);
		}
		TransitionPainter painter = new TransitionPainter(g);
		for (Transition entry : grafcet.getTransitions()) {
			entry.accept(painter);
		}
	}

	@AllArgsConstructor
	public class TransitionPainter implements TransitionVisitor<Void> {

		Graphics g;

		@Override
		public Void visit(AndConvergenceS2T andConv) {
			Set<Step> upSet = andConv.getUpstreamSteps();
			Step down = andConv.getDownstream();
			Point downLoc = stepsLocation.get(down);
			int xMin = scale * upSet.stream()
									.mapToInt(step -> stepsLocation.get(step).x)
									.min()
									.getAsInt();
			int xMax = scale * upSet.stream()
									.mapToInt(step -> stepsLocation.get(step).x)
									.max()
									.getAsInt();
			int xAvg = down == null ? (int) (scale * upSet.stream()
															.mapToInt(step -> stepsLocation.get(step).x)
															.average()
															.getAsDouble()) : scale * downLoc.x;
			int yMin = scale * upSet.stream()
									.mapToInt(step -> stepsLocation.get(step).y)
									.max()
									.getAsInt();
			int yMax = down == null ? scale * (upSet.stream()
													.mapToInt(step -> stepsLocation.get(step).y)
													.max()
													.getAsInt() + 2) : scale * downLoc.y;
			int yAvg = (yMin + yMax) / 2;
			g.drawLine(xAvg - scale / 4, yAvg + scale / 4, xAvg + scale / 4, yAvg + scale / 4); // transition itself
			g.drawString(String.valueOf(andConv.getNum()), xAvg, yAvg + scale / 4);
			g.drawLine(xMin - scale / 4, yAvg - scale / 4 - 2, xMax + scale / 4, yAvg - scale / 4 - 2); // horiz 1
			g.drawLine(xMin - scale / 4, yAvg - scale / 4 + 2, xMax + scale / 4, yAvg - scale / 4 + 2); // horiz 2
			for (Step up : upSet) {
				Point upLoc = stepsLocation.get(up);
				g.drawLine(scale * upLoc.x, scale * upLoc.y + scale / 2, scale * upLoc.x, yAvg + scale / 4 - 2); // lines from step to convergence line
			}
			if (down != null) {
				g.drawLine(scale * downLoc.x, scale * downLoc.y + scale / 2, xAvg, yAvg + scale / 4 + 2); // line from step to transition
			}
			return null;
		}

		@Override
		public Void visit(AndDivergenceT2S andDiv) {
			Collection<Step> downSet = andDiv.getDownstreamSteps();
			Step up = andDiv.getSource();
			Point upLoc = stepsLocation.get(up);
			int yMin = downSet.stream()
								.mapToInt(step -> stepsLocation.get(step).y)
								.min()
								.getAsInt();
			int yAvg = (int) ((scale * upLoc.getY() + scale * yMin) / 2); // height of split bar
			int xMin = scale * downSet.stream()
										.mapToInt(step -> stepsLocation.get(step).x)
										.min()
										.getAsInt();
			int xMax = scale * downSet.stream()
										.mapToInt(step -> stepsLocation.get(step).x)
										.max()
										.getAsInt();
			g.drawLine((int) (scale * upLoc.getX()) - scale / 4, yAvg - scale / 4, (int) (scale * upLoc.getX()) + scale / 4, yAvg - scale / 4); // transition itself
			g.drawString(String.valueOf(andDiv.getNum()), (int) (scale * upLoc.getX()), yAvg - scale / 4);
			g.drawLine((int) (scale * upLoc.getX()), (int) (scale * upLoc.getY()) + scale / 2, (int) (scale * upLoc.getX()), yAvg + scale / 4 - 2); // line from step to transition
			g.drawLine(xMin - scale / 4, yAvg + scale / 4 - 2, xMax + scale / 4, yAvg + scale / 4 - 2); // horiz 1
			g.drawLine(xMin - scale / 4, yAvg + scale / 4 + 2, xMax + scale / 4, yAvg + scale / 4 + 2); // horiz 2
			for (Step down : downSet) {
				Point downLoc = stepsLocation.get(down);
				g.drawLine(scale * downLoc.x, yAvg + scale / 4 + 2, scale * downLoc.x, scale * downLoc.y - scale / 2); // lines from split to substeps
			}
			return null;
		}

		@Override
		public Void visit(OrConvergence andConv) {
			Step down = andConv.getDownstream();
			Point downLoc = stepsLocation.get(down);
			Set<Step> upSet = andConv.getUpstreamSteps()
										.keySet();
			int yMin = upSet.stream()
							.mapToInt(step -> stepsLocation.get(step).y)
							.min()
							.getAsInt();
			int yAvg = (int) ((scale * stepsLocation.get(down)
													.getY() + scale * yMin) / 2);
			int xAvg = (int) (scale * upSet.stream()
											.mapToInt(step -> stepsLocation.get(step).x)
											.average()
											.getAsDouble());
			g.drawLine((int) (scale * downLoc.getX()), (int) (scale * downLoc.getY()), xAvg, yAvg);
			for (Step up : upSet) {
				Point upLoc = stepsLocation.get(up);
				g.drawLine(xAvg, yAvg, (int) (scale * upLoc.getX()), (int) (scale * upLoc.getY()));
			}
			return null;
		}

		@Override
		public Void visit(OrDivergence andDiv) {
			Collection<Step> downSet = andDiv.getDownstreamSteps()
												.values();
			Step up = andDiv.getSource();
			Point upLoc = stepsLocation.get(up);
			int yMin = downSet.stream()
								.mapToInt(step -> stepsLocation.get(step).y)
								.min()
								.getAsInt();
			int yAvg = (int) ((scale * upLoc.getY() + scale * yMin) / 2); // height of split bar
			int xMin = scale * downSet.stream()
										.mapToInt(step -> stepsLocation.get(step).x)
										.min()
										.getAsInt();
			int xMax = scale * downSet.stream()
										.mapToInt(step -> stepsLocation.get(step).x)
										.max()
										.getAsInt();
			g.drawLine((int) (scale * upLoc.getX()) - scale / 4, yAvg - scale / 4, (int) (scale * upLoc.getX()) + scale / 4, yAvg - scale / 4); // transition itself
			g.drawString(String.valueOf(andDiv.getNum()), (int) (scale * upLoc.getX()), yAvg - scale / 4);
			g.drawLine((int) (scale * upLoc.getX()), (int) (scale * upLoc.getY()) + scale / 2, (int) (scale * upLoc.getX()), yAvg + scale / 4); // line from step to transition
			g.drawLine(xMin, yAvg + scale / 4, xMax, yAvg + scale / 4); // horiz
			for (Step down : downSet) {
				Point downLoc = stepsLocation.get(down);
				g.drawLine(scale * downLoc.x, yAvg + scale / 4, scale * downLoc.x, scale * downLoc.y - scale / 2); // lines from split to substeps
			}
			return null;
		}

	}
	*/
}
