/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.DrillDownAdapter;

import de.bmw.yamaica.base.ui.internal.Activator;

public class ViewerToolBar extends Composite implements ICheckStateListener, PaintListener
{
    public static final int              DRILL_DOWN           = 0x1;
    public static final int              SELECT               = 0x2;
    public static final int              FILTER               = 0x4;
    public static final int              REFRESH              = 0x8;

    public static final int              SELECT_ALL_ACTION    = 0x1;
    public static final int              DESELECT_ALL_ACTION  = 0x2;
    public static final int              FILTER_ACTION        = 0x4;
    public static final int              REFRESH_ACTION       = 0x8;

    protected HashSet<ActionRunListener> listeners            = new HashSet<ActionRunListener>();
    protected ToolBarManager             toolBarManager       = null;
    protected Viewer                     viewer               = null;
    protected ViewerFilter[]             viewerFilters        = new ViewerFilter[] {};
    protected boolean                    viewerFiltersEnabled = false;
    protected Object                     viewerInput          = null;
    protected DrillDownAdapter           drillDownAdapter     = null;
    protected int                        toolBarStyle         = 0;
    protected IAction                    selectAction         = null;
    protected IAction                    deselectAction       = null;
    protected IAction                    filterAction         = null;
    protected IAction                    refreshAction        = null;
    protected String                     filterText           = "Filter Elements";

    public ViewerToolBar(Composite parent, int style)
    {
        this(parent, style, DRILL_DOWN | SELECT | FILTER | REFRESH);
    }

    public ViewerToolBar(Composite parent, int style, int toolBarStyle)
    {
        super(parent, style);

        this.toolBarStyle = toolBarStyle;

        createToolBar();
    }

    public IAction getAction(int actionType)
    {
        switch (actionType)
        {
            case SELECT_ALL_ACTION:
                return selectAction;

            case DESELECT_ALL_ACTION:
                return deselectAction;

            case FILTER_ACTION:
                return filterAction;

            case REFRESH_ACTION:
                return refreshAction;

            default:
                return null;
        }
    }

    public void addActionRunListener(ActionRunListener listener)
    {
        listeners.add(listener);
    }

    public void removeActionRunListener(ActionRunListener listener)
    {
        listeners.remove(listener);
    }

    protected void createToolBar()
    {
        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
        setLayout(layout);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.BEGINNING;

        toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolBarManager.createControl(this);
        toolBar.setLayoutData(gridData);
    }

    public void setViewer(Viewer viewer)
    {
        Assert.isNotNull(viewer);

        this.viewer = viewer;

        createToolBarButtons();

        toolBarManager.update(true);

        Control viewerControl = viewer.getControl();

        viewerControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        layout();

        viewerControl.addPaintListener(this);
    }

    public void setFilterEnabled(boolean enabled)
    {
        viewerFiltersEnabled = enabled;

        updateViewerFilters();
        updateFilterButtons();
        updateSelectButtons();
        updateRefreshButtons();
    }

    public boolean isFilterEnabled()
    {
        return viewerFiltersEnabled;
    }

    public String getFilterText()
    {
        return filterText;
    }

    public void setFilterText(String filterText)
    {
        this.filterText = filterText;
    }

    protected void createToolBarButtons()
    {
        if (viewer instanceof TreeViewer && (toolBarStyle & DRILL_DOWN) > 0)
        {
            createDrillDownButtons();
        }

        toolBarManager.add(new Separator());

        if ((toolBarStyle & SELECT) > 0)
        {
            createSelectButtons();
        }

        toolBarManager.add(new Separator());

        if (viewer instanceof StructuredViewer && (toolBarStyle & FILTER) > 0)
        {
            createFilterButtons();
        }

        toolBarManager.add(new Separator());

        if (viewer instanceof StructuredViewer && (toolBarStyle & REFRESH) > 0)
        {
            createRefreshButtons();
        }
    }

    protected void createDrillDownButtons()
    {
        drillDownAdapter = new DrillDownAdapter((TreeViewer) viewer);
        drillDownAdapter.addNavigationActions(toolBarManager);
    }

    protected void createSelectButtons()
    {
        if (viewer instanceof ICheckable)
        {
            ((ICheckable) viewer).addCheckStateListener(this);
        }

        deselectAction = new DeselectAllAction();
        deselectAction.setEnabled(false);

        selectAction = new SelectAllAction();
        selectAction.setEnabled(false);

        toolBarManager.add(deselectAction);
        toolBarManager.add(selectAction);
    }

