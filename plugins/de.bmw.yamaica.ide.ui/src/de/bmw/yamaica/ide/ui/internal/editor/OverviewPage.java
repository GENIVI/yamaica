/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.navigator.ResourceComparator;

import de.bmw.yamaica.common.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.common.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.common.ui.internal.dialogs.ExportWizard;
import de.bmw.yamaica.common.ui.internal.dialogs.ImportWizard;
import de.bmw.yamaica.common.ui.internal.dialogs.NewWizard;
import de.bmw.yamaica.common.ui.internal.dialogs.TransformWizard;
import de.bmw.yamaica.ide.ui.YamaicaConstants;
import de.bmw.yamaica.ide.ui.internal.Activator;

public class OverviewPage extends FormPage implements Listener
{
    private FormToolkit            toolkit;
    private ScrolledForm           form;
    private TreeViewer             importFilesTreeViewer, targetFilesTreeViewer;
    private IResourcePropertyStore store;

    private Button                 importFilesNewButton, importFilesEditButton, importFilesImportButton;
    private Button                 importFilesExportButton, importFilesTransformButton;
    private Button                 targetFilesEditButton, targetFilesExportButton;

    public OverviewPage(String id, String title)
    {
        super(id, title);
    }

    public OverviewPage(FormEditor editor, String id, String title)
    {
        super(editor, id, title);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm)
    {
        store = YamaicaXmlModel.acquireInstance(getFile(), this).getResourcePropertyStore(getProject());
        store.addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
            public void propertyChange(final PropertyChangeEvent event)
            {
                getPartControl().getDisplay().asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String propertyName = event.getPropertyName();

                        if (propertyName.equals(IResourcePropertyStore.IMPORT_FOLDER))
                        {
                            setViewerInput(importFilesTreeViewer, (String) event.getNewValue());

                            return;
                        }

                        if (propertyName.equals(IResourcePropertyStore.TARGET_FOLDER))
                        {
                            setViewerInput(targetFilesTreeViewer, (String) event.getNewValue());

                            return;
                        }
                    }
                });
            }
        });

        toolkit = managedForm.getToolkit();
        form = managedForm.getForm();
        form.setText(YamaicaConstants.YAMAICA_EDITOR_TITLE); //$NON-NLS-1$
        form.setBackgroundImage(Activator.imageDescriptorFromPlugin(YamaicaConstants.ECLIPSE_UI_INTRO_PLUGIN_ID,
                YamaicaConstants.YAMAICA_EDITOR_BANNER).createImage());

        Composite body = form.getBody();
        body.setLayout(new GridLayout(2, true));

        Composite importSectionClient = createSectionClient(body, YamaicaConstants.IMPORTED_FILES_TITLE,
                YamaicaConstants.IMPORTED_FILES_DESCRIPTION);

        importFilesTreeViewer = createTreeViewer(importSectionClient, 6);
        ArrayList<Button> importPaneButtons = new ArrayList<>();

        // Add "New File..." button
        //
        importFilesNewButton = createButton(importSectionClient, YamaicaConstants.NEW, YamaicaConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaConstants.NEW_FILE_ICON_PATH);
        importFilesNewButton.setEnabled(true);

        // Add "Edit..." button
        //
        importFilesEditButton = createButton(importSectionClient, YamaicaConstants.EDIT, YamaicaConstants.ECLIPSE_TEXTEDITOR_PLUGIN_ID,
                YamaicaConstants.EDIT_TEMPLATE_ICON_PATH);
        if (importFilesEditButton != null)
            importPaneButtons.add(importFilesEditButton);

        // Add "Import..." button
        //
        importFilesImportButton = createButton(importSectionClient, YamaicaConstants.IMPORT, YamaicaConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaConstants.IMPORT_ICON_PATH);
        importFilesImportButton.setEnabled(true);

        // Add "Export..." button
        //
        importFilesExportButton = createButton(importSectionClient, YamaicaConstants.EXPORT, YamaicaConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaConstants.EXPORT_ICON_PATH);
        if (importFilesExportButton != null)
            importPaneButtons.add(importFilesExportButton);

        // Add "Transform" button, only if there are any registered transformations available at all.
        //
        if (TransformWizard.isRegisteredTransformationsInPlatform())
        {
            importFilesTransformButton = createButton(importSectionClient, YamaicaConstants.TRANSFORM,
                    YamaicaConstants.ECLIPSE_JDT_UI_PLUGIN_ID, YamaicaConstants.TRANSFORM_ICON_PATH);
            if (importFilesTransformButton != null)
                importPaneButtons.add(importFilesTransformButton);
        }

        importFilesTreeViewer.addSelectionChangedListener(new TreeViewerButtonEnabler(importPaneButtons.toArray(new Button[importPaneButtons.size()])));

        setViewerInput(importFilesTreeViewer, getPropertyValue(IResourcePropertyStore.IMPORT_FOLDER));

        Composite targetSectionClient = createSectionClient(body, YamaicaConstants.TARGET_FILES_TITLE,
                YamaicaConstants.TARGET_FILES_DESCRIPTION);

        targetFilesTreeViewer = createTreeViewer(targetSectionClient, 3);
        targetFilesEditButton = createButton(targetSectionClient, YamaicaConstants.EDIT, YamaicaConstants.ECLIPSE_TEXTEDITOR_PLUGIN_ID,
                YamaicaConstants.EDIT_TEMPLATE_ICON_PATH);
        targetFilesExportButton = createButton(targetSectionClient, YamaicaConstants.EXPORT, YamaicaConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaConstants.EXPORT_ICON_PATH);
        targetFilesTreeViewer.addSelectionChangedListener(new TreeViewerButtonEnabler(new Button[] { targetFilesEditButton,
                targetFilesExportButton }));

        setViewerInput(targetFilesTreeViewer, getPropertyValue(IResourcePropertyStore.TARGET_FOLDER));

        toolkit.paintBordersFor(form.getBody());
    }

    @Override
    public void setFocus()
    {
        form.setFocus();
    }

    @Override
    public void dispose()
    {
        // Use project to release instance since the file may no longer
        // exist when the editor gets disposed.
        YamaicaXmlModel.releaseInstance(getFile().getProject(), this);
        toolkit.dispose();
        super.dispose();
    }

    private TreeViewer createTreeViewer(Composite parent, int verticalSpan)
    {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, verticalSpan);
        gridData.minimumWidth = 100;
        gridData.widthHint = 100;

        Tree tree = toolkit.createTree(parent, SWT.BORDER | SWT.MULTI);
        tree.setHeaderVisible(false);
        tree.setLayoutData(gridData);

        final TreeViewer treeViewer = new TreeViewer(tree);
        treeViewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        treeViewer.setContentProvider(new WorkbenchContentProvider());
        treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        treeViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                ITreeSelection selection = (ITreeSelection) event.getSelection();

                Object selectedElement = selection.getFirstElement();

                if (selectedElement instanceof IFile)
                {
                    openEditor(selection);
                }
                else if (selectedElement instanceof IContainer)
                {
                    // toggle expand state
                    treeViewer.setExpandedState(selectedElement, !treeViewer.getExpandedState(selectedElement));
                }
            }
        });

        return treeViewer;
    }

    private Button createButton(Composite parent, String text, String pluginId, String iconPath)
    {
        Button button = toolkit.createButton(parent, text, SWT.PUSH); //$NON-NLS-1$
        button.setImage(Activator.imageDescriptorFromPlugin(pluginId, iconPath).createImage());
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        button.setEnabled(false);
        button.addListener(SWT.Selection, this);

        return button;
    }

    private Composite createSectionClient(Composite parent, String text, String description)
    {
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText(text);
        section.setDescription(description);
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout sectionClientLayout = new GridLayout(2, false);
        sectionClientLayout.marginWidth = sectionClientLayout.marginHeight = 0;

        Composite sectionClient = toolkit.createComposite(section);
        section.setClient(sectionClient);
        sectionClient.setLayout(sectionClientLayout);

        return sectionClient;
    }

    private ITreeSelection createSingleSelection(ISelection selection, Object element)
    {
        ITreeSelection treeSelection = (ITreeSelection) selection;

        if (treeSelection.isEmpty() || treeSelection.size() > 1)
        {
            Object[] segments = new Object[] { element };
            treeSelection = new TreeSelection(new TreePath(segments));
        }

        return treeSelection;
    }

    private void setViewerInput(Viewer viewer, String path)
    {
        if (null == viewer)
        {
            return;
        }

        if (null == path)
        {
            viewer.setInput(null);
        }
        else
        {
            viewer.setInput(getContainer(path));
        }
    }

    private IFile getFile()
    {
        return ((IFileEditorInput) getEditorInput()).getFile();
    }

    private IProject getProject()
    {
        return getFile().getProject();
    }

    private IFolder getContainer(String path)
    {
        Assert.isNotNull(path);

        return getProject().getFolder(path);
    }

    private String getPropertyValue(String propertyName)
    {
        Assert.isNotNull(propertyName);

        return store.getProperty(propertyName,
                de.bmw.yamaica.common.ui.internal.Activator.getDefault().getPreferenceStore().getString(propertyName));
    }

    private void openEditor(ISelection selection)
    {
        ITreeSelection treeSelection = (ITreeSelection) selection;

        if (treeSelection.size() == 1)
        {
            Object selectedElement = treeSelection.getFirstElement();

            if (selectedElement instanceof IFile)
            {
                try
                {
                    IFile selectedFile = (IFile) selectedElement;
                    String filename = selectedFile.getName();
                    IWorkbench workbench = PlatformUI.getWorkbench();

                    IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(selectedFile.getContents(), filename);
                    IEditorDescriptor editorDescriptor = workbench.getEditorRegistry().getDefaultEditor(filename, contentType);
                    String editorId = null == editorDescriptor ? YamaicaConstants.DEFAULT_TEXT_EDITOR_ID : editorDescriptor.getId();
                    workbench.getActiveWorkbenchWindow().getPages()[0].openEditor(new FileEditorInput(selectedFile), editorId);
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
        }
    }

    private void openWizard(IWorkbenchWizard wizard, ISelection selection)
    {
        IWorkbench workbench = PlatformUI.getWorkbench();

        wizard.init(workbench, (IStructuredSelection) selection);

        WizardDialog wizardDialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
        wizardDialog.setTitle(wizard.getWindowTitle());
        wizardDialog.open();
    }

    @Override
    public void handleEvent(Event event)
    {
        if (event.widget.equals(importFilesNewButton))
        {
            Object element = getContainer(getPropertyValue(IResourcePropertyStore.IMPORT_FOLDER));

            openWizard(new NewWizard(), createSingleSelection(importFilesTreeViewer.getSelection(), element));

            return;
        }

        if (event.widget.equals(importFilesEditButton))
        {
            openEditor(importFilesTreeViewer.getSelection());

            return;
        }

        if (event.widget.equals(importFilesImportButton))
        {
            Object element = getContainer(getPropertyValue(IResourcePropertyStore.IMPORT_FOLDER));

            openWizard(new ImportWizard(), createSingleSelection(importFilesTreeViewer.getSelection(), element));

            return;
        }

        if (event.widget.equals(importFilesExportButton))
        {
            openWizard(new ExportWizard(), importFilesTreeViewer.getSelection());

            return;
        }

        if (importFilesTransformButton != null && event.widget.equals(importFilesTransformButton))
        {
            openWizard(new TransformWizard(), importFilesTreeViewer.getSelection());

            return;
        }

        if (event.widget.equals(targetFilesEditButton))
        {
            openEditor(targetFilesTreeViewer.getSelection());

            return;
        }

        if (event.widget.equals(targetFilesExportButton))
        {
            openWizard(new ExportWizard(), targetFilesTreeViewer.getSelection());

            return;
        }
    }

    private class TreeViewerButtonEnabler implements ISelectionChangedListener
    {
        private Button[] buttons;

        public TreeViewerButtonEnabler(Button[] buttons)
        {
            this.buttons = buttons;
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();

            if (!selection.isEmpty() && selection.getFirstElement() instanceof IFile)
            {
                selectButtons(true);
            }
            else
            {
                selectButtons(false);
            }
        }

        private void selectButtons(boolean select)
        {
            for (Button button : buttons)
            {
                button.setEnabled(select);
            }
        }
    }
}