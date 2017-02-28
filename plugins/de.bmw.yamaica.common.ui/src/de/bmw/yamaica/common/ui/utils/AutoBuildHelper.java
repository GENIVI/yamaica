/* Copyright (C) 2016 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;

public class AutoBuildHelper
{
    public static boolean isWorkspaceAutoBuilding()
    {
        try
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceDescription description = workspace.getDescription();
            return description.isAutoBuilding();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static void setWorkspaceAutoBuild(boolean enable)
    {
        try
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceDescription description = workspace.getDescription();
            description.setAutoBuilding(enable);
            workspace.setDescription(description);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
