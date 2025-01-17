/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.nmr.converter.supplier.nmrml.internal.v100.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultipletType", propOrder = {"atoms", "multiplicity", "peakList"})
public class MultipletType {

	@XmlElement(required = true)
	protected AtomRefsType atoms;
	@XmlElement(required = true)
	protected CVTermType multiplicity;
	protected PeakListType peakList;
	@XmlAttribute(name = "center", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String center;

	public AtomRefsType getAtoms() {

		return atoms;
	}

	public void setAtoms(AtomRefsType value) {

		this.atoms = value;
	}

	public CVTermType getMultiplicity() {

		return multiplicity;
	}

	public void setMultiplicity(CVTermType value) {

		this.multiplicity = value;
	}

	public PeakListType getPeakList() {

		return peakList;
	}

	public void setPeakList(PeakListType value) {

		this.peakList = value;
	}

	public String getCenter() {

		return center;
	}

	public void setCenter(String value) {

		this.center = value;
	}
}
