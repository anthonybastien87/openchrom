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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Set of Samples used in this Experiment.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SampleReferenceSetType", propOrder = {"sampleReference", "sampleInheritance"})
public class SampleReferenceSetType {

	@XmlElement(name = "SampleReference")
	protected List<SampleReferenceType> sampleReference;
	@XmlElement(name = "SampleInheritance")
	protected List<SampleInheritanceType> sampleInheritance;
	@XmlAttribute(name = "id")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;

	public List<SampleReferenceType> getSampleReference() {

		if(sampleReference == null) {
			sampleReference = new ArrayList<SampleReferenceType>();
		}
		return this.sampleReference;
	}

	public List<SampleInheritanceType> getSampleInheritance() {

		if(sampleInheritance == null) {
			sampleInheritance = new ArrayList<SampleInheritanceType>();
		}
		return this.sampleInheritance;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
