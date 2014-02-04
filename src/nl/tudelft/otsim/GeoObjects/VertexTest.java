package nl.tudelft.otsim.GeoObjects;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.FileIO.StaXWriter;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * 
 * Test the methods in the Vertex class.
 * 
 * @author Peter Knoppers
 */
public class VertexTest {

	/**
	 * Test the creator taking three double arguments.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertexDoubleDoubleDouble() {
		Vertex v = new Vertex(1.23456, 2.34567, 3.45678);
		assertEquals("Check X", 1.23456, v.x, 0.0000001);
		assertEquals("Check Y", 2.34567, v.y, 0.0000001);
		assertEquals("Check Z", 3.45678, v.z, 0.0000001);
	}

	/**
	 * Test the creator taking a Vertex as argument.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertexVertex() {
		Vertex v1 = new Vertex(1, 2, 3);
		Vertex v2 = new Vertex(v1);
		assertFalse("Should not be the same instance", v1 == v2);
		assertEquals("Check X", v1.x, v2.x, 0.0000001);
		assertEquals("Check Y", v1.y, v2.y, 0.0000001);
		assertEquals("Check Z", v1.z, v2.z, 0.0000001);
		
		v1.x = 123;
		assertFalse("Should be independent", v1.x == v2.x);
		
	}

	/**
	 * Test the creator taking a Point2D.Double and a double as arguments.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertexDoubleDouble() {
		Vertex v = new Vertex(new Point2D.Double(1.23456, 2.34567), 3.45678);
		assertEquals("Check X", 1.23456, v.x, 0.0000001);
		assertEquals("Check Y", 2.34567, v.y, 0.0000001);
		assertEquals("Check Z", 3.45678, v.z, 0.0000001);
	}

	/**
	 * Test the creator taking no arguments.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertex() {
		Vertex v = new Vertex();
		assertEquals("Check X", 0, v.x, 0.0000001);
		assertEquals("Check Y", 0, v.y, 0.0000001);
		assertTrue("Check Z", Double.isNaN(v.z));
	}

	/**
	 * Test the creator taking a ParsedNode as argument.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertexParsedNode() {
		Vertex v1 = new Vertex(new Point2D.Double(1.23456, 2.34567), 3.45678);
		String xmlText = String.format(Locale.US, "<?xml version=\"1.0\"?>\r\n<bla><%s>%s</%s><%s>%s</%s><%s>%s</%s>\r\n</bla>\r\n",
					Vertex.XML_X, Double.toString(v1.x), Vertex.XML_X, 
					Vertex.XML_Y, Double.toString(v1.y), Vertex.XML_Y, 
					Vertex.XML_Z, Double.toString(v1.z), Vertex.XML_Z);
		System.out.println(xmlText);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlText.getBytes());
		ParsedNode pn = null;
		try {
			pn = new ParsedNode(inputStream);
		} catch (Exception e) {
			fail("Unexpected exception");
		}
		Vertex v2 = null;
		try {
			v2 = new Vertex(pn.getSubNode("bla", 0));
		} catch (Exception e) {
			fail ("Unexpected exception");
		}
		assertEquals("Reconstructed vertex should be at same location", 0, v1.distance(v2), 0.000001);
	}

	/**
	 * Test the creator taking a Coordinate as argument.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testVertexCoordinate() {
		Coordinate c = new Coordinate(10, 20, 30.5);
		Vertex v = new Vertex(c);
		assertEquals("X", c.x, v.x, 0.000001);
		assertEquals("Y", c.y, v.y, 0.000001);
		assertEquals("Z", c.z, v.z, 0.000001);
	}

	/**
	 * Test the writeVertexXML method.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testWriteVertexXML() {
		Vertex v1 = new Vertex(new Point2D.Double(1.23456, 2.34567), 3.45678);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StaXWriter sw = null;
		try {
			sw = new StaXWriter(outputStream);
		} catch (Exception e1) {
			fail("Caught unexpected exception");
		}
		try {
			assertTrue("Writer should go fine", v1.writeVertexXML(sw));
			sw.close();
		} catch (Exception e) {
			fail("Caught unexpected exception in creation of the XML text");
		}
		System.out.println("outputStream contains\"" + outputStream.toString() + "\"");
		assertEquals("Expected xml", 
				"<?xml version=\"1.0\"?>\n" +
				"<X>1.23456</X>\n" +
				"<Y>2.34567</Y>\n" +
				"<Z>3.45678</Z>\n", outputStream.toString());
	}

	@Test
	public void testWeightedVertex() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistanceTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetX() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetX() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetY() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetY() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetZ() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetZ() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPointDoubleDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPointDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPointVertex() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLog() {
		fail("Not yet implemented");
	}

	@Test
	public void testPaintGraphicsPanel() {
		fail("Not yet implemented");
	}

	@Test
	public void testPaintGraphicsPanelColor() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals2D() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistance() {
		fail("Not yet implemented");
	}

}
