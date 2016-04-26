/* Copyright (C) 2013-2015 BMW Group
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

import de.bmw.yamaica.common.ui.dialogs.YamaicaWizardExportPage;
import de.bmw.yamaica.common.ui.utils.FileSystemExportOperation;
import de.bmw.yamaica.franca.common.core.YamaicaFrancaConstants;

public class ExportPage extends YamaicaWizardExportPage
{
    private static final String EXPORT_FRANCA_IDL_FILES = "Export Franca IDL files.";
    private static final String FRANCA_IDL_EXPORTER = "Franca IDL Exporter";

    public ExportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, FRANCA_IDL_EXPORTER);

        setTitle(FRANCA_IDL_EXPORTER);
        setDescription(EXPORT_FRANCA_IDL_FILES);
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
        return new String[] { YamaicaFrancaConstants.FIDL, YamaicaFrancaConstants.FDEPL };
    }
}
