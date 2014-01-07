package nl.tudelft.otsim.GeoObjects;

import java.awt.Color;
import java.util.ArrayList;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.FileIO.StaXWriter;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.InputValidator;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.SpatialTools.Planar;
import nl.tudelft.otsim.Utilities.Reversed;

/**
 * A Variable Message Sign (VMS) shows a time-varying message to passing traffic.
 * 
 * @author Peter Knoppers
 */
public class VMS extends CrossSectionObject {
	private String ID;
	/** Label in XML representation of a VMS */
	public static final String XMLTAG = "VMS";
	
	/** Label of ID in XML representation of a VMS */
	private static final String XML_ID = "ID";
	/** Label of longitudinalPosition in XML representation of a VMS */
	private static final String XML_LONGITUDINALPOSITION = "longitudinalPosition";
	/** Label of lateralCenter in XML representation of a VMS */
	private static final String XML_LATERALPOSITION = "lateralCenter";
	/** Label of width in XML representation of a VMS */
	private static final String XML_WIDTH = "width";

	/**
	 * Create a VMS from a parsed XML file.
	 * @param crossSectionElement {@link CrossSectionElement}; owner of the new VMS
	 * @param pn {@link ParsedNode}; the root of the VMS in the parsed XML file
	 * @throws Exception
	 */
	public VMS(CrossSectionElement crossSectionElement, ParsedNode pn) throws Exception {
		this.crossSectionElement = crossSectionElement;
		lateralReference = CrossSectionElement.LateralReferenceCenter;
		longitudinalPosition = lateralPosition = lateralWidth = Double.NaN;
		longitudinalLength = 1;
		ID = null;
		for (String fieldName : pn.getKeys()) {
			String value = pn.getSubNode(fieldName, 0).getValue();
			if (fieldName.equals(XML_ID))
				ID = value;
			else if (fieldName.equals(XML_LONGITUDINALPOSITION))
				longitudinalPosition = Double.parseDouble(value);
			else if (fieldName.equals(XML_LATERALPOSITION))
				lateralPosition = Double.parseDouble(value);
			else if (fieldName.equals(XML_WIDTH))
				lateralWidth = Double.parseDouble(value);
			else
				throw new Exception("VMS does not have a field " + fieldName);
		}
		if ((null == ID) || Double.isNaN(longitudinalPosition) || Double.isNaN(lateralPosition) || Double.isNaN(lateralWidth))
			throw new Exception("VMS is not completely defined" + pn.lineNumber + ", " + pn.columnNumber);
	}

	/**
	 * Create a new VMS, put it somewhere on the give CrossSectionElement and give it a unique ID.
	 * @param CSE CrossSectionElement; the CrossSectionElement that will own the new VMS
	 */
	public VMS(CrossSectionElement CSE) {
		longitudinalPosition = CSE.getCrossSection().getLongitudinalLength() / 2;	// put it half way
		lateralReference = CrossSectionElement.LateralReferenceCenter;
		lateralPosition = 0;
		lateralWidth = CSE.getWidth_r();
		longitudinalLength = 1;
		for (int idRank = 1; null == ID; idRank++) {
			ID = "" + idRank;
			for (CrossSectionObject cso : CSE.getCrossSectionObjects(VMS.class))
				if (((VMS) cso).ID.equals(ID)) {
					ID = null;	// try the next possible value
					break;
				}
		}
		this.crossSectionElement = CSE;
	}

	/**
	 * Return the ID of this VMS.
	 * @return String; the ID of this VMS
	 */
	public String getID_r() {
		return ID;
	}
	
