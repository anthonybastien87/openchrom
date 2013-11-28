/*******************************************************************************
 * Copyright (c) 2013 Marwin Wollschläger.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marwin Wollschläger - initial API and implementation
 *******************************************************************************/
package net.openchrom.supplier.cdk.core;



import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.interfaces.IMolecule;

public class CDKPolarizabilityDescriptor implements IStructureDescriptor {

	Polarizability polarizability;

	public CDKPolarizabilityDescriptor() {

		polarizability = new Polarizability();
	}

	@Override
	public String describe(IMolecule molecule) {

		double result = polarizability.calculateKJMeanMolecularPolarizability(molecule);
		return "" + result;
	}

}
