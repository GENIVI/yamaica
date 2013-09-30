/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import de.bmw.yamaica.base.core.internal.Activator;

public class ResourceUtils
{
    public static InputStream getResourceStreamFromPlugin(String pluginId, String resourcePath)
    {
        Assert.isNotNull(pluginId);
        Assert.isNotNull(resourcePath);

        try
        {
            URL resourceURL = new URL("platform:/plugin/" + pluginId + "/" + resourcePath);

            return resourceURL.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static InputStream getResourceStreamFromResourcesPlugin(String resourcePath)
    {
        Assert.isNotNull(resourcePath);

        try
        {
            URL resourceURL = new URL("platform:/plugin/" + Activator.RESOURCES_PLUGIN_ID + "/resources/" + resourcePath);

            return resourceURL.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static File getResourceFileFromResourcesPlugin(String resourcePath)
    {
        Assert.isNotNull(resourcePath);

        IPath path = new Path("");

        try
        {
            File file = FileLocator.getBundleFile(Platform.getBundle(Activator.RESOURCES_PLUGIN_ID));
            path = new Path(file.getPath()).append("resources").append(resourcePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new File(path.toOSString());
    }

    public static String changeFilenameExtension(String filename, String newExtension)
    {
        return new Path(filename).removeFileExtension().addFileExtension(newExtension).toString();
    }

    public static String performStringSubstitution(String filename)
    {
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();

        try
        {
            return variableManager.performStringSubstitution(filename);
        }
        catch (CoreException e)
        {
            return null;
        }
    }

    public static IPath getPlatformRelativePath(IPath path)
    {
        Assert.isNotNull(path);

        IResource resource = getResourceForLocation(path.toString());

        if (null == resource)
        {
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            resource = workspaceRoot.findMember(path);

            if (null == resource)
            {
                resource = workspaceRoot.getFile(path);

                if (null == resource)
                {
                    return null;
                }
            }
        }

        return new Path("platform:/resource").append(resource.getFullPath());
    }

    public static IResource getResourceForLocation(String pathAsString)
    {
        try
        {
            IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
            pathAsString = variableManager.performStringSubstitution(pathAsString);

            IPath path = new Path(pathAsString);
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = workspaceRoot.getFileForLocation(path);

            if (null != resource)
            {
                return resource;
            }

            return workspaceRoot.getContainerForLocation(path);
        }
        catch (CoreException e)
        {

        }

        return null;
    }
}
