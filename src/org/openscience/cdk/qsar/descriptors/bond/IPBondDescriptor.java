/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-05-04 21:29:58 +0200 (Do, 04 Mai 2006) $
 *  $Revision: 6171 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.HashMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  This class returns the ionization potential of a bond. It is
 *  based in learning machine (in this case J48 see J48WModel) 
 *  from experimental values. Up to now is
 *  only possible predict for double bonds and they are not belong to
 *  conjugated system or not adjacent to an heteroatom.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author         Miguel Rojas
 * @cdk.created    2006-05-26
 * @cdk.module     qsar
 * @cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:ionizationPotential
 * @cdk.depends weka.jar
 * @cdk.builddepends weka.jar
 * @see J48WModel
 */
public class IPBondDescriptor implements IBondDescriptor {
	
	/** Hash map which contains the classAttribu = value of IP */
	private HashMap hash = null;
	
	private String[] classAttrib = {
			"05_0","05_1","05_2","05_3","05_4","05_5","05_6","05_7","05_8","05_9",
			"06_0","06_1","06_2","06_3","06_4","06_5","06_6","06_7","06_8","06_9",
			"07_0","07_1","07_2","07_3","07_4","07_5","07_6","07_7","07_8","07_9",
			"08_0","08_1","08_2","08_3","08_4","08_5","08_6","08_7","08_8","08_9",
			"09_0","09_1","09_2","09_3","09_4","09_5","09_6","09_7","09_8","09_9",
			"10_0","10_1","10_2","10_3","10_4","10_5","10_6","10_7","10_8","10_9",
			"11_0","11_1","11_2","11_3","11_4","11_5","11_6","11_7","11_8","11_9",
			"12_0","12_1","12_2","12_3","12_4","12_5","12_6","12_7","12_8","12_9",
			"13_0","13_1","13_2","13_3","13_4","13_5","13_6","13_7","13_8","13_9",
			"14_0","14_1","14_2","14_3","14_4","14_5","14_6","14_7","14_8","14_9",};

	/**
	 *  Constructor for the IPBondDescriptor object
	 */
	public IPBondDescriptor() {
		this.hash = new HashMap();
		double value = 5.05;
		for(int i = 0 ; i < classAttrib.length ; i++){
			this.hash.put(classAttrib[i],new Double(value));
			value += 0.1;
		}
	}
	/**
	 *  Gets the specification attribute of the IPBondDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPBondDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}
    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPBondDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }
	/**
	 *  This method calculates the ionization potential of an bond.
	 *
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IBond bond, IAtomContainer container) throws CDKException
	{
		double resultD = -1.0;
		boolean isTarget = false;
		Double[][] resultsH = null;
		String path = "";
		if(bond.getOrder() == 2 && bond.getAtom(0).getSymbol().equals("C") && 
				bond.getAtom(1).getSymbol().equals("C")){

			resultsH = calculatePiSystWithoutHeteroDescriptor(bond, container);
			path = "data/arff/PySystWithoutHetero.arff";
			isTarget = true;
		}
		if(isTarget){
			J48WModel j48 = new J48WModel(true,path);
    		String[] options = new String[4];
    		options[0] = "-C";
    		options[1] = "0.25";
    		options[2] = "-M";
    		options[3] = "2";
    		j48.setOptions(options);
    		j48.build();
    		j48.setParameters(resultsH);
            j48.predict();
    		String[] result = (String[])j48.getPredictPredicted();
    		resultD = ((Double)hash.get(result[0])).doubleValue();
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD));
	}
	
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculatePiSystWithoutHeteroDescriptor(IBond bond, IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IAtom positionC = bond.getAtom(0);
		IAtom positionX = bond.getAtom(1);
		try {
        	/*0_1*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1_1*/
    		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC, atomContainer).getValue()).doubleValue());
    		
    		/*0_2*/
    		SigmaElectronegativityDescriptor descriptor3 = new SigmaElectronegativityDescriptor();
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*1_2*/
    		PartialSigmaChargeDescriptor descriptor4 = new PartialSigmaChargeDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
    		
    		/*  */
    		ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
			params[0] = new Integer(atomContainer.getBondNumber(bond));
			descriptor5.setParameters(params);
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(atomContainer).getValue());
			results[0][4] = new Double(dar.get(0));
			results[0][5] = new Double(dar.get(1));
    		
    		
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 *  Gets the parameterNames attribute of the IPBondDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "targetPosition";
		return params;
	}
	/**
	 *  Gets the parameterType attribute of the IPBondDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Integer(0);
	}
}

