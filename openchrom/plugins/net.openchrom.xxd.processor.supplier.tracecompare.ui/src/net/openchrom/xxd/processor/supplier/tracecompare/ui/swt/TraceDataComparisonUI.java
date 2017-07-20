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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.export.ImageSupplier;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.openchrom.xxd.processor.supplier.tracecompare.model.ProcessorModel;
import net.openchrom.xxd.processor.supplier.tracecompare.model.SampleLaneModel;

public class TraceDataComparisonUI extends Composite {

	private static final int HORIZONTAL_INDENT = 15;
	//
	private Label labelSampleLane;
	private Combo comboReferenceSampleLanes;
	private Button buttonIsEvaluated;
	private Button buttonIsMatched;
	private Button buttonIsSkipped;
	private Button buttonCreateSnapshot;
	private Text notesText;
	//
	private TraceDataUI sampleDataUI;
	private TraceDataUI referenceDataUI;
	//
	private ProcessorModel processorModel;
	private SampleLaneModel sampleLaneModel;
	private String sampleGroup;
	private String referenceGroup;
	private Map<Integer, Map<String, ISeriesData>> sampleMeasurementsData;
	private Map<Integer, Map<String, ISeriesData>> referenceMeasurementsData;
	//
	private Map<String, Color> colorMap;
	private Color colorDefault;

	public TraceDataComparisonUI(Composite parent, int style) {
		super(parent, style);
		//
		colorMap = new HashMap<String, Color>();
		colorMap.put("190", Colors.YELLOW);
		colorMap.put("200", Colors.BLUE);
		colorMap.put("220", Colors.CYAN);
		colorMap.put("240", Colors.GREEN);
		colorMap.put("260", Colors.BLACK);
		colorMap.put("280", Colors.RED);
		colorMap.put("300", Colors.DARK_RED);
		colorDefault = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		//
		createControl();
	}

