package nl.tudelft.otsim.ShortesPathAlgorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.tudelft.otsim.GeoObjects.Network;
import nl.tudelft.otsim.GeoObjects.Node;
import nl.tudelft.otsim.TrafficDemand.TripPattern;
import nl.tudelft.otsim.TrafficDemand.TripPatternPath;

/**
 *
 * @author gtamminga
 */
public class CreatePaths {
   
    /**
     * Create paths between all pairs of nodes, if possible.
     * Save the paths in a list of paths
     */
    private static final long MEGABYTE = 1024L * 1024L;

    private static long bytesToMegabytes(long bytes) {
      return bytes / MEGABYTE;
    }
    
    public static void CreatePathsTripPatterns(Network network, ArrayList<TripPattern> tripPatternList) {
    	class CompareNodeNumbers implements Comparator<Path> {
			@Override
			public int compare(Path path1, Path path2) {
				return path1.getNodeList().get(0).getNodeID() - path2.getNodeList().get(0).getNodeID();
			}
		}
    	
    	// Generate a list of all paths from the tripPatternList
    	ArrayList<Path> pathList = new ArrayList<Path>();
		for (TripPattern tp : tripPatternList)
			for (TripPatternPath tpp : tp.getTripPatternPathList())
				pathList.addAll(tpp.getPathList());
		// sort pathList by NodeID of start node
		Collections.sort(pathList, new CompareNodeNumbers());
		
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(network);
		// Collect the starting nodes of all paths in pathList
		ArrayList<Node> startNodes = new ArrayList<Node>();
		Node prevNode = null;
		for (Path path : pathList) {
			Node startNode = path.getNodeList().get(0);
			if ((null == prevNode) || (startNode.getNodeID() != prevNode.getNodeID()))
				startNodes.add(startNode);
			prevNode = startNode;
		}
		// TODO There is no need for ArrayList<Node> startNodes; the loop below 
		// that uses one startNode at a time can be merged with the loop above 
		// that generates the startNodes.
        int pathIndex = 0;
		for (Node startNode: startNodes) {
	        // Find all routes from a certain node to all other nodes
	        dijkstra.execute(startNode);
	        
	        while (pathIndex < pathList.size()) {
	        	Path path = pathList.get(pathIndex);
	        	if (path.getNodeList().get(0).getNodeID() > startNode.getNodeID())
	        		break;
	        	if (path.getNodeList().get(0).equals(startNode)) {
			        Node toNode = path.getNodeList().get(path.getNodeList().size() - 1);
			        Node fromNode = path.getNodeList().get(0);
		        	if (fromNode.equals(startNode)) {
		        		ArrayList<Node> getPath = dijkstra.getPathNodes(toNode);
		        		if (getPath != null)
		        			path.setNodeList(getPath);
		        		else	// TODO Do you really want to continue when this happens???
		        			System.out.println("no valid path found between " + fromNode.getNodeID() + " and " + toNode.getNodeID());
		        	}
				}
	        	pathIndex++; 
        	}
        }
    }

}