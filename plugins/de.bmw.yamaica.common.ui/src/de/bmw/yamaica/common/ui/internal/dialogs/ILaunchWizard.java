/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import de.bmw.yamaica.common.core.launch.ILaunchConfigurationPreparer;

import org.eclipse.debug.core.ILaunchConfigurationType;

public interface ILaunchWizard
{
    public ILaunchConfigurationType getLaunchILaunchConfigurationType();

    public ILaunchConfigurationPreparer getLaunchConfigurationPreparer();

    public String getWizardTitle();
}
