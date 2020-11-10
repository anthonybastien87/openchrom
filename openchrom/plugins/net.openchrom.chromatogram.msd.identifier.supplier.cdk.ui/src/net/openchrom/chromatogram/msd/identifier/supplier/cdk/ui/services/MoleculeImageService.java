/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.identifier.supplier.cdk.ui.services;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import net.openchrom.chromatogram.msd.identifier.supplier.cdk.ui.Activator;
import net.openchrom.chromatogram.msd.identifier.supplier.cdk.ui.converter.ImageConverter;

@Component(service = {IMoleculeImageService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class MoleculeImageService implements IMoleculeImageService {

	private static final Logger logger = Logger.getLogger(MoleculeImageService.class);

	@Activate
	public void start() {

		logger.info("Service started: " + getName());
	}

	@Override
	public String getName() {

		return "Molecule Image (CDK)";
	}

	@Override
	public String getVersion() {

		Bundle bundle = Activator.getDefault().getBundle();
		Version version = bundle.getVersion();
		return version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
	}

	@Override
	public String getDescription() {

		return "This service tries to create an image by using OPSIN and the Chemistry Development Kit (CDK).";
	}

	@Override
	public Image create(Display display, ILibraryInformation libraryInformation, int width, int height) {

		Image image = null;
		if(display != null && libraryInformation != null) {
			image = createImage(display, libraryInformation, width, height);
		}
		//
		return image;
	}

	private Image createImage(Display display, ILibraryInformation libraryInformation, int width, int height) {

		Image image = null;
		/*
		 * SMILES
		 */
		String smiles = libraryInformation.getSmiles().trim();
		if(!smiles.isEmpty()) {
			image = convertMoleculeToImage(display, true, smiles, width, height);
		}
		/*
		 * IUPAC
		 */
		if(image == null) {
			String name = libraryInformation.getName().trim();
			image = convertMoleculeToImage(display, false, name, width, height);
		}
		//
		return image;
	}

	private Image convertMoleculeToImage(Display display, boolean useSmiles, String converterInput, int width, int height) {

		Image image = null;
		try {
			/*
			 * DepictionGenerator depictionGenerator = new DepictionGenerator();
			 * depictionGenerator.depict(molecule).writeTo(path);
			 */
			Point point = calculateMoleculeImageSize(width, height);
			image = ImageConverter.getInstance().moleculeToImage(display, useSmiles, converterInput, point);
		} catch(Exception e) {
			logger.warn(e);
		}
		return image;
	}

	private Point calculateMoleculeImageSize(int width, int height) {

		Point point;
		if(width > 0 && height > 0) {
			point = new Point(width, height);
		} else {
			point = new Point(ImageConverter.DEFAULT_WIDTH, ImageConverter.DEFAULT_HEIGHT);
		}
		//
		return point;
	}
}
