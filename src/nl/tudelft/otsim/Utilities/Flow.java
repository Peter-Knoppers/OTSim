package nl.tudelft.otsim.Utilities;

import java.util.ArrayList;
import java.util.Locale;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.FileIO.StaXWriter;
import nl.tudelft.otsim.FileIO.XML_IO;
import nl.tudelft.otsim.GeoObjects.Network;

/**
 * This class holds a list of time/flow pairs and allows retrieval of 
 * (interpolated) flow values for any time.
 * 
 * @author Peter Knoppers
 */
public class Flow implements XML_IO {
	/** Tag of a Flow when stored in XML */
	public static final String XMLTAG = "TimeFlowSets";
	private static final String XML_TIMEFLOWPAIR = "Set";
	private static final String XML_TIME = "Time";
	private static final String XML_FLOW = "Flow";
	private ArrayList<Double> times = new ArrayList<Double>();
	private ArrayList<Double> flows = new ArrayList<Double>();
	private final Network network;
	private final static String format = "%.3f"; 
	
	/**
	 * Create an empty instance of Flow.
	 * @param network {@link Network}; the Network that will own this Flow (may be null)
	 */
	public Flow(Network network) {
		this.network = network;
	}
	
	/**
	 * Create a Flow from an XML description
	 * @param network {@link Network}; the Network that will own this flow (may be null)
	 * @param pn {@link ParsedNode} XML node of the Flow object 
	 * @throws Exception
	 */
	public Flow(Network network, ParsedNode pn) throws Exception {
		this(network);
		for (int index = 0; index < pn.size(); index++) {
			ParsedNode subNode = pn.getSubNode(XML_TIMEFLOWPAIR, index);
			for (String fieldName : subNode.getKeys()) {
				if (fieldName.equals(XML_TIMEFLOWPAIR)) {
					for (int i = 0; i < subNode.size(fieldName); i++) {
						double time = Double.NaN;
						double flow = Double.NaN;
						ParsedNode valueNode = subNode.getSubNode(XML_TIME, 0);
						if (null != valueNode)
							time = Double.parseDouble(valueNode.getValue());
						valueNode = subNode.getSubNode(XML_FLOW, 0);
						if (null != valueNode)
							flow = Double.parseDouble(valueNode.getValue());
						if (Double.isNaN(time) || Double.isNaN(flow))
							throw new Exception("incompletely defined time/flow pair near " + subNode.description());
						insertPair(time, flow);
					}
				}
			}
		}
	}
	
	/**
	 * Insert a time/flow pair.
	 * <br /> If several values are inserted with the exact same time, the 
	 * order of the stored time/flow pairs is undefined.
	 * @param time Double; the time (in s relative to simulation start time)
	 * @param flow Double; the flow in vehicles / hour
	 */
	public void insertPair (double time, double flow) {
		int position;
		for (position = 0; (position < times.size()) && (time > times.get(position)); position++)
			;
		times.add(position, time);
		flows.add(position, flow);
		if (null != network)
			network.setModified();
	}
	
	/**
	 * Retrieve the number of time/flow pairs stored.
	 * @return Integer; the number of time/flow pairs stored.
	 */
	public int size() {
		return times.size();
	}
	
	/**
	 * Retrieve the time of a particular time/flow pair.
	 * @param index Integer; the index of the time/flow pair
	 * @return Double; the time of the selected time/flow pair
	 */
	public double getTime(int index) {
		return times.get(index);
	}
	
	/**
	 * Retrieve the flow of a particular time/flow pair.
	 * @param index Integer; the index of the time/flow pair
	 * @return Double; the flow of the selected time/flow pair
	 */
	public double getFlow(int index) {
		return flows.get(index);
	}

	/**
	 * Remove a time/flow pair.
	 * @param index Integer; the index of the time/flow pair to remove
	 */
	public void deletePair (int index) {
		times.remove(index);
		flows.remove(index);
		if (null != network)
			network.setModified();
	}
	
	/**
	 * Retrieve the flow value at a specified time.
	 * <br />Before the first specified time and after the last specified time
	 * the returned flow value remains constant.
	 * @param time Double; the time in s relative to simulation start time
	 * @return Double; the (interpolated) flow in vehicles / hour at the specified time 
	 */
	public double getFlow(double time) {
		if (flows.size() == 0)
			throw new Error("undefined flow");
		double prevTime = 0;
		double prevFlow = flows.get(0);
		for (int i = 0; i < times.size(); i++) {
			Double thisTime = times.get(i);
			Double thisFlow = flows.get(i);
			if (thisTime <= time) {
				prevTime = thisTime;
				prevFlow = thisFlow;
			} else if (thisTime > prevTime )
				return prevFlow + (thisFlow - prevFlow) * (time - prevTime) / (thisTime - prevTime);
			else
				return thisFlow;
		}
		return prevFlow;
	}

	private boolean writePairs(StaXWriter staXWriter) {
		for (int index = 0; index < size(); index++) {
			if (! staXWriter.writeNodeStart(XML_TIMEFLOWPAIR))
				return false;
			if (! staXWriter.writeNode(XML_TIME, String.format(Locale.US, format, getTime(index))))
				return false;
			if (! staXWriter.writeNode(XML_FLOW, String.format(Locale.US, format, getFlow(index))))
				return false;
			if (! staXWriter.writeNodeEnd(XML_TIMEFLOWPAIR))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean writeXML(StaXWriter staXWriter) {
		return staXWriter.writeNodeStart(XMLTAG)
				&& writePairs(staXWriter)
				&& staXWriter.writeNodeEnd(XMLTAG);
	}
	
	/**
	 * Export this Flow in a textual format.
	 * @return String; this Flow in a textual format
	 */
	public String export () {
		String result = "";
		final String formatPair = format + "/" + format;
		for (int i = 0; i < size(); i++) {
			double time = times.get(i);
			double flow = flows.get(i);
			//if ((time > 0) && (result.length() == 0))	// Insert initial value for convenience
			//	result = String.format(Locale.US, formatPair, time, flow);
			result += String.format(Locale.US, "%s" + formatPair, result.length() > 0 ? "\t" : "", time, flow);
		}
		return result;
	}
	
	/**
	 * Create a Flow from a textual description with no owning Network.
	 * <br /> This can be useful in {@link Simulators}.
	 * @param description
	 */
	public Flow (String description) {
		network = null;
		String pairs[] = description.split("\t");
		for(String pair : pairs) {
			String fields[] = pair.split("/");
			double time = Double.parseDouble(fields[0]);
			double flow = Double.parseDouble(fields[1]);
			insertPair(time, flow);
		}
	}
}