    protected void createFilterButtons()
    {
        filterAction = new FilterAction();
        filterAction.setEnabled(false);

        toolBarManager.add(filterAction);
    }

    protected void createRefreshButtons()
    {
        refreshAction = new RefreshAction();
        refreshAction.setEnabled(false);

        toolBarManager.add(refreshAction);
    }

    protected void updateSelectButtons()
    {
        int itemCount = 0;
        int checkedItemCount = 0;

        if (viewer instanceof CheckboxTreeViewer)
        {
            Tree tree = ((CheckboxTreeViewer) viewer).getTree();
            TreeItem[] treeItems = getAllTreeItems(tree.getItems());

            itemCount = treeItems.length;

            for (TreeItem treeItem : treeItems)
            {
                if (treeItem.getChecked())
                {
                    checkedItemCount++;
                }
            }
        }
        else if (viewer instanceof CheckboxTableViewer)
        {
            Table table = ((CheckboxTableViewer) viewer).getTable();
            TableItem[] tableItems = table.getItems();

            itemCount = tableItems.length;

            for (TableItem tableItem : tableItems)
            {
                if (tableItem.getChecked())
                {
                    checkedItemCount++;
                }
            }
        }

        if (null != selectAction)
        {
            if (itemCount == checkedItemCount)
            {
                selectAction.setEnabled(false);
            }
            else
            {
                selectAction.setEnabled(true);
            }
        }

        if (null != deselectAction)
        {
            if (0 == checkedItemCount)
            {
                deselectAction.setEnabled(false);
            }
            else
            {
                deselectAction.setEnabled(true);
            }
        }
    }

    protected TreeItem[] getAllTreeItems(TreeItem[] treeItems)
    {
        LinkedList<TreeItem> list = new LinkedList<TreeItem>();

        for (TreeItem treeItem : treeItems)
        {
            // Do not add JFace dummy items
            if (null != treeItem.getData())
            {
                list.add(treeItem);

                if (treeItem.getItemCount() > 0)
                {
                    list.addAll(Arrays.asList(getAllTreeItems(treeItem.getItems())));
                }
            }
        }

        return list.toArray(new TreeItem[list.size()]);
    }

    protected void updateFilterButtons()
    {
        if (null == filterAction)
        {
            return;
        }

        if (null == viewerInput)
        {
            filterAction.setEnabled(false);

            return;
        }

        if (viewer instanceof StructuredViewer)
        {
            StructuredViewer structuredViewer = (StructuredViewer) viewer;
            ViewerFilter[] currentViewerFilters = structuredViewer.getFilters();

            if (currentViewerFilters.length == 0 && viewerFilters.length == 0)
            {
                filterAction.setEnabled(false);
            }
            else if (currentViewerFilters.length != viewerFilters.length)
            {
                filterAction.setEnabled(true);

                if (currentViewerFilters.length > 0)
                {
                    filterAction.setChecked(true);
                }
                else
                {
                    filterAction.setChecked(false);
                }
            }
            else
            {
                filterAction.setEnabled(false);
            }
        }
        else
        {
            filterAction.setEnabled(false);
        }
    }

    protected void updateViewerFilters()
    {
        if (viewer instanceof StructuredViewer)
        {
            StructuredViewer structuredViewer = (StructuredViewer) viewer;
            ViewerFilter[] currentViewerFilters = structuredViewer.getFilters();

            if (currentViewerFilters.length > 0 && viewerFilters.length > 0)
            {
                viewerFilters = new ViewerFilter[] {};
            }

            if (viewerFiltersEnabled)
            {
                if (currentViewerFilters.length == 0 && viewerFilters.length > 0)
                {
                    structuredViewer.setFilters(viewerFilters);

                    viewerFilters = new ViewerFilter[] {};
                }
            }
            else
            {
                if (currentViewerFilters.length > 0 && viewerFilters.length == 0)
                {
                    viewerFilters = structuredViewer.getFilters();

                    structuredViewer.setFilters(new ViewerFilter[] {});
                }
            }
        }
    }

    protected void updateRefreshButtons()
    {
        if (null == refreshAction)
        {
            return;
        }

        if (null != viewer.getInput())
        {
            refreshAction.setEnabled(true);
        }
        else
        {
            refreshAction.setEnabled(false);
        }
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event)
    {
        updateSelectButtons();
    }

