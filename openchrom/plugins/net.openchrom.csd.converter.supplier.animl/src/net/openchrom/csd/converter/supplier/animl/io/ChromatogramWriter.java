/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.csd.converter.supplier.animl.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import net.openchrom.xxd.converter.supplier.animl.internal.converter.BinaryReader;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.AnIMLType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.AuditTrailEntrySetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.AuditTrailEntryType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.AuthorType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.EncodedValueSetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.ExperimentStepSetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.ExperimentStepType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.IndividualValueSetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.MethodType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.ResultType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.SampleSetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.SampleType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.SeriesSetType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.SeriesType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.SoftwareType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.TechniqueType;
import net.openchrom.xxd.converter.supplier.animl.internal.model.astm.core.UnitType;
import net.openchrom.xxd.converter.supplier.animl.preferences.PreferenceSupplier;

public class ChromatogramWriter extends AbstractChromatogramCSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriter.class);

	@Override
	public void writeChromatogram(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(AnIMLType.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			//
			AnIMLType anIML = new AnIMLType();
			anIML.setSampleSet(createSampleSet(chromatogram));
			anIML.setExperimentStepSet(createExperimentStep(chromatogram));
			anIML.setAuditTrailEntrySet(createAuditTrail(chromatogram));
			marshaller.marshal(anIML, file);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		} catch(Exception e) // TODO remove
		{
			e.printStackTrace();
		}
	}

	private SampleSetType createSampleSet(IChromatogramCSD chromatogram) {

		SampleSetType sampleSet = new SampleSetType();
		SampleType sample = new SampleType();
		sample.setName(chromatogram.getDataName());
		sample.setBarcode(chromatogram.getBarcode());
		sample.setComment(chromatogram.getMiscInfo());
		sample.setSampleID(chromatogram.getDetailedInfo());
		sampleSet.getSample().add(sample);
		return sampleSet;
	}

	private ExperimentStepSetType createExperimentStep(IChromatogramCSD chromatogram) {

		ExperimentStepSetType experimentStepSet = new ExperimentStepSetType();
		//
		ExperimentStepType chromatographyStep = new ExperimentStepType();
		chromatographyStep.setTechnique(createChromatographyTechnique());
		experimentStepSet.getExperimentStep().add(chromatographyStep);
		//
		ExperimentStepType detectorStep = new ExperimentStepType();
		detectorStep.getResult().add(createResult(chromatogram));
		detectorStep.setMethod(createMethod(chromatogram));
		detectorStep.setSourceDataLocation(chromatogram.getFile().getAbsolutePath());
		detectorStep.setTechnique(createFlameIonizationTechnique());
		experimentStepSet.getExperimentStep().add(detectorStep);
		return experimentStepSet;
	}

	private ResultType createResult(IChromatogramCSD chromatogram) {

		SeriesSetType seriesSet = new SeriesSetType();
		seriesSet.setName("FID Trace");
		seriesSet.setLength(chromatogram.getNumberOfScans());
		//
		SeriesType retentionTimeSeries = new SeriesType();
		UnitType retentionTimeUnit = new UnitType();
		retentionTimeUnit.setLabel("Time");
		retentionTimeUnit.setQuantity("ms");
		retentionTimeSeries.setUnit(retentionTimeUnit);
		//
		SeriesType totalSignalSeries = new SeriesType();
		UnitType totalSignalUnit = new UnitType();
		totalSignalUnit.setLabel("Signal");
		totalSignalUnit.setQuantity("arbitrary");
		totalSignalSeries.setUnit(totalSignalUnit);
		//
		if(PreferenceSupplier.getChromatogramSaveEncoded()) {
			int scans = chromatogram.getNumberOfScans();
			float[] retentionTimes = new float[scans];
			float[] totalSignals = new float[scans];
			int i = 0;
			for(IScan scan : chromatogram.getScans()) {
				retentionTimes[i] = scan.getRetentionTime();
				totalSignals[i] = scan.getTotalSignal();
				i++;
			}
			EncodedValueSetType encodedRetentionTimes = new EncodedValueSetType();
			encodedRetentionTimes.setValue(BinaryReader.encodeArray(retentionTimes));
			retentionTimeSeries.getEncodedValueSet().add(encodedRetentionTimes);
			//
			EncodedValueSetType encodedTotalSignals = new EncodedValueSetType();
			encodedTotalSignals.setValue(BinaryReader.encodeArray(totalSignals));
			totalSignalSeries.getEncodedValueSet().add(encodedTotalSignals);
		} else {
			IndividualValueSetType retentionTimes = new IndividualValueSetType();
			IndividualValueSetType totalSignals = new IndividualValueSetType();
			for(IScan scan : chromatogram.getScans()) {
				retentionTimes.getF().add((float)scan.getRetentionTime());
				totalSignals.getF().add((float)scan.getTotalSignal());
			}
			retentionTimeSeries.getIndividualValueSet().add(retentionTimes);
			totalSignalSeries.getIndividualValueSet().add(totalSignals);
		}
		seriesSet.getSeries().add(retentionTimeSeries);
		seriesSet.getSeries().add(totalSignalSeries);
		ResultType result = new ResultType();
		result.setSeriesSet(seriesSet);
		return result;
	}

	private MethodType createMethod(IChromatogramCSD chromatogram) {

		AuthorType author = new AuthorType();
		author.setName(chromatogram.getOperator());
		MethodType method = new MethodType();
		method.setAuthor(author);
		method.setSoftware(createSoftware());
		return method;
	}

	private SoftwareType createSoftware() {

		SoftwareType software = new SoftwareType();
		software.setName("OpenChrom");
		IProduct product = Platform.getProduct();
		Version version = product.getDefiningBundle().getVersion();
		software.setVersion(version.toString());
		software.setManufacturer("Lablicate GmbH");
		software.setOperatingSystem(System.getProperty("os.name"));
		return software;
	}

	private TechniqueType createChromatographyTechnique() {

		TechniqueType technique = new TechniqueType();
		technique.setName("Chromatography");
		technique.setUri("https://raw.githubusercontent.com/AnIML/techniques/6e30b1e593e6df661a44ae2a9892b6198def0641/chromatography.atdd");
		return technique;
	}

	private TechniqueType createFlameIonizationTechnique() {

		TechniqueType technique = new TechniqueType();
		technique.setName("Flame Ionization Detector");
		technique.setUri("https://github.com/AnIML/techniques/blob/6e30b1e593e6df661a44ae2a9892b6198def0641/fid-trace.atdd");
		return technique;
	}

	private AuditTrailEntrySetType createAuditTrail(IChromatogramCSD chromatogram) throws DatatypeConfigurationException {

		AuditTrailEntrySetType auditTrailEntrySet = new AuditTrailEntrySetType();
		for(IEditInformation editHistory : chromatogram.getEditHistory()) {
			AuditTrailEntryType auditTrail = new AuditTrailEntryType();
			auditTrail.setSoftware(createSoftware());
			AuthorType author = new AuthorType();
			author.setName(editHistory.getEditor());
			auditTrail.setAuthor(author);
			auditTrail.setComment(editHistory.getDescription());
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(editHistory.getDate());
			auditTrail.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
		}
		return auditTrailEntrySet;
	}
}