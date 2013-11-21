/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import de.bmw.yamaica.base.core.YamaicaNature;
import de.bmw.yamaica.base.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.base.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.base.ui.dialogs.YamaicaWizard;
import de.bmw.yamaica.ide.ui.internal.Activator;
import de.bmw.yamaica.ide.ui.internal.preferences.Preferences;

public class ProjectWizard extends YamaicaWizard implements INewWizard
{
    private WizardNewProjectCreationPage projectPage;
    private ProjectSettingsPage          projectSettingsPage;

    public ProjectWizard()
    {
        super("YamaicaProjectWizard");
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setNeedsProgressMonitor(true);
        setWindowTitle("New yamaica Project");
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin("org.eclipse.ui.ide", "/icons/full/wizban/importproj_wiz.png"));
    }

    @Override
    public void addPages()
    {
        super.addPages();

        projectPage = new WizardNewProjectCreationPage("NewYamaicaProjectWizard");
        projectPage.setTitle("yamaica Project");
        projectPage.setDescription("Create a yamaica project");

        projectSettingsPage = new ProjectSettingsPage(workbench, structuredSelection);

        addPage(projectPage);
        addPage(projectSettingsPage);
    }

    @Override
    public boolean canFinish()
    {
        // Make a case sensitive check if there already exists a project with the specified name
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
        {
            if (project.getName().toLowerCase().equals(projectPage.getProjectName().toLowerCase()))
            {
                projectPage.setErrorMessage("A project with this name already exists.");

                return false;
            }
        }

        return super.canFinish();
    }

    @Override
    public boolean performFinish()
    {
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
        {
            @Override
            protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException
            {
                createProject(monitor != null ? monitor : new NullProgressMonitor());
            }
        };

        try
        {
            getContainer().run(false, true, operation);
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();

            return false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void createProject(IProgressMonitor monitor)
    {
        monitor.beginTask("Creating yamaica project", 20);

        try
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();
            IProject project = root.getProject(projectPage.getProjectName());
            IProjectDescription description = workspace.newProjectDescription(project.getName());

            if (!Platform.getLocation().equals(projectPage.getLocationPath()))
            {
                description.setLocation(projectPage.getLocationPath());
            }

            project.create(description, monitor);
            project.open(IProject.BACKGROUND_REFRESH, monitor);

            monitor.worked(10);

            monitor.subTask("Creating project resources");

            addYamaicaSpecificProjectSettings(project, monitor);

            monitor.worked(10);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        finally
        {
            monitor.done();
        }
    }

    @Override
    public void createPageControls(Composite pageContainer)
    {

    }

    public static void addYamaicaSpecificProjectSettings(IProject project, IProgressMonitor monitor)
    {
        try
        {
            IProjectDescription projectDescription = project.getDescription();
            String xtextNature = "org.eclipse.xtext.ui.shared.xtextNature";
            String[] natureIds = projectDescription.getNatureIds();
            ArrayList<String> newNatureIds = new ArrayList<String>(natureIds.length + 2);

            for (String natureId : natureIds)
            {
                newNatureIds.add(natureId);
            }

            if (!projectDescription.hasNature(YamaicaNature.NATURE_ID))
            {
                newNatureIds.add(YamaicaNature.NATURE_ID);
            }

            if (!projectDescription.hasNature(xtextNature))
            {
                newNatureIds.add(xtextNature);

                ICommand command = projectDescription.newCommand();
                command.setBuilderName("org.eclipse.xtext.ui.shared.xtextBuilder");
                projectDescription.setBuildSpec(new ICommand[] { command });
            }

            projectDescription.setNatureIds(newNatureIds.toArray(new String[newNatureIds.size()]));
            project.setDescription(projectDescription, monitor);

            IPreferenceStore baseStore = de.bmw.yamaica.base.ui.Preferences.getPreferenceStore();
            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            YamaicaXmlModel model = YamaicaXmlModel.acquireInstance(project, project);
            IResourcePropertyStore resourcePropertyStore = model.getResourcePropertyStore(project);
            String importFolderPathAsString = resourcePropertyStore.getProperty(IResourcePropertyStore.IMPORT_FOLDER);
            String targetFolderPathAsString = resourcePropertyStore.getProperty(IResourcePropertyStore.TARGET_FOLDER);

            if (null == importFolderPathAsString)
            {
                importFolderPathAsString = baseStore.getString(de.bmw.yamaica.base.ui.Preferences.IMPORT_FOLDER);
                resourcePropertyStore.setProperty(IResourcePropertyStore.IMPORT_FOLDER, importFolderPathAsString);
            }

            if (null == targetFolderPathAsString)
            {
                targetFolderPathAsString = baseStore.getString(de.bmw.yamaica.base.ui.Preferences.TARGET_FOLDER);
                resourcePropertyStore.setProperty(IResourcePropertyStore.TARGET_FOLDER, targetFolderPathAsString);
            }

            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IFolder importFolder = project.getFolder(new Path(importFolderPathAsString));
            IFolder targetFolder = project.getFolder(new Path(targetFolderPathAsString));

            if (!importFolder.exists())
            {
                new ContainerCreator(workspace, importFolder.getFullPath()).createContainer(monitor);
            }

            if (!targetFolder.exists())
            {
                new ContainerCreator(workspace, targetFolder.getFullPath()).createContainer(monitor);
            }

            model.save();

            if (store.getBoolean(Preferences.CREATE_YAMAICA_EDITOR_LINK))
            {
                IWorkspaceRoot root = workspace.getRoot();
                IPath projectPath = project.getFullPath();
                IPath yamaicaXmlFilePath = projectPath.append(store.getString(Preferences.YAMAICA_EDITOR_LINK_NAME));
                IFile projectFile = root.getFile(yamaicaXmlFilePath);
                IFile projectSettingsFile = YamaicaXmlModel.getSettingsFile(project);

                URI projectSettingsFileUri = projectSettingsFile.getPathVariableManager().convertToRelative(
                        projectSettingsFile.getLocationURI(), false, "PROJECT_LOC");

                if (!projectFile.exists())
                {
                    projectFile.createLink(projectSettingsFileUri, IResource.ALLOW_MISSING_LOCAL, monitor);
                }
            }

            YamaicaXmlModel.releaseInstance(project, project);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    public static void removeYamaicaSpecificProjectSettings(IProject project, IProgressMonitor monitor)
    {
        try
        {
            IProjectDescription projectDescription = project.getDescription();
            String[] natureIds = projectDescription.getNatureIds();
            ArrayList<String> newNatureIds = new ArrayList<String>(natureIds.length);

            for (String natureId : natureIds)
            {
                if (!natureId.equals(YamaicaNature.NATURE_ID))
                {
                    newNatureIds.add(natureId);
                }
            }

            projectDescription.setNatureIds(newNatureIds.toArray(new String[newNatureIds.size()]));
            project.setDescription(projectDescription, monitor);

            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            String yamaicaEditorLinkName = store.getDefaultString(Preferences.YAMAICA_EDITOR_LINK_NAME);
            IFile yamaicaEditorLinkFile = project.getFile(yamaicaEditorLinkName);

            if (yamaicaEditorLinkFile.exists())
            {
                yamaicaEditorLinkFile.delete(true, monitor);
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }
}
