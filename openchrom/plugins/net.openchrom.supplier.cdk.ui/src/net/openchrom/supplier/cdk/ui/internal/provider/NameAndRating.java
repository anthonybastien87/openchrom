/*******************************************************************************
 * Copyright (c) 2013 Marwin Wollschläger.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marwin Wollschläger - initial API and implementation
*******************************************************************************/
package net.openchrom.supplier.cdk.ui.internal.provider;


public class NameAndRating {
	public String name;
	public Double rating;
	
	public NameAndRating(String name,Double rating)
	{
		this.name=name;
		this.rating=rating;
	}
}
