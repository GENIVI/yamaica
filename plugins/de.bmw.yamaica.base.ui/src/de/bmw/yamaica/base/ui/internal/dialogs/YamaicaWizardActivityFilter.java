/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

public class YamaicaWizardActivityFilter extends ViewerFilter
{
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (WorkbenchActivityHelper.filterItem(element))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
