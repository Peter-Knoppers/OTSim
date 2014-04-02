package nl.tudelft.otsim.Simulators.MacroSimulator;

import nl.tudelft.otsim.Simulators.LaneSimulator.Model;

/**
 * A single cell of road. Different cells are connected in the
 * longitudinal or lateral direction. The <tt>jMacroCell</tt> object also provides a
 * few network utilities to get traffic state information.
 * <br>
 * <br>
 */
public class MacroCell {
	
	// Geographical info
    /** Array of x-coordinates defining the lane curvature. */
    final public double[] x;

    /** Array of y-coordinates defining the lane curvature. */
    final public double[] y;

    /** Length of the lane [m]. */
    final public double l;

    /** Main model. */
    final public Model model;

	/** ID of cell for user recognition. */
    final int id;

    /** Set of upstream cells in case of a merge. */
    public java.util.ArrayList<MacroCell> ups = new java.util.ArrayList<MacroCell>();

    /** Set of downstream cells in case of a split. */
    public java.util.ArrayList<MacroCell> downs = new java.util.ArrayList<MacroCell>();

    /** Left cell (if any). for multi-lane modeling */
    public MacroCell left;

    /** Right cell (if any). */
    public MacroCell right;

    /** Destination number, NODESTINATION if no destination. */
    public int destination;

    /** Origin number, NOORIGIN if no origin. */
    public int origin;

    //Traffic states
    /** Flow in this cell. [veh/h or veh/s] */
    public double QCell;
    
    /** Density in this cell. [veh/km or veh/m] */
    public double KCell;
    
    /** Average spacing in this cell. [km or m] */
    public double SCell;
    
    /** Average speed in this cell. [km/h or m/s] */
    public double VCell;
    
    /** Flux into this cell. [veh/h or veh/s] */
    public double FluxIn;
    
    /** Flux out from this cell. [veh/h or veh/s] */
    public double FluxOut;
    
    // Parameters    
    /** Legal speed limit [km/h]. */
    public double vLim = 120;
    
    /** Legal critical density [veh/km]. */
    public double kCri = 25;
    
    /** Legal jam density [veh/km]. */
    public double kJam = 125;
    
    /** Legal flow capacity [veh/h/lane]. */
    public double qCap = 2000;
    
    /**
     * Constructor that will calculate the lane length from the x and y
     * coordinates.
     * @param x X coordinates of curvature.
     * @param y Y coordinates of curvature.
     * @param id User recognizable lane id.
     * @param model Main model.
     */
    public MacroCell(Model model, double[] x, double[] y, int id) {
        this.model = model;
        this.x = x;
        this.y = y;
        this.id = id;
        l = calculateLength();
    }
    
    /**
     * Sets the lane length based on the x and y coordinates. This method is 
     * called within the constructor and should only be used if coordinates are
     * changed afterwards (for instance to nicely connect lanes at the same 
     * point).
     * @return double; Total length
     */
    public double calculateLength() {
        // compute and set length
        double cumLength = 0;
        double dx;
        double dy;
        for (int i=1; i<=x.length-1; i++) {
            dx = this.x[i]-this.x[i-1];
            dy = this.y[i]-this.y[i-1];
            cumLength = cumLength + Math.sqrt(dx*dx + dy*dy);
        }
        return cumLength;
    }
    
    
    
    /**
     * Retrieve the upstream connected MacroCell of this MacroCell.
     * @return MacroCell; the upstream connected MacroCell of this MacroCell
     */
    public java.util.ArrayList<MacroCell> getUps_r() {
    	return ups;
    }
    
    /**
     * Retrieve the downstream connected MacroCell of this MacroCell.
     * @return MacroCell; the downstream connected MacroCell of this MacroCell
     */
    public java.util.ArrayList<MacroCell> getDowns_r() {
    	return downs;
    }
    
    /**
     * Retrieve the left MacroCell of this MacroCell.
     * @return MacroCell; the left MacroCell of this MacroCell
     */
    public MacroCell getLeft_r() {
    	return left;
    }
    
    /**
     * Retrieve the right MacroCell of this MacroCell.
     * @return MacroCell; the right MacroCell of this MacroCell
     */
    public MacroCell getRight_r() {
    	return right;
    }
    
    /**
     * Return the destination of this MacroCell.
     * @return Integer; the destination of this MacroCell, or a negative value if
     * this MacroCell is not a destination
     */
    public int getDestination_r() {
    	return destination;
    }
    
    /**
     * Return the origin of this MacroCell.
     * @return Integer; the origin of this MacroCell or a negative value if this
     * MacroCell is not an origin
     */
    public int getOrigin_r() {
    	return origin;
    }
    
    /**
     * Retrieve the speed limit on this MacroCell.
     * @return Double; the speed limit on this MacroCell in m/s
     */
    public double getSpeedLimit_r() {
    	return vLim;
    }
    
    /**
     * Retrieve the flow of this MacroCell.
     * @return Double; the flow of this MacroCell
     */
    public double getQ_r() {
    	return QCell;
    }
    
    /**
     * Retrieve the density of this MacroCell.
     * @return Double; the density of this MacroCell
     */
    public double getK_r() {
    	return KCell;
    }
    
    /**
     * Retrieve the average speed of this MacroCell.
     * @return Double; the average speed of this MacroCell
     */
    public double getV_r() {
    	return VCell;
    }
    
    
    /**
     * Sets the flow of this MacroCell.
     * @param flow Flow of this cell [veh/h or veh/s].
     */
    public void setQ(double flow) {
        this.QCell = flow;
    }
    
    /**
     * Sets the density of this MacroCell.
     * @param density Density of this cell [veh/km or veh/m].
     */
    public void setK(double density) {
        this.KCell = density;
    }
    
    /**
     * Sets the average speed of this MacroCell.
     * @param speed Speed of this cell [km/h or m/s].
     */
    public void setV(double speed) {
        this.VCell = speed;
    }
    
	/**
     * Returns the ID of the lane.
     * @return ID of the lane.
     */
    public int id() {
        return id;
    }


    /**
     * Returns the speed limit in m/s.
     * @return Speed limit [m/s]
     */
    public double getVLim() {
        return vLim/3.6;
    }

}