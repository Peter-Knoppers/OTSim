package nl.tudelft.otsim.Simulators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleAnchor;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.Events.Step;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.SpatialTools.Planar;

/**
 * Runtime counterpart of a {@link MeasurementPlan}.
 * 
 * @author Peter Knoppers
 */
public class Measurement extends JFrame implements Step, SimulatedObject, XYDataset, ShutDownAble {
	private static final long serialVersionUID = 1L;
	private final Point2D.Double[] area;
	private final Point2D.Double[] projectionPath;
	private final Simulator simulator;
	private final Scheduler scheduler;
	private HashMap<SimulatedObject, Integer> indices = new HashMap<SimulatedObject, Integer>();
	private ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
	private transient EventListenerList listenerList = new EventListenerList();
	DatasetGroup datasetGroup = null;
	final javax.swing.JTabbedPane tabbedPaneGraphs;
	private double distanceGranularity = 10.0; 		// [m]
	private double timeGranularity = 2;				// [s]
	private double minimumTimeRange = 600.0;		// [s]
    private final double distanceRange;
    private double timeRange = minimumTimeRange;	// [s]
    XYPlot speedPlot;

	/**
	 * Create a new Measurement.
	 * @param name String; name of the new Measurement
	 * @param area String; textual description of the outline of the measurement area
	 * @param projectionPath String; textual description of the center of the measurement area
	 * @param simulator {@link Simulator}; the Simulator that owns the moving {@link SimulatedObject SimulatedObjects}
	 * @param scheduler {@link Scheduler}; the Scheduler of the simulation
	 */
	public Measurement(String name, String area, String projectionPath, Simulator simulator, Scheduler scheduler) {
		setTitle(name);
		setMinimumSize(new Dimension(1000, 800));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());
		tabbedPaneGraphs = new javax.swing.JTabbedPane();
		tabbedPaneGraphs.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		getContentPane().add(tabbedPaneGraphs, java.awt.BorderLayout.CENTER);

