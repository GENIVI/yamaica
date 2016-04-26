/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;

public class ResourceExtensionFilter extends BaseExtensionFilter
{
    public ResourceExtensionFilter(String[] fileExtensions)
    {
        super(fileExtensions);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        IResource resource = (IResource) element;

        if (resource instanceof IContainer)
        {
            return true;
        }

        return hasValidExtension(resource.getName());
    }
}