    @Override
    public void paintControl(PaintEvent e)
    {
        Object currentInput = viewer.getInput();

        if (viewerInput != currentInput)
        {
            viewerInput = currentInput;

            updateViewerFilters();

            Display.getDefault().asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    updateSelectButtons();
                    updateFilterButtons();
                    updateRefreshButtons();
                }
            });
        }
    }

    protected class SelectAllAction extends Action implements Runnable
    {
        public SelectAllAction()
        {

        }

        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/elcl16/expandall.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/select_all_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Select All";
        }

        @Override
        public void run()
        {
            if (viewer instanceof CheckboxTableViewer)
            {
                ((CheckboxTableViewer) viewer).setAllChecked(true);
            }
            else if (viewer instanceof CheckboxTreeViewer)
            {
                CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer) viewer;

                for (TreeItem treeItem : checkboxTreeViewer.getTree().getItems())
                {
                    // Update all child tree items too! Thus we don't call
                    // setCheck() method of tree item.
                    checkboxTreeViewer.setChecked(treeItem.getData(), true);
                }
            }

            updateSelectButtons();
        }

        @Override
        public void runWithEvent(Event event)
        {
            firePreActionRunEvent(this, SELECT_ALL_ACTION);

            BusyIndicator.showWhile(getDisplay(), this);

            firePostActionRunEvent(this, SELECT_ALL_ACTION);
        }
    }

    protected class DeselectAllAction extends Action implements Runnable
    {
        public DeselectAllAction()
        {

        }

        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/elcl16/collapseall.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/deselect_all_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Deselect All";
        }

        @Override
        public void run()
        {
            if (viewer instanceof CheckboxTableViewer)
            {
                ((CheckboxTableViewer) viewer).setAllChecked(false);
            }
            else if (viewer instanceof CheckboxTreeViewer)
            {
                ((CheckboxTreeViewer) viewer).setCheckedElements(new Object[] {});
            }

            updateSelectButtons();
        }

        @Override
        public void runWithEvent(Event event)
        {
            firePreActionRunEvent(this, DESELECT_ALL_ACTION);

            BusyIndicator.showWhile(getDisplay(), this);

            firePostActionRunEvent(this, DESELECT_ALL_ACTION);
        }
    }

    protected class FilterAction extends Action implements Runnable
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui.ide", "icons/full/elcl16/filter_ps.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/filter_disabled.gif");
        }

        @Override
        public String getText()
        {
            return filterText;
        }

        @Override
        public int getStyle()
        {
            return AS_CHECK_BOX;
        }

        @Override
        public void run()
        {
            setFilterEnabled(isChecked());

            updateViewerFilters();
        }

        @Override
        public void runWithEvent(Event event)
        {
            firePreActionRunEvent(this, FILTER_ACTION);

            BusyIndicator.showWhile(getDisplay(), this);

            firePostActionRunEvent(this, FILTER_ACTION);
        }
    }

    protected class RefreshAction extends Action implements Runnable
    {
        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin("org.eclipse.ui.browser", "icons/elcl16/nav_refresh.gif");
        }

        @Override
        public ImageDescriptor getDisabledImageDescriptor()
        {
            return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/refresh_disabled.gif");
        }

        @Override
        public String getText()
        {
            return "Refresh";
        }

        @Override
        public int getStyle()
        {
            return AS_PUSH_BUTTON;
        }

        @Override
        public void run()
        {
            updateRefreshButtons();

            if (viewer instanceof TableViewer)
            {
                ((TableViewer) viewer).refresh(true, true);
            }
            else if (viewer instanceof TreeViewer)
            {
                ((TreeViewer) viewer).refresh(true);
            }
            else
            {
                viewer.refresh();
            }
        }

        @Override
        public void runWithEvent(Event event)
        {
            firePreActionRunEvent(this, REFRESH_ACTION);

            BusyIndicator.showWhile(getDisplay(), this);

            firePostActionRunEvent(this, REFRESH_ACTION);
        }
    }

    protected void firePreActionRunEvent(IAction action, int type)
    {
        for (ActionRunListener listener : listeners)
        {
            listener.preActionRun(new ActionRunEvent(action, type));
        }
    }

    protected void firePostActionRunEvent(IAction action, int type)
    {
        for (ActionRunListener listener : listeners)
        {
            listener.postActionRun(new ActionRunEvent(action, type));
        }
    }
}