		this.area = Planar.coordinatesToPoints(area.replaceAll("[()m,]", "").split(" "));
		this.projectionPath = Planar.coordinatesToPoints(projectionPath.replaceAll("[()m,]", "").split(" "));
		this.simulator = simulator;
		this.scheduler = scheduler;
		scheduler.enqueueEvent(0, this);
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
		JFreeChart trajectoryChart = ChartFactory.createScatterPlot(name, "Distance", "Time", this, PlotOrientation.HORIZONTAL, false, false, false);
        ValueAxis x = trajectoryChart.getXYPlot().getRangeAxis();
        x.setAutoRange(true);
        x.setLowerBound(0);
        x.setUpperBound(minimumTimeRange);
		ValueAxis y = trajectoryChart.getXYPlot().getDomainAxis();
		y.setAutoRange(true);
		y.setLowerBound(0);
		double length = 0;
		Point2D.Double prevPoint = null;
		for (Point2D.Double p : this.projectionPath) {
			if (null != prevPoint)
				length += prevPoint.distance(p);
			prevPoint = p;
		}
		distanceRange = length;
		y.setUpperBound(distanceRange);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) trajectoryChart.getXYPlot().getRenderer();
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesVisible(false);
		ChartPanel cp = new ChartPanel(trajectoryChart);
        cp.setFillZoomRectangle(true);
        cp.setMouseWheelEnabled(true);
		tabbedPaneGraphs.addTab("Trajectories", cp);
		
        NumberAxis xAxis = new NumberAxis("Time");
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis("Distance");
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        XYBlockRenderer blockRenderer = new XYBlockRenderer();
        blockRenderer.setBlockHeight(distanceGranularity);
        blockRenderer.setBlockWidth(timeGranularity);
        blockRenderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
        double[] boundaries = { 0, 10,  50};
        Color[] boundaryColors = { Color.RED, Color.YELLOW, Color.GREEN};
        PaintScale scale = new ColorPaintScale(boundaries, boundaryColors);
        blockRenderer.setPaintScale(scale);
        speedPlot = new XYPlot(createSpeedDataset(), xAxis, yAxis, blockRenderer);
        speedPlot.setBackgroundPaint(Color.lightGray);
        speedPlot.setDomainGridlinesVisible(false);
        speedPlot.setRangeGridlinePaint(Color.white);
        // Apply the settings that the ChartFactory applies to the scatterPlot 
        JFreeChart chart = new JFreeChart(name, JFreeChart.DEFAULT_TITLE_FONT, speedPlot, false);
        new StandardChartTheme("JFree").apply(chart);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        cp = new ChartPanel(chart);
        tabbedPaneGraphs.addTab("Speed contours", cp);
        System.out.println(String.format (Main.locale, "Block size %.2f x %.2f", blockRenderer.getBlockWidth(), blockRenderer.getBlockHeight()));

		setVisible(true);
	}
	
    private XYZDataset createSpeedDataset() {
        return new XYZDataset() {
            @Override
			public int getSeriesCount() {
                return 1;
            }
            
            private int distances() {
            	return (int) Math.ceil(distanceRange / distanceGranularity);
            }
            
            private int times() {
            	return (int) Math.ceil(timeRange / timeGranularity);
            }
            @Override
			public int getItemCount(int series) {
            	return distances() * times();
            }
            @Override
			public Number getX(int series, int item) {
                return new Double(getXValue(series, item));
            }
            @Override
			public double getXValue(int series, int item) {
            	return item / distances() * timeGranularity;
            }
            @Override
			public Number getY(int series, int item) {
                return new Double(getYValue(series, item));
            }
            @Override
			public double getYValue(int series, int item) {
                return item % distances() * distanceGranularity;
            }
            @Override
			public Number getZ(int series, int item) {
                return new Double(getZValue(series, item));
            }
            @Override
			public double getZValue(int series, int item) {
                double t = getXValue(series, item);
                double distance = getYValue(series, item);
                double sumSpeed = 0;
                int count = 0;
                try {
					for (Trajectory trajectory : trajectories) {
						int step = trajectory.getItem(t + timeGranularity) - (int) (timeGranularity / trajectory.timeStep);
						if (step < 0)
							continue;
						double trajectoryDistance = trajectory.getDistance(step);
						if (Double.isNaN(trajectoryDistance))
							continue;
						if (trajectoryDistance > distance + distanceGranularity)
							continue;
						double endDistance = trajectory.getDistance(step + (int) (timeGranularity / trajectory.timeStep));
						if (Double.isNaN(endDistance))
							continue;
						if (endDistance < distance)
							continue;
						double prevT = t;
						while (trajectory.getTime(++step) <= t + timeGranularity) {
							double nextTrajectoryDistance = trajectory.getDistance(step + 1);
							if (Double.isNaN(nextTrajectoryDistance))
								continue;
							double thisT = trajectory.getTime(step);
							double speed = (nextTrajectoryDistance - trajectoryDistance) / (thisT - prevT);
							sumSpeed += speed;
							count++;
							trajectoryDistance = nextTrajectoryDistance;
							prevT = thisT;
						}
					}
				} catch (ConcurrentModificationException e) {
					; // Ignore
				}
                if (0 == count)
                	return Double.NaN;
                return sumSpeed / count;
                /*
                double r = Math.sqrt((x * x + y * y) / 50);
                // Sync function
                if (0 == r)
                	return 1;
                return (Math.sin(r) / r);
                //return Math.sin(Math.sqrt(x * x + y * y) / 5.0);
                 */
            }
        	@Override
        	public void addChangeListener(DatasetChangeListener listener) {
        		listenerList.add(DatasetChangeListener.class, listener);
        	}

        	@Override
        	public void removeChangeListener(DatasetChangeListener listener) {
        		listenerList.remove(DatasetChangeListener.class, listener);
        	}
        	
            @Override
			public DatasetGroup getGroup() {
                return null;
            }
            
            @Override
			public void setGroup(DatasetGroup group) {
                // ignore
            }
            
            @Override
			public Comparable<String> getSeriesKey(int series) {
                return "speed";
            }
            
            @SuppressWarnings("rawtypes")
			@Override
			public int indexOf(Comparable seriesKey) {
                return 0;
            }
            
            @Override
			public DomainOrder getDomainOrder() {
                return DomainOrder.ASCENDING;
            }        
        };
    }

	@Override
	public boolean step(double now) {
		if (now > timeRange)
			timeRange = now;
		for (SimulatedObject vehicle : simulator.SampleMovables()) {
			if (Planar.polygonContainsPoint(area, vehicle.center(now))) {
				Integer index = indices.get(vehicle);
				Trajectory trajectory;
				if (null == index) {
					trajectory = new Trajectory(now);
					trajectories.add(trajectory);
					indices.put(vehicle, trajectories.indexOf(trajectory));
				} else
					trajectory = trajectories.get(index);
				double longitudinal = 0;
				double bestLongitudinal = java.lang.Double.NaN;
				double bestLateral = java.lang.Double.NaN;
				Point2D.Double vehicleLocation = vehicle.center(now);
				Point2D.Double prevPoint = null;
				double bestDistance = java.lang.Double.MAX_VALUE;
				for (Point2D.Double p : projectionPath) {
					if (null != prevPoint) {
						Line2D.Double lineSegment = new Line2D.Double(prevPoint, p);
						double thisDistance = Planar.distanceLineSegmentToPoint(lineSegment, vehicleLocation);
						if (thisDistance < bestDistance) {
							Point2D.Double projection = Planar.nearestPointOnLine(lineSegment, vehicleLocation);
							bestLongitudinal = longitudinal + prevPoint.distance(projection);
							bestLateral = thisDistance;
							// figure out if the vehicle is to the left or to the right of the projectionPath
							double projectionPathDirection = Math.atan2(lineSegment.y2 - lineSegment.y1, lineSegment.x2 - lineSegment.x1);
							double vehicleDirection = Math.atan2(vehicleLocation.y - prevPoint.y, vehicleLocation.x - prevPoint.x);
							double difference = (vehicleDirection - projectionPathDirection + 2 * Math.PI) % (2 * Math.PI);
							if (difference < Math.PI)
								bestLateral = -bestLateral;
							bestDistance = thisDistance;
						}
						longitudinal += prevPoint.distance(p);
					}
					prevPoint = p;
				}
				trajectory.extend(now, new Point2D.Double(bestLongitudinal, bestLateral));
			}
		}
		notifyListeners(new DatasetChangeEvent(this, null));	// This guess work actually works!
		if (0 == now % timeGranularity)
			speedPlot.notifyListeners(new PlotChangeEvent(speedPlot));
		scheduler.enqueueEvent(now + 1, this);
		return true;
	}

	@Override
	public void paint(double when, GraphicsPanel graphicsPanel) {
		graphicsPanel.setColor(Color.BLUE);
		graphicsPanel.setStroke(0);
		graphicsPanel.drawPolyLine(area);
	}

	@Override
	public Point2D.Double[] outline(double when) {
		return area;
	}

	private class Trajectory {
		final double startTime;
		final double timeStep = 1.0;
		private ArrayList<Point2D.Double> path = new ArrayList<Point2D.Double>();
		
		public Trajectory(double startTime) {
			this.startTime = startTime;
		}

		public void extend(double when, Point2D.Double where) {
			int index = (int) ((when - startTime) / timeStep);
			if (index < path.size())
				throw new Error("vehicle generated too many samples");
			while (index > path.size())
				path.add(null);
			path.add(new Point2D.Double(where.x, where.y));
		}

		public int size() {
			return path.size();
		}

		public double getTime(int item) {
			return startTime + item * timeStep;
		}

		public double getDistance(int item) {
			if ((item < 0) || (item >= path.size()))
				return Double.NaN;
			Point2D.Double p = path.get(item);
			if (null == p)
				return Double.NaN;
			return p.getX();
		}
		
		public int getItem(double time) {
			time -= startTime;
			if (time < 0)
				return -1;
			int step = (int) (time / timeStep);
			if (step > path.size())
				return -1;
			return step;
		}
	}

	@Override
	public Point2D.Double center(double when) {
		return null;
	}

	@Override
	public int getSeriesCount() {
		return trajectories.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int series) {
		return series;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int indexOf(Comparable seriesKey) {
		if (seriesKey instanceof Integer)
			return (Integer) seriesKey;
		return -1;
	}

	@Override
	public void addChangeListener(DatasetChangeListener listener) {
		listenerList.add(DatasetChangeListener.class, listener);
	}

	@Override
	public void removeChangeListener(DatasetChangeListener listener) {
		listenerList.remove(DatasetChangeListener.class, listener);
	}
	
	private void notifyListeners(DatasetChangeEvent event) {
		for (DatasetChangeListener dcl : listenerList.getListeners(DatasetChangeListener.class))
			dcl.datasetChanged(event);
	}

	@Override
	public DatasetGroup getGroup() {
		return datasetGroup;
	}

	@Override
	public void setGroup(DatasetGroup group) {
		datasetGroup = group;
	}

	@Override
	public DomainOrder getDomainOrder() {
		return DomainOrder.ASCENDING;
	}

	@Override
	public int getItemCount(int series) {
		if ((series < 0) || (series >= trajectories.size()))
			return 0;
		return trajectories.get(series).size();
	}

	@Override
	public Number getX(int series, int item) {
		if ((series < 0) || (series >= trajectories.size()))
			return null;
		return trajectories.get(series).getDistance(item);
	}

	@Override
	public double getXValue(int series, int item) {
		if ((series < 0) || (series >= trajectories.size()))
			return Double.NaN;
		return trajectories.get(series).getDistance(item);
	}

	@Override
	public Number getY(int series, int item) {
		if ((series < 0) || (series >= trajectories.size()))
			return null;
		return trajectories.get(series).getTime(item);
	}

	@Override
	public double getYValue(int series, int item) {
		if ((series < 0) || (series >= trajectories.size()))
			return Double.NaN;
		return trajectories.get(series).getTime(item);
	}

	@Override
	public void ShutDown() {
		dispose();
	}
	
	private class ColorPaintScale implements PaintScale {

		private double[] bounds;
		private Color[] boundColors;
		
		ColorPaintScale(double bounds[], Color boundColors[]) {
			if (bounds.length < 2)
				throw new Error("bounds must have >= 2 entries");
			if (bounds.length != boundColors.length)
				throw new Error("bounds must have same length as boundColors");
			for (int i = 1; i < bounds.length; i++)
				if (bounds[i] - bounds[i - 1] <= 0)
					throw new Error("bounds values must be strictly ascending");
			this.bounds = bounds;
			this.boundColors = boundColors;
		}
		@Override
		public double getLowerBound() {
			return -1;
		}

		private int mixComponent (double ratio, int low, int high) {
			double mix = low * (1 - ratio) + high * ratio;
			int result = (int) mix;
			if (result < 0)
				result = 0;
			if (result > 255)
				result = 255;
			return result;
		}
		
		@Override
		public Paint getPaint(double value) {
			int bucket;
			for (bucket = 0; bucket < bounds.length - 1; bucket++)
				if (value < bounds[bucket + 1])
					break;
			if (bucket >= bounds.length - 1)
				bucket = bounds.length - 2;
			double ratio = (value - bounds[bucket]) / (bounds[bucket + 1] - bounds[bucket]);
			
			Color mix = new Color (mixComponent(ratio, boundColors[bucket].getRed(), boundColors[bucket + 1].getRed()), 
					mixComponent(ratio, boundColors[bucket].getGreen(), boundColors[bucket + 1].getGreen()), 
					mixComponent(ratio, boundColors[bucket].getBlue(), boundColors[bucket + 1].getBlue()));
			return mix;
		}

		@Override
		public double getUpperBound() {
			return 1;
		}

	}

}

