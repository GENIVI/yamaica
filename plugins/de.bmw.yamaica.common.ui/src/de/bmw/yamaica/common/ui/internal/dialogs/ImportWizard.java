/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.dialogs.YamaicaWizard;
import de.bmw.yamaica.common.ui.internal.Activator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ImportWizard extends YamaicaWizard
{
    private static final String YAMAICA_IMPORT_SELECTION_WIZARD = "YamaicaImportSelectionWizard";

    public ImportWizard()
    {
        super(YAMAICA_IMPORT_SELECTION_WIZARD);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setWindowTitle(YamaicaUIConstants.IMPORT);
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID,
                YamaicaUIConstants.IMPORT_WIZARD_BANNER_PATH));
        setForcePreviousAndNextButtons(true);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages()
    {
        addPage(new ImportPage(workbench, structuredSelection));
    }
}
