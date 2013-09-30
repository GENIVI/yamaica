/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.io.File;
import java.util.Comparator;

import org.eclipse.jface.viewers.ViewerComparator;

public class FileSystemComparator extends ViewerComparator
{
    protected static final int   DIRECTORY  = 1;
    protected static final int   FILE       = 2;

    protected Comparator<String> comparator = null;

    @Override
    public int category(Object element)
    {
        File file = (File) element;

        if (file.isFile())
        {
            return FILE;
        }

        if (file.isDirectory())
        {
            return DIRECTORY;
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
