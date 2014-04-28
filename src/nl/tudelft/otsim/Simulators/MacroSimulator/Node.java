package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.awt.Color;
import java.util.ArrayList;

import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GeoObjects.Vertex;

//import nl.tudelft.otsim.GUI.GraphicsPanel;

abstract public class Node {
	public ArrayList<MacroCell> cellsIn = new ArrayList<MacroCell>();
	public ArrayList<MacroCell> cellsOut = new ArrayList<MacroCell>();
	public Vertex location = new Vertex();
	protected int nrIn;
	protected int nrOut;
	public double[] fluxesIn;
	public double[] fluxesOut;
	public double[] turningRatio;
	
	
	public Node(Vertex loc) {
		this.location = loc;
		
	}
	public void init() {
		nrIn = cellsIn.size();
		nrOut = cellsOut.size();
		
		if (nrIn>0)
			fluxesIn = new double[nrIn];
		else
			fluxesIn = new double[1];
		
		if (nrOut>0)
			fluxesOut = new double[nrOut];
		else
			fluxesOut = new double[1];
		
		turningRatio = new double[nrOut];
		
	}
	public void draw(GraphicsPanel graphicsPanel) {
		
    	location.paint(graphicsPanel);
   	
	}
	abstract public void calcFlux();
	
	
	
}
