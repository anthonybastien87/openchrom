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
package net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Combination of SI Units used to represent Scientific unit
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SIUnitType", propOrder = {"value"})
public class SIUnitType {

	@XmlValue
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String value;
	@XmlAttribute(name = "factor")
	protected Double factor;
	@XmlAttribute(name = "exponent")
	protected Double exponent;
	@XmlAttribute(name = "offset")
	protected Double offset;

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public double getFactor() {

		if(factor == null) {
			return 1.0D;
		} else {
			return factor;
		}
	}

	public void setFactor(Double value) {

		this.factor = value;
	}

	public double getExponent() {

		if(exponent == null) {
			return 1.0D;
		} else {
			return exponent;
		}
	}

	public void setExponent(Double value) {

		this.exponent = value;
	}

	public double getOffset() {

		if(offset == null) {
			return 0.0D;
		} else {
			return offset;
		}
	}

	public void setOffset(Double value) {

		this.offset = value;
	}
}
