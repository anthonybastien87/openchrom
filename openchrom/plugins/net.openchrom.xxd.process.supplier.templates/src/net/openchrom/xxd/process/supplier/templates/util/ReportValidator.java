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
package net.openchrom.xxd.process.supplier.templates.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.openchrom.xxd.process.supplier.templates.model.ReportSetting;
import net.openchrom.xxd.process.supplier.templates.model.ReportStrategy;

public class ReportValidator extends AbstractTemplateValidator implements ITemplateValidator {

	public static final Set<ReportStrategy> REPORT_STRATEGIES = Collections.unmodifiableSet(EnumSet.of(ReportStrategy.ALL, ReportStrategy.BEST_MATCH, ReportStrategy.LARGEST_AREA, ReportStrategy.SMALLEST_AREA));
	//
	private static final String ERROR_ENTRY = "Please enter an item, e.g.: '" + ReportListUtil.EXAMPLE_SINGLE + "'";
	private static final String SEPARATOR_TOKEN = ReportListUtil.SEPARATOR_TOKEN;
	private static final String SEPARATOR_ENTRY = ReportListUtil.SEPARATOR_ENTRY;
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private double startRetentionTimeMinutes = 0;
	private double stopRetentionTimeMinutes = 0;
	private String name = "";
	private String casNumber = "";
	private ReportStrategy reportStrategy = ReportStrategy.ALL;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR_ENTRY;
		} else {
			if(value instanceof String) {
				String text = ((String)value).trim();
				if(text.contains(SEPARATOR_TOKEN)) {
					message = ERROR_TOKEN;
				} else if("".equals(text.trim())) {
					message = ERROR_ENTRY;
				} else {
					/*
					 * Extract retention time, ...
					 */
					String[] values = text.trim().split("\\" + SEPARATOR_ENTRY); // The pipe needs to be escaped.
					if(values.length >= 5) {
						/*
						 * Evaluation
						 */
						startRetentionTimeMinutes = parseDouble(values, 0);
						stopRetentionTimeMinutes = parseDouble(values, 1);
						//
						if(startRetentionTimeMinutes == 0.0d && stopRetentionTimeMinutes == 0.0d) {
							/*
							 * No validation if both values are zero -> complete chromatogram range.
							 */
						} else {
							/*
							 * Validate > zero
							 */
							if(startRetentionTimeMinutes < 0.0d) {
								message = "The start retention time must be not lower than 0.";
							}
							//
							if(stopRetentionTimeMinutes <= startRetentionTimeMinutes) {
								message = "The stop retention time must be greater then the start retention time.";
							}
						}
						//
						name = parseString(values, 2);
						if("".equals(name)) {
							message = "A substance name needs to be set.";
						}
						//
						casNumber = parseString(values, 3);
						//
						try {
							reportStrategy = ReportStrategy.valueOf(parseString(values, 4));
						} catch(Exception e) {
							// Shouldn't happen.
						}
					} else {
						message = ERROR_ENTRY;
					}
				}
			} else {
				message = ERROR_ENTRY;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public ReportSetting getSetting() {

		ReportSetting setting = new ReportSetting();
		setting.setStartRetentionTimeMinutes(startRetentionTimeMinutes);
		setting.setStopRetentionTimeMinutes(stopRetentionTimeMinutes);
		setting.setName(name);
		setting.setCasNumber(casNumber);
		setting.setReportStrategy(reportStrategy);
		return setting;
	}
}
