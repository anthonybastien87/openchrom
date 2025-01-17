/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.converter.supplier.mzmlb.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.io.IFormat;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.XmlReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzML;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.RunType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ScanType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.openchrom.msd.converter.supplier.mzmlb.io.support.IScanMarker;
import net.openchrom.msd.converter.supplier.mzmlb.io.support.ScanMarker;
import net.openchrom.msd.converter.supplier.mzmlb.model.IVendorChromatogram;
import net.openchrom.msd.converter.supplier.mzmlb.model.IVendorScanProxy;
import net.openchrom.msd.converter.supplier.mzmlb.model.VendorChromatogram;
import net.openchrom.msd.converter.supplier.mzmlb.model.VendorScanProxy;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;

public class ChromatogramReader extends AbstractChromatogramReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);

	public ChromatogramReader() {

	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return null;
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorChromatogram chromatogram = null;
		try {
			IHDF5SimpleReader reader = HDF5Factory.openForReading(file);
			byte[] xml = reader.readAsByteArray(IConstants.NODE_MZML);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(xml);
			Document document = documentBuilder.parse(inputStream);
			NodeList topNode = document.getElementsByTagName(IConstants.NODE_MZML);
			JAXBContext jaxbContext = JAXBContext.newInstance(IFormat.CONTEXT_PATH_V_110);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			MzML mzML = (MzML)unmarshaller.unmarshal(topNode.item(0));
			RunType run = mzML.getRun();
			chromatogram = new VendorChromatogram();
			SpectrumListType spectrumList = run.getSpectrumList();
			monitor.beginTask(IConstants.IMPORT_CHROMATOGRAM, spectrumList.getCount().intValue());
			for(SpectrumType spectrum : spectrumList.getSpectrum()) {
				monitor.subTask(IConstants.SCAN + " " + spectrum.getIndex().intValue());
				float abundance = 0.0f;
				int retentionTime = 0;
				short msLevel = 0;
				String intensityDataset = null;
				String mzDataset = null;
				int length = 0;
				int offset = 0;
				for(CVParamType cvParamSpectrum : spectrum.getCvParam()) {
					if(cvParamSpectrum.getAccession().equals("MS:1000285") && cvParamSpectrum.getName().equals("total ion current")) {
						abundance = Float.parseFloat(cvParamSpectrum.getValue());
					}
					if(cvParamSpectrum.getAccession().equals("MS:1000511") && cvParamSpectrum.getName().equals("ms level")) {
						msLevel = Short.parseShort(cvParamSpectrum.getValue());
					}
					for(ScanType scan : spectrum.getScanList().getScan()) {
						for(CVParamType cvParamScan : scan.getCvParam()) {
							if(cvParamScan.getAccession().equals("MS:1000016") && cvParamScan.getName().equals("scan start time")) {
								int multiplicator = XmlReader.getTimeMultiplicator(cvParamScan);
								retentionTime = Math.round(Float.parseFloat(cvParamScan.getValue()) * multiplicator);
							}
						}
					}
					for(BinaryDataArrayType binaryDataArray : spectrum.getBinaryDataArrayList().getBinaryDataArray()) {
						String dataSet = "";
						boolean mzs = false;
						boolean intensities = false;
						for(CVParamType cvParamBinary : binaryDataArray.getCvParam()) {
							if(cvParamBinary.getAccession().equals("MS:1002841") && cvParamBinary.getName().equals("external HDF5 dataset")) {
								dataSet = cvParamBinary.getValue();
							}
							if(cvParamBinary.getAccession().equals("MS:1002842") && cvParamBinary.getName().equals("external offset")) {
								offset = Integer.parseInt(cvParamBinary.getValue());
							}
							if(cvParamBinary.getAccession().equals("MS:1002843") && cvParamBinary.getName().equals("external array length")) {
								length = Integer.parseInt(cvParamBinary.getValue());
							}
							if(cvParamBinary.getAccession().equals("MS:1000514") && cvParamBinary.getName().equals("m/z array")) {
								mzs = true;
							}
							if(cvParamBinary.getAccession().equals("MS:1000515") && cvParamBinary.getName().equals("intensity array")) {
								intensities = true;
							}
						}
						if(mzs) {
							mzDataset = dataSet;
						} else if(intensities) {
							intensityDataset = dataSet;
						}
						mzs = false;
						intensities = false;
					}
				}
				IScanMarker scanMarker = new ScanMarker(mzDataset, intensityDataset, length, offset);
				IVendorScanProxy scanProxy = new VendorScanProxy(reader, scanMarker);
				scanProxy.setScanNumber(spectrum.getIndex().intValue());
				scanProxy.setIdentifier(spectrum.getId());
				scanProxy.setRetentionTime(retentionTime);
				scanProxy.setMassSpectrometer(msLevel);
				scanProxy.setTotalSignal(abundance);
				chromatogram.addScan(scanProxy);
				chromatogram.setFile(file);
			}
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		}
		monitor.done();
		return chromatogram;
	}
}
