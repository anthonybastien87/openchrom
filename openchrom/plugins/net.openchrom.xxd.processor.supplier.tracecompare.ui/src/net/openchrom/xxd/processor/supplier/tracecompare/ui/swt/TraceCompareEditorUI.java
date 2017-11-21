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
package net.openchrom.xxd.processor.supplier.tracecompare.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import net.openchrom.xxd.processor.supplier.tracecompare.core.DataProcessor;
import net.openchrom.xxd.processor.supplier.tracecompare.model.IProcessorModel;
import net.openchrom.xxd.processor.supplier.tracecompare.model.v1000.ReferenceModel_v1000;
import net.openchrom.xxd.processor.supplier.tracecompare.model.v1000.TrackModel_v1000;
import net.openchrom.xxd.processor.supplier.tracecompare.preferences.PreferenceSupplier;
import net.openchrom.xxd.processor.supplier.tracecompare.ui.editors.EditorProcessor;
import net.openchrom.xxd.processor.supplier.tracecompare.ui.internal.runnables.MeasurementImportRunnable;

public class TraceCompareEditorUI extends Composite {

	private static final Logger logger = Logger.getLogger(TraceCompareEditorUI.class);
	//
	private EditorProcessor editorProcessor;
	//
	private TabFolder tabFolder;
	//
	private Map<Integer, TraceDataComparisonUI> traceData;
	private IProcessorModel processorModel;
	private Map<String, Map<Integer, Map<String, ISeriesData>>> modelData = new HashMap<String, Map<Integer, Map<String, ISeriesData>>>();
	//
	private String sampleGroup = "0196"; // TODO
	private String referenceGroup = "0236"; // TODO

	public TraceCompareEditorUI(Composite parent, int style) {
		super(parent, style);
		traceData = new HashMap<Integer, TraceDataComparisonUI>();
		modelData = new HashMap<String, Map<Integer, Map<String, ISeriesData>>>();
		initialize(parent);
	}

	public void update(Object object) {

		if(object instanceof EditorProcessor) {
			//
			editorProcessor = (EditorProcessor)object;
			processorModel = editorProcessor.getProcessorModel();
			if(tabFolder.getItems().length <= 1) {
				initializeTraceComparators();
			}
		}
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		/*
		 * Elements
		 */
		createTraceComparators(composite);
	}

	public void selectTrack(int track) {

		int size = tabFolder.getItemCount();
		if(track > 0 && track <= size) {
			loadTrack(track);
			tabFolder.setSelection(track - 1);
		}
	}

