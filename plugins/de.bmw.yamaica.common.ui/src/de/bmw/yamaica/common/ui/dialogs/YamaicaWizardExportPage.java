/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import de.bmw.yamaica.common.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.common.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.utils.ActionRunEvent;
import de.bmw.yamaica.common.ui.utils.ActionRunListener;
import de.bmw.yamaica.common.ui.utils.ResourceComparator;
import de.bmw.yamaica.common.ui.utils.ResourceExtensionFilter;
import de.bmw.yamaica.common.ui.utils.ViewerToolBar;

public abstract class YamaicaWizardExportPage extends WizardExportResourcesPage implements ICheckStateListener, ActionRunListener
{
    private static final Logger          LOGGER                            = Logger.getLogger(YamaicaWizardExportPage.class.getName());

    protected final IWorkbench           workbench;
    protected final IStructuredSelection structuredSelection;
    protected boolean                    restrictWizardPage                = false;
    protected boolean                    isFileExportWizard                = false;

    // widgets
    protected ViewerToolBar              viewerToolBar;
    protected YamaicaCheckedTreeViewer   resourceSelectionTreeViewer;
    protected Composite                  destinationSelectionGroup;
    protected Combo                      destinationNameField;
    protected Button                     destinationBrowseButton;
    protected Button                     overwriteExistingFilesCheckbox;

    protected String[]                   fileExtensions                    = new String[0];
    protected ResourceExtensionFilter    extensionFilter;

    // dialog store id constants
    protected static final String        STORE_DESTINATION_NAMES_ID        = "YamaicaWizardExportPage.STORE_DESTINATION_NAMES_ID";            //$NON-NLS-1$
    protected static final String        STORE_OVERWRITE_EXISTING_FILES_ID = "YamaicaWizardExportPage.STORE_OVERWRITE_EXISTING_FILES_ID";     //$NON-NLS-1$
    protected static final String        STORE_SHOW_ALL_FILES_ID           = "YamaicaWizardImportPage.STORE_SHOW_ALL_FILES_ID";               //$NON-NLS-1$

    protected static final String        SELECT_A_FILE_TO_EXPORT_TO        = "Select a file to export to";
    protected static final String        TO_DIRECTORY                      = "To director&y:";
    protected static final String        DESTINATION_FILE                  = "Des&tination file:";
    protected static final String        BROWSE                            = "Bro&wse...";
    protected static final String        DIRECTORY_SELECTION_MESSAGE       = "Select a directory to export to.";
    protected static final String        EXPORT_TO_DIRECTORY               = "Export to Directory";
    protected static final String        RESOURCE_SELECTION_ERROR_MESSAGE  = "There are no resources currently selected for export.";
    protected static final String        ENTER_DESTINATION_MESSAGE         = "Please enter a destination directory.";
    protected static final String        COULD_NOT_CREATE_MESSAGE          = "Target directory could not be created.";
    protected static final String        ALREADY_EXISTS_MESSAGE            = "Target directory already exists as a file.";
    protected static final String        DIRECTORY_CREATE_QUESTION         = "Target directory does not exist.  Would you like to create it?";
    protected static final String        LOCATION_CONFLICT_ERROR_MESSAGE   = "Destination directory conflicts with location of {0}.";
    protected static final String        PROJECT_DAMAGED_WARNING           = "The project {0} may be damaged after this operation";

    public YamaicaWizardExportPage(IWorkbench workbench, IStructuredSelection structuredSelection, String name)
    {
        this(workbench, structuredSelection, name, false);
    }

    public YamaicaWizardExportPage(IWorkbench workbench, IStructuredSelection structuredSelection, String name, boolean isFileExportWizard)
    {
        super(name, structuredSelection);

        this.workbench = workbench;
        this.structuredSelection = structuredSelection;
        this.isFileExportWizard = isFileExportWizard;
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);

        String[] fileExtensions = getFileExtensions();

