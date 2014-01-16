package nl.tudelft.otsim.SpatialTools;

import static org.junit.Assert.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import nl.tudelft.otsim.GeoObjects.Vertex;

import org.junit.Test;

/**
 * Test the methods in the Planar class
 * 
 * @author Peter Knoppers
 */
public class PlanarTest {

	/**
	 * Test parser for String[] to Point2D.Double[]
	 */
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

	/**
	 * Test slicing of String[] and parsing to Point2D.Double[]
	 */
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

	/**
	 * Test fixRadix
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testFixRadix() {
		String in = "123.45abc";
		assertEquals("No comma in input should return unchanged input", Planar.fixRadix(in), in);
		String in2 = "123,456,78";
		assertEquals("Only first comma should be replaced", Planar.fixRadix(in2), "123.456,78");
	}

	/**
	 * Test computation of the length of an ArrayList<Vertex>
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testLength() {
		ArrayList<Vertex> in = new ArrayList<Vertex> ();
		assertEquals("empty list has effective length of 0 (this is actually debatable)", 0, Planar.length(in), 0.000001);
		in.add(new Vertex(10, 20, 30));
		assertEquals("List of one vertex has effective length of 0", 0, Planar.length(in), 0.000001);
		in.add(new Vertex(10, 20, 30));
		assertEquals("List of two identical vertices has effective length of 0", 0, Planar.length(in), 0.000001);
		in.remove(1);
		assertEquals("check remove op", 1, in.size());
		in.add(new Vertex(110, 220, 330));
		assertEquals("Length of two-vertex list", Math.sqrt(100 * 100 + 200 * 200 + 300 * 300), Planar.length(in), 0.0001);
		in.add(new Vertex(10, 20, 30));
		assertEquals("Length of three-vertex list", 2 * Math.sqrt(100 * 100 + 200 * 200 + 300 * 300), Planar.length(in), 0.0001);
	}

	/**
	 * Test rotateTranslatePolyLine
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRotateTranslatePolyLine() {
		Point2D.Double[] emptyArray = new Point2D.Double[0];
		assertEquals("Empty array in should result in empty array out", 0, Planar.rotateTranslatePolyLine(emptyArray, 10, 20, 30).length);
		Point2D.Double[] onePointArray = new Point2D.Double[1];
		onePointArray[0] = new Point2D.Double(1, 2);
		Point2D.Double[] result = Planar.rotateTranslatePolyLine(onePointArray, Math.PI / 2, 10, 20);
		assertEquals("One point array in should result in one point array out", 1, result.length);
		assertEquals("Expected result point", 0, new Point2D.Double(8, 21).distance(result[0]), 0.000001);
		Point2D.Double[] threePointArray = new Point2D.Double[3];
		threePointArray[0] = new Point2D.Double(1, 2);
		threePointArray[1] = new Point2D.Double(3, 4);
		threePointArray[2] = new Point2D.Double(-5, -6);
		result = Planar.rotateTranslatePolyLine(threePointArray, -Math.PI / 2, 10, 20);
		assertEquals("Three point array in should result in three point array out", 3, result.length);
		assertEquals("Expected result point", 0, new Point2D.Double(12, 19).distance(result[0]), 0.000001);
		assertEquals("Expected result point", 0, new Point2D.Double(14, 17).distance(result[1]), 0.000001);
		assertEquals("Expected result point", 0, new Point2D.Double(4, 25).distance(result[2]), 0.000001);
	}

	/**
	 * Test the rotate around origin method
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRotatePoint() {
		Point2D.Double p = new Point2D.Double (10, 0);
		Point2D.Double q = Planar.rotatePoint(p, Math.PI / 2);
		assertEquals("rotated point should be at expected location", 0, q.distance(new Point2D.Double(0, 10)), 0.000001);
		p = new Point2D.Double (10, 10);
		q = Planar.rotatePoint(p, Math.PI / 2);
		assertEquals("rotated point should be at expected location", 0, q.distance(new Point2D.Double(-10, 10)), 0.000001);
		q = Planar.rotatePoint(q, Math.PI / 4);
		assertEquals("rotated point should be at expected location", 0, q.distance(new Point2D.Double(-Math.sqrt(2) * 10, 0)), 0.000001);
	}

	/**
	 * Test translatePoint
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testTranslatePoint() {
		Point2D.Double p = new Point2D.Double(10, 0);
		Point2D.Double q = Planar.translatePoint(p, 5, 6);
		assertEquals("q should be translated p", 0, q.distance(new Point2D.Double(15, 6)), 0.000001);
		q = Planar.translatePoint(q, -12, -100);
		assertEquals("q should be translated p", 0, q.distance(new Point2D.Double(3, -94)), 0.000001);
	}

	/**
	 * Test nearestPointOnLine
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testNearestPointOnLine() {
		// Doing the math to figure out the correct solutions was not as easy as I expected
		Line2D.Double l = new Line2D.Double(6, 0, 0, 8);
		Point2D.Double p = new Point2D.Double(0, 0);
		Point2D.Double q = Planar.nearestPointOnLine(l, p);
		Point2D.Double expected = new Point2D.Double (8 / (6d / 8 + 8d / 6), 6 / (6d / 8 + 8d / 6));
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
		p = new Point2D.Double(3, 0);
		q = Planar.nearestPointOnLine(l, p);
		expected = new Point2D.Double (3 + 4 / (6d / 8 + 8d / 6), 3 / (6d / 8 + 8d / 6));
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
		p = new Point2D.Double (6, 0);
		q = Planar.nearestPointOnLine(l, p);		
		assertEquals("Distance to endpoint is 0", 0, new Point2D.Double(6, 0).distance(q), 0.000001);
		p = new Point2D.Double (12, 0);
		q = Planar.nearestPointOnLine(l, p);		
		expected = new Point2D.Double (12 - 8 / (6d / 8 + 8d / 6), -6 / (6d / 8 + 8d / 6));
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
		p = new Point2D.Double(0, 4);
		q = Planar.nearestPointOnLine(l, p);		
		expected = new Point2D.Double (4 / (6d / 8 + 8d / 6), 4 + 3 / (6d / 8 + 8d / 6));
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
		p = new Point2D.Double(3, 4);
		q = Planar.nearestPointOnLine(l, p);		
		expected = new Point2D.Double (3,4);
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
		p = new Point2D.Double(6, 8);
		q = Planar.nearestPointOnLine(l, p);		
		expected = new Point2D.Double (6 - 8 / (6d / 8 + 8d / 6), 8 - 6 / (6d / 8 + 8d / 6));
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Distance from expected location should be 0", 0, expected.distance(q), 0.00001);
	}

	/**
	 * Test distanceLineSegmentToPoint
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testDistanceLineSegmentToPoint() {
		Line2D.Double l = new Line2D.Double(6, 0, 0, 8);
		Point2D.Double p = new Point2D.Double(0, 0);
		double d = Planar.distanceLineSegmentToPoint(l, p);
		double expected = new Point2D.Double (8 / (6d / 8 + 8d / 6), 6 / (6d / 8 + 8d / 6)).distance(p);
		//System.out.println("e " + expected.x + ", " + expected.y);
		//System.out.println("q " + q.x + ", " + q.y);
		assertEquals("Expected distance", expected, d, 0.00001);
		p = new Point2D.Double(20, 0);
		assertEquals("Expected distance to end point", 14, Planar.distanceLineSegmentToPoint(l, p), 0.00001);
		p = new Point2D.Double(26, -20);
		assertEquals("Expected distance to end point", Math.sqrt(2) * 20, Planar.distanceLineSegmentToPoint(l, p), 0.00001);
		p = new Point2D.Double(-10, 18);
		assertEquals("Expected distance to end point", Math.sqrt(2) * 10, Planar.distanceLineSegmentToPoint(l, p), 0.00001);
	}

	/**
	 * Test polytonContainsPoint
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testPolygonContainsPoint() {
		// Simple triangle; a bit off the "grid" so we don't run into boundary cases
		Point2D.Double[] polygon = new Point2D.Double[3];
		polygon[0] = new Point2D.Double(10.2, 10.1);
		polygon[1] = new Point2D.Double(20.2, 10.1);
		polygon[2] = new Point2D.Double(20.2, 20.1);
		for (int x = -20; x <= 30; x++) {
			for (int y = -20; y <= 30; y++) {
				final Point2D.Double p = new Point2D.Double(x, y);
				// figure out the truth
				final boolean expected = (y > 10) && (x > 10) && (x <= 20) && (y < x);
				//System.out.println("p (" + x + "," + y + ") expects " + (expected ? "true" : "false"));
				if (expected)
					assertTrue("Is inside", Planar.polygonContainsPoint(polygon, p));
				else
					assertFalse("Is outside", Planar.polygonContainsPoint(polygon, p));
			}
		}
		// TODO write tests using a more "interesting" polygon
	}

	@Test
	public void testDistancePolygonToPoint() {
		Point2D.Double[] polygon = new Point2D.Double[3];
		polygon[0] = new Point2D.Double(10, 10);
		polygon[1] = new Point2D.Double(20, 10);
		polygon[2] = new Point2D.Double(20, 20);
		
		for (int x = -20; x <= 30; x++) {
			for (int y = -20; y <= 30; y++) {
				final Point2D.Double p = new Point2D.Double(x, y);
				// figure out the truth
				double expected;
				if ((y >= 10) && (x >= 10) && (x <= 20) && (y <= x))
					expected = 0;
				else if (y < 10) {
					if (x < 10)
						expected = p.distance(polygon[0]);
					else if (x > 20)
						expected = p.distance(polygon[1]);
					else
						expected = 10 - y;
				} else if (x > 20) {
					if (y < 10)
						expected = p.distance(polygon[1]);
					else if (y > 20)
						expected = p.distance(polygon[2]);
					else
						expected = x - 20;
				} else {
					if (y + x < 20)
						expected = p.distance(polygon[0]);
					else if (y + x > 40)
						expected = p.distance(polygon[2]);
					else
						expected = (y - x) / Math.sqrt(2);
				}
				System.out.println("p (" + x + "," + y + ") expects " + expected);
				assertEquals("Expected distance", expected, Planar.distancePolygonToPoint(polygon, p), 0.00001);
			}
		}
	}

	@Test
	public void testPolygonIntersectsPolygon() {
		// Simple triangle; a bit off the "grid" so we don't run into boundary cases
		Point2D.Double[] polygon1 = new Point2D.Double[3];
		polygon1[0] = new Point2D.Double(10.2, 10.1);
		polygon1[1] = new Point2D.Double(20.2, 10.1);
		polygon1[2] = new Point2D.Double(20.2, 20.1);
		for (int x = -20; x <= 30; x++) {
			for (int y = -20; y <= 30; y++) {
				// Simple square; on the grid
				Point2D.Double[] polygon2 = new Point2D.Double[4];
				polygon2[0] = new Point2D.Double(x, y);
				polygon2[1] = new Point2D.Double(x + 2, y);
				polygon2[2] = new Point2D.Double(x + 2, y + 2);
				polygon2[3] = new Point2D.Double(x, y + 2);
				// figure out the truth
				final boolean expected = (y > 8) && (y <= 20) && (x > 8) && (x <= 20) && (y < x + 2);
				System.out.println("p (" + x + "," + y + ") expects " + (expected ? "true" : "false"));
				if (expected)
					assertTrue("Is intersecting", Planar.polygonIntersectsPolygon(polygon1, polygon2));
				else
					assertFalse("Is disjunct", Planar.polygonIntersectsPolygon(polygon1, polygon2));
			}
		}
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

	/**
	 * Test the circleCoveringPoints method.
	 */
	@Test
	public void testCircleCoveringPoints() {
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		assertNull("Empty input list should return null", Planar.circleCoveringPoints(points));
		points.add(new Point2D.Double(123.4, 567.9));
		Circle c = Planar.circleCoveringPoints(points);
		assertEquals("radius of circle covering 1 point should be 0", 0, c.radius(), 0.00000001);
		assertEquals("center of circle covering 1 point should be the point", 0, c.center().distance(points.get(0)), 0.000001);
		points.clear();
		points.add(new Point2D.Double(1, 0));
		points.add(new Point2D.Double(-2, 0));
		c = Planar.circleCoveringPoints(points);
		assertEquals("radius of circle covering 2 point should be half the distance between those points", points.get(0).distance(points.get(1)) / 2, c.radius(), 0.002);
		assertEquals("center of circle coverint 2 points should be the half-way point", 0, c.center().distance(new Point2D.Double((points.get(0).x + points.get(1).x) / 2, (points.get(0).y + points.get(1).y) / 2)), 0.002);
		points.add(new Point2D.Double (-0.5, 0.5));
		c = Planar.circleCoveringPoints(points);
		assertEquals("adding a point clearly inside the circle should not alter the radius", points.get(0).distance(points.get(1)) / 2, c.radius(), 0.002);
		assertEquals("adding a point clearly inside the circle should not alter the center", 0, c.center().distance(new Point2D.Double((points.get(0).x + points.get(1).x) / 2, (points.get(0).y + points.get(1).y) / 2)), 0.000001);
		points.clear();
		// 4 points forming the corners of a square
		points.add(new Point2D.Double(0, 0));
		points.add(new Point2D.Double(2, 0));
		points.add(new Point2D.Double(2, 2));
		points.add(new Point2D.Double(0, 2));
		c = Planar.circleCoveringPoints(points);
		assertEquals("circle should be centered on the square it covers", 0, c.center().distance(new Point2D.Double(1, 1)), 0.002);
		assertEquals("radius should be half the diagonal of the square", Math.sqrt(8) / 2, c.radius(), 0.002);
		// Now run the test for previously found problems that were (supposedly) fixed
		testCircleCoveringPoints1();
		testCircleCoveringPoints2();
		testCircleCoveringPoints3();
	}
	
