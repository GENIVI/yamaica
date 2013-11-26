/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.utils.ResourceContentProvider;

public class YamaicaResourceSelectionDialog extends SelectionDialog
{
    private static final String EMPTY_STRING               = "";                                                     //$NON-NLS-1$
    private static final String RESOURCE_SELECTION_MESSAGE = "Enter or select the parent folder:";
    private static final String HELP_SYSTEM_CONTEXT_ID     = "org.eclipse.ui.ide.container_selection_dialog_context";

    private IContainer          rootContainer;
    private IContainer          initialSelection;
    private boolean             allowNewContainerName      = true;
    private boolean             showClosedProjects         = true;
    private Label               statusMessage;
    private ISelectionValidator validator;
    private Text                containerNameField;
    private TreeViewer          treeViewer;

    public YamaicaResourceSelectionDialog(Shell parentShell, IContainer rootContainer, IContainer initialSelection,
            boolean allowNewContainerName, String message)
    {
        super(parentShell);

        setTitle(YamaicaUIConstants.FOLDER_SELECTION);

        this.initialSelection = initialSelection;
        this.rootContainer = null != rootContainer ? rootContainer : ResourcesPlugin.getWorkspace().getRoot();
        this.allowNewContainerName = allowNewContainerName;

        if (message != null)
        {
            setMessage(message);
        }
        else
        {
            setMessage(RESOURCE_SELECTION_MESSAGE);
        }

        setShellStyle(getShellStyle() | SWT.SHEET);
    }

    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, HELP_SYSTEM_CONTEXT_ID);
    }

    protected Control createDialogArea(Composite parent)
    {
        // create composite
        Composite area = (Composite) super.createDialogArea(parent);

        final Listener listener = new Listener()
        {
            public void handleEvent(Event event)
            {
                if (statusMessage != null && validator != null)
                {
                    String errorMsg = validator.isValid(getContainerFullPath());

                    if (errorMsg == null || errorMsg.equals(EMPTY_STRING))
                    {
                        statusMessage.setText(EMPTY_STRING);
                        getOkButton().setEnabled(true);
                    }
                    else
                    {
                        statusMessage.setText(errorMsg);
                        getOkButton().setEnabled(false);
                    }
                }
            }
        };

        Label label = new Label(area, SWT.WRAP);
        label.setText(getMessage());
        label.setFont(area.getFont());

        if (allowNewContainerName)
        {
            containerNameField = new Text(area, SWT.SINGLE | SWT.BORDER);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.widthHint = 320;
            containerNameField.setLayoutData(gd);
            containerNameField.addListener(SWT.Modify, listener);
            containerNameField.setFont(area.getFont());
        }
        else
        {
            // filler...
            new Label(area, SWT.NONE);
        }

        DrillDownComposite drillDown = new DrillDownComposite(area, SWT.BORDER);
        GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
        spec.widthHint = 320;
        spec.heightHint = 300;
        drillDown.setLayoutData(spec);

        // Create tree viewer inside drill down.
        treeViewer = new TreeViewer(drillDown, SWT.NONE);
        drillDown.setChildTree(treeViewer);
        ResourceContentProvider cp = new ResourceContentProvider();
        cp.showClosedProjects(showClosedProjects);
        treeViewer.setContentProvider(cp);
        treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        treeViewer.setComparator(new ViewerComparator());
        treeViewer.setUseHashlookup(true);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                IContainer container = (IContainer) selection.getFirstElement(); // allow null

                if (null != containerNameField)
                {
                    if (null == container)
                    {
                        containerNameField.setText(EMPTY_STRING);//$NON-NLS-1$
                    }
                    else
                    {
                        String text = TextProcessor.process(container.getFullPath().makeRelativeTo(rootContainer.getFullPath()).toString());
                        containerNameField.setText(text);
                        containerNameField.setToolTipText(text);
                    }
                }

                // fire an event so the parent can update its controls
                if (listener != null)
                {
                    Event changeEvent = new Event();
                    changeEvent.type = SWT.Selection;
                    changeEvent.widget = treeViewer.getControl();
                    listener.handleEvent(changeEvent);
                }
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                ISelection selection = event.getSelection();

                if (selection instanceof IStructuredSelection)
                {
                    Object item = ((IStructuredSelection) selection).getFirstElement();

                    if (item == null)
                    {
                        return;
                    }

                    if (treeViewer.getExpandedState(item))
                    {
                        treeViewer.collapseToLevel(item, 1);
                    }
                    else
                    {
                        treeViewer.expandToLevel(item, 1);
                    }
                }
            }
        });

        treeViewer.setInput(rootContainer);

        // container selection group
        // group = new ContainerSelectionGroup(area, listener, allowNewContainerName, getMessage(), showClosedProjects);

        if (initialSelection != null)
        {
            // group.setSelectedContainer(initialSelection);
            // expand to and select the specified container
            List<IContainer> itemsToExpand = new ArrayList<IContainer>();
            IContainer parentContainer = initialSelection.getParent();

            while (parentContainer != null)
            {
                itemsToExpand.add(0, parentContainer);
                parentContainer = parentContainer.getParent();
            }

            treeViewer.setExpandedElements(itemsToExpand.toArray());
            treeViewer.setSelection(new StructuredSelection(initialSelection), true);
        }

        statusMessage = new Label(area, SWT.WRAP);
        statusMessage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusMessage.setText(" \n "); //$NON-NLS-1$
        statusMessage.setFont(parent.getFont());

        return dialogArea;
    }

    protected void okPressed()
    {
        List<IPath> chosenContainerPathList = new ArrayList<IPath>();
        IPath returnValue = getContainerFullPath();

        if (null != returnValue)
        {
            chosenContainerPathList.add(returnValue);
        }

        setResult(chosenContainerPathList);

        super.okPressed();
    }

    public void setValidator(ISelectionValidator validator)
    {
        this.validator = validator;
    }

    public void showClosedProjects(boolean show)
    {
        this.showClosedProjects = show;
    }

    private IPath getContainerFullPath()
    {
        if (null != containerNameField)
        {
            String pathName = containerNameField.getText();

            if (null == pathName || pathName.length() == 0)
            {
                return null;
            }
            // The user may not have made this absolute so do it for them
            return (new Path(TextProcessor.deprocess(pathName))).makeAbsolute();
        }

        if (null != initialSelection)
        {
            return initialSelection.getFullPath();
        }

        return null;
    }
}
