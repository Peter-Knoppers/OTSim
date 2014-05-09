package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.util.Arrays;

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
		String otsimConfiguration = "EndTime:\t3600.00\nSeed:\t1\nRoadway:	0	from	1	to	2	speedlimit	50	lanes	1	vertices	(0.000,-2.000,0.000)	(1500.000,-2.000,0.000)	ins	outs\nTrafficClass	passengerCar_act	4.000	140.000	-6.000	0.900000	600.000\nTripPattern	numberOfTrips:	[0.000/4000.000000][0.000/1.000000]	LocationPattern:	[z1, z2]	Fractions	passengerCar_act:1.000000\nTripPatternPath	numberOfTrips:	[0.000/4000.000000][0.000/1.000000]	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (1500.00m, 0.00m, 0.00m)]\nPath:	1.00000	nodes:	1	2";
		//double inflowBoundary = (2000.0/3600.0);
		//System.out.println(inflowBoundary);
		Scheduler scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), otsimConfiguration);
		// Do something with the scheduler
		Model macromodel = (Model) scheduler.getSimulator().getModel();
		macromodel.init();
		
		System.out.println("Time: "+scheduler.getSimulatedTime());
		System.out.println(Arrays.toString(macromodel.saveStateToArray()));
		scheduler.stepUpTo(10.0);
		System.out.println("Time: "+scheduler.getSimulatedTime());
		System.out.println(Arrays.toString(macromodel.saveStateToArray()));
		
		scheduler.stepUpTo(20.0);
		System.out.println("Time: "+scheduler.getSimulatedTime());
		double[] arr = macromodel.saveStateToArray();
		System.out.println(Arrays.toString(arr));
		double[] arr2 = new double[arr.length];
		Arrays.fill(arr2, 0.01);
		
		macromodel.restoreState(arr2);
		System.out.println(Arrays.toString(macromodel.saveStateToArray()));
		scheduler.stepUpTo(30.0);
		System.out.println("Time: "+scheduler.getSimulatedTime());
		System.out.println(Arrays.toString(macromodel.saveStateToArray()));
		
	}

}
