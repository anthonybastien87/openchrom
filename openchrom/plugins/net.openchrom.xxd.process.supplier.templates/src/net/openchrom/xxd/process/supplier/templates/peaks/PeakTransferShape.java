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
package net.openchrom.xxd.process.supplier.templates.peaks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.AbstractPeakDetector;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.implementation.PeakModelCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

import net.openchrom.xxd.process.supplier.templates.settings.PeakDetectorSettings;
import net.openchrom.xxd.process.supplier.templates.settings.PeakTransferShapeSettings;
import net.openchrom.xxd.process.supplier.templates.support.PeakSupport;

@SuppressWarnings("rawtypes")
public class PeakTransferShape extends AbstractPeakDetector implements IPeakDetectorMSD, IPeakDetectorCSD {

	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD settings, IProgressMonitor monitor) {

		return applyDetector(chromatogramSelection, settings, monitor);
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		PeakTransferShapeSettings settings = getSettings();
		return detect(chromatogramSelection, settings, monitor);
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettingsCSD settings, IProgressMonitor monitor) {

		return applyDetector(chromatogramSelection, settings, monitor);
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		PeakTransferShapeSettings settings = getSettings();
		return detect(chromatogramSelection, settings, monitor);
	}

	private PeakTransferShapeSettings getSettings() {

		PeakTransferShapeSettings settings = new PeakTransferShapeSettings();
		return settings;
	}

	@SuppressWarnings({"unchecked"})
	private IProcessingInfo applyDetector(IChromatogramSelection<? extends IPeak, ?> chromatogramSelection, IPeakDetectorSettings settings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, settings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(settings instanceof PeakTransferShapeSettings) {
				PeakTransferShapeSettings peakTransferSettings = (PeakTransferShapeSettings)settings;
				transferPeaks(chromatogramSelection, peakTransferSettings);
			} else {
				processingInfo.addErrorMessage(PeakDetectorSettings.DETECTOR_DESCRIPTION, "The settings instance is wrong.");
			}
		}
		return processingInfo;
	}

	@SuppressWarnings("unchecked")
	private void transferPeaks(IChromatogramSelection<? extends IPeak, ?> chromatogramSelection, PeakTransferShapeSettings peakTransferSettings) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		List<IPeak> peaks = chromatogram.getPeaks(chromatogramSelection);
		List<IChromatogram> referencedChromatograms = chromatogram.getReferencedChromatograms();
		for(IChromatogram referencedChromatogram : referencedChromatograms) {
			transferPeaks(peaks, referencedChromatogram, peakTransferSettings);
		}
	}

	private void transferPeaks(List<IPeak> peaks, IChromatogram<?> chromatogramSink, PeakTransferShapeSettings peakTransferSettings) {

		Map<Integer, List<IPeak>> peakGroups = extractPeakGroups(peaks, peakTransferSettings);
		List<Integer> groups = new ArrayList<>();
		groups.addAll(peakGroups.keySet());
		Collections.sort(groups);
		//
		for(int group : groups) {
			List<IPeak> peakz = peakGroups.get(group);
			if(peakz.size() == 1) {
				/*
				 * Single Peak
				 */
				IPeak peak = peakz.get(0);
				double percentageIntensity = getPercentageIntensity(peak);
				transfer(peak, percentageIntensity, chromatogramSink, peakTransferSettings);
			} else {
				/*
				 * Peak Group
				 */
				boolean createGaussPeaks = true;
				if(createGaussPeaks) {
					/*
					 * Gauss
					 */
					for(IPeak peak : peakz) {
						IPeakModel peakModel = peak.getPeakModel();
						double percentageIntensityPeak = getPercentageIntensity(peak);
						if(chromatogramSink instanceof IChromatogramCSD) {
							IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogramSink;
							int startRetentionTime = peakModel.getStartRetentionTime();
							int stopRetentionTime = peakModel.getStopRetentionTime();
							int retentionTime = peakModel.getRetentionTimeAtPeakMaximum();
							// int delta = Math.max(retentionTime - startRetentionTime, stopRetentionTime - retentionTime);
							int scanMax = chromatogramCSD.getScanNumber(retentionTime);
							//
							if(scanMax > 0 && scanMax <= chromatogramCSD.getNumberOfScans()) {
								int startScan = scanMax - 10;
								int stopScan = scanMax + 10;
								float intensity = getIntensity(chromatogramCSD, startScan, stopScan);
								intensity *= percentageIntensityPeak;
								IPeak peakSink = createDefaultGaussPeak(chromatogramCSD, (retentionTime - 2000), (retentionTime + 5000), intensity);
								if(peakSink != null) {
									transferTarget(peak, peakSink);
									((IChromatogram)chromatogramSink).addPeak(peakSink);
								}
							}
						}
					}
				} else {
					/*
					 * Scaled
					 */
					int startRetentionTime = getGroupStartRetentionTime(peakz);
					int stopRetentionTime = getGroupStopRetentionTime(peakz);
					double area = getGroupArea(peakz);
					//
					for(IPeak peak : peakz) {
						/*
						 * Calculate the intensity percentage of the peak at scan max
						 * and the percentage of the peak area. The product of both
						 * tries to match the real peak area in the reference.
						 */
						double percentageIntensityPeak = getPercentageIntensity(peak);
						double percentageIntensityArea = 1.0d / area * peak.getIntegratedArea();
						double percentageIntensity = percentageIntensityArea * percentageIntensityPeak;
						transfer(peak, startRetentionTime, stopRetentionTime, percentageIntensity, chromatogramSink, peakTransferSettings);
					}
				}
			}
		}
	}

	private int getGroupStartRetentionTime(List<IPeak> peaks) {

		int retentionTime = Integer.MAX_VALUE;
		for(IPeak peak : peaks) {
			IPeakModel peakModel = peak.getPeakModel();
			retentionTime = Math.min(retentionTime, peakModel.getStartRetentionTime());
		}
		//
		return retentionTime;
	}

	private int getGroupStopRetentionTime(List<IPeak> peaks) {

		int retentionTime = Integer.MIN_VALUE;
		for(IPeak peak : peaks) {
			IPeakModel peakModel = peak.getPeakModel();
			retentionTime = Math.max(retentionTime, peakModel.getStopRetentionTime());
		}
		//
		return retentionTime;
	}

	private double getGroupArea(List<IPeak> peaks) {

		double area = 0;
		for(IPeak peak : peaks) {
			area += peak.getIntegratedArea();
		}
		//
		return area;
	}

	private Map<Integer, List<IPeak>> extractPeakGroups(List<IPeak> peaks, PeakTransferShapeSettings peakTransferSettings) {

		/*
		 * Sort out unidentified peaks and peaks without area.
		 */
		List<IPeak> peaksSource = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(!peak.getTargets().isEmpty()) {
				if(peak.getIntegratedArea() > 0) {
					peaksSource.add(peak);
				}
			}
		}
		/*
		 * Sort by retention time.
		 */
		Collections.sort(peaksSource, (p1, p2) -> Integer.compare(p1.getPeakModel().getRetentionTimeAtPeakMaximum(), p2.getPeakModel().getRetentionTimeAtPeakMaximum()));
		ListIterator<IPeak> listIterator = peaksSource.listIterator();
		Map<Integer, List<IPeak>> peakGroups = new HashMap<>();
		//
		double minCoverage = peakTransferSettings.getCoverage();
		int group = 1;
		while(listIterator.hasNext()) {
			IPeak peakCurrent = listIterator.next();
			IPeakModel peakModelCurrent = peakCurrent.getPeakModel();
			int stopRetentionTimeCurrent = peakModelCurrent.getStopRetentionTime();
			//
			List<IPeak> peakz = peakGroups.get(group);
			if(peakz == null) {
				peakz = new ArrayList<>();
				peakGroups.put(group, peakz);
			}
			peakz.add(peakCurrent);
			//
			if(listIterator.hasNext()) {
				/*
				 * Test if the next peak covers the current peak.
				 */
				IPeak peakNext = listIterator.next();
				IPeakModel peakModelNext = peakNext.getPeakModel();
				int startRetentionTimeNext = peakModelNext.getStartRetentionTime();
				if(stopRetentionTimeCurrent <= startRetentionTimeNext) {
					group++;
				} else {
					int stopRetentionTimeNext = peakModelNext.getStopRetentionTime();
					double width = stopRetentionTimeNext - startRetentionTimeNext + 1;
					double part = stopRetentionTimeCurrent - startRetentionTimeNext + 1;
					double coverage = 100.0d / width * part;
					if(coverage < minCoverage) {
						group++;
					}
				}
				//
				listIterator.previous();
			}
		}
		//
		return peakGroups;
	}

	private void transfer(IPeak peakSource, double percentageIntensity, IChromatogram chromatogramSink, PeakTransferShapeSettings peakTransferSettings) {

		int deltaRetentionTimeLeft = peakTransferSettings.getDeltaRetentionTimeLeft();
		int deltaRetentionTimeRight = peakTransferSettings.getDeltaRetentionTimeRight();
		//
		IPeakModel peakModelSource = peakSource.getPeakModel();
		int startRetentionTime = peakModelSource.getStartRetentionTime() - deltaRetentionTimeLeft;
		int stopRetentionTime = peakModelSource.getStopRetentionTime() + deltaRetentionTimeRight;
		//
		transfer(peakSource, startRetentionTime, stopRetentionTime, percentageIntensity, chromatogramSink, peakTransferSettings);
	}

	@SuppressWarnings("unchecked")
	private void transfer(IPeak peakSource, int startRetentionTime, int stopRetentionTime, double percentageIntensity, IChromatogram chromatogramSink, PeakTransferShapeSettings peakTransferSettings) {

		PeakSupport peakSupport = new PeakSupport();
		//
		boolean includeBackground = peakSource.getPeakType().equals(PeakType.VV);
		boolean optimizeRange = peakTransferSettings.isOptimizeRange();
		int numberTraces = peakTransferSettings.getNumberTraces();
		Set<Integer> traces = getTraces(peakSource, numberTraces);
		//
		IPeak peakSink = peakSupport.extractPeakByRetentionTime(chromatogramSink, startRetentionTime, stopRetentionTime, includeBackground, optimizeRange, traces);
		if(peakSink != null) {
			adjustPeakIntensity(peakSource, peakSink, percentageIntensity, peakTransferSettings);
			transferTarget(peakSource, peakSink);
			chromatogramSink.addPeak(peakSink);
		}
	}

	private Set<Integer> getTraces(IPeak peakSource, int numberTraces) {

		Set<Integer> traces = new HashSet<>();
		if(peakSource instanceof IChromatogramPeakMSD) {
			IChromatogramPeakMSD peakMSD = (IChromatogramPeakMSD)peakSource;
			if(peakMSD.getPurity() < 1.0f && numberTraces > 0) {
				IScanMSD scanMSD = peakMSD.getExtractedMassSpectrum();
				if(scanMSD.getIons().size() <= numberTraces) {
					IExtractedIonSignal extractedIonSignal = peakMSD.getExtractedMassSpectrum().getExtractedIonSignal();
					for(int ion = extractedIonSignal.getStartIon(); ion <= extractedIonSignal.getStopIon(); ion++) {
						if(extractedIonSignal.getAbundance(ion) > 0.0f) {
							traces.add(ion);
						}
					}
				}
			}
		}
		return traces;
	}

	private void adjustPeakIntensity(IPeak peakSource, IPeak peakSink, double percentageIntensity, PeakTransferShapeSettings peakTransferSettings) {

		if(peakTransferSettings.isUseAdjustPeak()) {
			if(percentageIntensity > 0.0d && percentageIntensity < 1.0d) {
				IScan peakMaximum = peakSink.getPeakModel().getPeakMaximum();
				float totalSignal = peakMaximum.getTotalSignal();
				peakMaximum.adjustTotalSignal((float)(totalSignal * percentageIntensity));
			}
		}
	}

	private double getPercentageIntensity(IPeak peakSource) {

		double percentageIntensity = 1.0d;
		if(peakSource instanceof IChromatogramPeak) {
			IChromatogramPeak peak = (IChromatogramPeak)peakSource;
			IChromatogram chromatogram = peak.getChromatogram();
			if(chromatogram != null) {
				int scanMax = peak.getScanMax();
				if(scanMax > 0 && scanMax <= chromatogram.getNumberOfScans()) {
					IScan scan = chromatogram.getScan(scanMax);
					float chromatogramTotalSignal = scan.getTotalSignal();
					IPeakModel peakModel = peak.getPeakModel();
					float peakTotalSignal = peakModel.getBackgroundAbundance() + peakModel.getPeakAbundance();
					//
					if(chromatogramTotalSignal > 0) {
						percentageIntensity = 1.0d / chromatogramTotalSignal * peakTotalSignal;
					}
				}
			}
		}
		return percentageIntensity;
	}

	private void transferTarget(IPeak peakSource, IPeak peakSink) {

		IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peakSource.getTargets());
		if(identificationTarget != null) {
			peakSink.getTargets().add(createIdentificationTarget(identificationTarget));
		}
	}

	private IIdentificationTarget createIdentificationTarget(IIdentificationTarget identificationTarget) {

		ILibraryInformation libraryInformation = new LibraryInformation(identificationTarget.getLibraryInformation());
		IComparisonResult comparisonResult = new ComparisonResult(identificationTarget.getComparisonResult());
		IIdentificationTarget identificationTargetSink = new IdentificationTarget(libraryInformation, comparisonResult);
		identificationTargetSink.setIdentifier(PeakTransferShapeSettings.DESCRIPTION);
		return identificationTargetSink;
	}

	private float getIntensity(IChromatogram<?> chromatogram, int startScan, int stopScan) {

		float intensity = Float.MIN_VALUE;
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			intensity = Math.max(intensity, scan.getTotalSignal());
		}
		//
		return intensity;
	}

	public IChromatogramPeakCSD createDefaultGaussPeak(IChromatogramCSD chromatogram, int startRetentionTime, int stopRetentionTime, float intensity) {

		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		/*
		 * Peak maximum
		 */
		// float intensity = Float.MIN_VALUE;
		// for(int i = startScan; i <= stopScan; i++) {
		// IScan scan = chromatogram.getScan(i);
		// intensity = Math.max(intensity, scan.getTotalSignal());
		// }
		//
		IScanCSD peakMaximum = new ScanCSD(intensity);
		/*
		 * Intensity profile
		 */
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues(intensity);
		double a = intensity;
		double j = -5.0d;
		double iDelta = 0.1d;
		//
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			int retentionTime = scan.getRetentionTime();
			float peakIntensity = (float)(a * Math.exp(-j * j / 2) / Math.sqrt(2 * Math.PI));
			peakIntensityValues.addIntensityValue(retentionTime, peakIntensity);
			j += iDelta;
		}
		peakIntensityValues.normalize();
		//
		IPeakModelCSD peakModel = new PeakModelCSD(peakMaximum, peakIntensityValues, 0, 0);
		return new ChromatogramPeakCSD(peakModel, chromatogram);
	}
}