/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileSystemContentProvider implements ITreeContentProvider
{
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {

    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        return ((File) parentElement).listFiles();
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return ((File) inputElement).listFiles();
    }

    @Override
    public Object getParent(Object element)
    {
        return ((File) element).getParentFile();
    }

    @Override
    public boolean hasChildren(Object element)
    {
        return null != ((File) element).listFiles();
    }

    @Override
    public void dispose()
    {

    }
}
