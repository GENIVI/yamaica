/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.dialogs;

import de.bmw.yamaica.base.ui.YamaicaUIConstants;
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

public class ImportPage extends YamaicaWizardSelectionPage
{
    private static final String YAMAICA_IMPORT_SELECTION_DIALOG = ".yamaica_import_selection_dialog";
    private static final String IMPORT_WIZARD_ID                = "importWizardId";
    private static final String YAMAICA_IMPORT_WIZARDS          = ".yamaicaImportWizards";
    private static final String CHOOSE_IMPORT_SOURCE            = "Choose import source.";
    private static final String YAMAICA_IMPORT_PAGE             = "yamaicaImportPage";

    ImportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, YAMAICA_IMPORT_PAGE);

        setTitle(YamaicaUIConstants.SELECT);
        setMessage(CHOOSE_IMPORT_SOURCE);
    }

    @Override
    protected Object getViewerInput()
    {
        LinkedList<IWizardDescriptor> yamaicaWizards = new LinkedList<IWizardDescriptor>();
        IWizardRegistry wizardRegistry = PlatformUI.getWorkbench().getImportWizardRegistry();

        for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(
                Activator.PLUGIN_ID + YAMAICA_IMPORT_WIZARDS))
        {
            IWizardDescriptor descriptor = wizardRegistry.findWizard(e.getAttribute(IMPORT_WIZARD_ID));

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

        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + YAMAICA_IMPORT_SELECTION_DIALOG);
    }

}
