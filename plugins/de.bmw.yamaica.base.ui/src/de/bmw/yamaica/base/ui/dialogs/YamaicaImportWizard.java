/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.dialogs;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.bmw.yamaica.base.ui.internal.Activator;

public abstract class YamaicaImportWizard extends YamaicaWizard implements IImportWizard
{
    protected YamaicaWizardImportPage yamaicaWizardImportPage;

    public YamaicaImportWizard(String name)
    {
        super(name);

        setWindowTitle("Import");
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setNeedsProgressMonitor(true);
        // setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/importdir_wiz.png"));
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin("org.eclipse.ui.ide", "icons/full/wizban/importdir_wiz.png"));

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
