package nl.tudelft.otsim.SpatialTools;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Test;

/**
 * Test the methods in the Planar class
 * 
 * @author Peter Knoppers
 */
public class PlanarTest {

	@SuppressWarnings("static-method")
	@Test
	public void testCoordinatesToPointsStringArray() {
		String[] in = {"123,45", "678.90", "12", "34"};
		Point2D.Double[] result = Planar.coordinatesToPoints(in);
		assertEquals("Length of result should be 2", 2, result.length);
		assertEquals("First point of result", result[0].distance(new Point2D.Double(123.45, 678.90)), 0, 0.00001);
		assertEquals("Second point of result", result[1].distance(new Point2D.Double(12, 34)), 0, 0.00001);
		String[] in2 = {"1234a", "4567"};
		try {
			Planar.coordinatesToPoints(in2);
			fail("Should have thrown a NumberFormatException");
		} catch (NumberFormatException e) {
			; // ignore
		}
		String[] in3 = {};
		Planar.coordinatesToPoints(in3);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testCoordinatesToPointsStringArrayIntInt() {
		String[] in = {"123,45", "678.90", "12", "34", "56", "78"};
		Point2D.Double[] result = Planar.coordinatesToPoints(in, 1, 5);
		assertEquals("Length of result should be 2", 2, result.length);
		assertEquals("First point of result", result[0].distance(new Point2D.Double(678.90, 12)), 0, 0.00001);
		assertEquals("Second point of result", result[1].distance(new Point2D.Double(34, 56)), 0, 0.00001);
		try {
			Planar.coordinatesToPoints(in, 1, 7);
			fail("Should have thrown an exception (array index out of bounds)");
		} catch (Exception e) {
			; // ignore
		}
		try {
			Planar.coordinatesToPoints(in, -1, 1);
			fail("Should have thrown an exception (array index out of bounds)");
		} catch (Exception e) {
			; // ignore
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFixRadix() {
		String in = "123.45abc";
		assertEquals("No comma in input should return unchanged input", Planar.fixRadix(in), in);
		String in2 = "123,456,78";
		assertEquals("Only first comma should be replaced", Planar.fixRadix(in2), "123.456,78");
	}

	@Test
	public void testLength() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotateTranslatePolyLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testRotatePoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testTranslatePoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testNearestPointOnLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistanceLineSegmentToPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolygonContainsPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistancePolygonToPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolygonIntersectsPolygon() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolyLineIntersectsLineDoubleArrayDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolyLineIntersectsLineDoubleArrayDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolyLineIntersectsPolyLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testLineSegmentIntersectsLineSegment() {
		fail("Not yet implemented");
	}

	@Test
	public void testLineIntersectsLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntersection() {
		fail("Not yet implemented");
	}

	@Test
	public void testNearestPointAtLink() {
		fail("Not yet implemented");
	}

	@Test
	public void testPointSideOfLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testCircleCoveringPoints() {
		// This really big case failed miserable due in stage 4 when dli and dlj got exactly the same value
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		points.add(new Point2D.Double(0.000000, -5.400000));
		points.add(new Point2D.Double(0.000000, -4.400000));
		points.add(new Point2D.Double(0.000000, -4.400000));
		points.add(new Point2D.Double(0.000000, -1.000000));
		points.add(new Point2D.Double(0.000000, -1.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(5.400000, 0.000000));
		points.add(new Point2D.Double(5.400000, -0.000000));
		points.add(new Point2D.Double(4.400000, 0.000000));
		points.add(new Point2D.Double(4.400000, -0.000000));
		points.add(new Point2D.Double(4.400000, 0.000000));
		points.add(new Point2D.Double(4.400000, -0.000000));
		points.add(new Point2D.Double(1.000000, 0.000000));
		points.add(new Point2D.Double(1.000000, -0.000000));
		points.add(new Point2D.Double(1.000000, 0.000000));
		points.add(new Point2D.Double(1.000000, -0.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(0.000000, -0.000000));
		points.add(new Point2D.Double(0.000000, 5.400000));
		points.add(new Point2D.Double(-0.000000, 5.400000));
		points.add(new Point2D.Double(0.000000, 4.400000));
		points.add(new Point2D.Double(-0.000000, 4.400000));
		points.add(new Point2D.Double(0.000000, 4.400000));
		points.add(new Point2D.Double(-0.000000, 4.400000));
		points.add(new Point2D.Double(0.000000, 1.000000));
		points.add(new Point2D.Double(-0.000000, 1.000000));
		points.add(new Point2D.Double(0.000000, 1.000000));
		points.add(new Point2D.Double(-0.000000, 1.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(-0.000000, 0.000000));
		points.add(new Point2D.Double(-5.400000, -0.000000));
		points.add(new Point2D.Double(-4.400000, -0.000000));
		points.add(new Point2D.Double(-4.400000, -0.000000));
		points.add(new Point2D.Double(-1.000000, -0.000000));
		points.add(new Point2D.Double(-1.000000, -0.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(0.000000, -0.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(0.000000, -0.000000));
		points.add(new Point2D.Double(0.000000, 0.000000));
		points.add(new Point2D.Double(1.000000, 0.000000));
		points.add(new Point2D.Double(1.000000, -5.400000));
		points.add(new Point2D.Double(1.000000, -4.400000));
		points.add(new Point2D.Double(1.000000, -4.400000));
		points.add(new Point2D.Double(1.000000, -1.000000));
		points.add(new Point2D.Double(1.000000, -1.000000));
		points.add(new Point2D.Double(1.000000, -0.000000));
		points.add(new Point2D.Double(1.000000, 0.000000));
		points.add(new Point2D.Double(1.000000, -5.400000));
		points.add(new Point2D.Double(1.000000, -4.400000));
		points.add(new Point2D.Double(1.000000, -4.400000));
		points.add(new Point2D.Double(1.000000, -1.000000));
		points.add(new Point2D.Double(1.000000, -1.000000));
		points.add(new Point2D.Double(1.000000, -0.000000));
		points.add(new Point2D.Double(4.400000, 0.000000));
		points.add(new Point2D.Double(4.400000, -5.400000));
		points.add(new Point2D.Double(4.400000, -4.400000));
		points.add(new Point2D.Double(4.400000, -4.400000));
		points.add(new Point2D.Double(4.400000, -1.000000));
		points.add(new Point2D.Double(4.400000, -1.000000));
		points.add(new Point2D.Double(4.400000, -0.000000));
		points.add(new Point2D.Double(4.400000, 0.000000));
		points.add(new Point2D.Double(4.400000, -5.400000));
		points.add(new Point2D.Double(4.400000, -4.400000));
		points.add(new Point2D.Double(4.400000, -4.400000));
		points.add(new Point2D.Double(4.400000, -1.000000));
		points.add(new Point2D.Double(4.400000, -1.000000));
		points.add(new Point2D.Double(4.400000, -0.000000));
		points.add(new Point2D.Double(5.400000, 0.000000));
		points.add(new Point2D.Double(5.400000, -5.400000));
		points.add(new Point2D.Double(5.400000, -4.400000));
		points.add(new Point2D.Double(5.400000, -4.400000));
		points.add(new Point2D.Double(5.400000, -1.000000));
		points.add(new Point2D.Double(5.400000, -1.000000));
		points.add(new Point2D.Double(5.400000, -0.000000));
		Circle result = Planar.circleCoveringPoints(points);
		fail("Not yet implemented");
	}

	@Test
	public void testTestCircleCoveringPoints() {
		fail("Not yet implemented");
	}

	@Test
	public void testTestCircleCoveringPoints2() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntersectRayAndCircle() {
		fail("Not yet implemented");
	}

	@Test
	public void testPointsToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testVerticesToStringArrayListOfVertex() {
		fail("Not yet implemented");
	}

	@Test
	public void testVerticesToStringArrayListOfVertexBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testLine2DToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGeneralPathToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLogPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvexHull() {
		fail("Not yet implemented");
	}

	@Test
	public void testLineIntersectsPolygon() {
		fail("Not yet implemented");
	}

	@Test
	public void testSlicePolyline() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreatePartlyParallelVertices() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateParallelVerticesArrayListOfVertexArrayListOfVertexDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateParallelVerticesArrayListOfVertexArrayListOfVertexDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateParallelVerticesArrayListOfVertexDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testClosePolyline() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAlignment() {
		fail("Not yet implemented");
	}

	@Test
	public void testArrayListOfPointsToArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpandBoundingBox() {
		fail("Not yet implemented");
	}

}
