/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.util.Comparator;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class WizardComparator extends ViewerComparator
{
    protected Comparator<String> comparator = null;

    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        if (e1 instanceof IWizardDescriptor && e2 instanceof IWizardDescriptor)
        {
            return compareWizards(viewer, (IWizardDescriptor) e1, (IWizardDescriptor) e2);
        }
        else if (e1 instanceof TreeNode && e2 instanceof TreeNode)
        {
            TreeNode tn1 = (TreeNode) e1;
            TreeNode tn2 = (TreeNode) e2;
            if (tn1.getValue() instanceof IWizardDescriptor && tn2.getValue() instanceof IWizardDescriptor)
                return compareWizards(viewer, (IWizardDescriptor) tn1.getValue(), (IWizardDescriptor) tn2.getValue());
        }
        return 0;
    }

    public int compareWizards(Viewer viewer, IWizardDescriptor wiz1, IWizardDescriptor wiz2)
    {
        // Order the Eclipse Generic File Import/Export Wizard always at the end of the list
        boolean wiz1FileSystem = WizardSelector.isGenericFileSystemWizard(wiz1);
        boolean wiz2FileSystem = WizardSelector.isGenericFileSystemWizard(wiz2);
        if (wiz1FileSystem && wiz2FileSystem)
            return 0;
        if (wiz1FileSystem)
            return 1;
        if (wiz2FileSystem)
            return -1;
        return super.compare(viewer, wiz1.getLabel(), wiz2.getLabel());
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
