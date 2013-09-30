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
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.base.ui.dialogs.YamaicaWizardExportPage;
import de.bmw.yamaica.base.ui.utils.FileSystemExportOperation;

public class ExportPage extends YamaicaWizardExportPage
{
    public ExportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, "Franca IDL Exporter");

        setTitle("Franca IDL Exporter");
        setDescription("Export Franca IDL files.");
    }

    @Override
    protected IRunnableWithProgress getExporter()
    {
        List<IResource> resourcesToExport = getSelectedResources();

        FileSystemExportOperation exportOperation = new FileSystemExportOperation(new Path(getDestinationValue()), getSourceContainer(),
                this, resourcesToExport);

        if (null != overwriteExistingFilesCheckbox)
        {
            exportOperation.setOverwriteFiles(overwriteExistingFilesCheckbox.getSelection());
        }

        return exportOperation;
    }

    @Override
    protected String[] getFileExtensions()
    {
        return new String[] { "fidl", "fdepl" };
    }
}
