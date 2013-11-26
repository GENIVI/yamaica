/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import de.bmw.yamaica.common.ui.dialogs.YamaicaImportWizard;

public class ImportWizard extends YamaicaImportWizard
{
    private static final String IMPORT_FRANCA_IDL_FILES = "Import Franca IDL Files";
    private static final String YAMAICA_FRANCA_IMPORT_WIZARD = "YamaicaFrancaImportWizard";

    public ImportWizard()
    {
        super(YAMAICA_FRANCA_IMPORT_WIZARD);

        setWindowTitle(IMPORT_FRANCA_IDL_FILES);
    }

    @Override
    public void addPages()
    {
        yamaicaWizardImportPage = new ImportPage(workbench, structuredSelection);

        addPage(yamaicaWizardImportPage);
    }
}
