package nl.tudelft.otsim.GUI;

import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

/**
 * This is the environment for running OpenTraffic embedded in a web browser.
 * 
 * @author Peter Knoppers
 */
public class OTSim extends JApplet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() {
		setLayout (new BorderLayout());
		add(new JLabel("Loading OTSim resources ..."), BorderLayout.CENTER);
	}
	
	@Override
	public void start() {
		add(new Main(this), BorderLayout.CENTER);
	}
	
	/**
	 * Put a JMenuBar in the NORTH area of the JApplet window.
	 * @param menuBar JMenuBar; the menu bar to put in the NORTH area of the window
	 */
	public void setMenuBar(JMenuBar menuBar) {
		add (menuBar, BorderLayout.NORTH);
	}

}
