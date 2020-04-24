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
package net.openchrom.xxd.process.supplier.templates.ui.swt.peaks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import net.openchrom.xxd.process.supplier.templates.ui.wizards.ProcessReviewSettings;

public class TemplatePeakReviewUI extends Composite {

	private PeakReviewControl peakReviewControl;
	private PeakDetectorChart peakDetectorChart;

	public TemplatePeakReviewUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(ProcessReviewSettings processSettings) {

		peakReviewControl.setInput(processSettings);
	}

	private void createControl() {

		setLayout(new FillLayout());
		SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		//
		peakReviewControl = createPeakReviewControl(sashForm);
		peakDetectorChart = createPeakDetectorChart(sashForm);
		//
		peakReviewControl.setPeakDetectorChart(peakDetectorChart);
		sashForm.setWeights(new int[]{400, 600});
	}

	private PeakReviewControl createPeakReviewControl(Composite parent) {

		return new PeakReviewControl(parent, SWT.NONE);
	}

	private PeakDetectorChart createPeakDetectorChart(Composite parent) {

		return new PeakDetectorChart(parent, SWT.BORDER);
	}
}
