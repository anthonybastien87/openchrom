/*******************************************************************************
 * Copyright (c) 2013, 2014 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.util.DirectorySuiteBuilder;
import junitx.util.SimpleTestFilter;

/**
 * This class collects all junit tests this plugin and runs them.<br/>
 * It is necessary that the tests end with *Test.
 * 
 * @author eselmeister
 */
public class AllTests extends TestSuite {

	public static Test suite() throws Exception {

		DirectorySuiteBuilder builder = new DirectorySuiteBuilder();
		builder.setFilter(new SimpleTestFilter());
		return builder.suite("bin/");
	}
}
