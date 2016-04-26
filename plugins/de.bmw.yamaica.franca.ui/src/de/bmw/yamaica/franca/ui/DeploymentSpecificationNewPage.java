/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.franca.deploymodel.dsl.fDeploy.FDPropertyHost;

import de.bmw.yamaica.common.ui.dialogs.YamaicaWizardNewFilePage;
import de.bmw.yamaica.common.ui.utils.ViewerToolBar;
import de.bmw.yamaica.franca.common.core.FrancaUtils;

public class DeploymentSpecificationNewPage extends YamaicaWizardNewFilePage
{
    private static final String   CREATE_A_NEW_FRANCA_DEPLOYMENT_SPECIFICATION_FILE = "Create a new Franca deployment specification file.";
    private static final String   FRANCA_DEPLOYMENT_SPECIFICATION                   = "Franca Deployment Specification";
    private static final String   NEW_FRANCA_DEPLOYMENT_SPECIFICATION               = "New Franca Deployment Specification";
    private static final String   NA_ME                                             = "Na&me:";
    private static final String   PROPERTY_HOSTS                                    = "&Property hosts:";
    private static final String   INVALID_SPECIFICATION_NAME                        = "Invalid specification name. ";
    protected Text                nameText                                          = null;
    protected boolean             nameTextWasModified                               = false;
    protected CheckboxTableViewer propertyHostsTableViewer                          = null;

    public DeploymentSpecificationNewPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, NEW_FRANCA_DEPLOYMENT_SPECIFICATION);

        setTitle(FRANCA_DEPLOYMENT_SPECIFICATION);
        setDescription(CREATE_A_NEW_FRANCA_DEPLOYMENT_SPECIFICATION_FILE);
    }

    @Override
    protected String[] getFileExtensions()
    {
        return new String[] { FrancaUtils.DEPLOYMENT_SPECIFICATION_FILE_EXTENSION };
    }

    @Override
    protected void extendFilenameGroup(Composite parent)
    {
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Font font = parent.getFont();

        Label packageLabel = new Label(parent, SWT.LEAD);
        packageLabel.setText(NA_ME);
        packageLabel.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
        packageLabel.setFont(font);

        nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        nameText.addListener(SWT.Modify, this);
        nameText.setFont(font);

        Label propertyHostsLabel = new Label(parent, SWT.LEAD);
        propertyHostsLabel.setText(PROPERTY_HOSTS);
        propertyHostsLabel.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false));
        propertyHostsLabel.setFont(font);

        GridData propertyHostsTableGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        propertyHostsTableGridData.heightHint = 140;

        ViewerToolBar viewerToolBar = new ViewerToolBar(parent, SWT.BORDER, ViewerToolBar.SELECT);
        viewerToolBar.setLayoutData(propertyHostsTableGridData);

        Table propertyHostsTable = new Table(viewerToolBar, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
        propertyHostsTable.setFont(font);
        propertyHostsTable.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                Table table = (Table) e.widget;
                table.getColumn(0).setWidth(table.getClientArea().width);
            }
        });

        propertyHostsTableViewer = new CheckboxTableViewer(propertyHostsTable);
        propertyHostsTableViewer.setContentProvider(new ArrayContentProvider());

        TableViewerColumn propertyHostNameViewerColumn = new TableViewerColumn(propertyHostsTableViewer, SWT.LEAD);
        propertyHostNameViewerColumn.setLabelProvider(new DeploymentSpecificationLabelProvider());

        viewerToolBar.setViewer(propertyHostsTableViewer);

        propertyHostsTableViewer.setInput(FDPropertyHost.VALUES);
    }

    @Override
    protected boolean validateExtensions()
    {
        if (null != nameText)
        {
            String name = nameText.getText();

            if (name.length() == 0)
            {
                setErrorMessage(INVALID_SPECIFICATION_NAME);

                return false;
            }

            try
            {
                FrancaUtils.normalizeNamespaceString(nameText.getText(), FrancaUtils.NONE, FrancaUtils.NONE);
            }
            catch (IllegalArgumentException e)
            {
                setErrorMessage(INVALID_SPECIFICATION_NAME + e.getMessage());

                return false;
            }
        }

        return true;
    }

    @Override
    public void handleEvent(Event event)
    {
        if (event.widget == filenameText)
        {
            if (!nameTextWasModified)
            {
                IPath containerPath = getContainerFullPath();
                IPath filePath = getFileFullPath();

                if (null != containerPath && null != filePath && validateFilenameGroup())
                {
                    String filenameWithoutFileExtension = filePath.makeRelativeTo(containerPath).removeFileExtension().toString();

                    nameText.setText(FrancaUtils.normalizeName(filenameWithoutFileExtension, FrancaUtils.ALL_FOR_DEPLOYMENT_DEFINITIONS,
                            FrancaUtils.ALL));
                }
                else
                {
                    nameText.setText("");
                }
            }

            updateWidgetEnablements();
        }
        else if (event.widget == nameText)
        {
            if (nameText.getText().length() > 0 && nameText.isFocusControl())
            {
                nameTextWasModified = true;
            }
            else
            {
                nameTextWasModified = false;
            }

            updateWidgetEnablements();
        }
        else
        {
            super.handleEvent(event);
        }
    }

    @Override
    protected void createOptionsGroup(Composite parent)
    {

    }

    @Override
    protected IRunnableWithProgress getFileCreator()
    {
        String name = nameText.getText();
        Object[] checkedElements = propertyHostsTableViewer.getCheckedElements();
        List<FDPropertyHost> propertyHosts = new ArrayList<FDPropertyHost>(checkedElements.length);

        for (Object checkedElement : checkedElements)
        {
            propertyHosts.add((FDPropertyHost) checkedElement);
        }

        return new DeploymentDescriptionCreationOperation(getSpecifiedFile(), name, propertyHosts);
    }
}
