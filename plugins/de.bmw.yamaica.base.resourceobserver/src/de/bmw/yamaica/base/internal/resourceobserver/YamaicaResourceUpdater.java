/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.internal.resourceobserver;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import de.bmw.yamaica.base.core.resourceproperties.YamaicaXmlModel;

public class YamaicaResourceUpdater implements IResourceChangeListener
{
    private static YamaicaResourceUpdater instance = null;

    private YamaicaResourceUpdater()
    {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    public static synchronized YamaicaResourceUpdater getInstance()
    {
        if (null == instance)
        {
            instance = new YamaicaResourceUpdater();
        }

        return instance;
    }

    public synchronized void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        instance = null;
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE)
        {
            IResourceDelta workspaceDelta = event.getDelta();

            if (null != workspaceDelta)
            {
                IResourceDelta[] projectDeltas = workspaceDelta.getAffectedChildren();

                for (IResourceDelta projectDelta : projectDeltas)
                {
                    IProject project = (IProject) projectDelta.getResource();
                    final IFile projectSettingsFile = YamaicaXmlModel.getSettingsFile(project);

                    // Search in every eclipse project for the yamaica settings file. If the yamaica
                    // settings file actually exists and if it is NOT part of the resource change event
                    // we have to check if a linked resource to this file was changed/edited.
                    if (null != projectSettingsFile && null == workspaceDelta.findMember(projectSettingsFile.getFullPath()))
                    {
                        // Get all eclipse resources that link to the yamaica settings file (e.g. yamaica.xml)
                        IFile[] settingFiles = project.getWorkspace().getRoot()
                                .findFilesForLocationURI(projectSettingsFile.getLocationURI());

                        // Check if any of the links to the yamaica setting file were edited (are part of this event)
                        for (IFile settingFile : settingFiles)
                        {
                            IResourceDelta resourceDelta = workspaceDelta.findMember(settingFile.getFullPath());

                            if (null != resourceDelta)
                            {
                                // If a linked resource was edited we must refresh the original resource. This must be done
                                // within a workspace job since the workspace is locked while this event is processed.
                                IWorkspace workspace = ((IWorkspaceRoot) workspaceDelta.getResource()).getWorkspace();
                                ISchedulingRule schedulingRule = workspace.getRuleFactory().refreshRule(projectSettingsFile);

                                WorkspaceJob job = new WorkspaceJob("Updating yamaica settings file.")
                                {
                                    @Override
                                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
                                    {
                                        projectSettingsFile.refreshLocal(IResource.DEPTH_ZERO, monitor);

                                        return Status.OK_STATUS;
                                    }
                                };
                                job.setRule(schedulingRule);
                                job.schedule();
                            }
                        }
                    }
                }
            }
        }

        // IResourceDelta resourceDelta = event.getDelta();
        // IResource resource = event.getResource();
        // int type = event.getType();
        //
        // String typeDescription = "";
        //
        // switch (type)
        // {
        // case IResourceChangeEvent.PRE_BUILD:
        // typeDescription = "PRE_BUILD";
        // break;
        //
        // case IResourceChangeEvent.POST_BUILD:
        // typeDescription = "POST_BUILD";
        // break;
        //
        // case IResourceChangeEvent.POST_CHANGE:
        // typeDescription = "POST_CHANGE";
        // break;
        //
        // case IResourceChangeEvent.PRE_CLOSE:
        // typeDescription = "PRE_CLOSE";
        // break;
        //
        // case IResourceChangeEvent.PRE_DELETE:
        // typeDescription = "PRE_DELETE";
        // break;
        //
        // case IResourceChangeEvent.PRE_REFRESH:
        // typeDescription = "PRE_REFRESH";
        // break;
        // }
        //
        // System.out.println("Resource Delta: " + resourceDelta);
        // System.out.println("Resource:       " + resource);
        // System.out.println("Type:           " + type + " (" + typeDescription + ")");
        //
        // printAffectedChildren(resourceDelta);
    }

    // private void printAffectedChildren(IResourceDelta resourceDelta)
    // {
    // if (null == resourceDelta)
    // {
    // return;
    // }
    //
    // for (IResourceDelta resourceDeltaChildren : resourceDelta.getAffectedChildren())
    // {
    // IResource resource = resourceDeltaChildren.getResource();
    //
    // if (null == resource)
    // {
    // System.err.println("error!");
    // continue;
    // }
    //
    // String resourceType = "";
    //
    // if (resource instanceof IFile)
    // {
    // resourceType = "IFile:    ";
    // }
    // else if (resource instanceof IFolder)
    // {
    // resourceType = "IFolder:  ";
    // }
    // else if (resource instanceof IProject)
    // {
    // resourceType = "IProject: ";
    // }
    //
    // String linksTo = "";
    //
    // IPath recourceLocation = resource.getLocation();
    //
    // if (null != recourceLocation)
    // {
    // linksTo = " -> " + recourceLocation.toString();
    // }
    //
    // System.out.println(resourceType + resource.getFullPath().toString() + linksTo + " - is linked: " + resource.isLinked());
    //
    // printAffectedChildren(resourceDeltaChildren);
    // }
    // }
}
