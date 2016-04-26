/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.ide.IDE;

import de.bmw.yamaica.common.ui.internal.Activator;

public abstract class YamaicaWizard extends Wizard implements IWorkbenchWizard, IYamaicaWizard
{
    protected IWorkbench           workbench;
    protected IStructuredSelection structuredSelection;
    protected boolean              restrictWizard = false;

    public YamaicaWizard(String name)
    {
        IDialogSettings workbenchSettings = Activator.getDefault().getDialogSettings();
        IDialogSettings section = workbenchSettings.getSection(name);//$NON-NLS-1$

        if (section == null)
        {
            section = workbenchSettings.addNewSection(name);//$NON-NLS-1$
        }

        setDialogSettings(section);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        this.workbench = workbench;
        this.structuredSelection = null != structuredSelection ? structuredSelection : new StructuredSelection();

        List<?> selectedResources = IDE.computeSelectedResources(this.structuredSelection);

        if (!selectedResources.isEmpty())
        {
            this.structuredSelection = new StructuredSelection(selectedResources);
        }

        // look it up if current selection (after resource adapting) is empty
        if (this.structuredSelection.isEmpty() && workbench.getActiveWorkbenchWindow() != null)
        {
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

            if (page != null)
            {
                IEditorPart currentEditor = page.getActiveEditor();

                if (currentEditor != null)
                {
                    Object selectedResource = currentEditor.getEditorInput().getAdapter(IResource.class);

                    if (selectedResource != null)
                    {
                        this.structuredSelection = new StructuredSelection(selectedResource);
                    }
                }
            }
        }
    }

    @Override
    public boolean canFinish()
    {
        return super.canFinish();
    }

    @Override
    public boolean performFinish()
    {
        return true;
    }

    @Override
    public void restrictWizard(boolean restrict)
    {
        restrictWizard = restrict;
    }
}
