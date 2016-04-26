/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import de.bmw.yamaica.common.ui.dialogs.YamaicaExportWizard;

public class HtmlExportWizard extends YamaicaExportWizard
{
    private static final String EXPORT_FRANCA_INTERFACE_DOCUMENTATION_FILES = "Export Franca Interface Documentation Files";
    private static final String YAMAICA_FRANCA_HTML_EXPORT_WIZARD           = "YamaicaFrancaHtmlExportWizard";

    public HtmlExportWizard()
    {
        super(YAMAICA_FRANCA_HTML_EXPORT_WIZARD);

        setWindowTitle(EXPORT_FRANCA_INTERFACE_DOCUMENTATION_FILES);
    }

    @Override
    public void addPages()
    {
        yamaicaWizardExportPage = new HtmlExportPage(workbench, structuredSelection);

        addPage(yamaicaWizardExportPage);
    }
}
