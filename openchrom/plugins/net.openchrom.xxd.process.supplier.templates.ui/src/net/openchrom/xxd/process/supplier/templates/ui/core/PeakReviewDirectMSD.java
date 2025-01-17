/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.ui.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.identifier.IIdentificationResults;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import net.openchrom.xxd.process.supplier.templates.io.ITemplateExport;
import net.openchrom.xxd.process.supplier.templates.model.ReviewSetting;
import net.openchrom.xxd.process.supplier.templates.peaks.AbstractPeakIdentifier;
import net.openchrom.xxd.process.supplier.templates.settings.PeakReviewSettings;
import net.openchrom.xxd.process.supplier.templates.ui.wizards.PeakReviewSupport;
import net.openchrom.xxd.process.supplier.templates.ui.wizards.ProcessReviewSettings;

public class PeakReviewDirectMSD<T> extends AbstractPeakIdentifier implements IPeakIdentifierMSD<IIdentificationResults>, ITemplateExport {

	private static final String DESCRIPTION = "PeakReviewMSD";

	@Override
	public IProcessingInfo<IIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IIdentificationResults> processingInfo = new ProcessingInfo<IIdentificationResults>();
		if(peaks == null || peaks.size() == 0) {
			processingInfo.addErrorMessage(DESCRIPTION, "No peak(s) have been found in the current selection.");
		} else {
			runProcess(peaks, peakIdentifierSettings, processingInfo, monitor);
		}
		return processingInfo;
	}

	private void runProcess(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProcessingInfo<IIdentificationResults> processingInfo, IProgressMonitor monitor) {

		/*
		 * No settings: peakIdentifierSettings == null
		 */
		IChromatogram<?> chromatogram = getChromatogram(peaks);
		List<ReviewSetting> reviewSettings = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(peak.getTargets().size() > 0) {
				float retentionIndex = peak.getPeakModel().getPeakMaximum().getRetentionIndex();
				IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndex);
				IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), identificationTargetComparator);
				if(identificationTarget != null) {
					ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
					IPeakModel peakModel = peak.getPeakModel();
					ReviewSetting reviewSetting = new ReviewSetting();
					reviewSetting.setStartRetentionTime(peakModel.getStartRetentionTime());
					reviewSetting.setStopRetentionTime(peakModel.getStopRetentionTime());
					reviewSetting.setName(libraryInformation.getName());
					reviewSetting.setCasNumber(libraryInformation.getCasNumber());
					reviewSetting.setDetectorType(PeakType.VV);
					reviewSetting.setTraces(getTraces(peak));
					reviewSetting.setOptimizeRange(true);
					reviewSettings.add(reviewSetting);
				}
			}
		}
		/*
		 * Check, that at least one review setting is set.
		 */
		if(reviewSettings.size() > 0) {
			PeakReviewSettings settings = new PeakReviewSettings();
			settings.setReviewSettings(reviewSettings);
			ProcessReviewSettings processSettings = new ProcessReviewSettings(processingInfo, chromatogram, settings);
			try {
				DisplayUtils.executeInUserInterfaceThread(new Runnable() {

					@Override
					public void run() {

						Shell shell = DisplayUtils.getShell();
						PeakReviewSupport peakReviewSupport = new PeakReviewSupport();
						peakReviewSupport.addSettings(shell, processSettings);
					}
				});
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch(ExecutionException e) {
				processingInfo.addErrorMessage(DESCRIPTION, "The execution failed, see attached log file.", e);
			}
		}
	}

	private IChromatogram<?> getChromatogram(List<? extends IPeakMSD> peaks) {

		for(IPeak peak : peaks) {
			if(peak instanceof IChromatogramPeak) {
				IChromatogramPeak chromatogramPeak = (IChromatogramPeak)peak;
				return chromatogramPeak.getChromatogram();
			}
		}
		//
		return null;
	}
}
