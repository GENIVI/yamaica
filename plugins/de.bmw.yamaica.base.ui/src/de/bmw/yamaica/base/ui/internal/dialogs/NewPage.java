/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.ui.internal.Activator;

import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

public class NewPage extends YamaicaWizardSelectionPage
{
    NewPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, "yamaicaNewPage");

        setTitle("Select");
        // setMessage("Choose import source.");
    }

    @Override
    protected Object getViewerInput()
    {
        LinkedList<IWizardDescriptor> yamaicaWizards = new LinkedList<IWizardDescriptor>();
        IWizardRegistry wizardRegistry = PlatformUI.getWorkbench().getNewWizardRegistry();

        for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(
                Activator.PLUGIN_ID + ".yamaicaNewWizards"))
        {
            IWizardDescriptor descriptor = wizardRegistry.findWizard(e.getAttribute("newWizardId"));

            if (null != descriptor && !yamaicaWizards.contains(descriptor))
            {
                yamaicaWizards.add(descriptor);
            }
        }

        return yamaicaWizards;
    }

    @Override
    public void createControl(Composite parent)
    {
        super.createControl(parent);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".yamaica_new_page_selection_dialog");
    }

}
