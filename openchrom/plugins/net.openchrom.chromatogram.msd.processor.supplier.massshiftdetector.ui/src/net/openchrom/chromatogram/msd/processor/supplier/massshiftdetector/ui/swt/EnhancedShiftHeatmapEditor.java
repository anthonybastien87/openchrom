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
package net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.listener.AbstractControllerComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class EnhancedShiftHeatmapEditor extends AbstractControllerComposite {

	private Button buttonCalculate;
	private Button buttonReset;
	private Button buttonPrevious;
	private Button buttonCheck;
	private Button buttonNext;
	private List<Button> buttons;
	//
	private ShiftHeatmapUI shiftHeatmapUI;
	private Map<Integer, Map<Integer, Map<Integer, Double>>> massShifts;

	public EnhancedShiftHeatmapEditor(Composite parent, int style) {
		super(parent, style);
		buttons = new ArrayList<Button>();
		createControl();
	}

	@Override
	public boolean setFocus() {

		plotData();
		return super.setFocus();
	}

	@Override
	public void setStatus(boolean readOnly) {

		for(Button button : buttons) {
			button.setEnabled(false);
		}
		/*
		 * Defaults when editable.
		 */
		if(!readOnly) {
			buttonCalculate.setEnabled(true);
			buttonReset.setEnabled(true);
			buttonPrevious.setEnabled(true);
			buttonCheck.setEnabled(true);
		} else {
			/*
			 * If the project is read only, next shall be enabled.
			 */
			buttonNext.setEnabled(true);
		}
	}

	/**
	 * Sets the table viewer input.
	 * 
	 * @param input
	 */
	@SuppressWarnings("unchecked")
	public void setInput(Object input) {

		if(input instanceof Map) {
			massShifts = (Map<Integer, Map<Integer, Map<Integer, Double>>>)input;
			plotData();
		}
	}

	private void createControl() {

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Standards Table
		 */
		Composite chartComposite = new Composite(composite, SWT.NONE);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Heatmap
		 */
		shiftHeatmapUI = new ShiftHeatmapUI(chartComposite, SWT.NONE);
		shiftHeatmapUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Button Bar
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(1, true));
		compositeButtons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		//
		GridData gridDataButtons = new GridData(GridData.FILL_HORIZONTAL);
		gridDataButtons.minimumWidth = 150;
		//
		buttons.add(buttonCalculate = createCalculateButton(compositeButtons, gridDataButtons));
		buttons.add(buttonCheck = createCheckButton(compositeButtons, gridDataButtons));
		buttons.add(buttonReset = createResetButton(compositeButtons, gridDataButtons));
		buttons.add(buttonPrevious = createPreviousButton(compositeButtons, gridDataButtons));
		buttons.add(buttonNext = createNextButton(compositeButtons, gridDataButtons));
	}

	private Button createCalculateButton(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Calculate");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CALCULATE, IApplicationImage.SIZE_16x16));
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean error = false;
				if(error) {
					buttonNext.setEnabled(false);
				} else {
					buttonNext.setEnabled(true);
					plotData();
				}
			}
		});
		return button;
	}

	private Button createResetButton(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Reset");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out.println("TODO");
				boolean error = false;
				if(error) {
					buttonNext.setEnabled(false);
				} else {
					buttonNext.setEnabled(true);
					plotData();
				}
			}
		});
		return button;
	}

	/*
	 * Plot the data if there is no validation error.
	 */
	private void plotData() {

		shiftHeatmapUI.update(massShifts);
	}

	private Button createCheckButton(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Check");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHECK, IApplicationImage.SIZE_16x16));
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out.println("TODO");
				processAction();
			}
		});
		return button;
	}

	private Button createNextButton(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Next");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.setLayoutData(gridData);
		button.setEnabled(false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdateNext();
			}
		});
		return button;
	}

	private Button createPreviousButton(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Previous");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdatePrevious();
			}
		});
		return button;
	}
}
