/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import de.bmw.yamaica.ide.ui.internal.dialogs.ProjectWizard;

public class RemoveYamaicaNature extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        ISelection selection = workbench.getActiveWorkbenchWindow().getSelectionService().getSelection();

        if (selection instanceof IStructuredSelection)
        {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            for (Object selectedElement : structuredSelection.toList())
            {
                if (selectedElement instanceof IProject)
                {
                    ProjectWizard.removeYamaicaSpecificProjectSettings((IProject) selectedElement, new NullProgressMonitor());
                }
            }
        }

        return null;
    }
}
