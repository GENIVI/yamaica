/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides content for a tree viewer that shows only containers.
 */
public class ResourceContentProvider implements ITreeContentProvider
{
    private boolean showClosedProjects = true;

    public ResourceContentProvider()
    {
    }

    public void dispose()
    {
    }

    public Object[] getChildren(Object element)
    {
        if (element instanceof IWorkspace)
        {
            // check if closed projects should be shown
            IProject[] allProjects = ((IWorkspace) element).getRoot().getProjects();

            if (showClosedProjects)
            {
                return allProjects;
            }

            ArrayList<IProject> accessibleProjects = new ArrayList<IProject>();

            for (int i = 0; i < allProjects.length; i++)
            {
                if (allProjects[i].isOpen())
                {
                    accessibleProjects.add(allProjects[i]);
                }
            }

            return accessibleProjects.toArray(new IProject[accessibleProjects.size()]);
        }
        else if (element instanceof IContainer)
        {
            IContainer container = (IContainer) element;

            if (container.isAccessible())
            {
                try
                {
                    List<IResource> children = new ArrayList<IResource>();
                    IResource[] members = container.members();

                    for (int i = 0; i < members.length; i++)
                    {
                        if (members[i].getType() != IResource.FILE)
                        {
                            children.add(members[i]);
                        }
                    }

                    return children.toArray(new IResource[children.size()]);
                }
                catch (CoreException e)
                {
                    // this should never happen because we call #isAccessible before invoking #members
                }
            }
        }

        return new Object[0];
    }

    public Object[] getElements(Object element)
    {
        return getChildren(element);
    }

    public Object getParent(Object element)
    {
        if (element instanceof IResource)
        {
            return ((IResource) element).getParent();
        }

        return null;
    }

    public boolean hasChildren(Object element)
    {
        return getChildren(element).length > 0;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {

    }

    public void showClosedProjects(boolean show)
    {
        showClosedProjects = show;
    }
}
