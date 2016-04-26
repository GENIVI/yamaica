/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import de.bmw.yamaica.common.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.common.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.common.ui.YamaicaUIConstants;

public abstract class YamaicaWizardNewFilePage extends WizardPage implements Listener
{
    private static final String    ERROR_OCCURRED_DURING_OPERATION_0                           = "Error occurred during operation: {0}";
    private static final String    INTERNAL_ERROR                                              = "Internal error";
    private static final String    A_RESOURCE_AT_THE_SPECIFIED_PATH_ALREADY_EXISTS             = "A resource at the specified path already exists.";
    private static final String    THE_FILENAME_EXTENSION_MUST_BE                              = "The filename extension must be ";
    private static final String    THE_FILENAME_MAY_NOT_CONSIST_OF_THE_FILENAME_EXTENSION_ONLY = "The filename may not consist of the filename extension only.";
    private static final String    THE_FILENAME_IS_NOT_VALID                                   = "The filename is not valid.";
    private static final String    IS_NOT_OPENED                                               = "\" is not opened.";
    private static final String    DOES_NOT_EXIST                                              = "\" does not exist.";
    private static final String    THE_SPECIFIED_PROJECT                                       = "The specified project \"";
    private static final String    THE_TARGET_FOLDER_PATH_IS_NOT_VALID                         = "The target folder path is not valid.";
    private static final String    ADD_DEMO_CONTENT                                            = "Add demo &content";
    private static final String    OPTIONS                                                     = "Options";
    private static final String    FILENAME                                                    = "Fi&lename:";
    private static final String    BROWSE                                                      = "B&rowse...";
    private static final String    TARGET_FOLDER                                               = "Target fol&der:";
    protected IWorkbench           workbench;
    protected IStructuredSelection structuredSelection;
    protected boolean              restrictWizardPage                                          = false;

    protected IContainer           rootContainer;
    protected IContainer           targetContainer;

    protected Button               browseButton;
    protected Text                 targetFolderText;
    protected Text                 filenameText;
    protected Button               demoContentCheckbox;

    public YamaicaWizardNewFilePage(IWorkbench workbench, IStructuredSelection structuredSelection, String name)
    {
        super(name);

        this.workbench = workbench;
        this.structuredSelection = structuredSelection;

        if (structuredSelection.size() == 1)
        {
            Object selectedElement = structuredSelection.getFirstElement();

            if (selectedElement instanceof IFolder)
            {
                this.targetContainer = (IContainer) selectedElement;
            }
            else if (selectedElement instanceof IProject)
            {
                this.targetContainer = (IContainer) selectedElement;
            }
            else if (selectedElement instanceof IFile)
            {
                this.targetContainer = (IContainer) ((IFile) selectedElement).getParent();
            }
        }
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        rootContainer = workspaceRoot;

        if (restrictWizardPage && null != targetContainer)
        {
            IProject project = targetContainer.getProject();

            YamaicaXmlModel model = YamaicaXmlModel.acquireInstance(project, this);
            IResourcePropertyStore store = model.getResourcePropertyStore(project);
            // TODO
            // String path = store.getProperty(Preferences.IMPORT_FOLDER,
            // Activator.getDefault().getPreferenceStore().getDefaultString(Preferences.IMPORT_FOLDER));
            String path = store.getProperty(IResourcePropertyStore.IMPORT_FOLDER);
            YamaicaXmlModel.releaseInstance(project, this);

            IResource resource = project.findMember(path);

            if (null != resource && resource instanceof IContainer)
            {
                rootContainer = (IContainer) resource;
            }
        }

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setFont(parent.getFont());

        createFilenameGroup(composite);
        createOptionsGroup(composite);

        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());
        setErrorMessage(null); // should not initially have error message