	public void setData(ProcessorModel processorModel, SampleLaneModel sampleLaneModel, String referenceGroup, Map<Integer, Map<String, ISeriesData>> sampleMeasurementsData, Map<Integer, Map<String, ISeriesData>> referenceMeasurementsData) {

		this.processorModel = processorModel;
		this.sampleLaneModel = sampleLaneModel;
		this.sampleGroup = processorModel.getSampleGroup();
		this.referenceGroup = referenceGroup;
		this.sampleMeasurementsData = sampleMeasurementsData;
		this.referenceMeasurementsData = referenceMeasurementsData;
		//
		int sampleLane = sampleLaneModel.getSampleLane();
		setSampleData(sampleLane);
		setReferenceData(sampleLane);
		sampleLaneModel.setReferenceLane(sampleLane);
		initializeReferenceSampleLaneComboItems(sampleLane, referenceMeasurementsData.keySet().size());
		//
		notesText.setText(sampleLaneModel.getNotes());
		setSampleLane(sampleLaneModel.getSampleLane(), sampleGroup);
		//
		String imageEvaluated = (sampleLaneModel.isEvaluated()) ? IApplicationImage.IMAGE_EVALUATED : IApplicationImage.IMAGE_EVALUATE;
		buttonIsEvaluated.setImage(ApplicationImageFactory.getInstance().getImage(imageEvaluated, IApplicationImage.SIZE_16x16));
		String imageMatched = (sampleLaneModel.isMatched()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
		buttonIsMatched.setImage(ApplicationImageFactory.getInstance().getImage(imageMatched, IApplicationImage.SIZE_16x16));
		String imageSkipped = (sampleLaneModel.isSkipped()) ? IApplicationImage.IMAGE_SKIPPED : IApplicationImage.IMAGE_SKIP;
		buttonIsSkipped.setImage(ApplicationImageFactory.getInstance().getImage(imageSkipped, IApplicationImage.SIZE_16x16));
		//
		setElementStatus(sampleLaneModel);
	}

	private void setSampleData(int sampleLane) {

		if(sampleMeasurementsData != null) {
			sampleDataUI.addSeriesData(getLineSeriesDataList(sampleMeasurementsData, sampleLane), LineChart.MEDIUM_COMPRESSION);
		}
	}

	private void setReferenceData(int sampleLane) {

		if(referenceMeasurementsData != null) {
			referenceDataUI.addSeriesData(getLineSeriesDataList(referenceMeasurementsData, sampleLane), LineChart.MEDIUM_COMPRESSION);
		}
	}

	private void setSampleLane(int sampleLane, String sample) {

		Display display = Display.getDefault();
		Font font = new Font(display, "Arial", 14, SWT.BOLD);
		labelSampleLane.setFont(font);
		labelSampleLane.setText(sample + " > Sample Lane " + Integer.toString(sampleLane));
		font.dispose();
	}

	private List<ILineSeriesData> getLineSeriesDataList(Map<Integer, Map<String, ISeriesData>> measurementsData, int sampleLane) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		//
		if(measurementsData != null) {
			if(measurementsData.containsKey(sampleLane)) {
				Map<String, ISeriesData> wavelengthData = measurementsData.get(sampleLane);
				addLineSeriesData(lineSeriesDataList, wavelengthData);
			} else if(measurementsData.containsKey(1)) {
				Map<String, ISeriesData> wavelengthData = measurementsData.get(1);
				addLineSeriesData(lineSeriesDataList, wavelengthData);
			} else {
				ILineSeriesData lineSeriesData = createEmptyLineSeriesData();
				lineSeriesDataList.add(lineSeriesData);
			}
		} else {
			ILineSeriesData lineSeriesData = createEmptyLineSeriesData();
			lineSeriesDataList.add(lineSeriesData);
		}
		//
		return lineSeriesDataList;
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList, Map<String, ISeriesData> wavelengthData) {

		for(String wavelength : wavelengthData.keySet()) {
			ISeriesData seriesData = wavelengthData.get(wavelength);
			ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
			lineSerieSettings.setDescription(wavelength + " nm");
			lineSerieSettings.setEnableArea(false);
			lineSerieSettings.setLineColor((colorMap.containsKey(wavelength)) ? colorMap.get(wavelength) : colorDefault);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private ILineSeriesData createEmptyLineSeriesData() {

		double[] xSeries = new double[]{0, 1000};
		double[] ySeries = new double[]{0, 1000};
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, "0");
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setDescription("0 nm");
		lineSerieSettings.setEnableArea(false);
		lineSerieSettings.setLineColor(colorDefault);
		return lineSeriesData;
	}

	private void createControl() {

		setLayout(new GridLayout(2, true));
		//
		createButtonSection(this);
		createCommentsSection(this);
		createTraceDataSection(this);
		//
		setElementStatus(sampleLaneModel);
		showComments(false);
	}

	private void createButtonSection(Composite parent) {

		labelSampleLane = new Label(parent, SWT.NONE);
		labelSampleLane.setText("");
		GridData gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLabel.horizontalIndent = HORIZONTAL_INDENT;
		labelSampleLane.setLayoutData(gridDataLabel);
		//
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataComposite);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtons(composite);
	}

	private void createButtons(Composite parent) {

		createComboReferenceSampleLanes(parent);
		createButtonFlipComments(parent);
		createButtonCreateSnapshot(parent);
		createButtonIsMatched(parent);
		createButtonIsSkipped(parent);
		createButtonIsEvaluated(parent);
	}

