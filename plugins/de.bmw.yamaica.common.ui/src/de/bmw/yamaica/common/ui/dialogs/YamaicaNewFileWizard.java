/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.dialogs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;

public abstract class YamaicaNewFileWizard extends YamaicaWizard implements INewWizard
{
    protected YamaicaWizardNewFilePage yamaicaWizardNewFilePage;

    public YamaicaNewFileWizard(String name)
    {
        super(name);

        setWindowTitle(YamaicaUIConstants.NEW);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        // setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/newfile_wiz.png"));
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                YamaicaUIConstants.NEW_FILE_WIZARD_BANNER_PATH));

    }

    @Override
    public boolean performFinish()
    {
        if (null != yamaicaWizardNewFilePage)
        {
            return yamaicaWizardNewFilePage.finish();
        }

        return true;
    }
}
