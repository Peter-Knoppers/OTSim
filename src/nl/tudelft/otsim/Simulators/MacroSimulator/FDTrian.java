package nl.tudelft.otsim.Simulators.MacroSimulator;

public class FDTrian extends FD {

	public FDTrian() {
		// TODO Auto-generated constructor stub
	}
	public double calcQ(MacroCell mc) {
		double k = mc.KCell;
		double kJam = mc.kJam;
		double kCri = mc.kCri;
		double vLim = mc.vLim;
		
		if (k<0 || k > kJam) {
    		throw new Error ("density is not correct" + Double.toString(k));
    	
		} else if (k<kCri) {
    		/** triangular FD **/
    		return k*vLim;
		} else {
    		/** triangular FD **/
    		return (kJam - k)/(kJam - kCri)*(kCri*vLim);
	
		}
	}
	public double calcQcap(MacroCell mc) {
			
		return mc.kCri*mc.vLim;
	}
	
}
