package nl.tudelft.otsim.ModelIO;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import nl.tudelft.otsim.GUI.FileDialog;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GUI.Model;
import nl.tudelft.otsim.GeoObjects.CrossSection;
import nl.tudelft.otsim.GeoObjects.CrossSectionElement;
import nl.tudelft.otsim.GeoObjects.Node;
import nl.tudelft.otsim.GeoObjects.RoadMarkerAlong;
import nl.tudelft.otsim.GeoObjects.TurnArrow;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.TrafficDemand.TripPattern;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class ImportModelShapeWizard implements ActionListener {
    private static JPanel cards = new JPanel(new CardLayout());
	private int cardIndex ;
	private JButton cancelButton;
	private JButton finishButton;
    private JButton nextButton;
    private JButton prevButton;

	private JCheckBox optionNetwork;
	private JCheckBox optionTripPattern;

	private JFrame frame;
    private static FileChooser fChooser;
    private Model importedModel = new Model();
    private static String fileImportedMatrix;
	private static TableModelImport shapeImport = null;	
	private static int index; 
	private static int fileCount;

	public ImportModelShapeWizard() {
		//Class<?> klass;
		//klass = Coordinate.class;
		//URL location = klass.getResource('/' + klass.getName().replace(".", "/") + ".class");
		//System.out.print(location);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// Why? It does not actually work because it is overridden later!
        frame.setPreferredSize(new Dimension(700, 400));
        cards.setBorder(BorderFactory.createLineBorder(Color.black));
        cardIndex = 0;  // start at the initial/first card
       
    	JPanel choices = new JPanel() ;
        choices.setLayout(new GridBagLayout());	        
        GridBagConstraints gbConstraints = new GridBagConstraints();

        gbConstraints.anchor = GridBagConstraints.NORTHWEST;
        gbConstraints.insets = new Insets(15,30,0,0);
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        optionNetwork= new JCheckBox("Network Import (shape format)");
        optionNetwork.addActionListener(this);
        optionNetwork.setEnabled(true);
        choices.add(optionNetwork, gbConstraints);	
        gbConstraints.weightx = 0.4;
        gbConstraints.weighty = 0.4;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        optionTripPattern = new JCheckBox("TripPattern import (Origin, Destination, number of Trips");
        optionTripPattern.addActionListener(this);
        optionTripPattern.setEnabled(true);
        choices.add(optionTripPattern, gbConstraints);
        cards.add(choices, "Options");

        fChooser = null;
    	int files = 4;
    	String[] fileNames = new String[files];
    	fileNames[0] = "Browse";
    	fileNames[1] = "Browse";
    	fileNames[2] = "Browse";
    	fileNames[3] = "Browse";
    	String[] labels = new String[files];
    	labels[0] = "Shape Links: ";
    	labels[1] = "Shape Nodes: ";
    	labels[2] = "Shape Zones: ";
    	labels[3] = "Matrix file: ";	    	
    	fileCount = fileNames.length;
    	String[] commandNames = new String[files];
    	commandNames[0] = "ShapeLinks";
    	commandNames[1] = "ShapeNodes";
    	commandNames[2] = "ShapeZones";
    	commandNames[3] = "Matrix";
    	fChooser = new FileChooser(files, labels, fileNames, commandNames);
    	fChooser.setVisible(true); 
    	fChooser.setModuleName("ImportModelShape");
    	cards.add(fChooser, fChooser.toString());

    	JPanel control = new JPanel() ;
        prevButton = new JButton("\u22b2Prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(false);
        control.add(prevButton);	        
        nextButton = new JButton("Next\u22b3");
        nextButton.addActionListener(this);
        nextButton.setEnabled(false);
        control.add(nextButton);
        finishButton = new JButton("Finish");
    	finishButton.addActionListener(this);
    	finishButton.setEnabled(false);  	
    	control.add(finishButton);
    	cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(this);
    	cancelButton.setEnabled(true);  	
    	control.add(cancelButton);

    	frame.add(cards, BorderLayout.CENTER);
        frame.add(control, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println("actionperformed: command is " + command);
		if (command.startsWith("Network Import") )   {
			//this.getNextButton().setEnabled(true);
	        Main.mainFrame.getImportModelShapeWizard().getNextButton().setEnabled(true);
	        // FIXME: use a for loop!
	        if (optionNetwork.isSelected())  {
	        	fChooser.getFileButton()[0].setEnabled(true);
	        	fChooser.getFileButton()[1].setEnabled(true);
	        	fChooser.getFileButton()[2].setEnabled(true);
	        } else {
	        	fChooser.getFileButton()[0].setEnabled(false);
	        	fChooser.getFileButton()[1].setEnabled(false);
	        	fChooser.getFileButton()[2].setEnabled(false);	        		
        	}        		
	    }

		if (command.startsWith("TripPattern") )   {
	        Main.mainFrame.getImportModelShapeWizard().getNextButton().setEnabled(true);
	        if (optionNetwork.isSelected())  {
	        	fChooser.getFileButton()[3].setEnabled(true);	        		
	        }
        	else  {
	        	fChooser.getFileButton()[3].setEnabled(false);
        	} 
		}

		if (command.startsWith("Finish")) {
			System.out.println("Finish" + command);
			try {
				importedModel = getFeatures(getDataStoreLinks(), getDataStoreNodes(), 
						getDataStoreZones(), getTableShapeImport().getTableField(), 
						getTableShapeImport().getTableDirection());
				ArrayList<TripPattern> tripPatternList = importMatrix(getFileImportedMatrix());
				importedModel.trafficDemand.setTripPatternList(tripPatternList);
				importedModel.trafficDemand.rebuild();
				//importedModel.trafficDemand.createTripPatternList(tripPatternList);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        Main.mainFrame.setTitle("Imported");
	        Main.mainFrame.setActiveGraph();
	        this.frame.dispose();
		}
		if (command.startsWith("Cancel")) {
			System.out.println("Cancel" + command);
			importedModel = null;
			this.frame.dispose();
			
			//add close statement
		}
		if (command.startsWith("\u22b2Prev")) {
            CardLayout cl = (CardLayout) cards.getLayout();
            if (cardIndex == cards.getComponentCount() - 1)
            	this.finishButton.setEnabled(false);
            if (cardIndex > 0) {
            	cl.previous(cards);
            	this.nextButton.setEnabled(true);
            	cardIndex--;
            	System.out.print( "cardnumber" + cardIndex + "prev" );
            }
            if (cardIndex == 0)
            	this.prevButton.setEnabled(false);
		}
		if (command.startsWith("Next\u22b3")) {
            CardLayout cl = (CardLayout) cards.getLayout();
            if (cardIndex != cards.getComponentCount() - 1) {
            	cl.next(cards);
            	this.prevButton.setEnabled(true);
            	cardIndex++;
            	System.out.print( "cardnumber" + cardIndex + "next" );
            }
            if (cardIndex == cards.getComponentCount() - 1) { 
            	this.finishButton.setEnabled(true);
            	this.nextButton.setEnabled(false);
            }
        }
	}
	
	static void importModel() throws Exception   {
		if (fChooser.getCommand().startsWith("Shape")) {
			File file = new File(Main.mainFrame.initialDirectory);
	        file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null)
	            return;
	        
	        String name = fChooser.getCommand();
	        // FIXME: Peter K thinks this is dangerous and might not work as intended.
	        // see https://stackoverflow.com/questions/767372/java-string-equals-versus
        	if (name == "ShapeLinks") {
        		index = 0;
		        dataStoreLinks = FileDataStoreFinder.getDataStore(file);
		        dataStoreLinks.getFeatureSource();        		
            	linkAttributeNames = getAttributeNames(dataStoreLinks);
        	} else if (name == "ShapeNodes") {
        		index = 1;
		        dataStoreNodes = FileDataStoreFinder.getDataStore(file);
		        dataStoreLinks.getFeatureSource();        		
            	getAttributeNames(dataStoreLinks);
        	} else if (name == "ShapeZones") {
        		index = 2;
		        dataStoreZones = FileDataStoreFinder.getDataStore(file);
		        dataStoreLinks.getFeatureSource();        		
            	getAttributeNames(dataStoreLinks);
        	}
	        fChooser.getTextField()[index].setText(file.getAbsolutePath());
		}
		else if (fChooser.getCommand().startsWith("Matrix")) {
			index = 3;
			openDialogImportMatrix();
		}
		//set enabled true, but if not all files are selected puts it back to false
        Main.mainFrame.getImportModelShapeWizard().getNextButton().setEnabled(true);
        for (int i = 0; i < fileCount-1; i++)
        	if (fChooser.getTextField()[i].getText().toString().isEmpty())
        		Main.mainFrame.getImportModelShapeWizard().getNextButton().setEnabled(false);

        // TODO explain what this does
        if (Main.mainFrame.getImportModelShapeWizard().getNextButton().isEnabled()) {	
        	Object[] types = {"Jane", "Kathy", Boolean.TRUE};
        	Object[][] dataDir = {
				{"Direction indicator", "DIRECTION"},
				{"AB", "1"},
				{"BA", "2"},
				{"AB and BA", "3"},
			};
    		shapeImport = new TableModelImport(linkAttributeNames, types, dataDir);
    		shapeImport.setOpaque(true); //content panes must be opaque
			cards.add(shapeImport, shapeImport.toString());
        }
    
	}
	
	private static FileDataStore dataStoreLinks;
    private static FileDataStore dataStoreNodes;
    private static FileDataStore dataStoreZones;
    private static String[] linkAttributeNames;
	
    private static String[] names = null;
    
    private static String[] getAttributeNames(DataStore dataStore) throws Exception {
        String[] typeName = dataStore.getTypeNames();
        SimpleFeatureSource source = dataStore.getFeatureSource(typeName[0]);
        source.getFeatures();
        FeatureType ft = source.getSchema();
        Collection<PropertyDescriptor> attributeDescriptors = ft.getDescriptors();
        Iterator<PropertyDescriptor> iteratorAttributes = attributeDescriptors.iterator();
        int i = 0;
        names = new String[attributeDescriptors.size()];
        while( iteratorAttributes.hasNext())   {
        	PropertyDescriptor descr = iteratorAttributes.next();
        	Name name = descr.getName();
        	names[i] = name.toString();
        	i++;
        }
        return names;
    }

	private static DataStore getDataStoreLinks() {
		return dataStoreLinks;
	}

	private static DataStore getDataStoreNodes() {
		return dataStoreNodes;
	}

	private static DataStore getDataStoreZones() {
		return dataStoreZones;
	}

	private static String getFileImportedMatrix() {
		return fileImportedMatrix;
	}

	private static TableModelImport getTableShapeImport() {
		return shapeImport;
	}
		
	private Model getFeatures( DataStore linkStore, DataStore nodeStore, DataStore zoneStore, TableImport tableField, TableImport tableDirection) throws IOException {        
		// Reading Centroids (as nodes)
		DataStore dataStoreZone = zoneStore;
        String[] typeNameZone = dataStoreZone.getTypeNames();
        SimpleFeatureSource sourceZone = dataStoreZone.getFeatureSource(typeNameZone[0]);
        SimpleFeatureCollection featuresZone = sourceZone.getFeatures();
        //FeatureCollectionTableModel model = new FeatureCollectionTableModel(featuresNode);
        FeatureIterator<?> iteratorZone = featuresZone.features();	
        String propertyZone = "CENTROIDNR";	
        int zoneID = 0;
        while( iteratorZone.hasNext()) {
        	Vertex v = new Vertex();
        	Feature feature = iteratorZone.next();
        	Geometry test = (Geometry) feature.getDefaultGeometryProperty().getValue();
        	Coordinate[] coords = test.getCoordinates();
        	for (int i = 0; i < coords.length; i++)
        		v = new Vertex(coords[i]);
         	Property property = feature.getProperty(propertyZone);
        	String nodeNr = property.getValue().toString();
        	zoneID = Integer.parseInt(property.getValue().toString());
        	importedModel.network.addMicroZone(nodeNr, null, zoneID, v.getX(), v.getY(), v.getZ());
        	zoneID++;
        }
        
		// Reading Nodes
		DataStore dataStoreNode = nodeStore;
        String[] typeNameNode = dataStoreNode.getTypeNames();
        SimpleFeatureSource sourceNode = dataStoreNode.getFeatureSource(typeNameNode[0]);
        SimpleFeatureCollection featuresNode = sourceNode.getFeatures();
        //FeatureCollectionTableModel model = new FeatureCollectionTableModel(featuresNode);
        FeatureIterator<?> iteratorNode = featuresNode.features();	
        String propertyNode = "NODENR";	
        int nodeID = zoneID + 1;
        while( iteratorNode.hasNext()) {
        	Vertex v = new Vertex();
        	Feature feature = iteratorNode.next();
        	Geometry test = (Geometry) feature.getDefaultGeometryProperty().getValue();
        	Coordinate[] coords = test.getCoordinates();
        	for (int i = 0; i < coords.length; i++)
        		v = new Vertex(coords[i]);
        	Property property = feature.getProperty(propertyNode);
        	String nodeNr = property.getValue().toString();
        	nodeID = Integer.parseInt(property.getValue().toString());
        	importedModel.network.addNode(nodeNr, nodeID, v.getX(), v.getY(), v.getZ());
        	nodeID++;
        }
		
        //Reading Links
		DataStore dataStoreLink = linkStore;
        String[] typeNameLink = dataStoreLink.getTypeNames();
        SimpleFeatureSource sourceLink = dataStoreLink.getFeatureSource(typeNameLink[0]);
        SimpleFeatureCollection featuresLink = sourceLink.getFeatures();
        FeatureIterator<?> iteratorLink = featuresLink.features();	 
        // select the attributes
        String propertyLength = null;
        String propertyDirection = null;
        String propertyFromNode = null;
        String propertyToNode = null;
        String propertyCapacity = null;
        String propertyCapacityBA = null;
        String propertyLanes = null;
        String propertyLanesBA = null;
        String propertyTurnLanes = null;
        String propertyTurnLanesBA = null;
        String propertyExitLanes = null;
        String propertyExitLanesBA = null;
        String propertyMaxSpeed = null;
        String propertyMaxSpeedBA = null;
        int valueDirectionAB = -1;
        int valueDirectionBA = -1;
        int valueDirectionABBA = -1;
        for (int i = 0; i < tableDirection.getTable().getRowCount(); i++) {
			tableDirection.getTable().getValueAt(i, 0);
			Object attr2 = tableDirection.getTable().getValueAt(i, 1);	 
			if (i == 0)
				 propertyDirection = attr2.toString();
			if (i == 1)
				 valueDirectionAB = Integer.parseInt( attr2.toString() );
			if (i == 2)
				 valueDirectionBA = Integer.parseInt(attr2.toString() );
			if (i == 3)
				 valueDirectionABBA = Integer.parseInt(attr2.toString() );
        }
		for (int i = 0; i < tableField.getTable().getRowCount(); i++) {
			tableField.getTable().getValueAt(i, 0);
			Object attr2 = tableField.getTable().getValueAt(i, 1);
			tableField.getTable().getValueAt(i, 2);
			if (!attr2.toString().equals(TableImport.EMPTYCOLUMN)) {
				if (i == 1)
					 propertyFromNode = attr2.toString();
				else if (i == 2)
					 propertyToNode = attr2.toString();
				else if (i == 3) {
					 propertyCapacity = attr2.toString();
					 propertyCapacityBA = propertyCapacity.replaceAll("AB", "BA");
				} else if (i == 4) {
					 propertyLanes = attr2.toString();
					 propertyLanesBA = propertyLanes.replaceAll("AB", "BA");
				} else if (i == 5) {
					propertyTurnLanes = attr2.toString();
					propertyTurnLanesBA = propertyTurnLanes.replaceAll("AB", "BA");
				} else if (i == 6) {
					propertyExitLanes = attr2.toString();
					propertyExitLanesBA = propertyExitLanes.replaceAll("AB", "BA");
				} else if (i == 7) {
					 propertyMaxSpeed = attr2.toString();
					 propertyMaxSpeedBA = propertyMaxSpeed.replaceAll("AB", "BA");
				} else if (i == 8)
					 propertyLength = attr2.toString();
			}

		}

    	int linkID = 0;
        while( iteratorLink.hasNext())   {
        	Feature feature = iteratorLink.next();
        	Geometry test = (Geometry) feature.getDefaultGeometryProperty().getValue();
        	Coordinate[] coords = test.getCoordinates();
			ArrayList<Vertex> pointList = new ArrayList<Vertex>();
			ArrayList<Vertex> pointListBA = new ArrayList<Vertex>();					
			int fromNodeID = -1;
			int toNodeID = -1;
			double length = -1;
			double capacity = -1;
			String turnLanes = "";
			int exitLanes = -1;
			double maxSpeed = -1;
			int lanes = -1;
			double capacityBA = -1;
			String turnLanesBA = "";
			int exitLanesBA = -1;
			double maxSpeedBA = -1;
			int direction = -1; 
			int lanesBA = -1;
			String typologyName = "road";
			String linkType = "ANODE";
			Property property = null;
        	property = feature.getProperty(linkType);
    		boolean voedingsLinkAB = false;
        	boolean voedingsLinkBA = false;
    		int node;
        	node = Integer.parseInt(property.getValue().toString());
        	if (node<20) {
        		voedingsLinkAB = true;
        		voedingsLinkBA = true;
        	}
        	if (!propertyDirection.equals(""))   {
	        	property = feature.getProperty(propertyDirection);
	        	direction = Integer.parseInt(property.getValue().toString());
        	}
        	
        	if (!propertyFromNode.equals(""))   {
	        	property = feature.getProperty(propertyFromNode);
	        	fromNodeID = Integer.parseInt(property.getValue().toString());
        	}
        	if (!propertyToNode.equals(""))   {
	        	property = feature.getProperty(propertyToNode);
	        	toNodeID = Integer.parseInt(property.getValue().toString());
        	}
        	if (direction == valueDirectionAB  || direction == valueDirectionABBA)  {
	        	for (int i = 1; i < coords.length - 1; i++) {
	        		Vertex v = new Vertex(coords[i]);
	        		pointList.add(v);
	        	}
	        	
	        	if (! (propertyCapacity.equals("") || feature.getProperty(propertyCapacity)==null))   {
		        	property = feature.getProperty(propertyCapacity);
		        	capacity = Double.parseDouble(property.getValue().toString());
	        	}
	        	if (! (propertyTurnLanes.equals("") || feature.getProperty(propertyTurnLanes)==null))   {
		        	property = feature.getProperty(propertyTurnLanes);
		        	turnLanes = property.getValue().toString();
	        	}
	        	if (! (propertyExitLanes.equals("") || feature.getProperty(propertyExitLanes)==null))   {
		        	property = feature.getProperty(propertyExitLanes);
		        	// exitLanes are defined for the opposite direction!!!
		        	exitLanesBA = Integer.parseInt(property.getValue().toString());
	        	}
	        	if (! (propertyMaxSpeed.equals("") || feature.getProperty(propertyMaxSpeed)==null))   {
		        	property = feature.getProperty(propertyMaxSpeed);
		        	maxSpeed = Double.parseDouble(property.getValue().toString());
	        	}			        	
	        	if (! (propertyLength.equals("") || feature.getProperty(propertyLength)==null))   {
		        	property = feature.getProperty(propertyLength);
		        	length = Double.parseDouble(property.getValue().toString()) * 1000 ;
	        	}			        	
	        	if (! (propertyLanes.equals("") || feature.getProperty(propertyLanes)==null))   {
	        		property = feature.getProperty(propertyLanes);
		        	lanes = Integer.parseInt(property.getValue().toString());
	        	}
        	}

        	if (direction == valueDirectionBA  || direction == valueDirectionABBA)  {
	        	for (int i = coords.length - 2; i > 0; i--) {
	        		Vertex v = new Vertex(coords[i]);
	        		pointListBA.add(v);
	        	}
	        	if (! (propertyCapacityBA.equals("") || feature.getProperty(propertyCapacityBA) == null)) {
		        	property = feature.getProperty(propertyCapacityBA);
		        	capacityBA = Double.parseDouble(property.getValue().toString());
	        	}
	        	if (! (propertyTurnLanesBA.equals("") || feature.getProperty(propertyTurnLanesBA) == null)) {
		        	property = feature.getProperty(propertyTurnLanesBA);
		        	turnLanesBA = property.getValue().toString();
	        	}
	        	if (! (propertyExitLanesBA.equals("") || feature.getProperty(propertyExitLanesBA) == null)) {
		        	property = feature.getProperty(propertyExitLanesBA);
		        	// exitLanes are defined for the opposite direction!!!
		        	exitLanes = Integer.parseInt(property.getValue().toString());
	        	}
	        	if (! (propertyMaxSpeedBA.equals("") || feature.getProperty(propertyMaxSpeedBA) == null)) {
		        	property = feature.getProperty(propertyMaxSpeedBA);
		        	maxSpeedBA = Double.parseDouble(property.getValue().toString());
	        	}
	        	if (! (propertyLanesBA.equals("") || feature.getProperty(propertyLanesBA)==null)) {
	        		property = feature.getProperty(propertyLanesBA);
		        	lanesBA = Integer.parseInt(property.getValue().toString());
	        	}
        	}  
        	if (lanes <= 0)
        		lanes = deriveLanes(capacity, maxSpeed);
        	if (lanesBA <= 0)
        		lanesBA =  deriveLanes(capacityBA, maxSpeedBA);
        	if (lanes == 0)
        		System.out.print("geen lanes??????");
        	
        	final double defaultLaneWidth = 3.5;
        	if (voedingsLinkAB == false) {
	        	if (direction == valueDirectionAB  || direction == valueDirectionABBA) {
	        		addImportedLink(linkID, typologyName, fromNodeID, toNodeID, defaultLaneWidth, lanes, exitLanes, pointList, turnLanes, length);
		        	linkID++;
	        	}
        	} else {
    			List<Integer> nodeList = new ArrayList<Integer>();
        		if (fromNodeID < 20) {
        			nodeList.add(toNodeID);
        			importedModel.network.lookupMicroZone(fromNodeID).setNodeList(nodeList);
        		} else {
	        		nodeList.add(fromNodeID);
        			importedModel.network.lookupMicroZone(toNodeID);
        		}
        	}
        		
        	if (! voedingsLinkBA) {
	        	if (direction == valueDirectionBA  || direction == valueDirectionABBA) {
	        		addImportedLink(linkID, typologyName, toNodeID, fromNodeID, defaultLaneWidth, lanesBA, exitLanesBA, pointListBA, turnLanesBA, length);
		        	linkID++;
	        	}
        	}
        }
        
		// Construct the model
    	//settings.rebuild();
        importedModel.network.rebuild();
        importedModel.network.clearModified();
        importedModel.activities.rebuild();
        importedModel.population.rebuild();
        importedModel.trafficDemand.rebuild();
        return importedModel;
	}
	
	private static ArrayList<RoadMarkerAlong> createRMA(int lanes, double defaultLaneWidth)  {
		ArrayList<RoadMarkerAlong> rmaList = new ArrayList<RoadMarkerAlong>();
		rmaList.add(new RoadMarkerAlong("|", 0));
		for (int i = 0; i < lanes; i++)
			rmaList.add(new RoadMarkerAlong(i < lanes - 1 ? ":" : "|", (i + 1) * defaultLaneWidth));
		return rmaList;
	}
    private static void openDialogImportMatrix() {
    	String fileName = FileDialog.showFileDialog(true, "dat", "*.dat", 
    			Main.mainFrame.initialDirectory);
    	if (null == fileName)
    		return;
    	fileImportedMatrix = new File(fileName).getPath();
		fChooser.getTextField()[index].setText(fileImportedMatrix);
        System.out.printf("User selected network file \"%s\"", fileImportedMatrix);
    }
    
	private void addImportedLink(int linkID, String typologyName, int fromNodeID, int toNodeID,  double laneWidth, int lanes, 
			int exitLanes, ArrayList<Vertex> pointList, String turnLanes, double length)   {
		if (null == typologyName)
			typologyName = "road";
			//throw new Error ("CrossSectionElement has null typologyName");
    	ArrayList<RoadMarkerAlong> rmaList = createRMA(lanes, laneWidth);
    	ArrayList<CrossSection> csList = new ArrayList<CrossSection>();
    	CrossSection cs = new CrossSection(0.0, 0.0, null);
    	csList.add(cs);
    	CrossSectionElement cse = new CrossSectionElement(cs, typologyName, laneWidth * lanes, rmaList, null);
    	ArrayList<CrossSectionElement> cseList = new ArrayList<CrossSectionElement>();
    	cseList.add(cse);
    	cs.setCrossSectionElementList_w(cseList);
		ArrayList<Vertex> pointListAll = new ArrayList<Vertex>();
		pointListAll.addAll(pointList);
		// FIXME: rewrite using Network.lookupNode
		for (Node node : importedModel.network.getNodeList(false)) {
			if (node.getNodeID() == fromNodeID)
				pointListAll.add(0, node);	// insert from-node at start
			if (node.getNodeID() == toNodeID)
				pointListAll.add(node);		// append to-node at end
		}
    	double calculatedLength = calculateLength(pointListAll);
    	// if turnlanes are defined, we create an intermediate crossSection at a certain pre-defined distance from the junction (toNode)
    	if (turnLanes != null  && ! turnLanes.isEmpty())  {
    		ArrayList<TurnArrow> turnArrowList = analyseTurns(turnLanes, laneWidth);
    		if (lanes != turnArrowList.size()) {
    			double longPosition1 = 0;
    			double longPosition2 = 0;
    			// TODO explain all these constants: 70, 50, 35, 3.0/5, 3.1/5
    			if (calculatedLength > 70)   {
    				longPosition1 = calculatedLength - 50;
    				longPosition2 = calculatedLength - 35;
    			} else  {
    				longPosition1 = 3.0/5 * calculatedLength;
    				longPosition2 = 3.1/5 * calculatedLength;				
    			}
    			CrossSection cs1 = new CrossSection(longPosition1, 0.0, null);    			
    			CrossSection cs2 = new CrossSection(longPosition2, 0.0, null);
    	    	csList.add(cs1);
            	csList.add(cs2);	
    			ArrayList<RoadMarkerAlong> newRmaList = new ArrayList<RoadMarkerAlong>();
    			int newLanes = turnArrowList.size();
    			newRmaList = createRMA(newLanes, laneWidth);	
    			CrossSectionElement cse1 = new CrossSectionElement(cs1, typologyName, laneWidth * newLanes , newRmaList, turnArrowList);
    			CrossSectionElement cse2 = new CrossSectionElement(cs2, typologyName, laneWidth * newLanes , newRmaList, turnArrowList);
        		ArrayList<CrossSectionElement> cse1List = new ArrayList<CrossSectionElement>();
            	cse1List.add(cse1);
            	cs1.setCrossSectionElementList_w(cse1List);
        		ArrayList<CrossSectionElement> cse2List = new ArrayList<CrossSectionElement>();
            	cse2List.add(cse2);
            	cs2.setCrossSectionElementList_w(cse2List);
    		} else
    			cse = new CrossSectionElement(cs, typologyName, laneWidth * lanes, rmaList, turnArrowList);
    	}
    	if (exitLanes > 0) {
        	// FIXME: looks too much like the if (lanes !- turnArrorList.size()) code above
    		if (lanes != exitLanes) {
    			double longPosition1 = 0;
    			double longPosition2 = 0;
    			// TODO explain all these constants: 70, 50, 65, 2.0/5, 2.1/5
    			if (calculatedLength > 70)   {
    				longPosition1 = 0;
    				longPosition2 = 50;
    				cs.setLongitudalPosition_w(65);
    			} else {
    				longPosition1 = 0;
    				longPosition2 = (2.0/5) * calculatedLength;
    				cs.setLongitudalPosition_w( 2.1/5 * calculatedLength);
    			}
    			CrossSection cs1 = new CrossSection(longPosition1, 0.0, null);    			
    			CrossSection cs2 = new CrossSection(longPosition2, 0.0, null);
    	    	csList.add(0, cs1);
            	csList.add(1, cs2);	
    			ArrayList<RoadMarkerAlong> newRmaList = new ArrayList<RoadMarkerAlong>();
    			int newLanes = exitLanes;
    			newRmaList = createRMA(newLanes, laneWidth);	
    			CrossSectionElement cse1 = new CrossSectionElement(cs1, typologyName, laneWidth * newLanes , newRmaList, null);
    			CrossSectionElement cse2 = new CrossSectionElement(cs2, typologyName, laneWidth * newLanes , newRmaList, null);
        		ArrayList<CrossSectionElement> cse1List = new ArrayList<CrossSectionElement>();
            	cse1List.add(cse1);
            	cs1.setCrossSectionElementList_w(cse1List);
        		ArrayList<CrossSectionElement> cse2List = new ArrayList<CrossSectionElement>();
            	cse2List.add(cse2);
            	cs2.setCrossSectionElementList_w(cse2List);
    		}
    	}

    	String name = String.valueOf(linkID);
    	importedModel.network.addLink(name, fromNodeID, toNodeID, length, true, csList, pointList);
    }
	
	private static ArrayList<TurnArrow> analyseTurns(String turnLanes, Double laneWidth) {
		int lanes = turnLanes.length();
		int[] outLinkNumber = null;
		ArrayList<TurnArrow> turnArrowList = new ArrayList<TurnArrow>();
		for (int i = 0; i < lanes; i++)   {
			char turn = turnLanes.charAt(i);
			switch (turn)   {
				case 'r': // Right turn only
					outLinkNumber = new int[] {1};
					break;
				case 's': // Main (straight on) lane 
					outLinkNumber = new int[] {2};
					break;
				case 'l': // Left turn only 
					outLinkNumber = new int[] {3};
					break;
				case 'q': // Left turn and straight on 
					outLinkNumber = new int[] {2, 3};
					break;
				case 'p': // Right turn and straight on
					outLinkNumber = new int[] {1, 2};
					break;
				case 'u': // Left turn and right turn 
					outLinkNumber = new int[] {1, 2};
					break;
				case 'a': // Any turn lane 
					outLinkNumber = new int[] {1, 2, 3};
					break;
				case 'b': // Bus lane (straight on 
					outLinkNumber = new int[] {2};
					break;
			}
			Double lateralPosition = (i * laneWidth) + laneWidth / 2;
			TurnArrow turnArrow = new TurnArrow(null, outLinkNumber, lateralPosition, 0);
			turnArrowList.add(turnArrow);
		}
		return turnArrowList;
	}

	private static int deriveLanes(double capacity, double maxSpeed) {
		int lanes = 0;
    	int periodHours = 1;
    	// TODO: put those magic constants in final integers (or doubles) with descriptive names
		if (maxSpeed < 95) {
			if (capacity / periodHours < 2200)
				lanes = 1;
			else if ((capacity / periodHours >= 2200) && (capacity / periodHours < 4400))
				lanes = 2;
			else
				lanes = 3;
		}
		
		if (maxSpeed >= 95  && maxSpeed < 140) {
			if (capacity / periodHours < 2600)
				lanes = 1;
			else if ((capacity / periodHours >= 2600) && (capacity / periodHours < 5200))
				lanes = 2;
			else if ((capacity / periodHours >= 5200) && (capacity / periodHours < 7200))
				lanes = 3;
			else if ((capacity / periodHours >= 7200) && (capacity / periodHours < 9000))
				lanes = 4;
			else if ((capacity / periodHours >= 9000) && (capacity / periodHours < 10800))
				lanes = 5;
			else if (capacity / periodHours >= 10800)
				lanes = 6;
		}
		return lanes;
	}
	
    private static double calculateLength(List<Vertex> vertices) {	// compute length
        double cumLength = 0;
        for (int i = 1; i <= vertices.size() - 1; i++) {
            double dx = vertices.get(i).getX() - vertices.get(i - 1).getX();
            double dy = vertices.get(i).getY() - vertices.get(i - 1).getY();
            cumLength = cumLength + Math.sqrt(dx * dx + dy * dy);
        }
        return cumLength;
    }
    
    private static ArrayList<TripPattern> importMatrix(String fullFileName) throws IOException  {
    	InputStream fis = null;
    	BufferedReader br = null;
    	ArrayList<TripPattern> tripPatternList = new  ArrayList<TripPattern>();
    	try {
    		fis = new FileInputStream(fullFileName);
    		  DataInputStream in = new DataInputStream(fis);
    		  br = new BufferedReader(new InputStreamReader(in));
    		  String strLine;
    		  //Read File Line By Line
    		  while ((strLine = br.readLine()) != null)   {
    		  // Print the content on the console  
    			  if (strLine.startsWith("*"))
    				  continue;
    			  Scanner s = new Scanner(strLine);
    			  s.useDelimiter("\\s+");
    			  while (s.hasNext()) {
    				  String origin = "z" + s.nextInt();
    				  String destin = "z" + s.nextInt();
    				  ArrayList<Object> activityLocationID = new ArrayList<Object>(); 
    				  activityLocationID.add(origin);
    				  activityLocationID.add(destin);
    				  double count = s.nextDouble();
    				  TripPattern tripPattern = new TripPattern(null, count, activityLocationID);
    				  tripPatternList.add(tripPattern);	  
    			  }
    			  s.close();
    		  }	    		
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (null != br)
				br.close();
		}
		return tripPatternList;
    }
    
	public Model getImportedModel() {
		return importedModel;
	}

	JButton getFinishButton() {
		return finishButton;
	}

	JButton getNextButton() {
		return nextButton;
	}

	JButton getPrevButton() {
		return prevButton;
	}

}