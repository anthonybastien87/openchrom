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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BondType")
public class BondType {

	@XmlAttribute(name = "atomRefs", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String atomRefs;
	@XmlAttribute(name = "order", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String order;

	public String getAtomRefs() {

		return atomRefs;
	}

	public void setAtomRefs(String value) {

		this.atomRefs = value;
	}

	public String getOrder() {

		return order;
	}

	public void setOrder(String value) {

		this.order = value;
	}
}
