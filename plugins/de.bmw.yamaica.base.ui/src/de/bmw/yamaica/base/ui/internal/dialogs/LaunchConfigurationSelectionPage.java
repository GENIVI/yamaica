/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.core.launching.ILaunchConfigurationPreparer;
import de.bmw.yamaica.base.ui.internal.Activator;
import de.bmw.yamaica.base.ui.utils.ViewerToolBar;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchViewerComparator;

public class LaunchConfigurationSelectionPage extends WizardPage implements ILaunchConfigurationListener
{
    protected final static String          STORE_SHOW_ALL_LAUNCH_CONFIGURATIONS_ID = "yamaicaLaunchConfigurationSelectionPage.STORE_SHOW_ALL_LAUNCH_CONFIGURATIONS_ID"; //$NON-NLS-1$

    protected Action                       newAction;
    protected Action                       duplicateAction;
    protected Action                       deleteAction;
    protected Action                       editAction;

    protected IStructuredSelection         selection;
    protected ILaunchManager               launchManager;
    protected ILaunchConfigurationType     launchConfigurationType;
    protected ILaunchConfigurationPreparer launchConfigurationPreparer;
    protected ViewerToolBar                viewerToolBar;
    protected TableViewer                  tableViewer;
    protected ViewerFilter                 viewerFilter;

    public LaunchConfigurationSelectionPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super("yamaicaLaunchConfigurationSelectionPage");

        setMessage("Select a launch configuration.");

        selection = structuredSelection;
        launchManager = DebugPlugin.getDefault().getLaunchManager();

        viewerFilter = new Filter();
        newAction = new NewAction();
        duplicateAction = new DuplicateAction();
        deleteAction = new DeleteAction();
        editAction = new EditAction();

        duplicateAction.setEnabled(false);
        deleteAction.setEnabled(false);
        editAction.setEnabled(false);

