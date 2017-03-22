/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;

public class ProcessorWizardElements extends ChromatogramWizardElements implements IProcessorWizardElements {

	private String c12ChromatogramPath = "";
	private String c13ChromatogramPath = "";
	private int level = 1;
	private String notes = ""; // Could be ""
	private String description = ""; // Could be ""

	@Override
	public String getC12ChromatogramPath() {

		return c12ChromatogramPath;
	}

	@Override
	public void setC12ChromatogramPath(String c12ChromatogramPath) {

		this.c12ChromatogramPath = c12ChromatogramPath;
	}

	@Override
	public String getC13ChromatogramPath() {

		return c13ChromatogramPath;
	}

	@Override
	public void setC13ChromatogramPath(String c13ChromatogramPath) {

		this.c13ChromatogramPath = c13ChromatogramPath;
	}

	@Override
	public int getLevel() {

		return level;
	}

	@Override
	public void setLevel(int level) {

		this.level = level;
	}

	@Override
	public String getNotes() {

		return notes;
	}

	@Override
	public void setNotes(String notes) {

		this.notes = notes;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}
}
