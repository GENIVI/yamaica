/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.franca.deploymodel.dsl.fDeploy.FDModel;
import org.franca.deploymodel.dsl.fDeploy.FDPropertyHost;

import de.bmw.yamaica.common.ui.utils.AbstractFileCreationOperation;
import de.bmw.yamaica.franca.core.DeploymentDescriptionModelCreator;

public class DeploymentDescriptionCreationOperation extends AbstractFileCreationOperation
{
    protected final IFile                file;
    protected final String               name;
    protected final List<FDPropertyHost> propertyHosts;

    public DeploymentDescriptionCreationOperation(IFile file, String name, List<FDPropertyHost> propertyHosts)
    {
        this.file = file;
        this.name = name;
        this.propertyHosts = propertyHosts;
    }

    @Override
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException
    {
        try
        {
            monitor.beginTask(TASK_NAME_FILE_CREATION, 3);
            monitor.subTask(SUB_TASK_NAME_CONTAINER_CREATION);

            createContainer(file.getParent(), monitor);

            monitor.worked(1);
            checkCancel(monitor);
            monitor.subTask(SUB_TASK_NAME_FILE_CREATION);

            DeploymentDescriptionModelCreator modelCreator = new DeploymentDescriptionModelCreator();
            FDModel model = modelCreator.createModel(name, propertyHosts);

            monitor.worked(1);
            checkCancel(monitor);
            monitor.subTask(SUB_TASK_NAME_SAVING_FILE);

            SynchronizedXtextResourceSet resourcesSet = new SynchronizedXtextResourceSet();
            Resource resource = resourcesSet.createResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true));
            resource.getContents().add(model);
            resource.save(Collections.emptyMap());
        }
        catch (IOException e)
        {
            throw new InvocationTargetException(e);
        }
        finally
        {
            monitor.worked(1);
            monitor.done();
        }
    }
}
