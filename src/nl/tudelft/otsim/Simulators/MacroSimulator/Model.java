package nl.tudelft.otsim.Simulators.MacroSimulator;



public class Model {
    /** Time step number. Always starts as 0. */
    protected int k;
    
    /** Current time of the model [s]. Always starts at 0. */
    protected double t;
    
    /** Time step size of the model [s]. */
    public double dt;
    
    /** Maximum simulation period [s]. */
    public double period;
    
    protected java.util.ArrayList<MacroCell> cells = new java.util.ArrayList<MacroCell>();
    
    protected java.util.ArrayList<Node> nodes = new java.util.ArrayList<Node>();
	
    public void init() {
        // Set attributes
        k = 0;
        t = 0;
        cells = new java.util.ArrayList<MacroCell>();
        nodes = new java.util.ArrayList<Node>();
    }
    
	public void run(int n) {
        // Simulate n steps
        //int i = 0;
		//System.out.println("testrun");
    	for (int nn = 0; (nn < n) && (t < period); nn++) {
    		//System.out.println("test");
    		java.util.ArrayList<MacroCell> tmp2 = new java.util.ArrayList<MacroCell>(cells);
    		//System.out.println("size Arraylist: " + Integer.toString(tmp2.size()));
    		for (MacroCell c : tmp2) {
    			//System.out.println("ID:\t" + Integer.toString(c.id()));
    			//System.out.println(Double.toString(c.getK_r()));
    			//System.out.println(Double.toString(c.getV_r()));
    			//System.out.println("Ins: "+c.getIns() + " outs: " + c.getOuts());
    			//System.out.println(Integer.toString(k));
    			 c.calcDemand();
    			 c.calcSupply();
    			
            }
    		for (Node node: nodes ) {
    			node.calcFlux();
    			//
    			/*if (node.nrIn + node.nrOut > 2) {
    				for (double v: node.fluxesIn) {
    					System.out.println("FluxIn node:" + v);
    				}
    				for (double v: node.fluxesOut) {
    					System.out.println("FluxOut node:" + v);
    				}
    				
    			}*/
    		}
    		for (MacroCell c2: tmp2) {
    			 c2.calcFluxOut();
    			 c2.calcFluxIn();
    			 c2.updateDensity();
    			 //System.out.println(Double.toString(c2.qCap));
    			 //System.out.println(Double.toString(c2.getV_r()));
    		}
            // Update time
            k++; // Increment time step number
            t = k * dt; // time [s]
        }
    }
	public double t() {
	        return t;
	 }
	public void addMacroCell(MacroCell m) {
		cells.add(m);
	}
	public void addNode(Node m) {
		nodes.add(m);
	}
}
