/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.test.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import junit.framework.*;

/**
 * Checks the funcitonality of the IsotopeFactory
 */
public class IsotopeFactoryTest extends TestCase
{
	boolean standAlone = false;
	
	public IsotopeFactoryTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{

	}
	
	public static Test suite() 
	{
		return new TestSuite(IsotopeFactoryTest.class);
	}

	public void testIsotopeFactory()
	{
		Isotope isotope = null;
		IsotopeFactory isofac = null;
		try
		{
			isofac = IsotopeFactory.getInstance();
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem instantiating IsotopeFactory: " +  exc.toString());
		}
		if (standAlone) System.out.println("isoFac.getSize(): " + isofac.getSize());
		assertTrue(isofac.getSize() > 0);
		
		try
		{
			isotope = isofac.getMajorIsotope("Te");
			if (standAlone) System.out.println("Isotope: " + isotope);
			
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem getting isotope 'Te' from IsotopeFactory: "  +  exc.toString());
		}
		
		assertTrue(isotope.getExactMass() == 129.906229);

		try
		{
			isotope = isofac.getMajorIsotope(17);
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem getting Isotope 'Cl' from IsotopeFactory by atomicNumber 17: "  +  exc.toString());
		}
		
		assertTrue(isotope.getSymbol().equals("Cl"));
		
		
	}
    
	/**
	 *  A unit test for JUnit
	 */
	public void testElementFactory()
	{
		Element element = null;
		IsotopeFactory elfac = null;
		String findThis = "Br";
		try
		{
			elfac = IsotopeFactory.getInstance();
		}
		catch (Exception exc)
		{
			throw new AssertionFailedError("Problem instantiating IsotopeFactory: " + exc.toString());
		}

		assertTrue(elfac.getSize() > 0);

		try
		{
			element = elfac.getElement(findThis);
		}
		catch (Exception exc)
		{
			throw new AssertionFailedError("Problem getting isotope " + findThis + " from ElementFactory: " + exc.toString());
		}

		assertTrue(element.getAtomicNumber() == 35);
	}    
	
	
	
	public static void main(String[] args)
	{
		try{
			IsotopeFactoryTest ift = new IsotopeFactoryTest("IsotopeFactoryTest");
			ift.standAlone = true;
			ift.testIsotopeFactory();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}
