package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.Simulators.ShutDownAble;
import nl.tudelft.otsim.Simulators.SimulatedObject;
import nl.tudelft.otsim.Simulators.Simulator;

public class MacroSimulator extends Simulator implements ShutDownAble {
	/** Type of this Simulator */
	public static final String simulatorType = "Macro simulator";

	public MacroSimulator(String configuration, GraphicsPanel graphicsPanel, Scheduler scheduler) {
		// STUB
		System.out.println("Create a new MacroSimulator based on description:\n" + configuration);
	}

	@Override
	public void setModified() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repaintGraph(GraphicsPanel graphicsPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ShutDown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scheduler getScheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SimulatedObject> SampleMovables() {
		// TODO Auto-generated method stub
		return null;
	}

}