        if (null != fileExtensions)
        {
            this.fileExtensions = fileExtensions;
            this.extensionFilter = new ResourceExtensionFilter(fileExtensions);
        }

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);
        createDestinationGroup(composite);
        createOptionsGroup(composite);

        restoreResourceSpecificationWidgetValues(); // ie.- local
        restoreWidgetValues(); // ie.- subclass hook

        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());
        setErrorMessage(null); // should not initially have error message

        setControl(composite);

        destinationNameField.setFocus();

        // TODO Help
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "");
    }

    protected void createSourceGroup(Composite parent)
    {
        createResourceSelectionGroup(parent);
    }

    protected void createResourceSelectionGroup(Composite parent)
    {
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 240;

        viewerToolBar = new ViewerToolBar(parent, SWT.BORDER, ViewerToolBar.DRILL_DOWN | ViewerToolBar.SELECT | ViewerToolBar.FILTER);
        viewerToolBar.setLayoutData(data);
        viewerToolBar.setFilterText(YamaicaUIConstants.FILTER_FILE_EXTENSIONS);

        resourceSelectionTreeViewer = new YamaicaCheckedTreeViewer(viewerToolBar, SWT.NONE);
        resourceSelectionTreeViewer.setContentProvider(new WorkbenchContentProvider());
        resourceSelectionTreeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        resourceSelectionTreeViewer.setUseHashlookup(true);
        resourceSelectionTreeViewer.addCheckStateListener(this);
        resourceSelectionTreeViewer.setComparator(new ResourceComparator());
        resourceSelectionTreeViewer.addFilter(extensionFilter);

        viewerToolBar.addActionRunListener(this);
        viewerToolBar.setViewer(resourceSelectionTreeViewer);

        Object viewerInput = ResourcesPlugin.getWorkspace().getRoot();

        if (restrictWizardPage && !structuredSelection.isEmpty())
        {
            Object firstSelectedElement = structuredSelection.getFirstElement();

            if (firstSelectedElement instanceof IResource)
            {
                // We have to check if all initially selected files are inside the import or inside the target folder.
                // If all initially selected files are inside of one of these folder we can restrict the tree viewer
                // to this container.
                IProject project = ((IResource) firstSelectedElement).getProject();

                YamaicaXmlModel model = YamaicaXmlModel.acquireInstance(project, this);
                IResourcePropertyStore store = model.getResourcePropertyStore(project);
                // TODO
                // String importFolder = store.getProperty(IResourcePropertyStore.IMPORT_FOLDER, Preferences.getPreferenceProvider().getDefaultString(IResourcePropertyStore.IMPORT_FOLDER));
                // String targetFolder = store.getProperty(IResourcePropertyStore.TARGET_FOLDER, Preferences.getPreferenceProvider().getDefaultString(IResourcePropertyStore.TARGET_FOLDER));
                String importFolder = store.getProperty(IResourcePropertyStore.IMPORT_FOLDER);
                String targetFolder = store.getProperty(IResourcePropertyStore.TARGET_FOLDER);
                YamaicaXmlModel.releaseInstance(project, this);

                IResource importContainer = project.findMember(importFolder);
                IResource targetContainer = project.findMember(targetFolder);

                IPath importPath = null != importContainer ? importContainer.getFullPath() : new Path(YamaicaUIConstants.EMPTY_STRING);
                IPath targetPath = null != targetContainer ? targetContainer.getFullPath() : new Path(YamaicaUIConstants.EMPTY_STRING);

                boolean allFilesInsideImportFolder = true;
                boolean allFilesInsideTargetFolder = true;

                for (Object selectedElement : structuredSelection.toArray())
                {
                    if (selectedElement instanceof IResource)
                    {
                        IPath path = ((IResource) selectedElement).getFullPath();

                        if (allFilesInsideImportFolder && !importPath.isPrefixOf(path))
                        {
                            allFilesInsideImportFolder = false;
                        }

                        if (allFilesInsideTargetFolder && !targetPath.isPrefixOf(path))
                        {
                            allFilesInsideTargetFolder = false;
                        }
                    }
                }

                if (allFilesInsideImportFolder && null != importContainer && importContainer instanceof IContainer)
                {
                    viewerInput = importContainer;
                }
                else if (allFilesInsideTargetFolder && null != targetContainer && targetContainer instanceof IContainer)
                {
                    viewerInput = targetContainer;
                }
            }
        }

        resourceSelectionTreeViewer.setInput(viewerInput);

        for (Object selectedObject : structuredSelection.toArray())
        {
            resourceSelectionTreeViewer.expandToLevel(selectedObject, 0);
            resourceSelectionTreeViewer.setChecked(selectedObject, true);
        }
    }

    @Override
    protected void createDestinationGroup(Composite parent)
    {
        Font font = parent.getFont();

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 10;

        // destination specification group
        destinationSelectionGroup = new Composite(parent, SWT.NONE);
        destinationSelectionGroup.setLayout(layout);
        destinationSelectionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
        destinationSelectionGroup.setFont(font);

        Label destinationLabel = new Label(destinationSelectionGroup, SWT.NONE);

        if (!isFileExportWizard)
        {
            destinationLabel.setText(TO_DIRECTORY);
        }
        else
        {
            destinationLabel.setText(DESTINATION_FILE);
        }

        destinationLabel.setFont(font);

        // destination name entry field
        destinationNameField = new Combo(destinationSelectionGroup, SWT.SINGLE | SWT.BORDER);
        destinationNameField.addListener(SWT.Modify, this);
        destinationNameField.addListener(SWT.Selection, this);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        destinationNameField.setLayoutData(data);
        destinationNameField.setFont(font);

        // destination browse button
        destinationBrowseButton = new Button(destinationSelectionGroup, SWT.PUSH);
        destinationBrowseButton.setText(BROWSE);
        destinationBrowseButton.addListener(SWT.Selection, this);
        destinationBrowseButton.setFont(font);
        setButtonLayoutData(destinationBrowseButton);
    }

    @Override
    protected void createOptionsGroup(Composite parent)
    {
        super.createOptionsGroup(parent);
    }

    @Override
    protected void createOptionsGroupButtons(Group optionsGroup)
    {
        Font font = optionsGroup.getFont();

        // overwrite... checkbox
        overwriteExistingFilesCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        overwriteExistingFilesCheckbox.setText(YamaicaUIConstants.OVERWRITE_EXISTING_FILES_WITHOUT_WARNING);
        overwriteExistingFilesCheckbox.setFont(font);
    }

    protected IContainer getSourceContainer()
    {
        return (IContainer) resourceSelectionTreeViewer.getInput();
    }

    @Override
    protected List<IResource> getSelectedResources()
    {
        List<IResource> resources = new LinkedList<>();

        if (null != resourceSelectionTreeViewer)
        {
            for (Object element : resourceSelectionTreeViewer.getCheckedElements())
            {
                if (element instanceof IFile)
                {
                    resources.add((IFile) element);
                }
            }
        }

        return resources;
    }

    public boolean finish()
    {
        String destinationValue = getDestinationValue();
        if (!Paths.get(destinationValue).isAbsolute())
        {
            // Path is a workspace path.
            //
            // We may check the existence of such a path and/or create such a path here, using the
            // dedicated workspace functions. We must not use any Java-IO functions like we are using
            // for the external paths - because our workspace would get out of sync.
            //
        }
        else
        {
            // Path is an external full file system path (not a workspace path)
            //
            final File destinationDirectory = new File(destinationValue);

            if (!isFileExportWizard && !ensureTargetIsValid(destinationDirectory))
            {
                return false;
            }

            if (isFileExportWizard && !ensureTargetIsValid(destinationDirectory.getParentFile()))
            {
                return false;
            }
        }

        // Save dirty editors if possible but do not stop if not all are saved
        saveDirtyEditors();
        // about to invoke the operation so save our state
        saveWidgetValues();

        IRunnableWithProgress exporter = getExporter();

        if (null == exporter)
        {
            return false;
        }

        try
        {
            getContainer().run(true, getIsProgressMonitorCancelable(), exporter);

            return true;
        }
        catch (InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "InterruptedException occured! Message: " + e.getMessage());
        }
        catch (InvocationTargetException e)
        {
            LOGGER.log(Level.SEVERE, "InvocationTargetException occured! Message: " + e.getTargetException());
            displayErrorDialog(e.getTargetException());
        }
