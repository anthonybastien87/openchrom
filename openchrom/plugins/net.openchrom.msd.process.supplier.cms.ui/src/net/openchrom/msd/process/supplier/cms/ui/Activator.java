/*******************************************************************************
 * Copyright (c) 2016, 2017 Walter Whitlock, Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Walter Whitlock - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.process.supplier.cms.ui;

import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.osgi.framework.BundleContext;

import net.openchrom.msd.process.supplier.cms.preferences.PreferenceSupplier;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	/*
	 * Instance
	 */
	private static Activator plugin;
	private static final String SYSTEM_CONSOLE_NAME = "CMS-Process";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AbstractActivatorUI getDefault() {

		return plugin;
	}

	public static MessageConsole getMessageConsole() {

		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();
		for(int i = 0; i < existing.length; i++) {
			if(SYSTEM_CONSOLE_NAME.equals(existing[i].getName())) {
				/*
				 * Activate the console.
				 */
				// ((MessageConsole)existing[i]).activate(); // display console window only when tab is selected
				return (MessageConsole)existing[i];
			}
		}
		/*
		 * Create a new console if it not exists.
		 */
		MessageConsole messageConsole = new MessageConsole(SYSTEM_CONSOLE_NAME, null);
		IConsole[] consoles = new IConsole[]{messageConsole};
		consoleManager.addConsoles(consoles);
		return messageConsole;
	}
}