	/**
	 * Change the ID of this VMS.
	 * @param newName String; the new name for this VMS
	 */
	public void setID_w(String newName) {
		this.ID = newName;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Create an {@link InputValidator} that ensures a proper ID for this VMS.
	 * @return {@link InputValidator} for a proper VMS ID
	 */
	public InputValidator validateID_v() {
		return new InputValidator(new InputValidator.CustomValidator() {
			@Override
			public boolean validate(String originalValue, String proposedValue) {
				if (! proposedValue.matches("[a-zA-Z_][-a-zA-Z0-9_.]*"))
					return false;	// not a decent name
				if (proposedValue.equals(originalValue))
					return true;	// current name is OK
				// Anything else must be unique among the VMS's in the Network
				return null == crossSectionElement.getCrossSection().getLink().network.lookupVMS(proposedValue);
			}
		});
	}
	
	/**
	 * Retrieve the lateral position of this VMS.
	 * @return Double; the lateral position of this VMS in m from
	 * the center line of the parent {@link CrossSectionElement}
	 */
	public double getLateralPosition_r() {
		return lateralPosition;
	}
	
	/**
	 * Change the lateral position of this VMS.
	 * @param lateralPosition Double; the new lateral position in m from the
	 * center line of the parent (@link CrossSectionElement}
	 */
	public void setLateralPosition_w(double lateralPosition) {
		this.lateralPosition = lateralPosition;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Return an {@link InputValidator} for the lateral position of this VMS.
	 * @return {@link InputValidator} for the lateral position of this VMS
	 */
	public InputValidator validateLateralPosition_v() {
		double range = crossSectionElement.getWidth_r() - lateralWidth;
		return new InputValidator("[-.0-9].*", -range / 2, range / 2);
	}
	
	/**
	 * Retrieve the lateral width of this VMS.
	 * @return Double; the lateral width of this VMS in m
	 */
	public double getWidth_r() {
		return lateralWidth;
	}
	
	/**
	 * Change the lateral width of this VMS.
	 * @param width Double; the new lateral width of this VMS in m
	 */
	public void setWidth_w(double width) {
		lateralWidth = width;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Return an {@link InputValidator} for the lateral width of this VMS.
	 * @return {@link InputValidator} for the lateral width of this VMS
	 */
	public InputValidator validateWidth_v() {
		double limit = crossSectionElement.getWidth_r() - Math.abs(lateralPosition);
		return new InputValidator("[.0-9].*", 0.1, limit);
	}
	
	/**
	 * Return the Vertices that form the outline of the detection area of this
	 * VMS
	 * @return ArrayList&lt;{@link Vertex}&gt; vertices of the polygon of this 
	 * VMS
	 */
	public ArrayList<Vertex> getPolygon_r() {
		ArrayList<Vertex> guideLine = Planar.slicePolyline(crossSectionElement.getLinkPointList(lateralReference, true, false), longitudinalPosition, longitudinalLength);
		ArrayList<Vertex> result = Planar.createParallelVertices(guideLine, null, -lateralWidth / 2,  -lateralWidth / 2);
		for (Vertex v : Reversed.reversed(Planar.createParallelVertices(guideLine, null, lateralWidth / 2, lateralWidth / 2)))
			result.add(v);
		return result;
	}
	
	/**
	 * Retrieve the longitudinalPosition of this VMS.
	 * @return Double; the longitudinalPosition of this VMS
	 */
	public double getLongitudinalPosition_r() {
		return longitudinalPosition;
	}
	
	/**
	 * Change the longitudinalPosition of this VMS.
	 * @param longitudinalPosition Double; the new longitudinalPosition of this
	 * VMS
	 */
	public void setLongitudinalPosition_w(double longitudinalPosition) {
		this.longitudinalPosition = longitudinalPosition;
	}
	
	/**
	 * Validate a proposed longitudinalPosition for this VMS.
	 * @return InputValidator for proposed values of the longitudinalPosition 
	 * of this VMS
	 */
	public InputValidator validateLongitudinalPosition_v() {
		double length = crossSectionElement.getCrossSection().getLongitudinalLength();
		return new InputValidator("[-.,0-9].*", -length, length);
	}
	
	/**
	 * A VMS can always be deleted.
	 * <br /> This method is only used by the {@link nl.tudelft.otsim.GUI.ObjectInspector}.
	 * @return Boolean; always true
	 */
	@SuppressWarnings("static-method")
	public boolean mayDeleteVMS_d() {
		return true;
	}
	
	/**
	 * Delete this VMS.
	 */
	public void deleteVMS_d() {
		crossSectionElement.deleteCrossSectionObject(this);
	}
	
	@Override
	public String toString() {
		return String.format(Main.locale, "VMS %s at longitudinalPosition %.3fm, width %.3fm", ID, longitudinalPosition, lateralWidth);
	}
	
	@Override
	public void paint(GraphicsPanel graphicsPanel) {
		graphicsPanel.setStroke(0F);
		graphicsPanel.setColor(Color.BLUE);
		ArrayList<Vertex> polygon = getPolygon_r();
		//System.out.println("polygon is " + GeometryTools.verticesToString(polygon));
		if (polygon.size() > 0)
			graphicsPanel.drawPolygon(polygon.toArray());
	}

	@Override
	public boolean writeXML(StaXWriter staXWriter) {
		return staXWriter.writeNodeStart(XMLTAG)
				&& staXWriter.writeNode(XML_ID, getID_r())
				&& staXWriter.writeNode(XML_LATERALPOSITION, Double.toString(lateralPosition))
				&& staXWriter.writeNode(XML_WIDTH, Double.toString(lateralWidth))
				&& staXWriter.writeNode(XML_LONGITUDINALPOSITION, Double.toString(longitudinalPosition))
				&& staXWriter.writeNodeEnd(XMLTAG);
	}


}
