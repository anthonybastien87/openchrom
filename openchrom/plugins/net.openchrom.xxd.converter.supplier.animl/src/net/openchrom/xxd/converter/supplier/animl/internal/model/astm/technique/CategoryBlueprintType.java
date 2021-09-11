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
package net.openchrom.xxd.converter.supplier.animl.internal.model.astm.technique;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoryBlueprintType", propOrder = {"documentation", "seriesSetBlueprint", "parameterBlueprint", "categoryBlueprint"})
public class CategoryBlueprintType {

	@XmlElement(name = "Documentation")
	protected DocumentationType documentation;
	@XmlElement(name = "SeriesSetBlueprint")
	protected List<SeriesSetBlueprintType> seriesSetBlueprint;
	@XmlElement(name = "ParameterBlueprint")
	protected List<ParameterBlueprintType> parameterBlueprint;
	@XmlElement(name = "CategoryBlueprint")
	protected List<CategoryBlueprintType> categoryBlueprint;
	@XmlAttribute(name = "name", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String name;
	@XmlAttribute(name = "modality")
	protected ModalityType modality;
	@XmlAttribute(name = "maxOccurs")
	protected String maxOccurs;

	public DocumentationType getDocumentation() {

		return documentation;
	}

	public void setDocumentation(DocumentationType value) {

		this.documentation = value;
	}

	public List<SeriesSetBlueprintType> getSeriesSetBlueprint() {

		if(seriesSetBlueprint == null) {
			seriesSetBlueprint = new ArrayList<SeriesSetBlueprintType>();
		}
		return this.seriesSetBlueprint;
	}

	public List<ParameterBlueprintType> getParameterBlueprint() {

		if(parameterBlueprint == null) {
			parameterBlueprint = new ArrayList<ParameterBlueprintType>();
		}
		return this.parameterBlueprint;
	}

	public List<CategoryBlueprintType> getCategoryBlueprint() {

		if(categoryBlueprint == null) {
			categoryBlueprint = new ArrayList<CategoryBlueprintType>();
		}
		return this.categoryBlueprint;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public ModalityType getModality() {

		if(modality == null) {
			return ModalityType.REQUIRED;
		} else {
			return modality;
		}
	}

	public void setModality(ModalityType value) {

		this.modality = value;
	}

	public String getMaxOccurs() {

		if(maxOccurs == null) {
			return "1";
		} else {
			return maxOccurs;
		}
	}

	public void setMaxOccurs(String value) {

		this.maxOccurs = value;
	}
}
