package nl.tudelft.otsim.GUI;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.Events.Step;
import nl.tudelft.otsim.GeoObjects.SimplePolygon;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.SimulatedObject;
import nl.tudelft.otsim.SpatialTools.Circle;
import nl.tudelft.otsim.SpatialTools.Planar;
import nl.tudelft.otsim.Utilities.TimeScaleFunction;

/**
 * Superimpose a fake fundamental diagram on top of the simulation window
 * 
 * @author Peter Knoppers
 */
public class FakeFundamentalDiagram implements SimulatedObject, Step {
	final SimplePolygon areaCovered;
	final TimeScaleFunction densities;
	final Scheduler scheduler;
	final double intervalTime;
	/** Determines whether a graph or a disc is drawn */
	public boolean drawGraph = false;
	
	/**
	 * Create a FakeFundamentalDiagram
	 * @param polygonPoints String; textual description of the outline to fill
	 * @param timeDensityValues String; textual description of a {@link TimeScaleFunction} that generates the density values
	 * @param scheduler {@link Scheduler} the Scheduler of this simulation
	 * @param intervalTime Double; the update interval time of this FakeFundamentalDiagram
	 */
	public FakeFundamentalDiagram (String polygonPoints, String timeDensityValues, Scheduler scheduler, double intervalTime) {
		ArrayList<Vertex> points = new ArrayList<Vertex>();
		String[] fields = polygonPoints.split("\t");
		for (Point2D.Double p : Planar.coordinatesToPoints(fields))
			points.add(new Vertex(p, 0));
		areaCovered = new SimplePolygon(points);
		densities = new TimeScaleFunction(timeDensityValues);
		this.scheduler = scheduler;
		this.intervalTime = intervalTime;
		scheduler.enqueueEvent(0, this);
	}

	@Override
	public boolean step(double now) {
		scheduler.enqueueEvent(now + intervalTime, this);
		return true;
	}

	Circle circle = null;
	private double maxX = 150;		// [veh / km] 
	private double maxY = 2500;		// [veh / h]
	
	private void initScaling() {
		if (null == circle)
			circle = Planar.circleCoveringPoints(areaCovered.getProjection());		
	}
	private double scaleX(double x) {
		return circle.center().x - circle.radius() / 2 + (x / maxX) * circle.radius(); 
	}
	
	private double scaleY(double y) {
		return circle.center().y - circle.radius() / 2 + (y / maxY) * circle.radius();
	}
	
	private Point2D.Double scaleXY(double x, double y) {
		return new Point2D.Double (scaleX(x), scaleY(y));
	}
	
	private void paintGraph(double when, GraphicsPanel graphicsPanel) {
		// Draw the graph centered in the circle
		graphicsPanel.setStroke((float) (circle.radius() / 10));
		graphicsPanel.setColor(Color.RED);
		double flow = densities.getFactor((Math.floor(when / intervalTime)) * intervalTime);
		graphicsPanel.drawLine(scaleXY(flow, 0), scaleXY(flow, maxY));
		graphicsPanel.setStroke((float) (circle.radius() / 20));
		graphicsPanel.setColor(Color.BLACK);
		Point2D.Double[] points = {
				scaleXY(1, 20),
				scaleXY(15, 1300),
				scaleXY(17, 1500),
				scaleXY(22, 1750),
				scaleXY(30, 1950),
				scaleXY(40, 2000),
				scaleXY(45, 2000),
				scaleXY(52, 1950),
				scaleXY(60, 1800),
				scaleXY(144, 22)
		};
		graphicsPanel.drawPolyLine(points, false);
		graphicsPanel.setColor(Color.WHITE);
		graphicsPanel.drawLine(scaleXY(0, maxY), scaleXY(0, 0));
		graphicsPanel.drawLine(scaleXY(0, 0), scaleXY(maxX - 4, 0));		
	}
	
	static private Color ratioToColor(double ratio) {
		int red = 255;
		int green = 255;
		if (ratio > 0.5)
			green = (int) (510 * (1d - ratio));
		else
			red = (int) (510 * ratio);
		// make sure
		if (red < 0)
			red = 0;
		if (red > 255)
			red = 255;
		if (green < 0)
			green = 0;
		if (green > 255)
			green = 255;
		return new Color(red, green, 0);
	}
	
	private void drawDisc(double when, GraphicsPanel graphicsPanel) {
		double flow = densities.getFactor((Math.floor(when / intervalTime)) * intervalTime);
		Color color = ratioToColor(flow / maxX * 1.2);
		graphicsPanel.drawDisc(circle.center(), color, (int) (circle.radius() * graphicsPanel.getZoom()));		
	}
	
	@Override
	public void paint(double when, GraphicsPanel graphicsPanel) {
		initScaling();
		graphicsPanel.setColor(Color.LIGHT_GRAY);
		graphicsPanel.drawPolygon(areaCovered.getProjection());
		if (drawGraph)
			paintGraph(when, graphicsPanel);
		else
			drawDisc(when, graphicsPanel);
	}

	@Override
	public Point2D.Double[] outline(double when) {
		return areaCovered.getProjection();
	}

	@Override
	public Point2D.Double center(double when) {
		return Planar.circleCoveringPoints(outline(when)).center();
	}
	
}
