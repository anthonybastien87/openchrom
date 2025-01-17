/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.ui.internal.provider;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

import net.openchrom.xxd.process.supplier.templates.model.AssignerStandard;

public class StandardsAssignerComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof AssignerStandard && e2 instanceof AssignerStandard) {
			//
			AssignerStandard setting1 = (AssignerStandard)e1;
			AssignerStandard setting2 = (AssignerStandard)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = setting2.getName().compareTo(setting1.getName());
					break;
				case 1:
					sortOrder = Integer.compare(setting2.getStartRetentionTime(), setting1.getStartRetentionTime());
					break;
				case 2:
					sortOrder = Integer.compare(setting2.getStopRetentionTime(), setting1.getStopRetentionTime());
					break;
				case 3:
					sortOrder = Double.compare(setting2.getConcentration(), setting1.getConcentration());
					break;
				case 4:
					sortOrder = setting2.getConcentrationUnit().compareTo(setting1.getConcentrationUnit());
					break;
				case 5:
					sortOrder = Double.compare(setting2.getResponseFactor(), setting1.getResponseFactor());
					break;
				case 6:
					sortOrder = setting2.getTracesIdentification().compareTo(setting1.getTracesIdentification());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
