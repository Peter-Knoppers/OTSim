package nl.tudelft.otsim.Simulators.MacroSimulator;


import java.awt.event.MouseEvent;
import java.util.ArrayList;


import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.Events.Step;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.WED;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.ShutDownAble;
import nl.tudelft.otsim.Simulators.SimulatedObject;
import nl.tudelft.otsim.Simulators.Simulator;

import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;

/**
 * Macro Simulator for OpenTraffic
 * 
 * @author Peter Knoppers
 */
public class MacroSimulator extends Simulator implements ShutDownAble{
	/** Type of this Simulator */
	public static final String simulatorType = "Macro simulator";
	
	private final Model model = new Model();
	private final Scheduler scheduler;
	private double endTime = 1000;	// should be overridden in the configuration
	private double randomSeed = 0;	// idem
	private ArrayList<MacroCell> macroCells = new ArrayList<MacroCell>();

	/**
	 * Create a MacroSimulator.
	 * @param configuration String; textual description of the network,
	 * traffic demand and measurement plans
	 * @param graphicsPanel {@link GraphicsPanel} to draw on
	 * @param scheduler {@link Scheduler} for this simulation
	 * @throws Exception 
	 */
	public MacroSimulator(String configuration, GraphicsPanel graphicsPanel, Scheduler scheduler) throws Exception {
		System.out.println("Creating a new MacroSimulator based on description:\n" + configuration);
		this.scheduler = scheduler;
		scheduler.enqueueEvent(0, new Stepper(this));	// Set up my first evaluation
		model.period = 1800;
		model.dt = 0.2;
		//ArrayList<MacroCell> cells = new ArrayList<MacroCell>();
		ArrayList<MacroCell> copySimPaths = new ArrayList<MacroCell>();
		
		/*
		 * It does make sense to first join successive roadway sections that
		 * have the same capacity and speed limit. Then split up longer roadways 
		 * into multiple cells and create a class that macro-simulates one cell.
		 */
		for (String line : configuration.split("\n")) {
			String[] fields = line.split("\t");
			if (fields.length == 0)
				continue;	// Ignore empty lines in configuration
			else if (fields[0].equals("EndTime:"))
				this.endTime = Double.parseDouble(fields[1]);
			else if (fields[0].equals("Seed:"))
				this.randomSeed = Double.parseDouble(fields[1]);
			else if (fields[0].equals("Roadway:")) {
				MacroCell sp = new MacroCell(model);
				// set ID of MacroCell
				sp.setId(Integer.parseInt(fields[1]));
				for (int i = 2; i < fields.length; i++) {
					if (fields[i].equals("speedlimit"))
						sp.setSpeedLimit(Double.parseDouble(fields[++i]));
					if (fields[i].equals("lanes"))
						sp.setWidth(3.5 * Double.parseDouble(fields[++i]));
					else if (fields[i].equals("vertices")) {
						// add vertices
						while (fields[++i].startsWith("(")) {
							Vertex tmp = new Vertex(fields[i]);
							sp.addVertex(tmp);
							
						}
						
						// decrease i to start check the right field in the following loop
						--i;
					
					} else if (fields[i].equals("ins")) {
						// add all incoming links to MacroCell
						while (!fields[++i].startsWith("outs")) {
							sp.addIn(Integer.valueOf(fields[i]));
						}
						// decrease i to start check the right field in the following loop
						--i;
					} else if (fields[i].equals("outs")) {
						// add all outgoing links to MacroCell
						while (++i <= fields.length-1) {
							sp.addOut(Integer.valueOf(fields[i]));
						}
					}
			
				} 
				copySimPaths.add(sp.getId(),sp); 
			} else
				;//throw new Exception("Don't know how to parse " + line);
			// TODO: write code to handle the not-yet-handled lines in the configuration
		}
		// Now all macrocells are generated, link upstream and downstream macrocells together. 
		for (MacroCell mc: copySimPaths) {
			
			
			for (Integer i: mc.ins) {
				mc.addIn(copySimPaths.get((int) i));
			}
			for (Integer j: mc.outs) {
				mc.addOut(copySimPaths.get((int) j));
			}
			
		}
		
		// Next step: join cells as much as possible
		// Cells are joined when no difference in speed limit, no difference in lane, and no merges and splits are present
		
		// make list of links that still need to be joined
		ArrayList<Integer> todo = new ArrayList<Integer>();
		for (int i=0; i<copySimPaths.size(); i++) {
			todo.add((Integer) i);
		}
		
		
		
		// while there are links to be joined
		while (!(todo.size() == 0)) {
			// make new cell with the same properties as the first cell in the to do list
			MacroCell snew = new MacroCell(model);
			MacroCell sbegin = copySimPaths.get(todo.get(0));
			
			todo.remove(0);
			snew.id = sbegin.id;
			snew.vertices.addAll(0, sbegin.vertices);
			snew.ups = (ArrayList<MacroCell>) sbegin.ups.clone();
			for (MacroCell c: snew.ups) {
				c.downs.remove(sbegin);
				c.downs.add(snew);
			}
			snew.downs = (ArrayList<MacroCell>) sbegin.downs.clone();
			for (MacroCell c: snew.downs) {
				c.ups.remove(sbegin);
				c.ups.add(snew);
			}
			snew.setWidth(sbegin.getWidth());
			snew.setSpeedLimit(sbegin.getSpeedLimit());
			
			// if there is only one cell upstream of considered cell
			while((snew.ups.size() == 1)) {
				
				MacroCell sp = snew.ups.get(0);
				
				// test if cell upstream has the right nr of lanes and speedlimit
				if (!(sp.downs.size() == 1) || (!(sp.getWidth() == snew.getWidth())) || (!(sp.getSpeedLimit() == snew.getSpeedLimit()))) {
					
					break;
				} else {
					// cell upstream has the right nr of lanes and speed limit
					
					// add vertices of cell in front of vertices of current cell 
					snew.vertices.addAll(0,sp.vertices);
					
					// new cell to be considered is the upstream cell
					snew.ups = (ArrayList<MacroCell>) sp.ups.clone();
					// update links to upstream cells
					for (MacroCell c: snew.ups) {
						c.downs.remove(sp);
						c.downs.add(snew);
					}
					// remove the upstream cell from to do list
					todo.remove(new Integer(sp.getId()));
				}
			}
			// test if cell downstream has the right nr of lanes and speedlimit
			while((snew.downs.size() == 1)) {
				MacroCell sp = snew.downs.get(0);
				if (!(sp.ups.size() == 1) || (!(sp.getWidth() == snew.getWidth())) || (!(sp.getSpeedLimit() == snew.getSpeedLimit()))) {
					
					break;
				} else {
					// cell downstream has the right nr of lanes and speed limit
					
					// add vertices of cell at the end of vertices of current cell 
					snew.vertices.addAll(sp.vertices);
					
					// new cell to be considered is the downstream cell
					snew.downs = (ArrayList<MacroCell>) sp.downs.clone();
					// update links to downstream cells
					for (MacroCell c: snew.downs) {
						c.ups.remove(sp);
						c.ups.add(snew);
					}
					// remove the downstream cell from todo list
					todo.remove(new Integer(sp.getId()));
				}
			}
			
			// add new (joined) cell to the list of cells
			macroCells.add(snew);
			
			
		}
		
		// Next step: split the joined cells into smaller cells of similar size
		ArrayList<MacroCell> copyCells = new ArrayList<MacroCell>();
		for (MacroCell m: macroCells) {
			// determine number of parts in which the cell must be split
			int nrParts = (int) m.calcLength()/30;
			if (nrParts == 0)
				nrParts = 1;
			// add the cells that are split to the list
			copyCells.addAll(m.splitInParts(nrParts));
		}
		macroCells = copyCells;
		
		// give all the cells in the list new IDs
		int tel = 0;
		for (MacroCell m: macroCells) {
			m.setId(tel);
			tel++;
		}
		
		
		// initialize all cells (e.g. determine parameters needed for simulation) and add to the model
		for (MacroCell m: macroCells) {
		
			m.init();
			model.addMacroCell(m);
			
		}
			
	}
	
