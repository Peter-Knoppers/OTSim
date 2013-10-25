package nl.tudelft.otsim.ShortesPathAlgorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tudelft.otsim.GeoObjects.Link;
import nl.tudelft.otsim.GeoObjects.Network;
import nl.tudelft.otsim.GeoObjects.Node;

/**
 *
 * @author not gtamminga
 */
/**
 * from the Internet (devogella)
 * http://www.vogella.com/articles/JavaAlgorithmsDijkstra/article.html#dijkstra
 * @author gtamminga
 */
public class DijkstraAlgorithm extends ShortestPathAlgorithm {
	//private final List<Node> nodes;
	private final Collection<Link> edges;
	private Set<Node> settledNodes;
	private Set<Node> unSettledNodes;
	private Map<Node, Node> predecessors;
	private Map<Node, Double> cost;
	private final int totalNodes;
	private Node startNode;
	private Node endNode;
	int pathNumber;	// Ensure that plain Dijkstra returns only one path

	/**
	 * Prepare to run the Dijkstra algorithm.
	 * @param network {@link Network}; the Network to run Dijkstra on
	 */
	public DijkstraAlgorithm(Network network) {
		super (network);
		totalNodes = network.getAllNodeList(true).size();
		edges = network.getLinkList();
	}
	
	/**
	 * Run the Dijkstra algorithm to compute routes and distances to a specific {@link Node}.
	 * @param startNode {@link Node}; the Node to compute the routes and distances for 
	 */
	@Override
	public void execute(Node startNode, Node endNode) {
		pathNumber = 0;
		this.endNode = endNode;
		if (this.startNode == startNode)
			return;
		this.startNode = startNode;
		settledNodes = new HashSet<Node>();
		unSettledNodes = new HashSet<Node>();
		cost = new HashMap<Node, Double>();
		predecessors = new HashMap<Node, Node>();
		cost.put(startNode, 0.0);
		unSettledNodes.add(startNode);
		while (unSettledNodes.size() > 0) {
			Node node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			List<Node> adjacentNodes = getNeighbors(node);
			for (Node target : adjacentNodes) {
				if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
					cost.put(target, getShortestDistance(node) + getDistance(node, target));
					predecessors.put(target, node);
					unSettledNodes.add(target);
				}
			}
		}
		if (totalNodes != settledNodes.size())
			System.out.println(String.format("Dijkstra: Disjunct network: total nodes; %s, settled nodes: %d, start node %s", totalNodes, settledNodes.size(), startNode.toString()));
	}

	@Override
	public double getCost () {
		return cost.get(endNode);
	}

	private double getDistance(Node node, Node target) {
		for (Link link : edges)
			if (link.getFromNodeExpand().equals(node) && link.getToNodeExpand().equals(target))
				return link.getLength();
		throw new RuntimeException("Should not happen");
	}

	private List<Node> getNeighbors(Node node) {
		List<Node> neighbors = new ArrayList<Node>();
		for (Link link : edges)
			if (link.getFromNodeExpand().equals(node) && !isSettled(link.getToNodeExpand()))
				neighbors.add(link.getToNodeExpand());
		return neighbors;
	}

	private Node getMinimum(Set<Node> vertexes) {
		Node minimum = null;
		for (Node node : vertexes)
			if (minimum == null)
				minimum = node;
			else if (getShortestDistance(node) < getShortestDistance(minimum))
				minimum = node;
		return minimum;
	}

	private boolean isSettled(Node nodes) {
		return settledNodes.contains(nodes);
	}

	private double getShortestDistance(Node nodeB) {
		Double d = cost.get(nodeB);
		if (d == null)
			return Double.MAX_VALUE;
		return d;
	}

	/**
	 * Create a path (a set of {@link Link Links} connecting a set of {@link Node Nodes}.
	 * <br /> Remark: assumes that there is no more than one link between any pair of nodes. 
	 * @return ArrayList&lt;{@link Link}&gt;; the list of Links that form the path
	 */
	@Override
	public ArrayList<Link> getPathLinks () {
    	ArrayList<Link> pathLinks = new ArrayList<Link>();
        Node previousNode = null;
        for (Node node : getPathNodes()) {
            if (null != previousNode)
                for (Link link : edges)
                    if (link.getFromNodeExpand().equals(previousNode) && link.getToNodeExpand().equals(node))
                    	pathLinks.add(link);
            previousNode = node;
        }
        Collections.reverse(pathLinks);
        return pathLinks;
    }
	
	/**
	 * This method returns the path from the source to the selected target
	 * @return ArrayList&lt;{@link Node}&gt;; the set of Nodes that form the
	 * source to the target 
	 */
	@Override
	public ArrayList<Node> getPathNodes() {
		// Check if a path exists
		if ((null == predecessors.get(endNode)) || (pathNumber != 1))
			throw new Error("No (additional) path exists");
		ArrayList<Node> pathNodes = new ArrayList<Node>();
		Node step = endNode;
		pathNodes.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			pathNodes.add(step);
		}
		// Put it into the correct order
		Collections.reverse(pathNodes);		
		return pathNodes;
	}

	@Override
	public boolean hasNext() {
		return (0 == pathNumber++) && (cost.get(endNode) != null);
	}

}