	/**
	 * Check that the circleCovereringPoints method behaves well. There has
	 * been a case where it failed (due to a bug) on this particular set of 
	 * points.
	 */
	public static void testCircleCoveringPoints1() {
	    // This set of points caused circleCoveringPoints to fail (never finish) when margin was 0.000001
	    ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
	    points.add(new Point2D.Double(86444.20734684722, 442696.03123710706));
	    points.add(new Point2D.Double(86444.18221227782, 442699.5311468564));
	    points.add(new Point2D.Double(86444.28509257668, 442699.530562569));
	    points.add(new Point2D.Double(86444.22020688458, 442696.0311640711));
	    points.add(new Point2D.Double(86444.20166811542, 442695.0313359289));
	    points.add(new Point2D.Double(86444.13678242332, 442691.531937431));
	    points.add(new Point2D.Double(86444.11598296637, 442691.5323772043));
	    points.add(new Point2D.Double(86444.1990681833, 442695.03139090055));
	    Planar.circleCoveringPoints(points);
	}

	/**
	 * Check that the circleCoveringsPoints method behaves well. There has been
	 * a case where it failed (due to another bug) on this particular set of
	 * points.
	 */
	public static void testCircleCoveringPoints2() {
	    // This set of points caused circleCoveringPoints to fail (never finish) when margin was 0.000001
	    ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
	    points.add(new Point2D.Double(50.896195, -200.443661));
	    points.add(new Point2D.Double(46.415221, -198.225357));
	    //points.add(new Point2D.Double(46.415221, -198.225357));
	    points.add(new Point2D.Double(51.164450, -186.377939));
	    //points.add(new Point2D.Double(51.164450, -186.377939));
	    points.add(new Point2D.Double(45.631733, -184.030182));
	    //points.add(new Point2D.Double(45.631733, -184.030182));
	    points.add(new Point2D.Double(36.660364, -190.882476));
	    points.add(new Point2D.Double(44.697691, -184.743599));
	    points.add(new Point2D.Double(39.150151, -185.853107));
	    points.add(new Point2D.Double(35.653602, -191.651437));
	    //points.add(new Point2D.Double(35.653602, -191.651437));
	    points.add(new Point2D.Double(36.559156, -197.589149));
	    //points.add(new Point2D.Double(36.559156, -197.589149));
	    points.add(new Point2D.Double(49.215535, -196.077677));
	    points.add(new Point2D.Double(47.287538, -196.463277));
	    //points.add(new Point2D.Double(47.287538, -196.463277));
	    points.add(new Point2D.Double(49.215535, -196.077677));
	    points.add(new Point2D.Double(47.287538, -196.463277));
	    //points.add(new Point2D.Double(47.287538, -196.463277));
	    points.add(new Point2D.Double(50.196116, -200.980581));
	    Planar.circleCoveringPoints(points);
	}

	@SuppressWarnings("static-method")
	private void testCircleCoveringPoints3() {
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
		Planar.circleCoveringPoints(points);
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