	public final Model getModel() {
		return model;
	}
	
	@Override
	public void setModified() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repaintGraph(GraphicsPanel graphicsPanel) {
		for (MacroCell sp : macroCells) {
			sp.draw(graphicsPanel);
		}
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
		return scheduler;
	}

	@Override
	public ArrayList<SimulatedObject> SampleMovables() {
		// TODO Auto-generated method stub
		return null;
	}
class Stepper implements Step {
		final private MacroSimulator macroSimulator;
		
		public Stepper (MacroSimulator macroSimulator) {
			this.macroSimulator = macroSimulator;
		}
		
	@Override
	public boolean step(double now) {
    	System.out.println("step entered");
    	Model model = macroSimulator.getModel();
    	System.out.println(Double.toString(model.period));
    	System.out.println(Double.toString(now));
    	System.out.println(Double.toString(model.t()));
    	if (now >= model.period)
    		return false;
    	while (model.t() < now) {
    		System.out.println("step calling run(1)");
    		try {
    			//System.out.format(Main.locale, "Time is %.3f\r\n", now);
    			model.run(1);
    		} catch (RuntimeException e) {
    			WED.showProblem(WED.ENVIRONMENTERROR, "Error in LaneSimulator:\r\n%s", WED.exeptionStackTraceToString(e));
    			return false;
    		}
    	}
    	// re-schedule myself
    	macroSimulator.getScheduler().enqueueEvent(model.t() + model.dt, this);
    	System.out.println("step returning true");
		return true;
	}
}
}
