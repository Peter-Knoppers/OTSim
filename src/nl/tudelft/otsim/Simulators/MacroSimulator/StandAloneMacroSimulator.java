package nl.tudelft.otsim.Simulators.MacroSimulator;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;

/**
 * Run a MacroSimulator without GUI
 * <br />
 * Demonstration class only...
 * 
 * @author Peter Knoppers
 */
public class StandAloneMacroSimulator {

	/**
	 * @param args String[]; program arguments
	 */
	public static void main(String[] args) {
		// The MacroSimulator is not going to like the description in this demonstration...
		Scheduler scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), "Hello world");
		// Do something with the scheduler
		scheduler.stepUpTo(100.0);
	}

}
