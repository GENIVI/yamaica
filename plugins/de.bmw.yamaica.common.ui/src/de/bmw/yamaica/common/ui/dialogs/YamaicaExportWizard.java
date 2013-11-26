/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;

public abstract class YamaicaExportWizard extends YamaicaWizard implements IExportWizard
{
    protected YamaicaWizardExportPage yamaicaWizardExportPage;

    public YamaicaExportWizard(String name)
    {
        super(name);

        setWindowTitle(YamaicaUIConstants.EXPORT);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setNeedsProgressMonitor(true);
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaUIConstants.EXPORT_DIR_WIZARD_BANNER_PATH));
    }

    @Override
    public boolean performFinish()
    {
        if (null != yamaicaWizardExportPage)
        {
            return yamaicaWizardExportPage.finish();
        }

        return true;
    }
}
