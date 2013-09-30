/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.core.launching.ILaunchConfigurationPreparer;
import de.bmw.yamaica.base.ui.internal.Activator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class LaunchConfigurationTypeSelectionPage extends WizardPage
{
    protected TableViewer tableViewer;

    public LaunchConfigurationTypeSelectionPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super("yamaicaLaunchConfigurationTypeSelectionPage");

        setTitle("Select");
        setMessage("Choose a transform launch configuration type.");
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible)
        {
            IWizard wizard = getWizard();

            if (wizard instanceof TransformWizard)
            {
                TransformWizard transformWizard = (TransformWizard) wizard;

                transformWizard.setWindowTitle(transformWizard.getDefaultWindowTitle());
            }
        }
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        setControl(composite);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayout(tableColumnLayout);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        tableViewer.getTable().setFont(parent.getFont());
        tableViewer.setContentProvider(getContentProvider());

        TableViewerColumn launchConfigurationTypeViewerColumn = new TableViewerColumn(tableViewer, SWT.LEAD);
        launchConfigurationTypeViewerColumn.setLabelProvider((CellLabelProvider) getLabelProvider());

        tableColumnLayout.setColumnData(launchConfigurationTypeViewerColumn.getColumn(), new ColumnWeightData(100, 80, false));

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();

                if (!selection.isEmpty())
                {
                    ILaunchConfigurationType launchConfigurationType = ((LaunchConfigurationTypeData) selection.getFirstElement()).launchConfigurationType;

                    try
                    {
                        Set<String> modes = new HashSet<String>();
                        modes.add("run");

                        ILaunchDelegate[] launchDelegates = launchConfigurationType.getDelegates(modes);

                        if (null != launchDelegates && launchDelegates.length > 0)
                        {
                            setMessage(launchDelegates[0].getDescription());
                        }
                        else
                        {
                            setMessage(null);
                        }
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }

                getContainer().updateButtons();
            }
        });

        tableViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                getContainer().showPage(getNextPage());
            }
        });

        tableViewer.setInput(getViewerInput());

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
                ILaunchConfigurationType launchConfigurationType = ((LaunchConfigurationTypeData) cell.getElement()).launchConfigurationType;

                cell.setText(launchConfigurationType.getName());
                cell.setImage(DebugUITools.getDefaultImageDescriptor(launchConfigurationType).createImage());
            }
        };
    }

    protected Object getViewerInput()
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        List<LaunchConfigurationTypeData> yamaicaLaunchConfigurationTypeData = new LinkedList<LaunchConfigurationTypeData>();

        for (IConfigurationElement configurationElement : Platform.getExtensionRegistry().getConfigurationElementsFor(
                Activator.PLUGIN_ID + ".yamaicaLaunchConfigurationTypes"))
        {
            ILaunchConfigurationType yamaicaLaunchConfigurationType = launchManager.getLaunchConfigurationType(configurationElement
                    .getAttribute("launchConfigurationTypeId"));

            if (null != yamaicaLaunchConfigurationType)
            {
                ILaunchConfigurationPreparer launchConfigurationPreparer = null;

                try
                {
                    launchConfigurationPreparer = (ILaunchConfigurationPreparer) configurationElement.createExecutableExtension("class");
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    String wizardTitle = configurationElement.getAttribute("wizardTitle");

                    yamaicaLaunchConfigurationTypeData.add(new LaunchConfigurationTypeData(yamaicaLaunchConfigurationType,
                            launchConfigurationPreparer, wizardTitle));
                }
            }
        }

        // Test code to get the IDs of all available launch configuration types
        //
        // for (ILaunchConfigurationType launchConfigurationType : launchManager.getLaunchConfigurationTypes())
        // {
        // System.out.println(launchConfigurationType.getIdentifier());
        // }

        return yamaicaLaunchConfigurationTypeData;
    }

    @Override
    public boolean isPageComplete()
    {
        return !tableViewer.getSelection().isEmpty();
    }

    public ILaunchConfigurationType getLaunchConfigurationType()
    {
        return ((LaunchConfigurationTypeData) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement()).launchConfigurationType;
    }

    public ILaunchConfigurationPreparer getLaunchConfigurationPreparer()
    {
        return ((LaunchConfigurationTypeData) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement()).launchConfigurationPreparer;
    }

    public String getWizardTitle()
    {
        return ((LaunchConfigurationTypeData) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement()).wizardTitle;
    }

    protected class LaunchConfigurationTypeData
    {
        public final ILaunchConfigurationType     launchConfigurationType;
        public final ILaunchConfigurationPreparer launchConfigurationPreparer;
        public final String                       wizardTitle;

        public LaunchConfigurationTypeData(ILaunchConfigurationType launchConfigurationType,
                ILaunchConfigurationPreparer launchConfigurationPreparer, String wizardTitle)
        {
            this.launchConfigurationType = launchConfigurationType;
            this.launchConfigurationPreparer = launchConfigurationPreparer;
            this.wizardTitle = wizardTitle;
        }
    }
}
