package nl.tudelft.otsim.GeoObjects;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import nl.tudelft.otsim.FileIO.StaXWriter;

import org.junit.Test;

/**
 * Test the methods in the Node class
 * <br /> This test is (currently) very incomplete
 * 
 * @author Peter Knoppers
 *
 */
public class NodeTest {

	private final double laneWidth = 3.0;
	private final double grassWidth = 1.0;
	private final double stripeRoom = 0.2;
	private final double stripeWidth = 0.1;
	
	private Link createLink (Network network, String name, Node from, Node to, int laneCount) {
		if (0 == laneCount)
			return null;
		ArrayList<CrossSection> csl = new ArrayList<CrossSection>();
		ArrayList<CrossSectionElement> csel = new ArrayList<CrossSectionElement>();
		CrossSection cs = new CrossSection(0, 0, csel);
		csel.add(new CrossSectionElement(cs, "grass", grassWidth, new ArrayList<RoadMarkerAlong>(), null));
		ArrayList<RoadMarkerAlong> rmal = new ArrayList<RoadMarkerAlong>();
		rmal.add(new RoadMarkerAlong("|", stripeRoom / 2 + stripeWidth));
		for (int i = 1; i < laneCount; i++)
			rmal.add(new RoadMarkerAlong(":", i * (laneWidth + stripeRoom) + stripeRoom / 2 + stripeWidth));
		rmal.add(new RoadMarkerAlong("|", laneCount * (laneWidth + stripeWidth) + stripeRoom / 2 + stripeWidth));
		csel.add(new CrossSectionElement(cs, "road", laneCount * (laneWidth + stripeRoom) + stripeRoom, rmal, null));
		csel.add(new CrossSectionElement(cs, "grass", grassWidth, new ArrayList<RoadMarkerAlong>(), null));
		cs.setCrossSectionElementList_w(csel);
		csl.add(cs);
		return network.addLink(name, from.getNodeID(), to.getNodeID(), from.distance(to), false, csl, new ArrayList<Vertex>());
	}
	
	
	
	/**
	 * Test junction expansion
	 */
	@Test
	public void testFixLinkConnections() {
		String[] testJunctions = {
				"2,0,-90/0,1,0/0,1,180:2.0,1.0//"	// Simple T junction
		};
		double cx = 0;
		double cy = 0; 
		double cz = 0;
		for (String testJunction : testJunctions) {
			Junction junction = new Junction(testJunction.split(":")[0]);
			Network network = new Network();
			Node junctionNode = network.addNode ("junction", network.nextNodeID(), cx, cy, cz);
			int legCount = junction.legCount();
			Node[] otherNodes = new Node[legCount];
			Link[] incomingLinks = new Link[legCount];
			Link[] outgoingLinks = new Link[legCount];
			
			for (int legNo = 0; legNo < legCount; legNo++) {
				Junction.Leg leg = junction.getLeg(legNo);
				final double distance = 100;
				otherNodes[legNo] = network.addNode ("neighborNode" + legNo, network.nextNodeID(), cx + distance * Math.cos(Math.toRadians(leg.angle)), cy + distance * Math.sin(Math.toRadians(leg.angle)), cz);
				incomingLinks[legNo] = createLink(network, "feedLink" + legNo, otherNodes[legNo], junctionNode, leg.inLaneCount);
				outgoingLinks[legNo] = createLink(network, "exitLink" + legNo, junctionNode, otherNodes[legNo], leg.outLaneCount);
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			StaXWriter writer = null;
			try {
				writer = new StaXWriter(outputStream);
			} catch (Exception e) {
				fail("Caught unexpected exception in creation of the StaXWriter");
			}
			network.writeXML(writer);
			writer.close();
			String xmlText = outputStream.toString();
			System.out.println(xmlText);
			String expected = testJunction.split(":")[1];
			for (int legNo = 0; legNo < legCount; legNo++) {
				String connections = expected.split("/")[legNo];
				assertEquals ("Test description error; number of lanes in feeding link does not match lane count", connections.split(",").length, junction.getLeg(legNo).inLaneCount);
				ArrayList<Lane> incomingLanes = incomingLinks[legNo].getCrossSections_r().get(0).collectLanes();
				assertEquals("incoming link has unexpected number of lanes", junction.getLeg(legNo).inLaneCount, incomingLanes.size());
				int laneIndex = 0;
				for (String subConnection : connections.split(",")) {
					Lane incomingLane = incomingLanes.get(laneIndex++);
					System.out.println("Incoming lane is " + incomingLane.toString());
					ArrayList<Lane> connectingLanes = incomingLane.getDownLanes_r();
					for (Lane l : connectingLanes) {
						System.out.print("Connecting lane " + l.toString() + " -> [");
						ArrayList<Lane> leavingLanes = l.getDownLanes_r();
						for (Lane l2 : leavingLanes)
							System.out.print(l2.toString() + " ");
						System.out.println("]");
						
					}
					ArrayList<Boolean> connectionChecked = new ArrayList<Boolean> (incomingLanes.size());
					for (int i = 0; i < incomingLanes.size(); i++)
						connectionChecked.add(new Boolean(false));
					for (String subSubConnection : subConnection.split("\\+")) {
						int outLinkNo = Integer.parseInt(subSubConnection.split("\\.")[0]);
						int outLaneNo = Integer.parseInt(subSubConnection.split("\\.")[1]);
						System.out.println("outLinkNo " + outLinkNo + ", outLaneNo " + outLaneNo);
					}
				}
			}
		}

		fail("Not yet implemented");
	}

	class Junction {
		public class Leg {
			final int inLaneCount;
			final int outLaneCount;
			final double angle;
			
			public Leg(int inLaneCount, int outLaneCount, double angle) {
				this.inLaneCount = inLaneCount;
				this.outLaneCount = outLaneCount;
				this.angle = angle;
			}
			
		}

		private ArrayList<Leg> legs = new ArrayList<Leg>();
		
		public Junction (String description) {
			for (String legString : description.split("/")) {
				String fields[] = legString.split(",");
				legs.add(new Leg(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), Double.parseDouble(fields[2])));
			}
		}
		
		public Leg getLeg(int index) {
			return legs.get(index);
		}
		
		public int legCount() {
			return legs.size();
		}
	}
}
