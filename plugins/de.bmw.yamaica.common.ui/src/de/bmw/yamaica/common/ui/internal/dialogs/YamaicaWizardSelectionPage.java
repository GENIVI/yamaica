/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
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

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;
import de.bmw.yamaica.common.ui.utils.WizardComparator;
import de.bmw.yamaica.common.ui.utils.WizardSelector;

public abstract class YamaicaWizardSelectionPage extends WizardSelectionPage
{
    private IWorkbench           workbench;
    private IStructuredSelection structuredSelection;

    private TreeViewer          tableViewer;
    private TreeContentProvider contentProvider = new TreeContentProvider();
    private boolean             useWizardCategories;

    YamaicaWizardSelectionPage(IWorkbench workbench, IStructuredSelection structuredSelection, String pageName)
    {
        super(pageName);

        this.workbench = workbench;
        this.structuredSelection = structuredSelection;
    }

    YamaicaWizardSelectionPage(IWorkbench workbench, IStructuredSelection structuredSelection, String pageName, boolean useWizardCategories)
    {
        super(pageName);

        this.workbench = workbench;
        this.structuredSelection = structuredSelection;
        this.useWizardCategories = useWizardCategories;
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        setControl(composite);

        TreeColumnLayout tableColumnLayout = new TreeColumnLayout();

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayout(tableColumnLayout);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TreeViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(getContentProvider());
        tableViewer.setComparator(new WizardComparator());
        tableViewer.setFilters(new ViewerFilter[] { new YamaicaWizardActivityFilter() });

        TreeViewerColumn filenameViewerColumn = new TreeViewerColumn(tableViewer, SWT.LEAD);
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

                    if (selectedElement instanceof TreeNode)
                        selectedElement = ((TreeNode) selectedElement).getValue();
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

        Object viewerInput = getViewerInput();
        if (useWizardCategories)
            viewerInput = createTreeInput(viewerInput);
        tableViewer.setInput(viewerInput);
        tableViewer.expandAll();
    }

    protected IContentProvider getContentProvider()
    {
        return contentProvider;
    }

    protected IBaseLabelProvider getLabelProvider()
    {
        return new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                Object cellElement = cell.getElement();
                if (cellElement instanceof TreeNode)
                    cellElement = ((TreeNode)cellElement).getValue();

                if (cellElement instanceof IWizardDescriptor)
                {
                    IWizardDescriptor wizardDescriptor = (IWizardDescriptor) cellElement;

                    ImageDescriptor imageDescriptor = wizardDescriptor.getImageDescriptor();

                    if (null != imageDescriptor)
                    {
                        cell.setImage(imageDescriptor.createImage());
                    }

                    cell.setText(wizardDescriptor.getLabel());
                }
                else if (cellElement instanceof WizardCategory)
                {
                    WizardCategory wizardCategory = (WizardCategory)cellElement;
                    cell.setText(wizardCategory.getLabel());
                    if (wizardCategory.getImageDescriptor() != null)
                    {
                        cell.setImage(wizardCategory.getImageDescriptor().createImage());
                    }
                }
                else
                    cell.setText(cellElement.toString());
            }
        };
    }

    protected abstract Object getViewerInput();

    @Override
    public void dispose()
    {
        super.dispose();

        tableViewer.getTree().dispose();
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

    private class WizardCategory
    {
        private String label;
        private LinkedList<IWizardDescriptor> wizards = new LinkedList<>();
        private ImageDescriptor imageDescriptor;

        WizardCategory(String label, ImageDescriptor imageDescriptor) {
            this.label = label;
            this.setImageDescriptor(imageDescriptor);
        }

        String getLabel()
        {
            return label;
        }

        LinkedList<IWizardDescriptor> getWizards()
        {
            return wizards;
        }

        ImageDescriptor getImageDescriptor()
        {
            return imageDescriptor;
        }

        void setImageDescriptor(ImageDescriptor imageDescriptor)
        {
            this.imageDescriptor = imageDescriptor;
        }
    }

    @SuppressWarnings("unchecked")
    protected Object createTreeInput(Object input)
    {
        ArrayList<WizardCategory> wizardCategories = new ArrayList<>();
        WizardCategory cat1 = new WizardCategory("Transformations", Activator.imageDescriptorFromPlugin(YamaicaUIConstants.YAMAICA_COMMON_UI, YamaicaUIConstants.YAMAICA_ICON_PATH));
        WizardCategory cat2 = new WizardCategory("File System", Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.FOLDER_ICON_PATH));
        for (IWizardDescriptor descriptor : (LinkedList<IWizardDescriptor>) input)
        {
            IConfigurationElement configurationElement = descriptor.getAdapter(IConfigurationElement.class);
            if (configurationElement != null)
            {
                if (WizardSelector.isFileSystemWizard(configurationElement))
                {
                    if (!cat2.getWizards().contains(descriptor))
                        cat2.getWizards().add(descriptor);
                }
                else
                {
                    if (!cat1.getWizards().contains(descriptor))
                        cat1.getWizards().add(descriptor);
                }
            }
        }

        if (cat1.wizards.size() > 0)
            wizardCategories.add(cat1);
        if (cat2.wizards.size() > 0)
            wizardCategories.add(cat2);
        return wizardCategories;
    }

    class TreeContentProvider extends TreeNodeContentProvider
    {
        @SuppressWarnings("unchecked")
        @Override
        public Object[] getElements(Object inputElement)
        {
            if (inputElement instanceof ArrayList<?>)
            {
                ArrayList<TreeNode> elements = new ArrayList<>();
                for (WizardCategory category : (ArrayList<WizardCategory>) inputElement)
                {
                    ArrayList<TreeNode> elementChildren = new ArrayList<>();
                    for (IWizardDescriptor wizard : category.getWizards())
                    {
                        elementChildren.add(new TreeNode(wizard));
                    }

                    TreeNode element = new TreeNode(category);
                    element.setChildren(elementChildren.toArray(new TreeNode[elementChildren.size()]));
                    elements.add(element);
                }
                return elements.toArray(new TreeNode[elements.size()]);
            }
            else if (inputElement instanceof LinkedList<?>)
            {
                ArrayList<TreeNode> elements = new ArrayList<>();
                for (IWizardDescriptor wizard : (LinkedList<IWizardDescriptor>) inputElement)
                {
                    elements.add(new TreeNode(wizard));
                }
                return elements.toArray(new TreeNode[elements.size()]);
            }
            return super.getElements(inputElement);
        }
    }
}
