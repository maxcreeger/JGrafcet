package test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import jgrafcet.destructured.DGrafcet;
import jgrafcet.destructured.DGrafcetPainter;
import jgrafcet.destructured.DStep;
import jgrafcet.destructured.DTransition;
import jgrafcet.engine.Grafcet;
import jgrafcet.engine.GrafcetDesigner;
import jgrafcet.engine.GrafcetDesigner.StepBlueprint;
import jgrafcet.engine.GrafcetDesigner.TransitionBlueprint;
import jgrafcet.engine.actions.IAction;
import jgrafcet.engine.actions.NoAction;
import jgrafcet.engine.signal.AlwaysTrue;
import parsing.GrafcetReader;
import parsing.GrafcetReader.GrafcetAndPainter;

public class TestGrafcet {

	public static void main(String[] arg) throws InterruptedException, JAXBException {
		testLoadXML();
		//testOrDivergence();
		//testDestructured();
	}

	public static void testLoadXML() throws JAXBException, InterruptedException {
		GrafcetAndPainter bundle = GrafcetReader.build(new File("F:\\workspace\\JGrafcet\\resources\\test\\testGrafcet.xml"));
		displayInFrame(bundle.getPainter());
	}

	public static void testDestructured() throws InterruptedException {
		DGrafcet grafcet = new DGrafcet();
		DStep step1 = new DStep(true, 1, new IAction[] { new NoAction("action1") });
		DStep step2 = new DStep(true, 2, new IAction[] { new NoAction("action2") });
		DStep step3 = new DStep(true, 3, new IAction[] { new NoAction("action3") });
		DStep step4 = new DStep(true, 4, new IAction[] { new NoAction("action4") });
		grafcet.add(step1);
		grafcet.add(step2);
		grafcet.add(step3);
		grafcet.add(step4);
		DTransition tran1 = new DTransition(1, new AlwaysTrue());
		DTransition tran2 = new DTransition(2, new AlwaysTrue());
		DTransition tran3 = new DTransition(3, new AlwaysTrue());
		grafcet.add(tran1);
		grafcet.add(tran2);
		grafcet.add(tran3);
		grafcet.link(step1, tran1);
		grafcet.executeAllSteps(tran1, step2, step3, step4);
		grafcet.link(step2, tran2);
		grafcet.requireAllSteps(tran3, step2, step3, step4);
		grafcet.link(tran2, step1);

		//locations
		DGrafcetPainter painter = new DGrafcetPainter(grafcet);
		painter.setLocation(step1, new Point(1, 0));
		painter.setLocation(tran1, new Point(1, 1));
		painter.setLocation(step2, new Point(1, 2));
		painter.setLocation(step3, new Point(2, 2));
		painter.setLocation(step4, new Point(3, 2));
		painter.setLocation(tran2, new Point(1, 3));
		painter.setLocation(tran3, new Point(2, 3));

		displayInFrame(painter);
	}

	private static void displayInFrame(DGrafcetPainter painter) throws InterruptedException {
		// Make frame
		JFrame frame = new JFrame("test");
		JPanel display = new JPanel() {

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				painter.draw(g);
			}
		};
		display.setPreferredSize(new Dimension(800, 600));
		frame.add(display);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		while (true) {
			System.out.println("----------------------------------");
			Thread.sleep(5000);
			//	grafcet.iterate();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					frame.repaint();
				}
			});
		}
	}

	/*
	  _____
	 /     \
	 |     |
	 |    [1]
	 |     |
	 |    -+- 1
	 |     |
	 |    ===============
	 |     |     |     |
	 |    [2]   [3]   [4]
	 |     |     |     |
	/|\    +     |     |
	 |     |\    |     |
	 |     | \   |     |
	 |     |  \  |     |
	 |     |   \ |     |
	 |     |   ==========
	 |    -+-2     |
	 |     |       |
	 |     |      -+- 3
	 \_____/
	  
	 */
	private static void testOrDivergence() throws InterruptedException {
		GrafcetDesigner grafcetDesigner = new GrafcetDesigner();
		StepBlueprint step1 = grafcetDesigner.newInitialStep(1)
												.withAction(new NoAction("action01"));
		StepBlueprint step2 = grafcetDesigner.newStep(2)
												.withAction(new NoAction("action02"));
		StepBlueprint step3 = grafcetDesigner.newStep(3)
												.withAction(new NoAction("action03"));
		StepBlueprint step4 = grafcetDesigner.newStep(4)
												.withAction(new NoAction("action04"));
		// tran 1
		TransitionBlueprint tran01 = grafcetDesigner.newTransition(1)
													.withCondition(new AlwaysTrue());
		grafcetDesigner.linkStep(step1)
						.to(tran01);
		grafcetDesigner.whenTransitionFires(tran01)
						.activateAll(step2, step3, step4);

		//tran 2
		TransitionBlueprint trans02 = grafcetDesigner.newTransition(2)
														.withCondition(new AlwaysTrue());
		grafcetDesigner.linkStep(step2)
						.to(trans02);

		// Tran 3
		TransitionBlueprint trans03 = grafcetDesigner.newTransition(3)
														.withCondition(new AlwaysTrue());
		grafcetDesigner.clearAllSteps(step2, step3, step4)
						.whenFires(trans03);

		// Build
		Grafcet grafcet = grafcetDesigner.build();

		// Setup graph display
		/*GrafcetDisplay display = new GrafcetDisplay(grafcet);
		display.setLocation(step1, 10, 10);
		display.setLocation(step2, 10, 14);
		display.setLocation(step3, 12, 14);
		display.setLocation(step4, 14, 14);

		// Make frame
		JFrame frame = new JFrame("test");
		frame.add(display);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		new GrafcetDesigner().newInitialStep(0)
								.withAction(null);

		while (true) {
			System.out.println("----------------------------------");
			Thread.sleep(5000);
			grafcet.iterate();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					frame.repaint();
				}
			});
		}*/
	}
}
