package nl.tudelft.otsim.Simulators.MacroSimulator;

public class FDSmulders extends FD {

	public FDSmulders() {
		// TODO Auto-generated constructor stub
	}

	public double calcQ(MacroCell mc) {
		double k = mc.KCell;
		double kJam = mc.kJam;
		double kCri = mc.kCri;
		double vLim = mc.vLim;
		double w = kCri/kJam*vLim;
		
		if (k<0 || k > kJam) {
    		throw new Error ("density is not correct" + Double.toString(k));
    	
		} else if (k<=kCri) {
    		
    		return k*vLim*(1-(k/kJam));
		} else {
    		
    		return -w*(k-kJam);
	
		}
	}
	public double calcQcap(MacroCell mc) {
		double kJam = mc.kJam;
		double kCri = mc.kCri;
		double vLim = mc.vLim;
		return kCri*vLim*(1-(kCri/kJam));
	}

}