        launchManager.addLaunchConfigurationListener(this);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible)
        {
            IWizard wizard = getWizard();

            if (wizard instanceof ILaunchWizard)
            {
                ILaunchWizard launchWizard = (ILaunchWizard) wizard;

                if (wizard instanceof TransformWizard)
                {
                    ((TransformWizard) wizard).setWindowTitle(launchWizard.getWizardTitle());
                }

                launchConfigurationType = launchWizard.getLaunchILaunchConfigurationType();
                setTitle(launchConfigurationType.getName());

                try
                {
                    tableViewer.setInput(launchManager.getLaunchConfigurations(launchConfigurationType));
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }

                launchConfigurationPreparer = launchWizard.getLaunchConfigurationPreparer();
            }
        }
    }

    @Override
    public void createControl(Composite parent)
    {
        Font font = parent.getFont();

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        setControl(composite);

        viewerToolBar = new ViewerToolBar(composite, SWT.BORDER, ViewerToolBar.FILTER)
        {
            @Override
            protected void createToolBarButtons()
            {
                toolBarManager.add(newAction);
                toolBarManager.add(duplicateAction);
                toolBarManager.add(deleteAction);
                toolBarManager.add(new Separator());
                toolBarManager.add(editAction);

                super.createToolBarButtons();
            }
        };
        viewerToolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
        viewerToolBar.setFilterText("Filter Launch Configurations");

        Table table = new Table(viewerToolBar, SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setFont(font);
        table.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                Table table = (Table) e.widget;
                table.getColumn(0).setWidth(table.getClientArea().width);
            }
        });

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(getContentProvider());
        tableViewer.setComparator(new WorkbenchViewerComparator());
        tableViewer.addFilter(viewerFilter);

        TableViewerColumn launchConfigurationTypeViewerColumn = new TableViewerColumn(tableViewer, SWT.LEAD);
        launchConfigurationTypeViewerColumn.setLabelProvider((CellLabelProvider) getLabelProvider());

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();

                if (!selection.isEmpty())
                {
                    if (selection.size() == 1)
                    {
                        duplicateAction.setEnabled(true);
                        editAction.setEnabled(true);
                    }
                    else
                    {
                        duplicateAction.setEnabled(false);
                        editAction.setEnabled(false);
                    }

                    deleteAction.setEnabled(true);
                }
                else
                {
                    duplicateAction.setEnabled(false);
                    deleteAction.setEnabled(false);
                    editAction.setEnabled(false);
                }

                getContainer().updateButtons();
            }
        });

        tableViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                IWizard wizard = getWizard();
                IWizardContainer container = wizard.getContainer();

                if (container instanceof WizardDialog)
                {
                    if (wizard.performFinish())
                    {
                        ((WizardDialog) container).close();
                    }
                }
            }
        });

        viewerToolBar.setViewer(tableViewer);

        restoreWidgetValues();

        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".yamaica_transform_type_selection");
    }

    protected IContentProvider getContentProvider()
    {
        return ArrayContentProvider.getInstance();
    }

    protected IBaseLabelProvider getLabelProvider()
    {
        return new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                ILaunchConfiguration launchConfiguration = (ILaunchConfiguration) cell.getElement();

                cell.setText(launchConfiguration.getName());
                cell.setImage(DebugUITools.getDefaultImageDescriptor(launchConfiguration).createImage());
            }
        };
    }

    public boolean finish()
    {
        boolean success = false;

        saveWidgetValues();

        try
        {
            getContainer().run(true, true, new LaunchJobExecuter(getSelectedLaunchConfigurations()[0]));

            success = true;
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {

        }

        return success;
    }

    @Override
    public boolean isPageComplete()
    {
        if (!isCurrentPage())
        {
            return false;
        }

        if (((IStructuredSelection) tableViewer.getSelection()).size() != 1)
        {
            return false;
        }

        return true;
    }

    protected void restoreWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            if (null != viewerToolBar)
            {
                viewerToolBar.setFilterEnabled(!settings.getBoolean(STORE_SHOW_ALL_LAUNCH_CONFIGURATIONS_ID));
            }
        }
    }

    protected void saveWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();

        if (null != settings)
        {
            if (null != viewerToolBar)
            {
                settings.put(STORE_SHOW_ALL_LAUNCH_CONFIGURATIONS_ID, !viewerToolBar.isFilterEnabled());
            }
        }
    }

    @Override
    public void dispose()
    {
        launchManager.removeLaunchConfigurationListener(this);
    }

    @Override
    public void launchConfigurationAdded(ILaunchConfiguration configuration)
    {
        try
        {
            tableViewer.setInput(launchManager.getLaunchConfigurations(launchConfigurationType));
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }

        tableViewer.setSelection(new StructuredSelection(configuration));
    }

    @Override
    public void launchConfigurationChanged(ILaunchConfiguration configuration)
    {
        tableViewer.refresh(configuration);
    }

    @Override
    public void launchConfigurationRemoved(ILaunchConfiguration configuration)
    {
        try
        {
            tableViewer.setInput(launchManager.getLaunchConfigurations(launchConfigurationType));
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    protected ILaunchConfiguration[] getSelectedLaunchConfigurations()
    {
        List<?> list = ((IStructuredSelection) tableViewer.getSelection()).toList();

        return list.toArray(new ILaunchConfiguration[list.size()]);
    }

    protected class Filter extends ViewerFilter
    {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element)
        {
            try
            {
                IResource[] mappedResources = ((ILaunchConfiguration) element).getMappedResources();

                if (null != mappedResources)
                {
                    HashSet<IResource> mappedResourcesSet = new HashSet<IResource>(Arrays.asList(mappedResources));

                    Iterator<?> iterator = selection.iterator();

                    while (iterator.hasNext())
                    {
                        IResource selectedResource = (IResource) iterator.next();

                        if (mappedResourcesSet.contains(selectedResource))
                        {
                            return true;
                        }
                    }
                }
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }

    protected class NewAction extends Action
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui.ide", "icons/full/etool16/newfile_wiz.gif");
        }

        @Override
        public String getText()
        {
            return "Create new launch configuration";
        }

        @Override
        public void run()
        {
            try
            {
                ILaunchConfigurationWorkingCopy launchConfiguration = launchConfigurationPreparer.getPreparedLaunchConfiguration(selection,
                        IDebugUIConstants.ID_RUN_LAUNCH_GROUP);

                /** Create a new empty launch configuration if preparer could not create one **/
                if (null == launchConfiguration)
                {
                    String name;

                    if (!selection.isEmpty())
                    {
                        IResource resource = (IResource) selection.getFirstElement();

                        name = launchManager.generateLaunchConfigurationName(resource.getName());
                    }
                    else
                    {
                        name = launchManager.generateLaunchConfigurationName("New_configuration");
                    }

                    launchConfiguration = launchConfigurationType.newInstance(null, name);
                }

                launchConfiguration.doSave();
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected class DuplicateAction extends Action
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/etool16/copy_edit.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/copy_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Duplicate launch configuration";
        }

        @Override
        public void run()
        {
            ILaunchConfiguration selectedLaunchConfiguration = getSelectedLaunchConfigurations()[0];
            String name = launchManager.generateLaunchConfigurationName(selectedLaunchConfiguration.getName());

            try
            {
                selectedLaunchConfiguration.copy(name).doSave();
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected class DeleteAction extends Action
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/etool16/delete_edit.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/delete_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Delete launch configuration(s)";
        }

        @Override
        public void run()
        {
            try
            {
                for (ILaunchConfiguration launchConfiguration : getSelectedLaunchConfigurations())
                {
                    launchConfiguration.delete();
                }
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    // TODO ICONS
    protected class EditAction extends Action
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/edit.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/edit_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Edit launch configuration";
        }

        @Override
        public void run()
        {
            ILaunchConfiguration launchConfiguration = getSelectedLaunchConfigurations()[0];

            if (null != launchConfiguration)
            {
                DebugUITools
                        .openLaunchConfigurationPropertiesDialog(getShell(), launchConfiguration, IDebugUIConstants.ID_RUN_LAUNCH_GROUP);
            }
        }
    }

    // Using a job for the launch execution will bring up the default error dialog like the launch was
    // started by the default run menu.
    protected class LaunchJobExecuter implements IRunnableWithProgress
    {
        protected final ILaunchConfiguration launchConfiguration;

        public LaunchJobExecuter(ILaunchConfiguration launchConfiguration)
        {
            this.launchConfiguration = launchConfiguration;
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
            monitor.beginTask("Executing launch job", IProgressMonitor.UNKNOWN);

            if (null != launchConfiguration)
            {
                Job job = new Job("Launching " + launchConfiguration.getName())
                {
                    @Override
                    public IStatus run(IProgressMonitor monitor)
                    {
                        try
                        {
                            launchConfiguration.launch("run", monitor);

                            return Status.OK_STATUS;
                        }
                        catch (CoreException e)
                        {
                            e.printStackTrace();
                        }

                        return Status.CANCEL_STATUS;
                    }
                };

                job.schedule(500);

                while (job.getState() != Job.NONE)
                {
                    Thread.sleep(100);

                    if (monitor.isCanceled())
                    {
                        job.cancel();

                        throw new InterruptedException("Executing launch job was canceled.");
                    }
                }

                if (!job.getResult().equals(Status.OK_STATUS))
                {
                    throw new InterruptedException("Executing launch job did not finish normally.");
                }
            }

            monitor.done();
        }
    }
}
