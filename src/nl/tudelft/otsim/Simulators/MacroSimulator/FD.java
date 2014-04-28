package nl.tudelft.otsim.Simulators.MacroSimulator;

public abstract class FD {

	public FD() {
		// TODO Auto-generated constructor stub
	}
	abstract public double calcQ(MacroCell mc);
	abstract public double calcQcap(MacroCell mc);

}
