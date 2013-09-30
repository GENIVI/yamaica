/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.dialogs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
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
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardResourceImportPage;

import de.bmw.yamaica.base.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.base.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.base.ui.utils.ActionRunEvent;
import de.bmw.yamaica.base.ui.utils.ActionRunListener;
import de.bmw.yamaica.base.ui.utils.FileExtensionFilter;
import de.bmw.yamaica.base.ui.utils.FileSystemComparator;
import de.bmw.yamaica.base.ui.utils.FileSystemContentProvider;
import de.bmw.yamaica.base.ui.utils.FileSystemLabelProvider;
import de.bmw.yamaica.base.ui.utils.ViewerToolBar;

public abstract class YamaicaWizardImportPage extends WizardResourceImportPage implements ICheckStateListener, ActionRunListener
{
    protected IWorkbench               workbench;
    protected IStructuredSelection     structuredSelection;
    protected boolean                  restrictWizardPage                    = false;

    protected Combo                    sourceNameField;
    protected Button                   sourceBrowseButton;
    protected ViewerToolBar            viewerToolBar;
    protected YamaicaCheckedTreeViewer sourceSelectionTreeViewer;
    protected Text                     containerNameField;
    protected Button                   containerBrowseButton;
    protected Button                   overwriteExistingResourcesCheckbox;

    protected File                     sourceDirectory                       = new File("");
    protected IContainer               rootContainer;
    protected IContainer               targetContainer;
    protected String[]                 fileExtensions                        = new String[0];
    protected FileExtensionFilter      extensionFilter;

    // A boolean to indicate if the user has typed anything
    protected boolean                  entryChanged                          = false;

    // dialog store id constants
    protected final static String      STORE_SOURCE_NAMES_ID                 = "YamaicaWizardImportPage.STORE_SOURCE_NAMES_ID";                //$NON-NLS-1$
    protected final static String      STORE_OVERWRITE_EXISTING_RESOURCES_ID = "YamaicaWizardImportPage.STORE_OVERWRITE_EXISTING_RESOURCES_ID"; //$NON-NLS-1$
    protected final static String      STORE_SHOW_ALL_FILES_ID               = "YamaicaWizardImportPage.STORE_SHOW_ALL_FILES_ID";              //$NON-NLS-1$

    public YamaicaWizardImportPage(IWorkbench workbench, IStructuredSelection structuredSelection, String name)
    {
        super(name, structuredSelection);

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

    // Copied from base class since we need to display the destination group only
    // under some special circumstances (if more than one selection is referred through
    // the structured selection object.
    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);

        String[] fileExtensions = getFileExtensions();

