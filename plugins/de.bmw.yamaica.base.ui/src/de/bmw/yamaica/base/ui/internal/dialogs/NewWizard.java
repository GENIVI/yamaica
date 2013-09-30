/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.ui.dialogs.YamaicaWizard;
import de.bmw.yamaica.base.ui.internal.Activator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class NewWizard extends YamaicaWizard
{
    public NewWizard()
    {
        super("YamaicaNewSelectionWizard");
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super.init(workbench, structuredSelection);

        setWindowTitle("New");
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin("org.eclipse.ui", "/icons/full/wizban/new_wiz.png"));
        setForcePreviousAndNextButtons(true);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages()
    {
        addPage(new NewPage(workbench, structuredSelection));
    }
}
