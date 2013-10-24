/*******************************************************************************
 * Copyright (c) 2013 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.supplier.cdk.core;

import net.openchrom.supplier.cdk.TestPathHelper;
import net.openchrom.supplier.cdk.core.AWTMolDrawer;

import junit.framework.TestCase;

public class AWTMolDrawer_1_Test extends TestCase {

	private AWTMolDrawer awtMolDrawer;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		String imageFile = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_PNG);
		awtMolDrawer = new AWTMolDrawer("c1(nnc)cccc1c", imageFile);
	}

	@Override
	protected void tearDown() throws Exception {

		awtMolDrawer = null;
		super.tearDown();
	}

	public void testMethod_1() {

		assertEquals(200, awtMolDrawer.getHeight());
	}
}
