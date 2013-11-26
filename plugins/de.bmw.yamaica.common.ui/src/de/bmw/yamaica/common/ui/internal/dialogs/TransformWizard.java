/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.common.core.launch.ILaunchConfigurationPreparer;
import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.dialogs.YamaicaWizard;
import de.bmw.yamaica.common.ui.internal.Activator;

public class TransformWizard extends YamaicaWizard implements ILaunchWizard
{
    private static final String                    TRANSFORM                          = "Transform";
    private static final String                    YAMAICA_TRANSFORM_SELECTION_WIZARD = "YamaicaTransformSelectionWizard";
    protected LaunchConfigurationTypeSelectionPage launchConfigurationTypeSelectionPage;
    protected LaunchConfigurationSelectionPage     launchConfigurationSelectionPage;

    public TransformWizard()
    {
        super(YAMAICA_TRANSFORM_SELECTION_WIZARD);
    }

    public String getDefaultWindowTitle()
    {
        return TRANSFORM;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setWindowTitle(getDefaultWindowTitle());
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID,
                YamaicaUIConstants.RUN_WIZARD_BANNER_PATH));
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