        if (null != fileExtensions)
        {
            this.fileExtensions = fileExtensions;
            this.extensionFilter = new FileExtensionFilter(fileExtensions);
        }

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
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);
        createTargetGroup(composite);
        createOptionsGroup(composite);

        restoreWidgetValues();
        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());
        setErrorMessage(null); // should not initially have error message

        setControl(composite);

        validateSourceGroup();

        // TODO Help
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "");
    }

    @Override
    protected void createSourceGroup(Composite parent)
    {
        createRootDirectoryGroup(parent);
        createFileSelectionGroup(parent);
    }

    protected void createRootDirectoryGroup(Composite parent)
    {
        Font font = parent.getFont();

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = layout.marginHeight = 0;

        Composite sourceContainerGroup = new Composite(parent, SWT.NONE);
        sourceContainerGroup.setLayout(layout);
        sourceContainerGroup.setFont(font);
        sourceContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label groupLabel = new Label(sourceContainerGroup, SWT.NONE);
        groupLabel.setText("From director&y:");
        groupLabel.setFont(font);

        // source name entry field
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;

        sourceNameField = new Combo(sourceContainerGroup, SWT.BORDER);
        sourceNameField.setLayoutData(data);
        sourceNameField.setFont(font);
        sourceNameField.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                updateFromSourceField();
            }
        });
        sourceNameField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;

                if (e.character == SWT.CR)
                {
                    entryChanged = false;

                    updateFromSourceField();
                }
            }

            public void keyReleased(KeyEvent e)
            {

            }
        });
        sourceNameField.addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
                // Do nothing when getting focus
            }

            public void focusLost(FocusEvent e)
            {
                // Clear the flag to prevent constant update
                if (entryChanged)
                {
                    entryChanged = false;

                    updateFromSourceField();
                }
            }
        });

        // source browse button
        sourceBrowseButton = new Button(sourceContainerGroup, SWT.PUSH);
        sourceBrowseButton.setText("B&rowse...");
        sourceBrowseButton.addListener(SWT.Selection, this);
        sourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        sourceBrowseButton.setFont(font);
        setButtonLayoutData(sourceBrowseButton);
    }

    @Override
    protected void createFileSelectionGroup(Composite parent)
    {
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 240;

        viewerToolBar = new ViewerToolBar(parent, SWT.BORDER, ViewerToolBar.SELECT | ViewerToolBar.FILTER | ViewerToolBar.REFRESH);
        viewerToolBar.setLayoutData(data);
        viewerToolBar.setFilterText("Filter File Extensions");

        sourceSelectionTreeViewer = new YamaicaCheckedTreeViewer(viewerToolBar, SWT.NONE);
        sourceSelectionTreeViewer.setContentProvider(new FileSystemContentProvider());
        sourceSelectionTreeViewer.setLabelProvider(new FileSystemLabelProvider());
        sourceSelectionTreeViewer.setUseHashlookup(true);
        sourceSelectionTreeViewer.addCheckStateListener(this);
        sourceSelectionTreeViewer.setComparator(new FileSystemComparator());
        sourceSelectionTreeViewer.addFilter(extensionFilter);

        viewerToolBar.addActionRunListener(this);
        viewerToolBar.setViewer(sourceSelectionTreeViewer);
    }

    protected void createTargetGroup(Composite parent)
    {
        Font font = parent.getFont();

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 10;

        // container specification group
        Composite containerGroup = new Composite(parent, SWT.NONE);
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(font);

        // container label
        Label resourcesLabel = new Label(containerGroup, SWT.NONE);
        resourcesLabel.setText("Into fo&lder:");
        resourcesLabel.setFont(font);

        // container name entry field
        containerNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        containerNameField.addListener(SWT.Modify, this);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        containerNameField.setLayoutData(data);
        containerNameField.setFont(font);

        if (null != targetContainer)
        {
            containerNameField.setText(targetContainer.getFullPath().makeRelativeTo(rootContainer.getFullPath()).toString());
        }

        // container browse button
        containerBrowseButton = new Button(containerGroup, SWT.PUSH);
        containerBrowseButton.setText("Bro&wse...");
        containerBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        containerBrowseButton.addListener(SWT.Selection, this);
        containerBrowseButton.setFont(font);
        setButtonLayoutData(containerBrowseButton);
    }

    @Override
    protected void createOptionsGroup(Composite parent)
    {
        super.createOptionsGroup(parent);
    }

    @Override
    protected void createOptionsGroupButtons(Group optionsGroup)
    {
        // overwrite... checkbox
        overwriteExistingResourcesCheckbox = new Button(optionsGroup, SWT.CHECK);
        overwriteExistingResourcesCheckbox.setFont(optionsGroup.getFont());
        overwriteExistingResourcesCheckbox.setText("&Overwrite existing resources without warning");
        overwriteExistingResourcesCheckbox.addListener(SWT.Selection, this);

        updateWidgetEnablements();
    }

    /**
     * Use the dialog store to restore widget values to the values that they held
     * last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            if (null != sourceNameField)
            {
                String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);

                if (null != sourceNames)
                {
                    // set filenames history
                    for (String sourceName : sourceNames)
                    {
                        sourceNameField.add(sourceName);
                    }
                }
            }

            // radio buttons and checkboxes
            if (null != viewerToolBar)
            {
                viewerToolBar.setFilterEnabled(!settings.getBoolean(STORE_SHOW_ALL_FILES_ID));
            }

            if (null != overwriteExistingResourcesCheckbox)
            {
                overwriteExistingResourcesCheckbox.setSelection(settings.getBoolean(STORE_OVERWRITE_EXISTING_RESOURCES_ID));
            }

            updateWidgetEnablements();
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they
     * will persist into the next invocation of this wizard page
     */
    @Override
    protected void saveWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            if (null != sourceNameField)
            {
                // update source names history
                String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);

                if (null == sourceNames)
                {
                    sourceNames = new String[0];
                }

                sourceNames = addToHistory(sourceNames, sourceDirectory.getPath());
                settings.put(STORE_SOURCE_NAMES_ID, sourceNames);
            }

            // radio buttons and checkboxes
            if (null != viewerToolBar)
            {
                settings.put(STORE_SHOW_ALL_FILES_ID, !viewerToolBar.isFilterEnabled());
            }

            if (null != overwriteExistingResourcesCheckbox)
            {
                settings.put(STORE_OVERWRITE_EXISTING_RESOURCES_ID, overwriteExistingResourcesCheckbox.getSelection());
            }
        }
    }

    @Override
    protected void updateWidgetEnablements()
    {
        super.updateWidgetEnablements();
    }

    @Override
    public void handleEvent(Event event)
    {
        if (event.widget == sourceBrowseButton)
        {
            DirectoryDialog dialog = new DirectoryDialog(sourceBrowseButton.getShell(), SWT.SAVE | SWT.SHEET);
            dialog.setText("Import from directory");
            dialog.setMessage("Select a directory to import from.");
            dialog.setFilterPath(sourceDirectory.getPath());

            String selectedDirectory = dialog.open();

            if (null != selectedDirectory && null != sourceNameField)
            {
                sourceNameField.setText(selectedDirectory);

                updateFromSourceField();
            }
        }
        else if (event.widget == containerBrowseButton)
        {
            YamaicaResourceSelectionDialog dialog = new YamaicaResourceSelectionDialog(containerBrowseButton.getShell(), rootContainer,
                    getSpecifiedContainer(), true, "Select a folder to import into.");

            if (YamaicaResourceSelectionDialog.OK == dialog.open() && null != containerNameField)
            {
                Object[] paths = dialog.getResult();

                if (null != paths && paths.length > 0)
                {
                    containerNameField.setText(((IPath) paths[0]).makeRelative().toString());
                }
            }
        }

        updateWidgetEnablements();
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event)
    {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable()
        {
            @Override
            public void run()
            {
                updateWidgetEnablements();
            }
        });
    }

    @Override
    public void preActionRun(ActionRunEvent e)
    {

    }

    @Override
    public void postActionRun(ActionRunEvent e)
    {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable()
        {
            @Override
            public void run()
            {
                updateWidgetEnablements();
            }
        });
    }

    protected void updateFromSourceField()
    {
        if (null != sourceNameField)
        {
            File directory = new File(sourceNameField.getText());

            if (!directory.equals(sourceDirectory))
            {
                String path = directory.getPath();
                String[] currentItems = sourceNameField.getItems();
                int selectionIndex = -1;

                for (int i = 0; i < currentItems.length; i++)
                {
                    if (currentItems[i].equals(path))
                    {
                        selectionIndex = i;
                        break;
                    }
                }

                if (selectionIndex == -1)
                {
                    int oldLength = currentItems.length;
                    String[] newItems = new String[oldLength + 1];
                    System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                    newItems[oldLength] = path;
                    sourceNameField.setItems(newItems);
                    selectionIndex = oldLength;
                }

                sourceNameField.select(selectionIndex);

                sourceDirectory = directory;
            }
        }

        updateViewerInput();
        updateWidgetEnablements();
    }

    protected void updateViewerInput()
    {
        if (null != sourceSelectionTreeViewer)
        {
            if (null != sourceDirectory && sourceDirectory.isDirectory() && !sourceDirectory.equals(sourceSelectionTreeViewer.getInput()))
            {
                sourceSelectionTreeViewer.setInput(sourceDirectory);
                setErrorMessage(null);
            }
            else
            {
                sourceSelectionTreeViewer.setInput(null);
                setErrorMessage("Source directory is not valid or has not been specified.");
            }

            sourceSelectionTreeViewer.getTree().setFocus();
        }
    }

    @Override
    protected boolean allowNewContainerName()
    {
        return true;
    }

    @Override
    protected IContainer getSpecifiedContainer()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath path = getContainerFullPath();

        if (workspace.getRoot().exists(path))
        {
            IResource resource = workspace.getRoot().findMember(path);

            if (resource.getType() == IResource.FILE)
            {
                return null;
            }

            return (IContainer) resource;
        }

        return null;
    }

    @Override
    protected IPath getContainerFullPath()
    {
        if (null == containerNameField)
        {
            return null;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        IPath testPath = rootContainer.getFullPath().append(containerNameField.getText()).makeAbsolute();

        if (testPath.equals(workspace.getRoot().getFullPath()))
        {
            return testPath;
        }

        IStatus result = workspace.validatePath(testPath.toString(), IResource.PROJECT | IResource.FOLDER | IResource.ROOT);

        if (result.isOK())
        {
            return testPath;
        }

        return null;
    }

    @Override
    protected boolean sourceConflictsWithDestination(IPath sourcePath)
    {
        IContainer container = getSpecifiedContainer();

        if (null == container)
        {
            return false;
        }

        IPath destinationLocation = container.getLocation();

        if (null != destinationLocation)
        {
            return destinationLocation.isPrefixOf(sourcePath);
        }
        // null destination location is handled in
        // WizardResourceImportPage
        return false;
    }

    @Override
    protected boolean validateSourceGroup()
    {
        // return sourceDirectory.isDirectory();

        if (!sourceDirectory.exists())
        {
            setMessage("Source must not be empty.");

            return false;
        }

        if (sourceConflictsWithDestination(new Path(sourceDirectory.getPath())))
        {
            setMessage(null);
            setErrorMessage(getSourceConflictMessage());

            return false;
        }

        if (getSelectedResources().size() == 0)
        {
            setMessage(null);
            setErrorMessage("There are no resources currently selected for import.");

            return false;
        }

        setErrorMessage(null);

        return true;
    }

    protected boolean validateTargetGroup()
    {
        IContainer container = getSpecifiedContainer();

        if (container != null && container.isVirtual())
        {
            setMessage(null);
            setErrorMessage("Cannot import a file into a virtual folder.");

            return false;
        }

        IPath containerPath = getContainerFullPath();

        if (containerPath == null)
        {
            setMessage("Please specify folder");

            return false;
        }

        if (container == null)
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();

            // If it exists but is not valid then abort
            if (workspace.getRoot().exists(containerPath))
            {
                return false;
            }

            // if it is does not exist be sure the project does
            IPath projectPath = containerPath.removeLastSegments(containerPath.segmentCount() - 1);

            if (workspace.getRoot().exists(projectPath))
            {
                return true;
            }

            setErrorMessage("Destination project does not exist.");

            return false;
        }

        if (!container.isAccessible())
        {
            setErrorMessage("Folder must be accessible.");

            return false;
        }

        if (container.getLocationURI() == null)
        {
            if (container.isLinked())
            {
                setErrorMessage("Destination folder location is based on an undefined path variable.");
            }
            else
            {
                setErrorMessage("Destination folder does not exist.");
            }

            return false;
        }

        if (sourceConflictsWithDestination(containerPath))
        {
            setErrorMessage(getSourceConflictMessage());

            return false;
        }

        if (container instanceof IWorkspaceRoot)
        {
            setErrorMessage("Specify a project");

            return false;
        }

        return true;
    }

    @Override
    protected boolean validateOptionsGroup()
    {
        return super.validateOptionsGroup();
    }

    protected String[] getFileExtensions()
    {
        return null;
    }

    protected abstract IRunnableWithProgress getImporter();

    @Override
    protected List<File> getSelectedResources()
    {
        List<File> files = new LinkedList<File>();

        if (null != sourceSelectionTreeViewer)
        {
            for (Object element : sourceSelectionTreeViewer.getCheckedElements())
            {
                File file;

                if (element instanceof File && (file = (File) element).isFile())
                {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public boolean finish()
    {
        saveWidgetValues();

        IContainer container = null;

        try
        {
            container = new ContainerCreator(ResourcesPlugin.getWorkspace(), getContainerFullPath()).createContainer(null);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null == container || !container.exists())
            {
                return false;
            }
        }

        IRunnableWithProgress importer = getImporter();

        if (null == importer)
        {
            return false;
        }

        try
        {
            getContainer().run(true, true, importer);

            return true;
        }
        catch (InterruptedException e)
        {

        }
        catch (InvocationTargetException e)
        {
            displayErrorDialog(e.getTargetException());
        }
        finally
        {
            // IStatus status = exporter.getStatus();
            //
            // if (!status.isOK())
            // {
            // ErrorDialog.openError(getContainer().getShell(), DataTransferMessages.DataTransfer_exportProblems,
            // null, // no special message
            // status);
            // return false;
            // }
        }

        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    protected boolean determinePageCompletion()
    {
        // Check for valid projects before making the user do anything
        boolean openProjectAvailable = false;

        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
        {
            if (project.isOpen())
            {
                openProjectAvailable = true;

                break;
            }
        }

        if (!openProjectAvailable)
        {
            setErrorMessage("Cannot import into a workspace with no open projects. Please create a project before importing.");

            return false;
        }

        boolean complete = validateSourceGroup() && validateTargetGroup() && validateOptionsGroup();

        // Avoid draw flicker by not clearing the error
        // message unless all is valid.
        if (complete)
        {
            setErrorMessage(null);
        }

        return complete;
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

    // Methods that do not need but which we have to override
    // since they abstract in base class
    @Override
    protected final ITreeContentProvider getFileProvider()
    {
        return null;
    }

    @Override
    protected final ITreeContentProvider getFolderProvider()
    {
        return null;
    }
}
