/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.core.launch;

import de.bmw.yamaica.base.core.YamaicaConstants;
import de.bmw.yamaica.base.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.base.core.resourceproperties.YamaicaXmlModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;

public abstract class AbstractLaunchConfigurationPreparer implements ILaunchConfigurationPreparer
{
    @Override
    public abstract ILaunchConfigurationWorkingCopy getPreparedLaunchConfiguration(ISelection selection, String mode);

    public IFile getTargetFile(IFile importedFile, String targetFilename)
    {
        IProject project = importedFile.getProject();
        YamaicaXmlModel model = YamaicaXmlModel.acquireInstance(project, this);
        IResourcePropertyStore store = model.getResourcePropertyStore(project);
        String importFolder = store.getProperty(YamaicaConstants.IMPORT_FOLDER);
        String targetFolder = store.getProperty(YamaicaConstants.TARGET_FOLDER);
        YamaicaXmlModel.releaseInstance(project, this);

        IPath inputPath = importedFile.getFullPath();
        IPath importPath = project.getFullPath();
        IPath outputPath = project.getFullPath();

        String filename = importedFile.getName();

        // Check if selected file is inside of "IMPORT_FOLDER". If yes, create transformed file(s) inside "TARGET_FOLDER".
        // If not create file within same folder but do not overwrite it.
        if (null != importFolder && null != targetFolder && null != (importPath = importPath.append(importFolder))
                && importPath.isPrefixOf(inputPath))
        {
            outputPath = outputPath.append(targetFolder);

            for (int i = importPath.segmentCount(); i < inputPath.segmentCount() - 1; i++)
            {
                outputPath = outputPath.append(inputPath.segment(i));
            }

            outputPath = outputPath.append(targetFilename);
        }
        else
        {
            outputPath = inputPath.removeLastSegments(1);
            String targetFilenameExtension = new Path(targetFilename).getFileExtension();
            IPath targetFilenameWithoutExtension = new Path(targetFilename).removeFileExtension();

            if (filename.equals(targetFilename))
            {
                outputPath = outputPath.append(targetFilenameWithoutExtension + YamaicaConstants.GEN_FILE_EXTENSION);
            }
            else
            {
                outputPath = outputPath.append(targetFilenameWithoutExtension);
            }

            if (null != targetFilenameExtension)
            {
                outputPath = outputPath.addFileExtension(targetFilenameExtension);
            }
        }

        return ResourcesPlugin.getWorkspace().getRoot().getFile(outputPath);
    }

    public ILaunchConfigurationWorkingCopy getNewLaunchConfiguration(String name, String launchConfigurationTypeId)
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(launchConfigurationTypeId);

        try
        {
            return launchConfigurationType.newInstance(null, launchManager.generateLaunchConfigurationName(name));
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String generateWorkspacePathExpression(IResource resource, boolean inline)
    {
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();

        String pathAsVariableExpression = null;

        if (inline)
        {
            // Generate something like ${workspace_loc:project_name/folder_name/file_name}
            pathAsVariableExpression = variableManager.generateVariableExpression(YamaicaConstants.WORKSPACE_LOC, resource.getFullPath()
                    .makeRelative().toString());
        }
        else
        {
            // Generate something like ${workspace_loc:project_name}/folder_name/file_name
            // This is needed if file does not exist in workspace!
            pathAsVariableExpression = variableManager.generateVariableExpression(YamaicaConstants.WORKSPACE_LOC, resource.getProject()
                    .getName())
                    + resource.getProjectRelativePath().makeAbsolute().toString();
        }

        return pathAsVariableExpression;
    }
}
