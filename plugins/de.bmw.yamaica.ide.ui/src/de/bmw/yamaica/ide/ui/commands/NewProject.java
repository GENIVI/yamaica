/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewProject extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        ISelection selection = workbench.getActiveWorkbenchWindow().getSelectionService().getSelection();
        IStructuredSelection structuredSelection = null;

        if (selection instanceof IStructuredSelection)
        {
            structuredSelection = (IStructuredSelection) selection;
        }

        IWizardDescriptor descriptor = workbench.getNewWizardRegistry().findWizard("de.bmw.yamaica.ui.projectWizard");

        if (null != descriptor)
        {
            try
            {
                IWorkbenchWizard workbenchWizard = descriptor.createWizard();
                workbenchWizard.init(workbench, structuredSelection);

                WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveShell(event), workbenchWizard);
                wizardDialog.setTitle(workbenchWizard.getWindowTitle());
                wizardDialog.open();
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }
}
