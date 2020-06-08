/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.openchrom.xxd.process.supplier.templates.preferences.PreferenceSupplier;
import net.openchrom.xxd.process.supplier.templates.ui.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Template Processor");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new DirectoryFieldEditor(PreferenceSupplier.P_LIST_PATH_IMPORT, "List Path Import", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_LIST_PATH_EXPORT, "List Path Export", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHART_BUFFERED_SELECTION, "Chart Buffered Selection", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_UI_DETECTOR_DELTA_LEFT_MINUTES, "Detector UI Delta Minutes Left", PreferenceSupplier.MIN_DELTA_MINUTES, PreferenceSupplier.MAX_DELTA_MINUTES, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_UI_DETECTOR_DELTA_RIGHT_MINUTES, "Detector UI Delta Minutes Right", PreferenceSupplier.MIN_DELTA_MINUTES, PreferenceSupplier.MAX_DELTA_MINUTES, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_UI_DETECTOR_REPLACE_PEAK, "Detector UI Replace Peak", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SET_REVIEW_TARGET_NAME, "Set Review Target Name", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_AUTO_SELECT_BEST_PEAK_MATCH, "Auto Select Best Match", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
