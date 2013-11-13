package nl.tudelft.otsim.SpatialTools;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Test;

public class PlanarTest {

	@Test
	public void testCoordinatesToPointsStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testCoordinatesToPointsStringArrayIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testFixRadix() {
		fail("Not yet implemented");
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
