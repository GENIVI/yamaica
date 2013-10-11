/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import de.bmw.yamaica.base.ui.dialogs.YamaicaExportWizard;

public class ExportWizard extends YamaicaExportWizard
{
    private static final String EXPORT_FRANCA_IDL_FILES      = "Export Franca IDL Files";
    private static final String YAMAICA_FRANCA_EXPORT_WIZARD = "YamaicaFrancaExportWizard";

    public ExportWizard()
    {
        super(YAMAICA_FRANCA_EXPORT_WIZARD);

        setWindowTitle(EXPORT_FRANCA_IDL_FILES);
    }

    @Override
    public void addPages()
    {
        yamaicaWizardExportPage = new ExportPage(workbench, structuredSelection);

        addPage(yamaicaWizardExportPage);
    }
}
