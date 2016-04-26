/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.IWizardDescriptor;

import de.bmw.yamaica.common.ui.utils.WizardComparator;

public abstract class YamaicaWizardSelectionPage extends WizardSelectionPage
{
    private IWorkbench           workbench;
    private IStructuredSelection structuredSelection;

    private TableViewer          tableViewer;

    YamaicaWizardSelectionPage(IWorkbench workbench, IStructuredSelection structuredSelection, String pageName)
    {
        super(pageName);

        this.workbench = workbench;
        this.structuredSelection = structuredSelection;
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
        tableViewer.setContentProvider(getContentProvider());
        tableViewer.setComparator(new WizardComparator());
        tableViewer.setFilters(new ViewerFilter[] { new YamaicaWizardActivityFilter() });
        tableViewer.getTable().setHeaderVisible(false);

        TableViewerColumn filenameViewerColumn = new TableViewerColumn(tableViewer, SWT.LEAD);
        filenameViewerColumn.setLabelProvider((CellLabelProvider) getLabelProvider());

        tableColumnLayout.setColumnData(filenameViewerColumn.getColumn(), new ColumnWeightData(100, 80, false));

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                ISelection selection = event.getSelection();

                if (!selection.isEmpty() && selection instanceof IStructuredSelection)
                {
                    Object selectedElement = ((IStructuredSelection) selection).getFirstElement();

                    if (selectedElement instanceof IWizardDescriptor)
                    {
                        IWizardDescriptor wizardDescriptor = (IWizardDescriptor) selectedElement;
                        IWizardNode wizardNode = createWizardNode(wizardDescriptor);

                        setMessage(wizardDescriptor.getDescription());
                        setSelectedNode(wizardNode);
                    }
                }
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
                IWizardDescriptor wizardDescriptor = (IWizardDescriptor) cell.getElement();

                ImageDescriptor imageDescriptor = wizardDescriptor.getImageDescriptor();

                if (null != imageDescriptor)
                {
                    cell.setImage(imageDescriptor.createImage());
                }

                cell.setText(wizardDescriptor.getLabel());
            }
        };
    }

    protected abstract Object getViewerInput();

    @Override
    public void dispose()
    {
        super.dispose();

        tableViewer.getTable().dispose();
    }

    private IWizardNode createWizardNode(IWizardDescriptor wizardDescriptor)
    {
        return new WizardNode(this, wizardDescriptor);
    }

    public IWorkbench getWorkbench()
    {
        return workbench;
    }

    public IStructuredSelection getSelection()
    {
        return structuredSelection;
    }
}
