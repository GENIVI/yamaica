/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class WizardComparator extends ViewerComparator
{
    protected Comparator<String> comparator = null;

    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        return super.compare(viewer, ((IWizardDescriptor) e1).getLabel(), ((IWizardDescriptor) e2).getLabel());
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
