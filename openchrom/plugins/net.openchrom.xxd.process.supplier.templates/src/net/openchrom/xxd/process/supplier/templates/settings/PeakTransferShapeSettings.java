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
package net.openchrom.xxd.process.supplier.templates.settings;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakTransferShapeSettings extends AbstractPeakDetectorSettingsMSD implements IPeakDetectorSettingsMSD, IPeakDetectorSettingsCSD {

	public static final String DETECTOR_DESCRIPTION = "Gaussian Model";
	public static final String IDENTIFIER_DESCRIPTION = "Peak Transfer [Shape]";
	//
	@JsonProperty(value = "Delta Retention Time Left [ms]", defaultValue = "0")
	@JsonPropertyDescription(value = "This is the left delta retention time in milliseconds.")
	private int deltaRetentionTimeLeft = 0;
	@JsonProperty(value = "Delta Retention Time Right [ms]", defaultValue = "0")
	@JsonPropertyDescription(value = "This is the right delta retention time in milliseconds.")
	private int deltaRetentionTimeRight = 0;
	@JsonProperty(value = "Peak Overlap Coverage [%]", defaultValue = "12.5")
	@JsonPropertyDescription(value = "If a peak overlaps with at least the given coverage, it is grouped.")
	@DoubleSettingsProperty(minValue = 0, maxValue = 100)
	private double coverage = 12.5d;
	@JsonProperty(value = "Adjust Peak", defaultValue = "true")
	@JsonPropertyDescription(value = "This flag enables to adjust the peak area by intensity comparison.")
	private boolean useAdjustPeak = true;
	@JsonProperty(value = "Optimize Range (VV)", defaultValue = "true")
	@JsonPropertyDescription(value = "Optimize the peak model range if the source peak type is VV.")
	private boolean optimizeRange = true;
	@JsonProperty(value = "Number Traces", defaultValue = "15")
	@JsonPropertyDescription(value = "If a peak (MSD) contains less/equals than the given number of traces, then a SIM instead of a TIC detection will be forced.")
	private int numberTraces = 15;

	public int getDeltaRetentionTimeLeft() {

		return deltaRetentionTimeLeft;
	}

	public void setDeltaRetentionTimeLeft(int deltaRetentionTimeLeft) {

		this.deltaRetentionTimeLeft = deltaRetentionTimeLeft;
	}

	public int getDeltaRetentionTimeRight() {

		return deltaRetentionTimeRight;
	}

	public void setDeltaRetentionTimeRight(int deltaRetentionTimeRight) {

		this.deltaRetentionTimeRight = deltaRetentionTimeRight;
	}

	public double getCoverage() {

		return coverage;
	}

	public void setCoverage(double coverage) {

		this.coverage = coverage;
	}

	public boolean isUseAdjustPeak() {

		return useAdjustPeak;
	}

	public void setUseAdjustPeak(boolean useAdjustPeak) {

		this.useAdjustPeak = useAdjustPeak;
	}

	public boolean isOptimizeRange() {

		return optimizeRange;
	}

	public void setOptimizeRange(boolean optimizeRange) {

		this.optimizeRange = optimizeRange;
	}

	public int getNumberTraces() {

		return numberTraces;
	}

	public void setNumberTraces(int numberTraces) {

		this.numberTraces = numberTraces;
	}
}
