/*******************************************************************************
 * Copyright (c) 2013 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.converter.supplier.cdf.io;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import net.openchrom.chromatogram.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import net.openchrom.chromatogram.model.signals.ITotalScanSignalExtractor;
import net.openchrom.chromatogram.model.signals.TotalScanSignalExtractor;
import net.openchrom.chromatogram.msd.converter.chromatogram.ChromatogramConverterMSD;
import net.openchrom.chromatogram.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import net.openchrom.chromatogram.msd.model.core.IChromatogramMSD;
import net.openchrom.chromatogram.msd.model.xic.ExtractedIonSignalExtractor;
import net.openchrom.chromatogram.msd.model.xic.IExtractedIonSignalExtractor;

import junit.framework.TestCase;

public class CDFChromatogramWriterTestCase extends TestCase {

	protected IChromatogramMSD chromatogramImport;
	protected IChromatogramMSD chromatogram;
	protected String pathImport;
	protected String pathExport;
	protected File fileImport;
	protected File fileExport;
	protected String extensionPointImport;
	protected String extensionPointExportReimport;
	protected ITotalScanSignalExtractor totalIonSignalExtractor;
	protected IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Import the chromatogram.
		 */
		fileImport = new File(this.pathImport);
		IChromatogramMSDImportConverterProcessingInfo processingInfoImport = ChromatogramConverterMSD.convert(fileImport, this.extensionPointImport, new NullProgressMonitor());
		chromatogramImport = processingInfoImport.getChromatogram();
		/*
		 * Export the chromatogram.
		 */
		fileExport = new File(this.pathExport);
		IChromatogramExportConverterProcessingInfo processingInfoExport = ChromatogramConverterMSD.convert(fileExport, chromatogramImport, this.extensionPointExportReimport, new NullProgressMonitor());
		fileExport = processingInfoExport.getFile();
		/*
		 * Reimport the exported chromatogram.
		 */
		chromatogramImport = null;
		IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(fileExport, this.extensionPointExportReimport, new NullProgressMonitor());
		chromatogram = processingInfo.getChromatogram();
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		pathExport = null;
		fileImport = null;
		fileExport = null;
		chromatogramImport = null;
		chromatogram = null;
		totalIonSignalExtractor = null;
		super.tearDown();
	}
}
