/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.model;

import org.eclipse.chemclipse.model.core.PeakType;

public enum DetectorType {
	VV, //
	BB, //
	MM;

	public static PeakType translate(DetectorType detectorType) {

		if(DetectorType.BB.equals(detectorType)) {
			return PeakType.BB;
		} else if(DetectorType.MM.equals(detectorType)) {
			return PeakType.MM;
		} else {
			return PeakType.VV;
		}
	}
}
