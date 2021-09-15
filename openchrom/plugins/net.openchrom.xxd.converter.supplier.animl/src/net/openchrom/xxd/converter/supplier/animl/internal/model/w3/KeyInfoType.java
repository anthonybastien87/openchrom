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
package net.openchrom.xxd.converter.supplier.animl.internal.model.w3;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyInfoType", propOrder = {"content"})
public class KeyInfoType {

	@XmlElementRefs({@XmlElementRef(name = "KeyValue", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "X509Data", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "PGPData", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "RetrievalMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "SPKIData", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "MgmtData", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "KeyName", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false)})
	@XmlMixed
	@XmlAnyElement(lax = true)
	protected List<Object> content;
	@XmlAttribute(name = "Id")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;

	/**
	 * Gets the value of the content property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the content property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getContent().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link KeyValueType }{@code >}
	 * {@link JAXBElement }{@code <}{@link X509DataType }{@code >}
	 * {@link JAXBElement }{@code <}{@link PGPDataType }{@code >}
	 * {@link Element }
	 * {@link JAXBElement }{@code <}{@link RetrievalMethodType }{@code >}
	 * {@link JAXBElement }{@code <}{@link SPKIDataType }{@code >}
	 * {@link JAXBElement }{@code <}{@link String }{@code >}
	 * {@link String }
	 * {@link Object }
	 * {@link JAXBElement }{@code <}{@link String }{@code >}
	 *
	 *
	 */
	public List<Object> getContent() {

		if(content == null) {
			content = new ArrayList<Object>();
		}
		return this.content;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}