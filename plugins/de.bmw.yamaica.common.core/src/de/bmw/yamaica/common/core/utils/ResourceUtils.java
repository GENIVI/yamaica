/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.emf.common.util.URI;

import de.bmw.yamaica.common.core.YamaicaConstants;
import de.bmw.yamaica.common.core.internal.Activator;

/**
 * A utility class for common Eclipse resource tasks.
 */
public class ResourceUtils
{
    /**
     * @deprecated use {@link FileLocator#openStream(org.osgi.framework.Bundle, IPath, boolean)}
     */
    @Deprecated
    public static InputStream getResourceStreamFromPlugin(String pluginId, String resourcePath)
    {
        Assert.isNotNull(pluginId);
        Assert.isNotNull(resourcePath);

        try
        {
            URL resourceURL = new URL(YamaicaConstants.PLATFORM_PLUGIN_PATH + pluginId + "/" + resourcePath);

            return resourceURL.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @deprecated use {@link FileLocator#openStream(org.osgi.framework.Bundle, IPath, boolean)}
     */
    @Deprecated
    public static InputStream getResourceStreamFromResourcesPlugin(String resourcePath)
    {
        return getResourceStreamFromPlugin(Activator.RESOURCES_PLUGIN_ID, resourcePath);
    }

    /**
     * Returns a {@link File} object representing a file within a folder bundle.
     * 
     * @param bundleId
     *            The ID of the bundle.
     * @param bundleRelativePath
     *            A relative path within the bundle folder.
     * @return a {@link File} object representing a file within a folder bundle.
     */
    public static File getResourceFileFromBundle(String bundleId, String bundleRelativePath)
    {
        Assert.isNotNull(bundleId);
        Assert.isNotNull(bundleRelativePath);

        try
        {
            File file = FileLocator.getBundleFile(Platform.getBundle(bundleId));
            IPath path = new Path(file.getPath()).append(bundleRelativePath);

            return new File(path.toOSString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @deprecated use {@link ResourceUtils#getResourceFileFromPlugin(String, String)}
     */
    @Deprecated
    public static File getResourceFileFromResourcesPlugin(String resourcePath)
    {
        return getResourceFileFromBundle(Activator.RESOURCES_PLUGIN_ID, new Path(YamaicaConstants.RESOURCES).append(resourcePath)
                .toString());
    }

    /**
     * Changes the file extension of the referred filename to the referred value.
     * 
     * @param filename
     *            The filename with the old file extension.
     * @param newExtension
     *            The new file extension.
     * @return the filename with the new file extension.
     */
    public static String changeFilenameExtension(String filename, String newExtension)
    {
        return new Path(filename).removeFileExtension().addFileExtension(newExtension).toString();
    }

    /**
     * Recursively resolves and replaces all variable references in the given expression with their corresponding values. Reports errors for
     * references to undefined variables.
     * 
     * @param expression
     *            expression referencing variables
     * @return expression with variable references replaced with variable values.
     * @throws CoreException
     *             if unable to resolve the value of one or more variables.
     * @see IStringVariableManager#performStringSubstitution(String)
     */
    public static String performStringSubstitution(String expression)
    {
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();

        try
        {
            return variableManager.performStringSubstitution(expression);
        }
        catch (CoreException e)
        {
            return expression;
        }
    }

    /**
     * Converts a workspace relative path (e.g. PROJECT_NAME/FOLDER_NAME/FILE_NAME) to a Eclipse platform path (e.g.
     * platform:/resource/PROJECT_NAME/FOLDER_NAME/FILE_NAME).
     * 
     * @param path
     *            A workspace relative path.
     * @return a Eclipse platform path.
     * @see ResourceUtils#createPlatformPath(String)
     * @see URI#createPlatformResourceURI(String, Boolean)
     */
    public static IPath createPlatformPath(IPath path)
    {
        Assert.isNotNull(path);

        return createPlatformPath(path.toString());
    }

    /**
     * Converts a workspace relative path (e.g. PROJECT_NAME/FOLDER_NAME/FILE_NAME) to a Eclipse platform path (e.g.
     * platform:/resource/PROJECT_NAME/FOLDER_NAME/FILE_NAME).
     * 
     * @param pathAsString
     *            A workspace relative path.
     * @return a Eclipse platform path.
     * @see URI#createPlatformResourceURI(String, Boolean)
     */
    public static IPath createPlatformPath(String pathAsString)
    {
        Assert.isNotNull(pathAsString);

        pathAsString = performStringSubstitution(pathAsString);

        return new Path(URI.createPlatformResourceURI(pathAsString, true).toString());
    }

    /**
     * Creates a EMF file or platform URI. If the referred path can be resolved to a workspace resource a platform URI will be created.
     * 
     * @param path
     *            May be a workspace relative or a absolute system path
     * @return a EMF file or platform URI.
     * @see ResourceUtils#createURIForLocation(String)
     */
    public static URI createURIForLocation(IPath path)
    {
        Assert.isNotNull(path);

        return createURIForLocation(path.toString());
    }

    /**
     * Creates a EMF file or platform URI. If the referred path can be resolved to a workspace resource a platform URI will be created.
     * 
     * @param pathAsString
     *            May be a workspace relative or a absolute system path
     * @return a EMF file or platform URI.
     */
    public static URI createURIForLocation(String pathAsString)
    {
        Assert.isNotNull(pathAsString);

        IPath path = new Path(performStringSubstitution(pathAsString));
        int segementCount = path.segmentCount();

        for (int i = segementCount; i > 0; i--)
        {
            IPath projectPath = path.removeLastSegments(i - 1);
            IPath workspacePath = path.removeFirstSegments(segementCount - i).setDevice(null);

            if (getResourceForLocation(projectPath.toString()) instanceof IProject)
            {
                return URI.createPlatformResourceURI(workspacePath.toString(), true);
            }
        }

        return URI.createFileURI(path.toString());
    }

    /**
     * Returns the workspace resource represented by the referred path. Finds only non linked resources.
     * 
     * @param pathAsString
     *            May be a workspace relative or a absolute system path
     * @return the workspace resource represented by the referred path.
     */
    public static IResource getResourceForLocation(String pathAsString)
    {
        Assert.isNotNull(pathAsString);

        pathAsString = performStringSubstitution(pathAsString);
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = workspaceRoot.findMember(pathAsString);

        if (null != resource)
        {
            return resource;
        }

        IPath path = new Path(pathAsString);
        resource = workspaceRoot.getFileForLocation(path);

        return (null != resource) ? resource : workspaceRoot.getContainerForLocation(path);
    }
}
