/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.dialogs;

import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

public class YamaicaCheckedTreeViewer extends ContainerCheckedTreeViewer
{
    protected UpdateStrategy updateStrategy = UpdateStrategy.WHITE_SELECT_PARENTS;

    public YamaicaCheckedTreeViewer(Composite parent, int style)
    {
        super(parent, style);
    }

    public YamaicaCheckedTreeViewer(Composite parent)
    {
        super(parent);
    }

    public YamaicaCheckedTreeViewer(Tree tree)
    {
        super(tree);
    }

    public enum UpdateStrategy
    {
        WHITE_SELECT_PARENTS, GRAY_SELECT_PARENTS
    }

    public void setUpdateStrategy(UpdateStrategy updateStrategy)
    {
        this.updateStrategy = updateStrategy;
    }

    public UpdateStrategy getUpdateStrategy()
    {
        return updateStrategy;
    }

    @Override
    public void addFilter(ViewerFilter filter)
    {
        super.addFilter(filter);

        updateCheckStatesAfterAddingFilter();
    }

    @Override
    public void removeFilter(ViewerFilter filter)
    {
        super.removeFilter(filter);

        updateCheckStatesAfterRemovingFilter();
    }

    @Override
    public void setFilters(ViewerFilter[] filters)
    {
        super.setFilters(filters);

        updateCheckStatesAfterAddingFilter();
    }

    @Override
    public void resetFilters()
    {
        super.resetFilters();

        updateCheckStatesAfterRemovingFilter();
    }

    @Override
    protected void inputChanged(Object input, Object oldInput)
    {
        LinkedList<Object> checkedElements = new LinkedList<Object>();

        if (null != input && null != oldInput && input != oldInput)
        {
            for (TreeItem treeItem : getAllCheckedRootTreeItems(getTree().getItems()))
            {
                checkedElements.add(treeItem.getData());
            }
        }

        super.inputChanged(input, oldInput);

        if (checkedElements.size() > 0)
        {
            for (Object checkedElement : checkedElements)
            {
                setChecked(checkedElement, true);
            }
        }

    }

    protected TreeItem[] getAllCheckedRootTreeItems(TreeItem[] treeItems)
    {
        LinkedList<TreeItem> list = new LinkedList<TreeItem>();

        for (TreeItem treeItem : treeItems)
        {
            // Do not add JFace dummy items
            if (null != treeItem.getData() && true == treeItem.getChecked())
            {
                if (false == treeItem.getGrayed())
                {
                    list.add(treeItem);
                }
                else
                {
                    list.addAll(Arrays.asList(getAllCheckedRootTreeItems(treeItem.getItems())));
                }
            }
        }

        return list.toArray(new TreeItem[list.size()]);
    }

    public void updateCheckStatesAfterAddingFilter()
    {
        // Adding a filter means that there may be less tree items within the tree
        // but not more afterwards. A grayed tree item has at least one checked and
        // one unchecked child tree item. If a tree item was filtered out we must
        // check if there is either no checked child item anymore (=> uncheck tree
        // item) or if all child items are checked now (=> check tree item).
        for (Object grayedElement : getGrayedElements())
        {
            TreeItem[] childTreeItems = ((TreeItem) findItem(grayedElement)).getItems();
            int checkedChildTreeItems = 0;

            for (TreeItem childTreeItem : childTreeItems)
            {
                if (childTreeItem.getChecked())
                {
                    // If a child tree item is grayed his parent must be grayed too.
                    if (childTreeItem.getGrayed())
                    {
                        checkedChildTreeItems = -1;

                        break;
                    }

                    checkedChildTreeItems++;
                }
            }

            if (-1 == checkedChildTreeItems)
            {
                continue;
            }
            else if (0 == checkedChildTreeItems)
            {
                setChecked(grayedElement, false);
            }
            else if (childTreeItems.length == checkedChildTreeItems)
            {
                setChecked(grayedElement, true);
            }
        }
    }

    public void updateCheckStatesAfterRemovingFilter()
    {
        // Removing a filter means that there may be more tree items within the tree
        // but not less afterwards. If there is a new tree item inside a parent tree
        // item which is checked but not grayed (which means all children are selected
        // and not grayed too) we have to check the new tree item.
        for (Object expandedElement : getExpandedElements())
        {
            TreeItem expandedTreeItem = (TreeItem) findItem(expandedElement);

            if (expandedTreeItem.getChecked() && !expandedTreeItem.getGrayed())
            {
                for (TreeItem treeItem : expandedTreeItem.getItems())
                {
                    treeItem.setChecked(true);
                }
            }
        }
    }

    public void selectAll()
    {
        for (TreeItem treeItem : getTree().getItems())
        {
            // Update all child tree items too! Thus we don't call
            // setCheck() method of tree item.
            setChecked(treeItem.getData(), true);
        }
    }

    public void deselectAll()
    {
        setCheckedElements(new Object[0]);
    }

    @Override
    protected void doCheckStateChanged(Object element)
    {
        if (updateStrategy == UpdateStrategy.WHITE_SELECT_PARENTS) // Just use default implementation
        {
            super.doCheckStateChanged(element);
        }
        else
        // UpdateStrategy.GRAY_SELECT_PARENTS
        {
            Widget item = findItem(element);

            if (item instanceof TreeItem)
            {
                TreeItem treeItem = (TreeItem) item;

                // True if checkbox was unchecked with last click...
                boolean grayParentItems = !treeItem.getChecked();

                // Use base class functionality!
                super.doCheckStateChanged(element);

                // ...but gray select all parent checkboxes again!
                if (true == grayParentItems)
                {
                    grayParentItems(treeItem);
                }
            }
        }
    }

    protected void grayParentItems(TreeItem item)
    {
        TreeItem parentTreeItem = item.getParentItem();

        if (null != parentTreeItem)
        {
            parentTreeItem.setChecked(true);
            parentTreeItem.setGrayed(true);

            grayParentItems(parentTreeItem);
        }
    }
}
