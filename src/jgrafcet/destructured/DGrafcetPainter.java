package jgrafcet.destructured;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DGrafcetPainter {

	final Map<DStep, Point> stepLocation = new HashMap<>();
	final Map<DTransition, Point> transitionLocation = new HashMap<>();

	final int SCALE = 100;
	final int STEP_SIZE = (int) (SCALE * 0.6);
	final int STEP_OFFSET = (SCALE - STEP_SIZE) / 2;
	final int ANCHOR_SPREAD = (int) (SCALE * 0.3);
	final int TRAN_WIDTH = (int) (SCALE * 0.6);
	final int TRAN_HEIGHT = (int) (SCALE * 0.1);
	final int TRAN_X_OFFSET = (SCALE - TRAN_WIDTH) / 2;
	final int TRAN_Y_OFFSET = (SCALE - TRAN_HEIGHT) / 2;
	DGrafcet grafcet;

	public void setLocation(DStep step, Point point) {
		stepLocation.put(step, point);
	}

	public void setLocation(DTransition tran, Point point) {
		transitionLocation.put(tran, point);
	}

	private Point tranLoc(DTransition from) {
		return transitionLocation.get(from);
	}

	private Point stepLoc(DStep from) {
		return stepLocation.get(from);
	}

	public abstract class Anchor {
		public int position;

		public abstract Point getLocation();
	}

	@AllArgsConstructor
	public class AnchorLink {
		Anchor from;
		Anchor to;
	}

	public class StepInAnchor extends Anchor {
		DStep step;

		public StepInAnchor(DStep step) {
			super();
			this.step = step;
		}

		@Override
		public Point getLocation() {
			Point stepLoc = stepLoc(step);
			return new Point(stepLoc.x * SCALE + SCALE / 2 + position, stepLoc.y * SCALE + STEP_OFFSET);
		}
	}

	public class StepOutAnchor extends Anchor {
		DStep step;

		public StepOutAnchor(DStep step) {
			super();
			this.step = step;
		}

		@Override
		public Point getLocation() {
			Point stepLoc = stepLoc(step);
			return new Point(stepLoc.x * SCALE + SCALE / 2 + position, stepLoc.y * SCALE + SCALE - STEP_OFFSET);
		}
	}

	public class TranInAnchor extends Anchor {
		DTransition tran;

		public TranInAnchor(DTransition tran) {
			super();
			this.tran = tran;
		}

		@Override
		public Point getLocation() {
			Point tranLoc = tranLoc(tran);
			return new Point(tranLoc.x * SCALE + SCALE / 2 + position, tranLoc.y * SCALE + TRAN_Y_OFFSET);
		}
	}

	public class TranOutAnchor extends Anchor {
		DTransition tran;

		public TranOutAnchor(DTransition tran) {
			super();
			this.tran = tran;
		}

		@Override
		public Point getLocation() {
			Point tranLoc = tranLoc(tran);
			return new Point(tranLoc.x * SCALE + SCALE / 2 + position, tranLoc.y * SCALE + SCALE - TRAN_Y_OFFSET);
		}
	}

	public class MidAnchor extends Anchor {
		Point loc;

		@Override
		public Point getLocation() {
			return new Point(loc.x + SCALE / 2, loc.y + SCALE / 2);
		}

		public void setLocation(Point loc) {
			this.loc = loc;
		}
	}

	public void draw(Graphics g) {
		new GrafcetDrawing().draw(g);
	}

	private class GrafcetDrawing {

		Map<DStep, List<Anchor>> anchorsStepOut = new HashMap<>();
		Map<DStep, List<Anchor>> anchorsStepIn = new HashMap<>();
		Map<DTransition, List<Anchor>> anchorsTranOut = new HashMap<>();
		Map<DTransition, List<Anchor>> anchorsTranIn = new HashMap<>();
		List<AnchorLink> anchorLinks = new ArrayList<>();

		public GrafcetDrawing() {

			// Create anchors
			for (Entry<DStep, Set<DTransition>> links2t : grafcet.links2t.entrySet()) {
				DStep step = links2t.getKey();
				for (DTransition tran : links2t.getValue()) {
					Anchor stepOut = new StepOutAnchor(step);
					anchorsStepOut.putIfAbsent(step, new ArrayList<>());
					anchorsStepOut.get(step)
									.add(stepOut);
					Anchor tranIn = new TranInAnchor(tran);
					anchorsTranIn.putIfAbsent(tran, new ArrayList<>());
					anchorsTranIn.get(tran)
									.add(tranIn);
					anchorLinks.add(new AnchorLink(stepOut, tranIn));
				}
			}
			for (Entry<DTransition, Set<DStep>> linkt2s : grafcet.linkt2s.entrySet()) {
				for (DStep step : linkt2s.getValue()) {
					DTransition tran = linkt2s.getKey();
					Anchor tranOut = new TranOutAnchor(tran);
					anchorsTranOut.putIfAbsent(tran, new ArrayList<>());
					anchorsTranOut.get(tran)
									.add(tranOut);
					Anchor stepIn = new StepInAnchor(step);
					anchorsStepIn.putIfAbsent(step, new ArrayList<>());
					anchorsStepIn.get(step)
									.add(stepIn);
					anchorLinks.add(new AnchorLink(tranOut, stepIn));
				}
			}
			for (DConvergence conv : grafcet.convergences) {
				DTransition tran = grafcet.convergenceTriggerTransition.get(conv);
				Anchor tranIn = new TranInAnchor(tran);
				anchorsTranIn.putIfAbsent(tran, new ArrayList<>());
				anchorsTranIn.get(tran)
								.add(tranIn);
				MidAnchor join = new MidAnchor();
				anchorLinks.add(new AnchorLink(join, tranIn));
				Set<DStep> req = grafcet.convergenceRequiredSteps.get(conv);
				int cumulX = tranLoc(tran).x * req.size();
				int cumulY = tranLoc(tran).y * req.size();
				for (DStep step : req) {
					Anchor stepOut = new StepOutAnchor(step);
					anchorsStepOut.putIfAbsent(step, new ArrayList<>());
					anchorsStepOut.get(step)
									.add(stepOut);
					anchorLinks.add(new AnchorLink(stepOut, join));
					cumulX += stepLoc(step).x;
					cumulY += stepLoc(step).y;
				}
				join.setLocation(new Point((cumulX * SCALE) / (2 * req.size()), (cumulY * SCALE) / (2 * req.size())));
			}
			for (DDivergence div : grafcet.divergences) {
				DTransition tran = grafcet.divergenceTriggerTransition.get(div);
				Anchor tranOut = new TranOutAnchor(tran);
				anchorsTranOut.putIfAbsent(tran, new ArrayList<>());
				anchorsTranOut.get(tran)
								.add(tranOut);
				MidAnchor join = new MidAnchor();
				anchorLinks.add(new AnchorLink(tranOut, join));
				Set<DStep> exec = grafcet.divergenceExecutedSteps.get(div);
				int cumulX = tranLoc(tran).x * exec.size();
				int cumulY = tranLoc(tran).y * exec.size();
				for (DStep step : exec) {
					Anchor stepIn = new StepInAnchor(step);
					anchorsStepIn.putIfAbsent(step, new ArrayList<>());
					anchorsStepIn.get(step)
									.add(stepIn);
					anchorLinks.add(new AnchorLink(join, stepIn));
					cumulX += stepLoc(step).x;
					cumulY += stepLoc(step).y;
				}
				join.setLocation(new Point((cumulX * SCALE) / (2 * exec.size()), (cumulY * SCALE) / (2 * exec.size())));
			}

			// Spread the anchors apart
			for (List<Anchor> anchors : anchorsStepOut.values()) {
				int n = 0;
				for (Anchor anchor : anchors) {
					anchor.position = anchors.size() == 1 ? 0 : (ANCHOR_SPREAD * n++) / Math.max(1, anchors.size() - 1) - ANCHOR_SPREAD / 2;
				}
			}
			for (List<Anchor> anchors : anchorsStepIn.values()) {
				int n = 0;
				for (Anchor anchor : anchors) {
					anchor.position = anchors.size() == 1 ? 0 : (ANCHOR_SPREAD * n++) / Math.max(1, anchors.size() - 1) - ANCHOR_SPREAD / 2;
				}
			}
			for (List<Anchor> anchors : anchorsTranOut.values()) {
				int n = 0;
				for (Anchor anchor : anchors) {
					anchor.position = anchors.size() == 1 ? 0 : (ANCHOR_SPREAD * n++) / Math.max(1, anchors.size() - 1) - ANCHOR_SPREAD / 2;
				}
			}
			for (List<Anchor> anchors : anchorsTranIn.values()) {
				int n = 0;
				for (Anchor anchor : anchors) {
					anchor.position = anchors.size() == 1 ? 0 : (ANCHOR_SPREAD * n++) / Math.max(1, anchors.size() - 1) - ANCHOR_SPREAD / 2;
				}
			}
		}

		public void draw(Graphics g) {
			// Draw steps
			grafcet.steps.stream()
							.forEach(step -> drawStep(g, step));
			// draw transitions
			grafcet.transitions.stream()
								.forEach(tran -> drawTran(g, tran));

			// Draw links
			// Draw all anchors
			g.setColor(Color.CYAN);
			for (AnchorLink link : anchorLinks) {
				Point from = link.from.getLocation();
				Point to = link.to.getLocation();
				g.drawLine(from.x, from.y, to.x, to.y);
			}
			g.setColor(Color.black);
			for (Entry<DStep, Set<DTransition>> links2t : grafcet.links2t.entrySet()) {
				drawLinkS2T(links2t.getKey(), links2t.getValue(), g);
			}
			for (Entry<DTransition, Set<DStep>> linkt2s : grafcet.linkt2s.entrySet()) {
				drawLinkT2s(linkt2s.getKey(), linkt2s.getValue(), g);
			}
			/*
			for (DConvergence conv : grafcet.convergences) {
				Set<DStep> req = grafcet.convergenceRequiredSteps.get(conv);
				DTransition tran = grafcet.convergenceTriggerTransition.get(conv);
				drawConvergence(g, req, tran);
			}
			for (DDivergence div : grafcet.divergences) {
				DTransition tran = grafcet.divergenceTriggerTransition.get(div);
				Set<DStep> exec = grafcet.divergenceExecutedSteps.get(div);
				drawDivergence(g, tran, exec);
			}*/

		}

		public void drawStep(Graphics g, DStep step) {
			Point p = stepLoc(step);
			g.setColor(Color.white);
			g.drawRect(p.x * SCALE, p.y * SCALE, SCALE, SCALE);
			g.setColor(Color.black);
			g.drawRect(p.x * SCALE + STEP_OFFSET, p.y * SCALE + STEP_OFFSET, STEP_SIZE, STEP_SIZE);
			g.drawString(String.valueOf(step.getNum()), p.x * SCALE + SCALE / 2 - 3, p.y * SCALE + SCALE / 2 + 5);
		}

		public void drawTran(Graphics g, DTransition tran) {
			g.setColor(Color.black);
			Point p = tranLoc(tran);
			g.fillRect(p.x * SCALE + TRAN_X_OFFSET, p.y * SCALE + TRAN_Y_OFFSET, TRAN_WIDTH, TRAN_HEIGHT);
			g.drawString(String.valueOf(tran.getNum()), p.x * SCALE + TRAN_X_OFFSET + TRAN_WIDTH + 2, p.y * SCALE + TRAN_Y_OFFSET + 5);
		}

		private void drawLinkS2T(DStep step, Set<DTransition> transitions, Graphics g) {
			if (transitions.isEmpty()) {
				return;
			}
			g.setColor(Color.green);
			Point stepLoc = stepLoc(step);
			g.drawLine(stepLoc.x * SCALE + 40, stepLoc.y * SCALE + STEP_OFFSET + STEP_SIZE, stepLoc.x * SCALE + 40, stepLoc.y * SCALE + SCALE + 10);
			for (DTransition from : transitions) {
				Point tranLoc = tranLoc(from);
				g.drawLine(stepLoc.x * SCALE + 40, stepLoc.y * SCALE + 80, stepLoc.x * SCALE + 40, stepLoc.y * SCALE + SCALE + 10); // down
				g.drawLine(stepLoc.x * SCALE + 40, stepLoc.y * SCALE + SCALE + 10, tranLoc.x * SCALE + 40, stepLoc.y * SCALE + SCALE + 10); // sideways
				g.drawLine(tranLoc.x * SCALE + 40, stepLoc.y * SCALE + SCALE + 10, tranLoc.x * SCALE + 40, tranLoc.y * SCALE + 45); //down
			}
			g.setColor(Color.black);
		}

		private void drawConvergence(Graphics g, Set<DStep> req, DTransition tran) {
			if (req.isEmpty()) {
				return;
			}
			g.setColor(Color.blue);
			Point tranLoc = tranLoc(tran);
			Point xMin = req.stream()
							.map(stepLocation::get)
							.min((p1, p2) -> p1.x - p2.x)
							.orElse(tranLoc);
			Point xMax = req.stream()
							.map(stepLocation::get)
							.max((p1, p2) -> p1.x - p2.x)
							.orElse(tranLoc);
			for (DStep from : req) {
				Point stepLoc = stepLoc(from);
				g.drawLine(stepLoc.x * SCALE + 53, stepLoc.y * SCALE + 80, stepLoc.x * SCALE + 53, tranLoc.y * SCALE + 0);
			}
			g.drawLine(xMin.x * SCALE + 53, tranLoc.y * SCALE + 0, xMax.x * SCALE + 53, tranLoc.y * SCALE + 0);
			g.drawLine(xMin.x * SCALE + 53, tranLoc.y * SCALE + 4, xMax.x * SCALE + 53, tranLoc.y * SCALE + 4);
			g.drawLine(tranLoc.x * SCALE + 53, tranLoc.y * SCALE + 4, tranLoc.x * SCALE + 53, tranLoc.y * SCALE + 47);
			g.setColor(Color.black);
		}

		private void drawDivergence(Graphics g, DTransition tran, Set<DStep> exec) {
			if (exec.isEmpty()) {
				return;
			}
			g.setColor(Color.red);
			Point tranLoc = tranLoc(tran);
			Point xMin = exec.stream()
								.map(stepLocation::get)
								.min((p1, p2) -> p1.x - p2.x)
								.orElse(tranLoc);
			Point xMax = exec.stream()
								.map(stepLocation::get)
								.max((p1, p2) -> p1.x - p2.x)
								.orElse(tranLoc);
			g.drawLine(tranLoc.x * SCALE + 60, tranLoc.y * SCALE + 53, tranLoc.x * SCALE + 60, tranLoc.y * SCALE + 96);
			g.drawLine(xMin.x * SCALE + 60, tranLoc.y * SCALE + 96, xMax.x * SCALE + 60, tranLoc.y * SCALE + 96);
			g.drawLine(xMin.x * SCALE + 60, tranLoc.y * SCALE + 100, xMax.x * SCALE + 60, tranLoc.y * SCALE + 100);
			for (DStep executed : exec) {
				Point stepLoc = stepLoc(executed);
				g.drawLine(stepLoc.x * SCALE + 60, tranLoc.y * SCALE + 100, stepLoc.x * SCALE + 60, stepLoc.y * SCALE + 20);
			}
			g.setColor(Color.black);
		}
	}

	private void drawLinkT2s(DTransition tran, Set<DStep> steps, Graphics g) {
		if (steps.isEmpty()) {
			return;
		}
		g.setColor(Color.ORANGE);
		Point tranLoc = tranLoc(tran);
		Point xMin = steps.stream()
							.map(this::stepLoc)
							.min((p1, p2) -> p1.x - p2.x)
							.orElse(tranLoc);
		Point xMax = steps.stream()
							.map(this::stepLoc)
							.max((p1, p2) -> p1.x - p2.x)
							.orElse(tranLoc);
		// From Tansition to cell exit
		g.drawLine(tranLoc.x * SCALE + 47, tranLoc.y * SCALE + 53, tranLoc.x * SCALE + 47, tranLoc.y * SCALE + 100);
		// Horizontal split
		g.drawLine(xMin.x * SCALE + 47, tranLoc.y * SCALE + 100, xMax.x * SCALE + 47, tranLoc.y * SCALE + 100);
		for (DStep from : steps) {
			Point stepLoc = stepLoc(from);
			if (stepLoc.y < tranLoc.y) { // lift back up
				// Exit sideways
				g.drawLine(stepLoc.x * SCALE + 47, tranLoc.y * SCALE + 100, stepLoc.x * SCALE, tranLoc.y * SCALE + 100);
				// Climb back up
				g.drawLine(stepLoc.x * SCALE, tranLoc.y * SCALE + 100, stepLoc.x * SCALE, stepLoc.y * SCALE);
				// Go back to center
				g.drawLine(stepLoc.x * SCALE + 47, stepLoc.y * SCALE, stepLoc.x * SCALE, stepLoc.y * SCALE);
				// Line Down from split to Step
				g.drawLine(stepLoc.x * SCALE + 47, stepLoc.y * SCALE, stepLoc.x * SCALE + 47, stepLoc.y * SCALE + 20);
			} else {
				// Line Down from split to Step
				g.drawLine(stepLoc.x * SCALE + 47, tranLoc.y * SCALE + 100, stepLoc.x * SCALE + 47, stepLoc.y * SCALE + 20);
			}
		}
	}

}
