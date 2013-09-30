/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import org.eclipse.jface.viewers.ViewerFilter;

public abstract class BaseExtensionFilter extends ViewerFilter
{
    protected String[] fileExtensions;

    public BaseExtensionFilter(String[] fileExtensions)
    {
        for (int i = 0; i < fileExtensions.length; i++)
        {
            fileExtensions[i] = fileExtensions[i].toLowerCase();
        }

        this.fileExtensions = fileExtensions;
    }

    protected boolean hasValidExtension(String filename)
    {
        for (String fileExtension : fileExtensions)
        {
            if (filename.toLowerCase().endsWith("." + fileExtension))
            {
                return true;
            }
        }

        return false;
    }
}
