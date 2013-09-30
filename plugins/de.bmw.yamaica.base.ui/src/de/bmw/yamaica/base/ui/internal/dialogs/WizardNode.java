/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.ui.dialogs.IYamaicaWizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class WizardNode implements IWizardNode
{
    private IWorkbenchWizard           workbenchWizard = null;
    private YamaicaWizardSelectionPage yamaicaWizardSelectionPage;
    private IWizardDescriptor          wizardDescriptor;

    public WizardNode(YamaicaWizardSelectionPage yamaicaWizardSelectionPage, IWizardDescriptor wizardDescriptor)
    {
        this.yamaicaWizardSelectionPage = yamaicaWizardSelectionPage;
        this.wizardDescriptor = wizardDescriptor;
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public Point getExtent()
    {
        return new Point(-1, -1);
    }

    @Override
    public IWizard getWizard()
    {
        if (null == workbenchWizard)
        {
            try
            {
                workbenchWizard = wizardDescriptor.createWizard();
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }

        if (workbenchWizard instanceof IYamaicaWizard)
        {
            ((IYamaicaWizard) workbenchWizard).restrictWizard(true);
        }

        // Get the adapted version of the selection that works for the
        // wizard node
        IStructuredSelection currentSelection = wizardDescriptor.adaptedSelection(yamaicaWizardSelectionPage.getSelection());

        workbenchWizard.init(yamaicaWizardSelectionPage.getWorkbench(), currentSelection);

        return workbenchWizard;
    }

    @Override
    public boolean isContentCreated()
    {
        return null != workbenchWizard;
    }
}
