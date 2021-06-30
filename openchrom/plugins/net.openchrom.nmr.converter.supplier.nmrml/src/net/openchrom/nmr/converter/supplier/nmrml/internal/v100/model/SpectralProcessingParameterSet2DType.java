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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpectralProcessingParameterSet2DType", propOrder = {"directDimensionParameterSet", "higherDimensionParameterSet"})
public class SpectralProcessingParameterSet2DType extends SpectralProcessingParameterSetType {

	@XmlElement(required = true)
	protected FirstDimensionProcessingParameterSetType directDimensionParameterSet;
	@XmlElement(required = true)
	protected List<HigherDimensionProcessingParameterSetType> higherDimensionParameterSet;

	public FirstDimensionProcessingParameterSetType getDirectDimensionParameterSet() {

		return directDimensionParameterSet;
	}

	public void setDirectDimensionParameterSet(FirstDimensionProcessingParameterSetType value) {

		this.directDimensionParameterSet = value;
	}

	public List<HigherDimensionProcessingParameterSetType> getHigherDimensionParameterSet() {

		if(higherDimensionParameterSet == null) {
			higherDimensionParameterSet = new ArrayList<HigherDimensionProcessingParameterSetType>();
		}
		return this.higherDimensionParameterSet;
	}
}
