package nl.tudelft.otsim.Simulators.MacroSimulator;

/**
 * A single cell of road. Different cells are connected in the
 * longitudinal or lateral direction. The <tt>jMacroCell</tt> object also provides a
 * few network utilities to get traffic state information.
 * <br>
 * <br>
 */
public class MacroCell {
	
	boolean marked;
	boolean markedForXadj;
	
	//Geo info
    /** Array of x-coordinates defining the lane curvature. */
    public double[] x;

    /** Array of y-coordinates defining the lane curvature. */
    public double[] y;

    /** Length of the lane [m]. */
    public double l;

    /** Main model. */
    public Model model;

	/** ID of lane for user recognition. */
    protected int id;

    /** Downstream cell that is a taper (if any). */
    public MacroCell taper;

    /** Upstream cell (if any). */
    public MacroCell up;
    
    /** Set of upstream cells in case of a merge. */
    public java.util.ArrayList<MacroCell> ups = new java.util.ArrayList<MacroCell>();

    /** Downstream cell (if any). */
    public MacroCell down;
    
    /** Set of downstream cells in case of a split. */
    public java.util.ArrayList<MacroCell> downs = new java.util.ArrayList<MacroCell>();

    /** Left cell (if any). for multi-lane modelling */
    public MacroCell left;

    /** Right cell (if any). */
    public MacroCell right;

    /** Whether one can change to the left lane. */
    public boolean goLeft;

    /** Whether one can change to the right lane. */
    public boolean goRight;

    //** Set of RSUs, ordered by position. */
    //protected java.util.ArrayList<RSU> RSUs = new java.util.ArrayList<RSU>();

    //** All Movables on this lane, in order of increasing x. */
    //private java.util.ArrayList<Movable> vehicles = new java.util.ArrayList<Movable>(0);

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
     * First downstream splitting lane. This is used for neighbor bookkeeping 
     * where pointers past a split from downstream are invalid (and thus removed).
     */
    //public Lane downSplit;
    
    /**
     * First upstream merging lane. This is used for neighbor bookkeeping 
     * where pointers past a merge from upstream are invalid (and thus removed).
     */
    //public Lane upMerge;
    
    /** 
     * Lane from which a vehicle entered the upstream side of a merge lane. This
     * is used to determine whether a vehicle upstream of a merge should follow
     * its leader downstream of a merge. If that vehicle came from the other
     * direction of the merge, it should not be followed.
     */
    //public Lane mergeOrigin;

	private boolean visible = true;	// Default is to paint the lane
    
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
        calculateLength();
    }
    
    /* Never used
    public Lane(double[] x, double[] y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        calculateLength();
    } 
    */
    
    /**
     * Sets the lane length based on the x and y coordinates. This method is 
     * called within the constructor and should only be used if coordinates are
     * changed afterwards (for instance to nicely connect lanes at the same 
     * point).
     */
    public void calculateLength() {
        // compute and set length
        double cumLength = 0;
        double dx;
        double dy;
        for (int i=1; i<=x.length-1; i++) {
            dx = this.x[i]-this.x[i-1];
            dy = this.y[i]-this.y[i-1];
            cumLength = cumLength + Math.sqrt(dx*dx + dy*dy);
        }
        l = cumLength;
    }
    
    
    
    /**
     * Retrieve the upstream connected MacroCell of this MacroCell.
     * @return MacroCell; the upstream connected MacroCell of this MacroCell
     */
    public MacroCell getUp_r() {
    	return up;
    }
    
    /**
     * Retrieve the downstream connected MacroCell of this MacroCell.
     * @return MacroCell; the downstream connected MacroCell of this MacroCell
     */
    public MacroCell getDown_r() {
    	return down;
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

    
    /* Never used junk
    public int getDestination() {
		return destination;
	}
    
	public void setDestination(int destination) {
		this.destination = destination;
	}
	
	public int getOrigin() {
		return origin;
	}
	
	public void setOrigin(int origin) {
		this.origin = origin;
	}*/
    
	/**
     * Returns the ID of the lane.
     * @return ID of the lane.
     */
    public int id() {
        return id;
    }


    /**
     * Returns the speed limit as m/s.
     * @return Speed limit [m/s]
     */
    public double getVLim() {
        return vLim/3.6;
    }


    /**
     * Returns whether this lane splits, i.e. whether there are split lanes.
     * @return Whether this lane splits.
     */
    public boolean isSplit() {
        return !downs.isEmpty();
    }
    
    /**
     * Return whether this lane merges, i.e. whether there are merge lanes.
     * @return Whether this lane merges.
     */
    public boolean isMerge() {
        return !ups.isEmpty();
    }
    


	/**
	 * @param visible Boolean; true if this Lane is to be painted (default);
	 * false if this Lane must not be painted
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return Boolean; true if this lane is visible (this is the default);
	 * false if this lane is invisible (hidden)
	 */
	public boolean isVisible() {
		return visible;
	}
}