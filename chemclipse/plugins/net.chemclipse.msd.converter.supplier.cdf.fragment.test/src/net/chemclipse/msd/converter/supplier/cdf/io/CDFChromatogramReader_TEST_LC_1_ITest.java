/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.chemclipse.msd.converter.supplier.cdf.io;

import net.chemclipse.msd.converter.supplier.cdf.TestPathHelper;

/**
 * Tests if upper case directories will be recognized.
 * 
 * @author eselmeister
 */
public class CDFChromatogramReader_TEST_LC_1_ITest extends CDFChromatogramReaderTestCase {

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_TEST_LC);
		super.setUp();
	}

	public void testUpperCase_1() {

		assertNotNull(chromatogram);
	}
}