	private void createTraceComparators(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int track = tabFolder.getSelectionIndex() + 1;
				loadTrack(track);
			}
		});
		//
		initializeTraceComparators();
	}

	private void loadTrack(int track) {

		int tracksSize = traceData.keySet().size();
		if(track > 0 && track < tracksSize) {
			Object object = traceData.get(track);
			if(object instanceof TraceDataComparisonUI) {
				TraceDataComparisonUI traceDataComparisonUI = (TraceDataComparisonUI)object;
				traceDataComparisonUI.loadData();
			}
		}
	}

	private void initializeTraceComparators() {

		String fileExtension = PreferenceSupplier.getFileExtension();
		/*
		 * Get the sample and reference.
		 */
		List<File> sampleFiles = new ArrayList<File>();
		List<File> referenceFiles = new ArrayList<File>();
		//
		if(processorModel != null) {
			/*
			 * Sample and reference files.
			 */
			String sampleDirectory = PreferenceSupplier.getSampleDirectory();
			sampleFiles = DataProcessor.getMeasurementFiles(sampleDirectory, fileExtension, sampleGroup);
			//
			String referenceDirectory = PreferenceSupplier.getReferenceDirectory();
			referenceFiles = DataProcessor.getMeasurementFiles(referenceDirectory, fileExtension, referenceGroup);
		}
		/*
		 * Clear the tab folder.
		 */
		for(TabItem tabItem : tabFolder.getItems()) {
			tabItem.dispose();
		}
		traceData.clear();
		/*
		 * Validate that files have been selected.
		 */
		if(sampleFiles.size() > 0 && referenceFiles.size() > 0) {
			createTrackTabItems(sampleFiles, referenceFiles);
		} else {
			createEmptyTabItem();
		}
	}

	private void createTrackTabItems(List<File> sampleFiles, List<File> referenceFiles) {

		TabItem tabItem;
		Composite composite;
		TrackModel_v1000 trackModel;
		/*
		 * Get the model.
		 */
		ReferenceModel_v1000 referenceModel = processorModel.getReferenceModels().get(referenceGroup);
		if(referenceModel == null) {
			referenceModel = new ReferenceModel_v1000();
			referenceModel.setReferenceGroup(referenceGroup);
			referenceModel.setReferencePath(PreferenceSupplier.getReferenceDirectory());
			processorModel.getReferenceModels().put(referenceGroup, referenceModel);
		}
		/*
		 * Extract the data.
		 * 0196 [Group]
		 * -> 1 [Track] -> 190 [nm], ISeriesData
		 * -> 1 [Track] -> 200 [nm], ISeriesData
		 * ...
		 * -> 18 [Track] -> 190 [nm], ISeriesData
		 * -> 18 [Track] -> 200 [nm], ISeriesData
		 * 0197 [Group]
		 * -> 1 [Track] -> 190 [nm], ISeriesData
		 * -> 1 [Track] -> 200 [nm], ISeriesData
		 * ...
		 * -> 18 [Track] -> 190 [nm], ISeriesData
		 * -> 18 [Track] -> 200 [nm], ISeriesData
		 */
		Map<Integer, Map<String, ISeriesData>> sampleMeasurementsData = modelData.get(sampleGroup);
		if(sampleMeasurementsData == null) {
			sampleMeasurementsData = extractMeasurementsData(sampleFiles);
			modelData.put(sampleGroup, sampleMeasurementsData);
		}
		//
		Map<Integer, Map<String, ISeriesData>> referenceMeasurementsData = modelData.get(referenceGroup);
		if(referenceMeasurementsData == null) {
			referenceMeasurementsData = extractMeasurementsData(referenceFiles);
			modelData.put(referenceGroup, referenceMeasurementsData);
		}
		/*
		 * Tracks
		 */
		int tracks = 2; // TODO sampleMeasurementsData.keySet().size();
		for(int track = 1; track <= tracks; track++) {
			/*
			 * Track #
			 */
			trackModel = referenceModel.getTrackModels().get(track);
			if(trackModel == null) {
				trackModel = new TrackModel_v1000();
				trackModel.setSampleTrack(track);
				referenceModel.getTrackModels().put(track, trackModel);
			}
			/*
			 * Set the current velocity and the reference track.
			 * Set the reference track only if it is 0.
			 * The user may have selected another reference track.
			 */
			trackModel.setScanVelocity(PreferenceSupplier.getScanVelocity());
			if(trackModel.getReferenceTrack() == 0) {
				trackModel.setReferenceTrack(track);
			}
			//
			tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText("Track " + track);
			composite = new Composite(tabFolder, SWT.NONE);
			composite.setLayout(new FillLayout());
			TraceDataComparisonUI traceDataComparisonUI = new TraceDataComparisonUI(composite, SWT.BORDER);
			traceDataComparisonUI.setBackground(Colors.WHITE);
			traceDataComparisonUI.setData(editorProcessor, processorModel, trackModel, referenceGroup, sampleMeasurementsData, referenceMeasurementsData);
			int previousTrack = (track > 1) ? track - 1 : track;
			int nextTrack = (track < tracks) ? track + 1 : track;
			traceDataComparisonUI.setTrackInformation(this, previousTrack, nextTrack);
			tabItem.setControl(composite);
			//
			traceData.put(track, traceDataComparisonUI);
		}
	}

	private Map<Integer, Map<String, ISeriesData>> extractMeasurementsData(List<File> measurementFiles) {

		Map<Integer, Map<String, ISeriesData>> measurementsData = new HashMap<Integer, Map<String, ISeriesData>>();
		//
		MeasurementImportRunnable runnable = new MeasurementImportRunnable(measurementFiles);
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		try {
			monitor.run(true, true, runnable);
		} catch(InterruptedException e1) {
			logger.warn(e1);
		} catch(InvocationTargetException e1) {
			logger.warn(e1);
		}
		//
		ISeriesData seriesData;
		//
		List<IChromatogramWSD> measurements = runnable.getMeasurements();
		for(IChromatogram measurement : measurements) {
			/*
			 * Track 1
			 */
			int index = 1;
			seriesData = extractMeasurement(measurement);
			addMeasurementData(measurementsData, seriesData, index++);
			/*
			 * Track 2 ... n
			 */
			for(IChromatogram additionalMeasurement : measurement.getReferencedChromatograms()) {
				seriesData = extractMeasurement(additionalMeasurement);
				addMeasurementData(measurementsData, seriesData, index++);
			}
		}
		//
		return measurementsData;
	}

	private void addMeasurementData(Map<Integer, Map<String, ISeriesData>> measurementsData, ISeriesData seriesData, int index) {

		Map<String, ISeriesData> wavelengthData = measurementsData.get(index);
		if(wavelengthData == null) {
			wavelengthData = new HashMap<String, ISeriesData>();
			measurementsData.put(index, wavelengthData);
		}
		wavelengthData.put(seriesData.getId(), seriesData);
	}

	private ISeriesData extractMeasurement(IChromatogram measurement) {

		List<IScan> scans = measurement.getScans();
		double[] xSeries = new double[scans.size()];
		double[] ySeries = new double[scans.size()];
		int wavelength = getWavelength(measurement);
		//
		int index = 0;
		ExtractedWavelengthSignalExtractor signalExtractor = new ExtractedWavelengthSignalExtractor((IChromatogramWSD)measurement);
		IExtractedWavelengthSignals extractedWavelengthSignals = signalExtractor.getExtractedWavelengthSignals();
		for(IExtractedWavelengthSignal extractedWavelengthSignal : extractedWavelengthSignals.getExtractedWavelengthSignals()) {
			xSeries[index] = extractedWavelengthSignal.getRetentionTime();
			ySeries[index] = extractedWavelengthSignal.getAbundance(wavelength);
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, Integer.toString(wavelength));
	}

	private int getWavelength(IChromatogram measurement) {

		for(IScan scan : measurement.getScans()) {
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				for(IScanSignalWSD signal : scanWSD.getScanSignals()) {
					int wavelength = (int)signal.getWavelength();
					return wavelength;
				}
			}
		}
		//
		return 0;
	}

	private void createEmptyTabItem() {

		/*
		 * No Data
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Message");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setBackground(Colors.GRAY);
		//
		Label label = new Label(composite, SWT.NONE);
		Display display = Display.getDefault();
		Font font = new Font(display, "Arial", 14, SWT.BOLD);
		label.setFont(font);
		label.setBackground(Colors.GRAY);
		label.setText("No data has been selected yet.");
		//
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		label.setLayoutData(gridData);
		font.dispose();
		//
		tabItem.setControl(composite);
	}
}
