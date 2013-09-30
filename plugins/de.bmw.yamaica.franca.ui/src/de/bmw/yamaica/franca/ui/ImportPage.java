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

import de.bmw.yamaica.base.ui.dialogs.YamaicaWizardImportPage;

public class ImportPage extends YamaicaWizardImportPage
{
    public ImportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, "Franca IDL Importer");

        setTitle("Franca IDL Importer");
        setDescription("Import Franca IDL files.");
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
        return new String[] { "fidl", "fdepl" };
    }
}
