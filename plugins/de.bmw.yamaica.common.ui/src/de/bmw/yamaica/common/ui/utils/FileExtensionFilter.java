/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;

public class FileExtensionFilter extends BaseExtensionFilter
{
    public FileExtensionFilter(String[] fileExtensions)
    {
        super(fileExtensions);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        File file = (File) element;

        if (file.isDirectory())
        {
            // Hide 'hidden' directories (e.g. ".settings").
            if (file.getName().startsWith("."))
                return false;
            return true;
        }

        return hasValidExtension(file.getName());
    }
}
