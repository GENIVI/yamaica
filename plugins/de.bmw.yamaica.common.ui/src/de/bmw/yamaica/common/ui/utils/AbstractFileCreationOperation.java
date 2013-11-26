/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public abstract class AbstractFileCreationOperation extends WorkspaceModifyOperation
{
    protected final String OPERATION_CANCELED_MESSAGE       = "Operation was canceled!";

    protected final String TASK_NAME_FILE_CREATION          = "Creating new file";
    protected final String SUB_TASK_NAME_CONTAINER_CREATION = "Creating target folder";
    protected final String SUB_TASK_NAME_FILE_CREATION      = "Creating file content";
    protected final String SUB_TASK_NAME_SAVING_FILE        = "Saving file";

    protected void createContainer(IContainer container, IProgressMonitor monitor) throws CoreException
    {
        new ContainerCreator(ResourcesPlugin.getWorkspace(), container.getFullPath()).createContainer(monitor);
    }

    protected void checkCancel(IProgressMonitor monitor) throws InterruptedException
    {
        if (monitor.isCanceled())
        {
            throw new InterruptedException(OPERATION_CANCELED_MESSAGE);
        }
    }
}
