/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.common.ui.dialogs.YamaicaWizardExportPage;
import de.bmw.yamaica.franca.base.core.YamaicaFrancaConstants;

public class HtmlExportPage extends YamaicaWizardExportPage
{
    private static final String EXPORT_FRANCA_INTERFACE_DOCUMENTATION_FILES_HTML = "Export Franca interface documentation files (HTML).";
    private static final String FRANCA_INTERFACE_DOCUMENTATION_FILES_HTML_EXPORTER = "Franca Interface Documentation Files (HTML) Exporter";
    private static final String FRANCA_INTERFACE_DOCUMENTATION_EXPORTER = "Franca Interface Documentation Exporter";

    public HtmlExportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, FRANCA_INTERFACE_DOCUMENTATION_EXPORTER);

        setTitle(FRANCA_INTERFACE_DOCUMENTATION_FILES_HTML_EXPORTER);
        setDescription(EXPORT_FRANCA_INTERFACE_DOCUMENTATION_FILES_HTML);
    }

    @Override
    protected IRunnableWithProgress getExporter()
    {
        List<IResource> resourcesToExport = getSelectedResources();

        HtmlExportOperation exportOperation = new HtmlExportOperation(new Path(getDestinationValue()), getSourceContainer(), this,
                resourcesToExport);

        return exportOperation;
    }

    @Override
    protected void createOptionsGroup(Composite parent)
    {
        // Override to create nothing since HtmlExportOperation does not support options
    }

    @Override
    protected String[] getFileExtensions()
    {
        return new String[] { YamaicaFrancaConstants.FIDL };
    }
}
