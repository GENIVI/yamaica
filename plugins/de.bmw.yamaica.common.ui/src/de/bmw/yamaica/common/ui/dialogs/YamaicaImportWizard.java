/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;

public abstract class YamaicaImportWizard extends YamaicaWizard implements IImportWizard
{
    protected YamaicaWizardImportPage yamaicaWizardImportPage;

    public YamaicaImportWizard(String name)
    {
        super(name);

        setWindowTitle(YamaicaUIConstants.IMPORT);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setNeedsProgressMonitor(true);
        // setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/importdir_wiz.png"));
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaUIConstants.IMPORT_DIR_WIZARD_BANNER_PATH));

    }

    @Override
    public boolean performFinish()
    {
        if (null != yamaicaWizardImportPage)
        {
            return yamaicaWizardImportPage.finish();
        }

        return true;
    }
}
