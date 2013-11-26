/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.io.File;
import java.util.List;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import de.bmw.yamaica.common.ui.dialogs.YamaicaWizardImportPage;
import de.bmw.yamaica.franca.common.core.YamaicaFrancaConstants;

public class ImportPage extends YamaicaWizardImportPage
{
    private static final String IMPORT_FRANCA_IDL_FILES = "Import Franca IDL files.";
    private static final String FRANCA_IDL_IMPORTER = "Franca IDL Importer";

    public ImportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, FRANCA_IDL_IMPORTER);

        setTitle(FRANCA_IDL_IMPORTER);
        setDescription(IMPORT_FRANCA_IDL_FILES);
    }

    @Override
    protected IRunnableWithProgress getImporter()
    {
        List<File> filesToImport = getSelectedResources();

        ImportOperation importOperation = new ImportOperation(getContainerFullPath(), sourceDirectory,
                FileSystemStructureProvider.INSTANCE, this, filesToImport);

        if (null != overwriteExistingResourcesCheckbox)
        {
            importOperation.setCreateContainerStructure(false);
            importOperation.setOverwriteResources(overwriteExistingResourcesCheckbox.getSelection());
        }

        return importOperation;
    }

    @Override
    protected String[] getFileExtensions()
    {
        return new String[] { YamaicaFrancaConstants.FIDL, YamaicaFrancaConstants.FDEPL };
    }
}
