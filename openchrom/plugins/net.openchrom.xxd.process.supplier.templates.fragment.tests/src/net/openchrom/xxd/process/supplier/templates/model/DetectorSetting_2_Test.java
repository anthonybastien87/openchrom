/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.model;

import junit.framework.TestCase;

public class DetectorSetting_2_Test extends TestCase {

	private DetectorSetting setting;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		setting = new DetectorSetting();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		setting.setStartRetentionTime(0.78d);
		assertEquals(0.78d, setting.getStartRetentionTime());
	}

	public void test2() {

		setting.setStopRetentionTime(1.28d);
		assertEquals(1.28d, setting.getStopRetentionTime());
	}

	public void test3() {

		setting.setDetectorType(DetectorSetting.DETECTOR_TYPE_BB);
		assertEquals("BB", setting.getDetectorType());
		assertTrue(setting.isIncludeBackground());
		//
		setting.setDetectorType(DetectorSetting.DETECTOR_TYPE_VV);
		assertEquals("VV", setting.getDetectorType());
		assertFalse(setting.isIncludeBackground());
	}

	public void test4() {

		setting.setTraces("103, 104");
		assertEquals("103, 104", setting.getTraces());
	}
}
