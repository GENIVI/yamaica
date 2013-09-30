/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.base.ui.dialogs.YamaicaWizardNewFilePage;
import de.bmw.yamaica.franca.base.core.FrancaUtils;

public class DeploymentDefinitionNewPage extends YamaicaWizardNewFilePage
{
    public DeploymentDefinitionNewPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, "New Franca Deployment Definition");

        setTitle("Franca Deployment Definition");
        setDescription("Create a new Franca deployment definition file.");
    }

    @Override
    protected String[] getFileExtensions()
    {
        return new String[] { FrancaUtils.DEPLOYMENT_DEFINITION_FILE_EXTENSION };
    }

    @Override
    protected void createOptionsGroup(Composite parent)
    {

    }

    @Override
    protected IRunnableWithProgress getFileCreator()
    {
        return new DeploymentDefinitionCreationOperation(getSpecifiedFile());
    }
}
