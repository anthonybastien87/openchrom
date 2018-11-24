/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.nmr.processing.supplier.base.settings;

import org.eclipse.chemclipse.nmr.processor.settings.IProcessorSettings;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.openchrom.nmr.processing.supplier.base.settings.support.ZERO_FILLING_FACTOR;

public class FourierTransformationSettings implements IProcessorSettings {

	@JsonProperty(value = "Zero Filling", defaultValue = "AUTO")
	@EnumSelectionSettingProperty
	private ZERO_FILLING_FACTOR zeroFillingFactor = ZERO_FILLING_FACTOR.AUTO;

	public FourierTransformationSettings() {

	}

	public ZERO_FILLING_FACTOR getZeroFillingFactor() {

		return zeroFillingFactor;
	}

	public void setZeroFillingFactor(ZERO_FILLING_FACTOR zeroFillingFactor) {

		this.zeroFillingFactor = zeroFillingFactor;
	}
}
