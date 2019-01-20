package jgrafcet.destructured;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jgrafcet.engine.actions.IAction;
import jgrafcet.engine.signal.AlwaysTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import parsing.Convergence;
import parsing.Divergence;
import parsing.Execute;
import parsing.Grafcet;
import parsing.Links;
import parsing.Step;
import parsing.Step2Transition;
import parsing.Steps;
import parsing.Transition;
import parsing.Transitions;
import parsing.Transtion2Step;

public class GrafcetReader {

	public static final class SpecialAction implements IAction {
		private final String msg;

		public SpecialAction(String msg) {
			this.msg = msg;
		}

		@Override
		public void perform() {
			System.out.println(msg);
		}
	}

	@AllArgsConstructor
	@Getter
	public static class GrafcetAndPainter {
		private final DGrafcet dGrafcet;
		private final DGrafcetPainter painter;
	}

	public static GrafcetAndPainter build(File file) throws JAXBException {
		Grafcet grafcetDAO = read(file);
		DGrafcet dGrafcet = new DGrafcet();
		DGrafcetPainter painter = new DGrafcetPainter(dGrafcet);

		// Build all steps
		List<DStep> dSteps = new ArrayList<>();
		Steps stepsDAO = grafcetDAO.getSteps();
		for (Step stepDAO : stepsDAO.getStep()) {

			List<String> allActions = stepDAO.getActions() == null ? new ArrayList<>() : stepDAO.getActions().getAction();
			IAction[] actionArray = new IAction[allActions.size()];
			for (int i = 0; i < allActions.size(); i++) {
				actionArray[i] = new SpecialAction("Performing " + allActions.get(i));
			}
			DStep dStep = new DStep(stepDAO.isInitial(), stepDAO.getNum(), actionArray);
			dSteps.add(dStep);
			dGrafcet.add(dStep);
			painter.setLocation(dStep, new Point(stepDAO.getX(), stepDAO.getY()));
		}

		// Build transitions
		List<DTransition> dTransitions = new ArrayList<>();
		Transitions transitionsDAO = grafcetDAO.getTransitions();
		for (Transition transitionDAO : transitionsDAO.getTransition()) {
			DTransition dTransition = new DTransition(transitionDAO.getNum(), new AlwaysTrue());
			dTransitions.add(dTransition);
			dGrafcet.add(dTransition);
			painter.setLocation(dTransition, new Point(transitionDAO.getX(), transitionDAO.getY()));
		}

		// Link
		Links linksDAO = grafcetDAO.getLinks();
		for (Object someLink : linksDAO.getStep2TransitionOrDivergenceOrConvergence()) {
			if (someLink instanceof Step2Transition) {
				Step2Transition step2transitionDAO = (Step2Transition) someLink;
				DStep dStep = dSteps.get(step2transitionDAO.getStep() - 1);
				DTransition dTransition = dTransitions.get(step2transitionDAO.getTransition() - 1);
				dGrafcet.link(dStep, dTransition);
			} else if (someLink instanceof Transtion2Step) {
				Transtion2Step transition2stepDAO = (Transtion2Step) someLink;
				DStep dStep = dSteps.get(transition2stepDAO.getStep() - 1);
				DTransition dTransition = dTransitions.get(transition2stepDAO.getTransition() - 1);
				dGrafcet.link(dTransition, dStep);
			} else if (someLink instanceof Divergence) {
				Divergence divergenceDAO = (Divergence) someLink;
				DTransition dTransition = dTransitions.get(divergenceDAO.getTransition() - 1);
				List<DStep> stepsToExecute = new ArrayList<>();
				for (Execute executeDAO : divergenceDAO.getExecute()) {
					DStep dStep = dSteps.get(executeDAO.getStep() - 1);
					stepsToExecute.add(dStep);
				}
				dGrafcet.executeAllSteps(dTransition, stepsToExecute.toArray(new DStep[stepsToExecute.size()]));
			} else if (someLink instanceof Convergence) {
				Convergence convergenceDAO = (Convergence) someLink;
				DTransition dTransition = dTransitions.get(convergenceDAO.getTransition() - 1);
				List<DStep> requiredSteps = new ArrayList<>();
				for (Execute executeDAO : convergenceDAO.getExecute()) {
					DStep dStep = dSteps.get(executeDAO.getStep() - 1);
					requiredSteps.add(dStep);
				}
				dGrafcet.requireAllSteps(dTransition, requiredSteps.toArray(new DStep[requiredSteps.size()]));
			}
		}

		return new GrafcetAndPainter(dGrafcet, painter);
	}

	protected static Grafcet read(File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(parsing.Grafcet.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Grafcet grafcetDAO = (Grafcet) unmarshaller.unmarshal(file);
		return grafcetDAO;
	}

}
