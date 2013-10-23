package nl.tudelft.otsim.ShortesPathAlgorithms;

import java.util.ArrayList;

import nl.tudelft.otsim.GeoObjects.Node;

/**
 * General shape of all Shortest path algorithm implementations
 * 
 * @author Peter Knoppers
 */
public abstract class ShortestPathAlgorithm {
	/**
	 * Find lowest cost path from all {@link Node Nodes} to the specified end node
	 * @param endNode {@Link Node}; end point of all paths
	 */
	public abstract void execute (Node endNode);
	
	/**
	 * Retrieve the cost of travel from the specified {@link Node startNode} to the endNode.
	 * @param startNode {@link Node}; the start node of the trip
	 * @return Double; the cost of a trip from the specified startNode to the endNode
	 */
	public abstract double getLowestCost (Node startNode);
	
	/**
	 * Retrieve the lowest cost path from {@link Node startNode} to the endNode.
	 * @param startNode {@link Node}; the start node of the trip
	 * @return ArrayList&lt;{@link Node}&gt;; the list of nodes visited in the lowest cost path
	 */
	public abstract ArrayList<Node> getPath (Node startNode);
	
	/**
	 * Add a penalty to a specified path. All penalties are reset when the 
	 * execute method is called or the clearPenalties method is called.
	 * @param ratio Double; fraction of the current cost to add to each path element
	 * @param perPathElement Double; fixed value to add to the cost of each path element
	 * @param path ArrayList&lt;{@link Node}&gt;; list of nodes of the path
	 */
	public abstract void addPenalty (double ratio, double perPathElement, ArrayList<Node> path);
	
	/**
	 * Clear all penalties added by the addPenalty method.
	 */
	public abstract void clearPenalties ();
	
}
