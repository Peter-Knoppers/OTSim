package nl.tudelft.otsim.ModelIO;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GeoObjects.CrossSection;
import nl.tudelft.otsim.GeoObjects.CrossSectionElement;
import nl.tudelft.otsim.GeoObjects.Link;
import nl.tudelft.otsim.GeoObjects.Network;
import nl.tudelft.otsim.GeoObjects.RoadMarkerAlong;
import nl.tudelft.otsim.GeoObjects.TurnArrow;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.SpatialTools.DutchRD;
import nl.tudelft.otsim.SpatialTools.WGS84Converter;

/**
 * Import the Network from an OpenStreetMap (OSM) file
 * 
 * @author Peter Knoppers
 */
public class ImportOSM {

	final private static String tagEntry = "tag";
	
	/**
	 * Load a {@link Network} from an OpenStreatMap (OSM) file.
	 * @param fileName String; name of the OSM file.
	 * @return {@link Network}; the Network constructed from the OSM file
	 * @throws Exception
	 */
	public static Network loadOSM(String fileName) throws Exception {
		WGS84Converter converter = new DutchRD();
		/* Test the converter
		Point2D.Double in = new Point2D.Double(4.37, 52);
		Point2D.Double out = converter.meters(in);
		System.out.println(String.format(Main.locale, "%f,%f -> %f,%f", in.x, in.y, out.x, out.y));
		in = new Point2D.Double(6, 53);
		out = converter.meters(in);
		System.out.println(String.format(Main.locale, "%f,%f -> %f,%f", in.x, in.y, out.x, out.y));
		*/
		ParsedNode parser = new ParsedNode(fileName);
		System.out.println(parser.toString(fileName, 10));
		final String mainEntry = "osm";
		if (parser.size(mainEntry) != 1)
			throw new Exception("file should contain one \"" + mainEntry + "\" entity");
		ParsedNode main = parser.getSubNode(mainEntry, 0);
		final String nodeEntry = "node";
		HashMap<Long, OSMNode> nodes = new HashMap<Long, OSMNode>();
		for (int index = main.size(nodeEntry); --index >= 0; ) {
			ParsedNode node = main.getSubNode(nodeEntry, index);
			long id = Long.parseLong(node.getAttributeValue("id"));
			if (null != nodes.get(id))
				throw new Exception("Duplicate node in OSM file: " + id);
			nodes.put(id, new OSMNode(Double.parseDouble(node.getAttributeValue("lon")), 
					Double.parseDouble(node.getAttributeValue("lat")), index));
		}
		final String wayEntry = "way";
		final String refEntry = "ref";
		HashMap<Long, ParsedNode> ways = new HashMap<Long, ParsedNode>();
		final String ndEntry = "nd";
		for (int index = main.size(wayEntry); --index >= 0; ) {
			ParsedNode way = main.getSubNode(wayEntry, index);
			long id = Long.parseLong(way.getAttributeValue("id"));
			if (null != ways.get(id))
				throw new Exception("Duplicate way in OSM file: " + id);
			if (null == getOSMTag(way, "highway"))
					continue;;
			ways.put(id,  way);
			int nodeCount = way.size(ndEntry);
			if (nodeCount < 2) {
				System.err.println("way " + id + " has too few " + ndEntry + " entries");
				continue;
			}
			long ref = Long.parseLong(way.getSubNode(ndEntry, 0).getAttributeValue(refEntry));
			OSMNode node = nodes.get(ref);
			if (null == node)
				throw new Exception("way " + id + " refers to undefined node " + ref);
			node.incrementReferenceCount();
			ref = Long.parseLong(way.getSubNode(ndEntry, nodeCount - 1).getAttributeValue(refEntry));
			node = nodes.get(ref);
			if (null == node)
				throw new Exception("way " + id + " refers to undefined node " + ref);
			node.incrementReferenceCount();			
		}
		Network result = new Network();
		for (long key : nodes.keySet()) {
			OSMNode node = nodes.get(key);
			if (node.referenceCount > 0) {
				Point2D.Double location = converter.meters(new Point2D.Double(node.longitude, node.lattitude));
				result.addNode(String.format("n%d", key), node.number, location.x, location.y, 0d);
			}
		}
		int lastLinkNumber = 0; 
		for (long key : ways.keySet()) {
			ParsedNode wayNode = ways.get(key);
			String linkName = getOSMTag(wayNode, "name");
			if (null == linkName)
				linkName = "";
			else
				linkName += " ";
			String speedLimitString = getOSMTag(wayNode, "maxspeed");
			double maxSpeed = null == speedLimitString ? 50 : Double.parseDouble(speedLimitString);
			int ndCount = wayNode.size(ndEntry);
			ArrayList<Vertex> intermediateVertices = new ArrayList<Vertex>();
			OSMNode prevNode = null;
			for (int i = 0; i < ndCount; i++) {
				long nodeID = Long.parseLong(wayNode.getSubNode(ndEntry, i).getAttributeValue(refEntry));
				OSMNode node = nodes.get(nodeID);
				if (node.referenceCount > 0) {
					if (null != prevNode) {
						ArrayList<CrossSection> csList = new ArrayList<CrossSection>();
						ArrayList<CrossSectionElement> cseList = new ArrayList<CrossSectionElement>();
						CrossSection cs = new CrossSection(0, 0, cseList);
						CrossSectionElement cse = new CrossSectionElement(cs, "road", 5.0, new ArrayList<RoadMarkerAlong>(), null);
						cseList.add(cse);
						csList.add(cs);
						if (prevNode.number == node.number)
							System.err.println("Cannot handle circular roads; yet (" + linkName + ")");
						else {
							try {
								Link newLink = result.addLink(linkName + ++lastLinkNumber, prevNode.number, node.number, 123, false, csList, intermediateVertices);
								newLink.setMaxSpeed_w(maxSpeed);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						intermediateVertices = new ArrayList<Vertex>();
					}
					prevNode = node;
				} else {
					Point2D.Double location = converter.meters(new Point2D.Double(node.longitude, node.lattitude));					
					intermediateVertices.add(new Vertex(location.x, location.y, 0));
				}
			}
		}
		return result;
	}
	
	private static String getOSMTag(ParsedNode node, String key) {
		for (int i = node.size(tagEntry); --i >= 0; ) {
			ParsedNode tagNode = node.getSubNode(tagEntry, i);
			if (tagNode.getAttributeValue("k").equals(key))
				return tagNode.getAttributeValue("v");
		}
		return null;
	}
	
	static class OSMNode {
		private int referenceCount = 0;
		final double lattitude;
		final double longitude;
		final int number;
		
		public OSMNode(double longitude, double lattitude, int number) {
			this.longitude = longitude;
			this.lattitude = lattitude;
			this.number = number;
		}
		
		public void incrementReferenceCount() {
			referenceCount++;
		}
		
		public int getReferenceCount() {
			return referenceCount;
		}
	}
	
}