//        finally
//        {
//            IStatus status = exporter.getStatus();
//
//            if (!status.isOK())
//            {
//                ErrorDialog.openError(getContainer().getShell(), DataTransferMessages.DataTransfer_exportProblems,
//                        null, // no special message
//                        status);
//                return false;
//            }
//        }

        return false;
    }

    @Override
    public void handleEvent(Event event)
    {
        if (event.widget == destinationBrowseButton)
        {
            String selectedDirectoryName = null;

            if (!isFileExportWizard)
            {
                DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE | SWT.SHEET);
                dialog.setMessage(DIRECTORY_SELECTION_MESSAGE);
                dialog.setText(EXPORT_TO_DIRECTORY);
                selectedDirectoryName = dialog.open();
            }
            else
            {
                FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE | SWT.SHEET);
                dialog.setText(SELECT_A_FILE_TO_EXPORT_TO);
                dialog.setFilterPath(getDestinationValue());
                Map<String, String> extensionMap = getSaveDialogExtensions();
                dialog.setFilterExtensions(extensionMap.keySet().toArray(new String[extensionMap.size()]));
                dialog.setFilterNames(extensionMap.values().toArray(new String[extensionMap.size()]));
                dialog.setFilterIndex(0);
                selectedDirectoryName = dialog.open();
            }

            if (selectedDirectoryName != null)
            {
                setErrorMessage(null);
                destinationNameField.setText(selectedDirectoryName);
            }
        }

        updatePageCompletion();
    }

    protected Map<String, String> getSaveDialogExtensions()
    {
        LinkedHashMap<String, String> returnMap = new LinkedHashMap<String, String>();
        returnMap.put("*.*", "All types");
        return returnMap;
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

    @Override
    protected void restoreWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            if (null != destinationNameField)
            {
                String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);

                if (null != directoryNames)
                {
                    destinationNameField.setText(directoryNames[0]);

                    for (int i = 0; i < directoryNames.length; i++)
                    {
                        destinationNameField.add(directoryNames[i]);
                    }
                }
            }

            // radio buttons and checkboxes
            if (null != viewerToolBar)
            {
                viewerToolBar.setFilterEnabled(!settings.getBoolean(STORE_SHOW_ALL_FILES_ID));
            }

            if (null != overwriteExistingFilesCheckbox)
            {
                overwriteExistingFilesCheckbox.setSelection(settings.getBoolean(STORE_OVERWRITE_EXISTING_FILES_ID));
            }
        }
    }

    @Override
    protected void saveWidgetValues()
    {
        // update directory names history
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);

            if (null == directoryNames)
            {
                directoryNames = new String[0];
            }

            directoryNames = addToHistory(directoryNames, getDestinationValue());
            settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);

            // radio buttons and checkboxes
            if (null != viewerToolBar)
            {
                settings.put(STORE_SHOW_ALL_FILES_ID, !viewerToolBar.isFilterEnabled());
            }

            if (null != overwriteExistingFilesCheckbox)
            {
                settings.put(STORE_OVERWRITE_EXISTING_FILES_ID, overwriteExistingFilesCheckbox.getSelection());
            }
        }
    }

    @Override
    protected boolean validateSourceGroup()
    {
        // there must be some resources selected for Export
        boolean isValid = true;

        if (getSelectedResources().size() == 0)
        {
            setErrorMessage(RESOURCE_SELECTION_ERROR_MESSAGE);

            isValid = false;
        }
        else
        {
            setErrorMessage(null);
        }

        return super.validateSourceGroup() && isValid;
    }

    @Override
    protected boolean validateDestinationGroup()
    {
        String destinationValue = getDestinationValue();

        if (destinationValue.length() == 0)
        {
            setMessage(ENTER_DESTINATION_MESSAGE);

            return false;
        }

        String conflictingContainer = getConflictingContainerNameFor(destinationValue);

        if (null == conflictingContainer)
        {
            // no error message, but warning may exists
            String threatenedContainer = getOverlappingProjectName(destinationValue);

            if (null == threatenedContainer)
            {
                setMessage(null);
            }
            else
            {
                setMessage(NLS.bind(PROJECT_DAMAGED_WARNING, threatenedContainer), WARNING);
            }

        }
        else
        {
            setErrorMessage(NLS.bind(LOCATION_CONFLICT_ERROR_MESSAGE, conflictingContainer));
            destinationNameField.setFocus();

            return false;
        }

        return true;
    }

    @Override
    protected boolean validateOptionsGroup()
    {
        return super.validateOptionsGroup();
    }

    private boolean ensureDirectoryExists(File directory)
    {
        if (!directory.exists())
        {
            if (!queryYesNoQuestion(DIRECTORY_CREATE_QUESTION))
            {
                return false;
            }

            if (!directory.mkdirs())
            {
                displayErrorDialog(COULD_NOT_CREATE_MESSAGE);
                destinationNameField.setFocus();

                return false;
            }
        }

        return true;
    }

    private boolean ensureTargetIsValid(File targetDirectory)
    {
        if (targetDirectory.exists() && !targetDirectory.isDirectory())
        {
            displayErrorDialog(ALREADY_EXISTS_MESSAGE);
            destinationNameField.setFocus();

            return false;
        }

        return ensureDirectoryExists(targetDirectory);
    }

    protected String getDestinationValue()
    {
        return destinationNameField.getText().trim();
    }

    private String getConflictingContainerNameFor(String targetDirectory)
    {
        IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        IPath testPath = new Path(targetDirectory);

        // cannot export into workspace root
        if (testPath.equals(rootPath))
            return rootPath.lastSegment();

        // Are they the same?
        if (testPath.matchingFirstSegments(rootPath) == rootPath.segmentCount())
        {
            String firstSegment = testPath.removeFirstSegments(rootPath.segmentCount()).segment(0);

            if (!Character.isLetterOrDigit(firstSegment.charAt(0)))
                return firstSegment;
        }

        return null;
    }

    private String getOverlappingProjectName(String targetDirectory)
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        URI testURI = new File(targetDirectory).toURI();

        IContainer[] containers = root.findContainersForLocationURI(testURI);

        if (containers.length > 0)
        {
            return containers[0].getProject().getName();
        }

        return null;
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

    protected abstract String[] getFileExtensions();

    protected abstract IRunnableWithProgress getExporter();

    protected boolean getIsProgressMonitorCancelable()
    {
        return true;
    }
}
