/* Copyright (C) 2016 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import de.bmw.yamaica.common.core.YamaicaConstants;

/**
 * Wizard helper for import and export pages.
 */
public class WizardHelper
{
    private static final Logger        LOGGER = Logger.getLogger(WizardHelper.class.getName());

    private final IStructuredSelection structuredSelection;

    private final Shell                shell;

    public WizardHelper(IStructuredSelection structuredSelection, Shell shell)
    {
        this.structuredSelection = structuredSelection;
        this.shell = shell;
    }

    public IPath outputFileNamingBySelection()
    {
        return outputGenFileNaming(null, getSelectedResourcesBySelection());
    }

    public IPath outputGenFileNaming(final List<IResource> selectedResources)
    {
        return outputGenFileNaming(null, selectedResources);
    }

    public IPath outputGenFileNaming(String defaultName, final List<IResource> selectedResources)
    {
        if (selectedResources.isEmpty())
        {
            LOGGER.log(Level.FINEST, "There is no resource selected. Skipped default output file naming.");
            return null;
        }

        final IResource file = selectedResources.get(0);
        IPath makeRelative = file.getFullPath().makeRelative();
        final IPath projectName = new Path(makeRelative.segment(0));

        if (selectedResources.size() > 1)
        {
            // In case of several fdpel files selected: Use defaultName as common name.
            makeRelative = findMutualParent(selectedResources);

            // In case of defaultName shall be used.
            if (defaultName != null && !defaultName.trim().isEmpty())
            {
                makeRelative = makeRelative.append(defaultName);
            }
        }

        // Remove the ProjectName first in case of at least two segments.
        // Depends on folder. Examples:
        // - ProjectName/work/myFolder
        // - ProjectName/work
        // - ProjectName/myFolder
        // - ProjectName (in case of chosen mutual parent is the project name itself)
        if (makeRelative.segmentCount() > 1)
        {
            makeRelative = makeRelative.removeFirstSegments(1);

            // In case of 'work' folder was used: Remove it!
            if (makeRelative.segment(0).equalsIgnoreCase(YamaicaConstants.WORK))
            {
                makeRelative = makeRelative.removeFirstSegments(1);
            }
        }
        return projectName.append(YamaicaConstants.GEN_FILE_EXTENSION).append(makeRelative);
    }

    /**
     * Helper mechanism for naming output folder.
     *
     * @return Directory name of selection.
     */
    public IPath retrieveInputDirectoryBySelection()
    {
        final List<IResource> selectedResources = getSelectedResourcesBySelection();
        if (!selectedResources.isEmpty())
        {
            if (selectedResources.size() == 1)
            {
                return retrieveFolder(selectedResources.get(0));
            }
            else
            {
                return findMutualParent(selectedResources);
            }
        }
        return null;
    }

    public static <T extends IResource> List<T> filteredBy(List<IResource> resources, Class<T> cls)
    {
        final List<T> result = new ArrayList<>();

        for (IResource resource : resources)
        {
            if (cls.isInstance(resource))
            {
                result.add(cls.cast(resource));
            }
        }
        return result;
    }

    public List<IResource> getSelectedResourcesBySelection()
    {
        final List<?> list = structuredSelection.toList();
        final List<IResource> resources = new ArrayList<>();

        for (Object obj : list)
        {
            if (obj instanceof IResource)
            {
                resources.add((IResource) obj);
            }
        }
        return resources;
    }

    public static String removeRootSlash(String path)
    {
        if (path.startsWith("/"))
            return path.substring(1);
        return path;
    }

    private static <T extends IResource> IPath findMutualParent(final List<T> resources)
    {
        final IPath firstElement = resources.get(0).getFullPath();
        final String[] bestSegments = firstElement.segments();

        int bestCount = firstElement.segmentCount();

        for (T res : resources)
        {
            final String[] segments = res.getFullPath().segments();
            bestCount = Math.min(bestCount, segments.length);

            for (int i = 0; i < bestCount; i++)
            {
                if (!bestSegments[i].equals(segments[i]))
                {
                    bestCount = i;
                    break;
                }
            }
        }
        return firstElement.removeLastSegments(firstElement.segmentCount() - bestCount);
    }

    private static IPath retrieveFolder(final IResource resource)
    {
        if (resource instanceof IFile)
        {
            final IFile file = (IFile) resource;
            return file.getParent().getFullPath();
        }
        else
        {
            // Expected: IContainer or IFolder.
            return resource.getFullPath();
        }
    }

    public Shell getShell()
    {
        return shell;
    }

    public void selectResourceAndFillIntoTextWidget(final Text text, final boolean allowMutlipleFiles)
    {
        final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setAllowMultiple(allowMutlipleFiles);

        if (ResourceSelectionDialog.OK == dialog.open())
        {
            final IResource resource = (IResource) dialog.getFirstResult();

            text.setText(resource.getFullPath().makeRelative().toString());
        }
    }

    public void selectContainerAndFillIntoTextWidget(final Text text)
    {
        final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                "");

        if (ResourceSelectionDialog.OK == dialog.open())
        {
            final IPath path = (IPath) dialog.getResult()[0];
            text.setText(path.makeRelative().toString());
        }
    }

    public void selectContainerAndFillIntoTextWidget(final Combo text)
    {
        final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                "");

        if (ResourceSelectionDialog.OK == dialog.open())
        {
            final IPath path = (IPath) dialog.getResult()[0];
            text.setText(path.makeRelative().toString());
        }
    }

    /**
     * Converting list of java.io.File(s) to a list of org.eclipse.core.resources.Resource(s).
     *
     * @param javaIOFiles
     *            List of java.io.File(s).
     * @return List of org.eclipse.core.resources.Resource(s).
     */
    public static List<IResource> convertIOFilesToResources(final List<java.io.File> javaIOFiles)
    {
        final List<IResource> eclipseCoreResources = new ArrayList<>();
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        for (final java.io.File currIOFile : javaIOFiles)
        {
            final IPath absolutePath = new Path(currIOFile.getAbsolutePath());
            eclipseCoreResources.add(workspaceRoot.getFileForLocation(absolutePath));
        }
        return eclipseCoreResources;
    }
}
