/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.util.Comparator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ViewerComparator;

public class ResourceComparator extends ViewerComparator
{
    protected static final int   CONTAINER  = 1;
    protected static final int   FILE       = 2;

    protected Comparator<String> comparator = null;

    @Override
    public int category(Object element)
    {
        IResource resource = (IResource) element;

        if (resource instanceof IFile)
        {
            return FILE;
        }

        if (resource instanceof IContainer)
        {
            return CONTAINER;
        }

        return super.category(element);
    }

    @Override
    protected Comparator<String> getComparator()
    {
        if (null == comparator)
        {
            comparator = new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return o1.compareToIgnoreCase(o2);
                }
            };
        }

        return comparator;
    }
}
