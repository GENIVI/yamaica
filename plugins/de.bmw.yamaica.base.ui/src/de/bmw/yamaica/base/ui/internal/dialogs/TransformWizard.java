/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.core.launching.ILaunchConfigurationPreparer;
import de.bmw.yamaica.base.ui.dialogs.YamaicaWizard;
import de.bmw.yamaica.base.ui.internal.Activator;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class TransformWizard extends YamaicaWizard implements ILaunchWizard
{
    protected LaunchConfigurationTypeSelectionPage launchConfigurationTypeSelectionPage;
    protected LaunchConfigurationSelectionPage     launchConfigurationSelectionPage;

    public TransformWizard()
    {
        super("YamaicaTransformSelectionWizard");
    }

    public String getDefaultWindowTitle()
    {
        return "Transform";
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setWindowTitle(getDefaultWindowTitle());
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin("org.eclipse.debug.ui", "/icons/full/wizban/run_wiz.png"));
        setForcePreviousAndNextButtons(true);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages()
    {
        launchConfigurationTypeSelectionPage = new LaunchConfigurationTypeSelectionPage(workbench, structuredSelection);
        launchConfigurationSelectionPage = new LaunchConfigurationSelectionPage(workbench, structuredSelection);

        addPage(launchConfigurationTypeSelectionPage);
        addPage(launchConfigurationSelectionPage);
    }

    @Override
    public ILaunchConfigurationType getLaunchILaunchConfigurationType()
    {
        return launchConfigurationTypeSelectionPage.getLaunchConfigurationType();
    }

    @Override
    public ILaunchConfigurationPreparer getLaunchConfigurationPreparer()
    {
        return launchConfigurationTypeSelectionPage.getLaunchConfigurationPreparer();
    }

    @Override
    public String getWizardTitle()
    {
        return launchConfigurationTypeSelectionPage.getWizardTitle();
    }

    @Override
    public boolean performFinish()
    {
        if (super.performFinish())
        {
            return launchConfigurationSelectionPage.finish();
        }

        return false;
    }
}