        setControl(composite);
    }

    protected void createFilenameGroup(Composite parent)
    {
        Font font = parent.getFont();

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(layout);
        composite.setFont(font);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label targetFolderLabel = new Label(composite, SWT.NONE);
        targetFolderLabel.setText(TARGET_FOLDER);
        targetFolderLabel.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
        targetFolderLabel.setFont(font);

        targetFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        targetFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        targetFolderText.addListener(SWT.Modify, this);
        targetFolderText.setFont(font);

        browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText(BROWSE);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        browseButton.setFont(font);
        browseButton.addListener(SWT.Selection, this);
        setButtonLayoutData(browseButton);

        Label filenameLabel = new Label(composite, SWT.NONE);
        filenameLabel.setText(FILENAME);
        filenameLabel.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
        filenameLabel.setFont(font);

        filenameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        filenameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        filenameText.setFont(font);

        String defaultFileExtensions = getDefaultFileExtension();

        if (null != defaultFileExtensions)
        {
            filenameText.setText("." + defaultFileExtensions);
        }

        // Add modify listener after the filename extension has been set
        filenameText.addListener(SWT.Modify, this);

        if (null != targetContainer)
        {
            targetFolderText.setText(targetContainer.getFullPath().makeRelativeTo(rootContainer.getFullPath()).toString());
        }

        extendFilenameGroup(composite);

        // TODO Help
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "");
    }

    protected void extendFilenameGroup(Composite parent)
    {

    }

    protected void createOptionsGroup(Composite parent)
    {
        // options group
        GridData optionsGroupGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        optionsGroupGridData.verticalIndent = 10;

        Group optionsGroup = new Group(parent, SWT.NONE);
        optionsGroup.setLayout(new GridLayout());
        optionsGroup.setLayoutData(optionsGroupGridData);
        optionsGroup.setText(OPTIONS);
        optionsGroup.setFont(parent.getFont());

        createOptionsGroupContent(optionsGroup);
    }

    protected void createOptionsGroupContent(Group optionsGroup)
    {
        demoContentCheckbox = new Button(optionsGroup, SWT.CHECK);
        demoContentCheckbox.setText(ADD_DEMO_CONTENT);
    }

    @Override
    public void handleEvent(Event event)
    {
        if (event.widget == browseButton)
        {
            YamaicaResourceSelectionDialog dialog = new YamaicaResourceSelectionDialog(browseButton.getShell(), rootContainer,
                    getSpecifiedContainer(), true, YamaicaUIConstants.SELECT_A_FOLDER_TO_IMPORT_INTO);

            if (dialog.open() == YamaicaResourceSelectionDialog.OK && null != targetFolderText)
            {
                Object[] paths = dialog.getResult();

                if (null != paths && paths.length > 0)
                {
                    targetFolderText.setText(((IPath) paths[0]).makeRelative().toString());
                }
            }
        }

        updateWidgetEnablements();
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible && null != filenameText)
        {
            filenameText.setFocus();
        }
    }

    protected String[] getFileExtensions()
    {
        return new String[0];
    }

    protected String getDefaultFileExtension()
    {
        String[] fileExtensions = getFileExtensions();

        if (fileExtensions.length == 1)
        {
            return fileExtensions[0];
        }

        return null;
    }

    protected void updateWidgetEnablements()
    {
        boolean pageComplete = determinePageCompletion();

        setPageComplete(pageComplete);

        if (pageComplete)
        {
            setErrorMessage(null);
        }
    }

    protected boolean determinePageCompletion()
    {
        boolean complete = validateFilenameGroup() && validateExtensions();

        // Avoid draw flicker by not clearing the error
        // message unless all is valid.
        if (complete)
        {
            setErrorMessage(null);
        }

        return complete;
    }

    /**
     * Checks if target folder value and filename value are valid and if their combination result
     * in valid file path.
     * 
     * @return Returns true if target folder value and filename value result in a valid file path.
     */
    protected boolean validateFilenameGroup()
    {
        // Check if target folder path is contains only valid characters
        IPath containerFullPath = getContainerFullPath();

        if (null == containerFullPath)
        {
            setErrorMessage(THE_TARGET_FOLDER_PATH_IS_NOT_VALID);

            return false;
        }

        // Check if targeted project exists and is opened
        IContainer container = getSpecifiedContainer();
        IProject project = container.getProject();

        if (!project.exists())
        {
            setErrorMessage(THE_SPECIFIED_PROJECT + project.getName() + DOES_NOT_EXIST);

            return false;
        }

        if (!project.isOpen())
        {
            setErrorMessage(THE_SPECIFIED_PROJECT + project.getName() + IS_NOT_OPENED);

            return false;
        }

        // Check if filename contains only valid characters
        IPath fileFullPath = getFileFullPath();

        if (null == fileFullPath)
        {
            setErrorMessage(THE_FILENAME_IS_NOT_VALID);

            return false;
        }

        // Check if filename extension is correct
        String[] validFileExtensions = getFileExtensions();
        String currentFileExtension = fileFullPath.getFileExtension();
        boolean hasValidFileExtension = false;

        for (String validFileExtension : validFileExtensions)
        {
            if (validFileExtension.equals(currentFileExtension))
            {
                hasValidFileExtension = true;

                break;
            }
        }

        // Error if filename equals extension
        if (hasValidFileExtension && fileFullPath.lastSegment().equals("." + currentFileExtension))
        {
            setErrorMessage(THE_FILENAME_MAY_NOT_CONSIST_OF_THE_FILENAME_EXTENSION_ONLY);

            return false;
        }

        // Error if filename extension is not correct
        if (validFileExtensions.length > 0 && !hasValidFileExtension)
        {
            String combinedFileExtensions = "\"" + validFileExtensions[0] + "\"";

            for (int i = 1; i < validFileExtensions.length - 1; i++)
            {
                combinedFileExtensions += ", \"" + validFileExtensions[i] + "\"";
            }

            if (validFileExtensions.length > 1)
            {
                combinedFileExtensions += " or " + "\"" + validFileExtensions[validFileExtensions.length - 1] + "\"";
            }

            setErrorMessage(THE_FILENAME_EXTENSION_MUST_BE + combinedFileExtensions + ".");

            return false;
        }

        // Check if a resource with the same path already exists
        // 1.) Check against Eclipse resources since file links and virtual folders do not
        // exist in local file system
        // 2.) Check against local file system since Eclipse check is case sensitive
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(fileFullPath);
        URI fileUri = getSpecifiedFile().getLocationURI();

        if (null != resource || (null != fileUri && new File(fileUri).exists()))
        {
            setErrorMessage(A_RESOURCE_AT_THE_SPECIFIED_PATH_ALREADY_EXISTS);

            return false;
        }

        return true;
    }

    /**
     * Should be used by super classes to check if added controls contain valid values.
     * 
     * @return true if additional controls contain valid values
     */
    protected boolean validateExtensions()
    {
        return true;
    }

    public boolean finish()
    {
        IRunnableWithProgress fileCreator = getFileCreator();

        if (null == fileCreator)
        {
            return false;
        }

        try
        {
            getContainer().run(true, getIsProgressMonitorCancelable(), fileCreator);

            IFile file = getSpecifiedFile();

            if (null != file && file.exists())
            {
                openFile(file);
            }

            return true;
        }
        catch (InterruptedException e)
        {

        }
        catch (InvocationTargetException e)
        {
            displayErrorDialog(e.getTargetException());
        }

        return false;
    }

    protected abstract IRunnableWithProgress getFileCreator();

    protected IFile getSpecifiedFile()
    {
        IPath filePath = getFileFullPath();

        if (null == filePath)
        {
            return null;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        return workspace.getRoot().getFile(filePath);
    }

    protected IContainer getSpecifiedContainer()
    {
        IPath containerPath = getContainerFullPath();

        if (null == containerPath)
        {
            return null;
        }

        int segementCount = containerPath.segmentCount();

        if (segementCount > 0)
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();

            if (segementCount == 1)
            {
                return workspace.getRoot().getProject(containerPath.toString());
            }

            return workspace.getRoot().getFolder(containerPath);
        }

        return null;
    }

    protected IPath getFileFullPath()
    {
        IPath containerFullPath = getContainerFullPath();

        if (null == containerFullPath || null == filenameText)
        {
            return null;
        }

        IPath filenamePath = new Path(filenameText.getText());

        if (filenamePath.segmentCount() != 1 || filenamePath.isAbsolute() || filenamePath.hasTrailingSeparator())
        {
            return null;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath testPath = containerFullPath.append(filenamePath).makeAbsolute();
        IStatus result = workspace.validatePath(testPath.toString(), IResource.FILE);

        if (result.isOK())
        {
            return testPath;
        }

        return null;
    }

    protected IPath getContainerFullPath()
    {
        if (null == targetFolderText)
        {
            return null;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath testPath = rootContainer.getFullPath().append(targetFolderText.getText()).makeAbsolute();
        IStatus result = workspace.validatePath(testPath.toString(), IResource.PROJECT | IResource.FOLDER);

        if (result.isOK())
        {
            return testPath;
        }

        return null;
    }

    /**
     * Display an error dialog with the specified message.
     * 
     * @param message
     *            the error message
     */
    protected void displayErrorDialog(String message)
    {
        MessageDialog.open(MessageDialog.ERROR, getContainer().getShell(), INTERNAL_ERROR, message, SWT.SHEET);
    }

    /**
     * Display an error dislog with the information from the
     * supplied exception.
     * 
     * @param exception
     *            Throwable
     */
    protected void displayErrorDialog(Throwable exception)
    {
        String message = exception.getMessage();

        // Some system exceptions have no message
        if (null == message)
        {
            message = NLS.bind(ERROR_OCCURRED_DURING_OPERATION_0, exception);
        }

        displayErrorDialog(message);
    }

    protected void openFile(final IFile file)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String filename = file.getName();
                    IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(file.getContents(), filename);
                    IEditorDescriptor editorDescriptor = workbench.getEditorRegistry().getDefaultEditor(filename, contentType);
                    String editorId = null == editorDescriptor ? "org.eclipse.ui.DefaultTextEditor" : editorDescriptor.getId();
                    workbench.getActiveWorkbenchWindow().getPages()[0].openEditor(new FileEditorInput(file), editorId);
                }
                catch (PartInitException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setWizard(IWizard newWizard)
    {
        if (newWizard instanceof YamaicaWizard)
        {
            restrictWizardPage = ((YamaicaWizard) newWizard).restrictWizard;
        }

        super.setWizard(newWizard);
    }

    protected boolean getIsProgressMonitorCancelable()
    {
        return true;
    }
}
