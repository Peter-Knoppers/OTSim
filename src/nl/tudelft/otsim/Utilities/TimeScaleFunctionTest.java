package nl.tudelft.otsim.Utilities;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.FileIO.StaXWriter;
import nl.tudelft.otsim.GUI.Storable;
import nl.tudelft.otsim.GeoObjects.Network;

import org.junit.Test;

public class TimeScaleFunctionTest {

	@Test
	public void testTimeScaleFunctionStorable() {
		Network network = new Network();
		network.clearModified();
		TimeScaleFunction f = new TimeScaleFunction(network);
		f.insertPair(10, 10);
		assertTrue("Inserting a pair must set the modified flag in the Storable", network.isModified());
		f = new TimeScaleFunction((Storable) null);
		try {
			f.insertPair(10, 20);	// should NOT try to set the modified flag in the null-network
		} catch (Exception e) {
			fail("If Storable is null, calling the setModified method should not be attempted");
		}		
	}

	@Test
	public void testTimeScaleFunctionStorableParsedNode() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 5; i++) {
			// Generate values with 3 decimal digits (which is the guaranteed precision)
			double time = Math.round(i * 10000000d / 333) / 1000d;
			double factor = Math.round(1000000000d - i * 200000000000d / 987) / 1000000d;
			f.insertPair(time, factor);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StaXWriter writer = null;
		try {
			writer = new StaXWriter(outputStream);
		} catch (Exception e) {
			fail("Caught unexpected exception in creation of the StaXWriter");
		}
		f.writeXML(writer);
		writer.close();
		String xmlText = outputStream.toString();
		//System.out.println(xmlText);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlText.getBytes());
		TimeScaleFunction f2 = null;
		try {
			ParsedNode pn = new ParsedNode(inputStream);
			//System.out.println(pn.toString("All XML "));
			ParsedNode tsf = pn.getSubNode(TimeScaleFunction.XMLTAG, 0);
			f2 = new TimeScaleFunction((Storable) null, tsf);
		} catch (Exception e) {
			fail("Parsing the XML should not throw any Exception");
		}
		assertEquals("Number of pairs in copy should be the same", f.size(), f2.size());
		for (int i = 0; i < 5; i++) {
			assertEquals("There should be no rounding error due to conversion to text and back", f.getTime(i), f2.getTime(i), 0.0000001);
			assertEquals("There should be no rounding error due to conversion to text and back", f.getFactor(i), f2.getFactor(i), 0.0000001);
		}
		// Now test that values between the pairs are also close enough
		for (double t = 0; t < 150.1; t += 0.5)
			assertEquals("There should be no significant rounding error due to the time/factor pairs being (virtually) identical", f.getFactor(t), f2.getFactor(t), 0.0000001);
	}

	@Test
	public void testInsertPair() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		f.insertPair(10,  20);
		assertEquals("Single entry time value can be retrieved", f.getTime(0), 10, 0.000001);
		assertEquals("Single entry factor value can be retrieved", f.getFactor(0), 20, 0.000001);
	}

	@Test
	public void testSize() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 100; i++) {
			assertEquals("Number of inserted values should match number in insertPair calls", f.size(), i);
			f.insertPair(10 * i, 5 + 20 * i);
		}
	}

	@Test
	public void testGetTime() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 100; i++)
			f.insertPair(10 * i, 5 + 20 * i);
		for (int i = 0; i < 100; i++)
			assertEquals("Time values can be retrieved and are correct", f.getTime(i), 10 * i, 0.0001);
		boolean exceptionThrown = false;
		try {
			f.getTime(-1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Negative index is not permitted in getTime", exceptionThrown);
		exceptionThrown = false;
		try {
			f.getTime(100);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Too large index is not permitted in getTime", exceptionThrown);
	}

	@Test
	public void testGetFactorInt() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 100; i++)
			f.insertPair(10 * i, 5 + 20 * i);
		for (int i = 0; i < 100; i++)
			assertEquals("Flow values can be retrieved and are correct", f.getFactor(i), 5 + 20 * i, 0.0001);
		boolean exceptionThrown = false;
		try {
			f.getFactor(-1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Negative index is not permitted in getFlow", exceptionThrown);
		exceptionThrown = false;
		try {
			f.getFactor(100);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Too large index is not permitted in getFlow", exceptionThrown);
	}

	@Test
	public void testDeletePair() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 100; i++)
			f.insertPair(10 * i, 5 + 20 * i);
		boolean exceptionThrown = false;
		try {
			f.deletePair(-1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Index may not be negative", exceptionThrown);
		exceptionThrown = false;
		try {
			f.deletePair(101);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Index must not exceed number of entries", exceptionThrown);
		int index = 0;
		int indexIncrement = 33;
		for (int i = 100; --i > 0; ) {
			assertEquals("Removing a (semi-random) entry reduces the number of entries by one", f.size(), i + 1);
			index %= f.size();
			f.deletePair(index);
			index += indexIncrement;
		}
		assertEquals("After removing all but one entries there should be one entry", f.size(), 1);
		f.deletePair(0);
		assertEquals("After removing all entries there should be none left", f.size(), 0);
		exceptionThrown = false;
		try {
			f.deletePair(0);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("calling remove on empty set throws exception", exceptionThrown);
		
	}

	@Test
	public void testGetFactorDouble() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		assertEquals("Empty TimeScaleFunction returns value 1.0", f.getFactor(0d), 1.0d, 0.0000000000001);
		f.insertPair(10,  20);
		assertEquals("Only one value results in a uniform flow", f.getFactor(0d), 20, 0.00001);
		assertEquals("Only one value results in a uniform flow", f.getFactor(100d), 20, 0.00001);
		assertEquals("Only one value results in a uniform flow; even for negative times", f.getFactor(-100d), 20, 0.00001);
		f.insertPair(40, 100);
		f.insertPair(70, 50);
		double t = 0;
		for ( ; t < 10.1; t += 0.5)
			assertEquals("Flow is constant up to first time value", f.getFactor(t), 20, 000001);
		for ( ; t < 40.1; t += 0.5)
			assertEquals("Flow changes linearly between time values (1)", f.getFactor(t), 20 + (t - 10) * (100 - 20) / 30, 0.00001);
		for ( ; t < 70.1; t += 0.5)
			assertEquals("Flow changes linearly between time values (2)", f.getFactor(t), 100 + (t - 40) * (50 - 100) / 30, 0.000001);
		for ( ; t < 100.1; t += 0.5) 
			assertEquals("Flow stays constant after last time value", f.getFactor(t), 50, 0.000001);
	}
	
	@Test
	public void testIsTrivial() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		assertTrue("empty TimeScaleFunction is always trivial", f.isTrivial());
		
	}

	@Test
	public void testWriteXML() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StaXWriter writer = null;
		try {
			writer = new StaXWriter(outputStream);
		} catch (Exception e) {
			fail("Caught unexpected exception in creation of the StaXWriter");
		}
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		f.insertPair(40, 100);
		f.insertPair(70, 50);
		f.writeXML(writer);
		writer.close();
		String result = outputStream.toString();
		//System.out.println(result);
		assertEquals("check expected XML output", result, 
				"<?xml version=\"1.0\"?>\n"
				+ "<TimeFactorScaleFunction>\n"
				+ "  <Pair>\n"
				+ "    <Time>40.000</Time>\n"
				+ "    <Factor>100.000000</Factor>\n"
				+ "  </Pair>\n  <Pair>\n"
				+ "    <Time>70.000</Time>\n"
				+ "    <Factor>50.000000</Factor>\n"
				+ "  </Pair>\n"
				+ "</TimeFactorScaleFunction>\n");
	}

	@Test
	public void testExport() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		assertEquals("empty Flow exports as empty string", f.export().length(), 0);
		f.insertPair(20, 10d / 30);
		assertEquals("check value and number of decimal digits", f.export(), "20.000/0.333333");
		f.insertPair(30,  40);
		assertEquals("check single tab char between entries", f.export(), "20.000/0.333333\t30.000/40.000000");
	}

	@Test
	public void testTimeScaleFunctionString() {
		TimeScaleFunction f = new TimeScaleFunction((Storable) null);
		for (int i = 0; i < 5; i++) {
			// Generate values with 3 decimal digits (which is the guaranteed precision)
			double time = Math.round(i * 10000000d / 333) / 1000d;
			double factor = Math.round(1000000000d - i * 200000000000d / 987) / 1000000d;
			f.insertPair(time, factor);
		}
		//System.out.println("export is \"" + f.export() + "\"");
		TimeScaleFunction f2 = new TimeScaleFunction(f.export());
		assertEquals("Number of pairs in copy should be the same", f.size(), f2.size());
		for (int i = 0; i < 5; i++) {
			assertEquals("There should be no rounding error due to conversion to text and back", f.getTime(i), f2.getTime(i), 0.0000001);
			assertEquals("There should be no rounding error due to conversion to text and back", f.getFactor(i), f2.getFactor(i), 0.0000001);
		}
		// Now test that values between the pairs are also close enough
		for (double t = 0; t < 150.1; t += 0.5)
			assertEquals("There should be no significant rounding error due to the time/factor pairs being (virtually) identical", f.getFactor(t), f2.getFactor(t), 0.0000001);
	}

}
