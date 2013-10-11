/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.bmw.yamaica.base.core.launch.AbstractLaunchConfigurationPreparer;
import de.bmw.yamaica.base.ui.YamaicaUIConstants;

public abstract class AbstractLaunchShortcut extends AbstractLaunchConfigurationPreparer implements ILaunchShortcut
{
    private static final String SELECT = "Select ";
    private static final String SELECT_EXISTING_CONFIGURATION = "Select existing configuration:";
    protected ILaunchConfiguration selectedLaunchConfiguration = null;

    @Override
    public void launch(IEditorPart editor, String mode)
    {
        IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();

        IStructuredSelection selection = new StructuredSelection(input.getFile());

        launch(selection, mode);
    }

    public ILaunchConfiguration[] getLaunchConfigurations(String launchConfigurationTypeId)
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(launchConfigurationTypeId);

        try
        {
            return launchManager.getLaunchConfigurations(launchConfigurationType);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String getLaunchConfigurationTypeName(String launchConfigurationTypeId)
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        return launchManager.getLaunchConfigurationType(launchConfigurationTypeId).getName();
    }

    public void launch(ILaunchConfiguration[] launchConfigurations, String launchConfigurationTypeName, final String mode)
    {
        switch (launchConfigurations.length)
        {
            case 0:
                break;

            case 1:
                selectedLaunchConfiguration = launchConfigurations[0];
                break;

            default:
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                ILabelProvider labelProvider = DebugUITools.newDebugModelPresentation();
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
                dialog.setTitle(SELECT + launchConfigurationTypeName);
                dialog.setMessage(SELECT_EXISTING_CONFIGURATION);
                dialog.setElements(launchConfigurations);

                if (dialog.open() == ElementListSelectionDialog.OK)
                {
                    selectedLaunchConfiguration = (ILaunchConfiguration) dialog.getFirstResult();
                }

                labelProvider.dispose();
        }

        if (null != selectedLaunchConfiguration)
        {
            Job job = new Job(YamaicaUIConstants.LAUNCHING + selectedLaunchConfiguration.getName())
            {
                @Override
                protected IStatus run(IProgressMonitor monitor)
                {
                    try
                    {
                        selectedLaunchConfiguration.launch(mode, monitor);
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }

                    return Status.OK_STATUS;
                }
            };

            job.schedule();
        }
    }
}
