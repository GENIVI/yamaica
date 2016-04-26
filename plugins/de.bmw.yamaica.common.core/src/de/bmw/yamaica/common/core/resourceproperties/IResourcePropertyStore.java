/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.resourceproperties;

import org.eclipse.core.resources.IResource;

import de.bmw.yamaica.common.core.utils.IPropertyChangeSupport;

public interface IResourcePropertyStore extends IPropertyChangeSupport
{
    public final String IMPORT_FOLDER = "IMPORT_FOLDER";
    public final String TARGET_FOLDER = "TARGET_FOLDER";

    public void setProperty(String name, String value);

    public String getProperty(String name);

    public String getProperty(String name, String value);

    public String[] getPropertyNames();

    public int getPropertyCount();

    public boolean hasProperty(String name);

    public IResource getResource();
}
