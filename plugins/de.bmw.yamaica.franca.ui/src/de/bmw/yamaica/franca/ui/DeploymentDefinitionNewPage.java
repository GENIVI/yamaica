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

import de.bmw.yamaica.common.ui.dialogs.YamaicaWizardNewFilePage;
import de.bmw.yamaica.franca.common.core.FrancaUtils;

public class DeploymentDefinitionNewPage extends YamaicaWizardNewFilePage
{
    private static final String CREATE_A_NEW_FRANCA_DEPLOYMENT_DEFINITION_FILE = "Create a new Franca deployment definition file.";
    private static final String FRANCA_DEPLOYMENT_DEFINITION = "Franca Deployment Definition";
    private static final String NEW_FRANCA_DEPLOYMENT_DEFINITION = "New Franca Deployment Definition";

    public DeploymentDefinitionNewPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, NEW_FRANCA_DEPLOYMENT_DEFINITION);

        setTitle(FRANCA_DEPLOYMENT_DEFINITION);
        setDescription(CREATE_A_NEW_FRANCA_DEPLOYMENT_DEFINITION_FILE);
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
