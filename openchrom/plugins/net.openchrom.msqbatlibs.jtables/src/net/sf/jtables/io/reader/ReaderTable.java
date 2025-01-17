/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package net.sf.jtables.io.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jtables.table.Row;
import net.sf.jtables.table.Table;
import net.sf.jtables.table.TableAnnotated;
import net.sf.jtables.table.impl.TableInteger;
import net.sf.jtables.table.impl.TableString;
import net.sf.kerner.utils.io.buffered.IOIterable;
import net.sf.kerner.utils.io.buffered.IOIterator;
import net.sf.kerner.utils.pair.Pair;

/**
 * 
 * A {@code TableReader} reads an {@link TableAnnotated} from an input source.
 * </p>
 * It does so by extending {@link IOIterable} in oder to provide
 * possibility to iterate over a table's rows.
 * </p>
 * Via {@link ReaderTable#readTableAtOnce()} it is also possible to read in a whole
 * table at once.
 * 
 * @see IOIterable
 * @see TableAnnotated
 * @see TableString
 * @see TableInteger
 * @see Row
 * 
 * @author <a href="mailto:alex.kerner.24@googlemail.com">Alexander Kerner</a>
 * @version 2013-05-13
 * 
 * @param <T>
 *            type of elements in {@code Table}
 */
public interface ReaderTable<T> extends IOIterable<Row<T>> {

	/**
	 * Close this reader.
	 * 
	 * @see Reader#close()
	 * 
	 */
	void close();

	Collection<? extends Pair<String, String>> getFilterRegex();

	/**
	 * Retrieve an {@link Iterator} to read one {@link Row} after another.
	 * 
	 * @throws IOException
	 *             if reading failed
	 */
	IOIterator<Row<T>> getIterator() throws IOException;

	/**
	 * 
	 * Read a {@link Table} at once.
	 * 
	 * @return new instance of {@code AnnotatedTable} that was read
	 * @throws IOException
	 *             if reading failed
	 */
	TableAnnotated<T> readTableAtOnce() throws IOException;

	ReaderTable<T> setFilterRegex(Collection<? extends Pair<String, String>> regexFilters);
}
