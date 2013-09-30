/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class YamaicaNature implements IProjectNature
{
    public YamaicaNature()
    {

    }

    @Override
    public void configure() throws CoreException
    {

    }

    @Override
    public void deconfigure() throws CoreException
    {

    }

    @Override
    public IProject getProject()
    {
        return null;
    }

    @Override
    public void setProject(IProject project)
    {

    }
}