	private void createComboReferenceSampleLanes(Composite parent) {

		comboReferenceSampleLanes = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.widthHint = 220;
		comboReferenceSampleLanes.setLayoutData(gridData);
		initializeReferenceSampleLaneComboItems(-1, 0);
		comboReferenceSampleLanes.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int sampleLane = comboReferenceSampleLanes.getSelectionIndex() + 1;
				sampleLaneModel.setReferenceLane(sampleLane);
				setReferenceData(sampleLane);
			}
		});
	}

	private void createButtonFlipComments(Composite parent) {

		Button buttonFlipComments = new Button(parent, SWT.PUSH);
		buttonFlipComments.setText("");
		buttonFlipComments.setToolTipText("Show/Hide Comments");
		buttonFlipComments.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		buttonFlipComments.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isVisible = !notesText.isVisible();
				showComments(isVisible);
				//
				if(isVisible) {
					buttonFlipComments.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COLLAPSE_ALL, IApplicationImage.SIZE_16x16));
				} else {
					buttonFlipComments.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createButtonCreateSnapshot(Composite parent) {

		buttonCreateSnapshot = new Button(parent, SWT.PUSH);
		buttonCreateSnapshot.setText("");
		buttonCreateSnapshot.setToolTipText("Create a snapshot.");
		buttonCreateSnapshot.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CREATE_SNAPSHOT, IApplicationImage.SIZE_16x16));
		buttonCreateSnapshot.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ImageSupplier imageSupplier = new ImageSupplier();
				//
				String fileNameSample = getImageName("Sample");
				ImageData imageDataSample = imageSupplier.getImageData(sampleDataUI.getBaseChart());
				imageSupplier.saveImage(imageDataSample, fileNameSample, SWT.IMAGE_PNG);
				sampleLaneModel.setPathSnapshotSample(fileNameSample);
				//
				String fileNameReference = getImageName("Reference");
				ImageData imageDataReference = imageSupplier.getImageData(referenceDataUI.getBaseChart());
				imageSupplier.saveImage(imageDataReference, fileNameReference, SWT.IMAGE_PNG);
				sampleLaneModel.setPathSnapshotReference(fileNameReference);
				//
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Save Image", "A screenshot of the sample and reference has been saved.");
			}
		});
	}

	private void createButtonIsMatched(Composite parent) {

		buttonIsMatched = new Button(parent, SWT.PUSH);
		buttonIsMatched.setText("");
		buttonIsMatched.setToolTipText("Flag as matched");
		buttonIsMatched.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16));
		buttonIsMatched.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isMatched = sampleLaneModel.isMatched();
				if(isMatched) {
					sampleLaneModel.setMatched(false);
					buttonIsMatched.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16));
				} else {
					sampleLaneModel.setMatched(true);
					buttonIsMatched.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16));
				}
				//
				setElementStatus(sampleLaneModel);
			}
		});
	}

	private void createButtonIsSkipped(Composite parent) {

		buttonIsSkipped = new Button(parent, SWT.PUSH);
		buttonIsSkipped.setText("");
		buttonIsSkipped.setToolTipText("Flag as skipped.");
		buttonIsSkipped.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SKIP, IApplicationImage.SIZE_16x16));
		buttonIsSkipped.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isSkipped = sampleLaneModel.isSkipped();
				if(isSkipped) {
					sampleLaneModel.setSkipped(false);
					buttonIsSkipped.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SKIP, IApplicationImage.SIZE_16x16));
				} else {
					sampleLaneModel.setSkipped(true);
					buttonIsSkipped.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SKIPPED, IApplicationImage.SIZE_16x16));
				}
				//
				setElementStatus(sampleLaneModel);
			}
		});
	}

	private void createButtonIsEvaluated(Composite parent) {

		buttonIsEvaluated = new Button(parent, SWT.PUSH);
		buttonIsEvaluated.setText("");
		buttonIsEvaluated.setToolTipText("Flag as evaluated.");
		buttonIsEvaluated.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EVALUATE, IApplicationImage.SIZE_16x16));
		buttonIsEvaluated.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isEvaluated = sampleLaneModel.isEvaluated();
				if(isEvaluated) {
					sampleLaneModel.setEvaluated(false);
					buttonIsEvaluated.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EVALUATE, IApplicationImage.SIZE_16x16));
				} else {
					sampleLaneModel.setEvaluated(true);
					buttonIsEvaluated.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EVALUATED, IApplicationImage.SIZE_16x16));
				}
				//
				setElementStatus(sampleLaneModel);
			}
		});
	}

	private void createCommentsSection(Composite parent) {

		notesText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		notesText.setText("");
		GridData gridData = getGridData();
		gridData.horizontalIndent = HORIZONTAL_INDENT;
		notesText.setLayoutData(gridData);
		notesText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				sampleLaneModel.setNotes(notesText.getText().trim());
			}
		});
	}

	private void createTraceDataSection(Composite parent) {

		TraceDataSettings traceDataSettingsSample = new TraceDataSettings();
		traceDataSettingsSample.setEnableRangeSelector(true);
		traceDataSettingsSample.setShowAxisTitle(false);
		traceDataSettingsSample.setEnableHorizontalSlider(false);
		traceDataSettingsSample.setCreateMenu(true);
		sampleDataUI = new TraceDataUI(parent, SWT.NONE, traceDataSettingsSample);
		sampleDataUI.setLayoutData(getGridData());
		//
		TraceDataSettings traceDataSettingsReference = new TraceDataSettings();
		traceDataSettingsReference.setEnableRangeSelector(false);
		traceDataSettingsReference.setShowAxisTitle(true);
		traceDataSettingsReference.setEnableHorizontalSlider(true);
		traceDataSettingsReference.setCreateMenu(false);
		referenceDataUI = new TraceDataUI(parent, SWT.NONE, traceDataSettingsReference);
		referenceDataUI.setLayoutData(getGridData());
		/*
		 * Link both charts.
		 */
		sampleDataUI.addLinkedScrollableChart(referenceDataUI);
		referenceDataUI.addLinkedScrollableChart(sampleDataUI);
	}

	private GridData getGridData() {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		return gridData;
	}

	private void showComments(boolean isVisible) {

		GridData gridData = (GridData)notesText.getLayoutData();
		gridData.exclude = !isVisible;
		notesText.setVisible(isVisible);
		Composite parent = notesText.getParent();
		parent.layout(false);
		parent.redraw();
	}

	private void setElementStatus(SampleLaneModel sampleLaneModel) {

		if(sampleLaneModel == null) {
			comboReferenceSampleLanes.setEnabled(true);
			buttonCreateSnapshot.setEnabled(true);
			buttonIsMatched.setEnabled(true);
			buttonIsSkipped.setEnabled(true);
			buttonIsEvaluated.setEnabled(true);
		} else {
			boolean isEvaluated = sampleLaneModel.isEvaluated();
			boolean isSkipped = sampleLaneModel.isSkipped();
			//
			if(isEvaluated) {
				//
				comboReferenceSampleLanes.setEnabled(false);
				buttonCreateSnapshot.setEnabled(false);
				buttonIsMatched.setEnabled(false);
				buttonIsSkipped.setEnabled(false);
				buttonIsEvaluated.setEnabled(true);
			} else if(isSkipped) {
				//
				comboReferenceSampleLanes.setEnabled((isSkipped) ? false : true);
				buttonCreateSnapshot.setEnabled((isSkipped) ? false : true);
				buttonIsMatched.setEnabled((isSkipped) ? false : true);
				buttonIsSkipped.setEnabled(true);
				buttonIsEvaluated.setEnabled((isSkipped) ? false : true);
			}
		}
	}

	private String getImageName(String type) {

		return processorModel.getImageDirectory() + File.separator + type + "_" + sampleGroup + "_vs_" + referenceGroup + "_" + sampleLaneModel.getSampleLane() + "vs" + sampleLaneModel.getReferenceLane() + ".png";
	}

	private void initializeReferenceSampleLaneComboItems(int sampleLane, int numberOfSampleLanes) {

		comboReferenceSampleLanes.removeAll();
		//
		List<String> sampleLanes = new ArrayList<String>();
		for(int i = 1; i <= numberOfSampleLanes; i++) {
			sampleLanes.add("Reference Sample Lane " + i);
		}
		comboReferenceSampleLanes.setItems(sampleLanes.toArray(new String[sampleLanes.size()]));
		//
		int size = comboReferenceSampleLanes.getItemCount();
		if(size > 0) {
			if(sampleLane > 0 && sampleLane <= size) {
				comboReferenceSampleLanes.select(sampleLane - 1);
			} else {
				comboReferenceSampleLanes.select(0);
			}
		}
	}
}
