/*******************************************************************************
 * Copyright (c) 2008, 2012 Philip (eselmeister) Wenig.
 * 
 * This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307, USA
 * 
 * 
 * Contributors: Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.converter.supplier.cdf.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import net.openchrom.chromatogram.msd.converter.exceptions.FileIsEmptyException;
import net.openchrom.chromatogram.msd.converter.exceptions.FileIsNotReadableException;
import net.openchrom.chromatogram.msd.model.core.IChromatogram;
import net.openchrom.chromatogram.msd.model.core.IChromatogramOverview;

public interface ICDFChromatogramReader {

	/**
	 * Reads an ANDI/AIA CDF chromatogram.<br/>
	 * If the chromatogram can not be parsed, null will be returned.
	 * 
	 * @param file
	 * @return IChromatogram
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	public IChromatogram read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;

	/**
	 * Reads an ANDI/AIA CDF chromatogram overview.<br/>
	 * If the chromatogram can not be parsed, null will be returned.
	 * 
	 * @param file
	 * @return IChromatogramOverview
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;
}
