/* Copyright (C) 2013-2016 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.internal.dialogs;

import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;
import de.bmw.yamaica.common.ui.utils.WizardSelector;

public class ExportPage extends YamaicaWizardSelectionPage
{
    private static final String YAMAICA_EXPORT_SELECTION_DIALOG = ".yamaica_export_selection_dialog";
    private static final String EXPORT_WIZARD_ID                = "exportWizardId";
    private static final String CHOOSE_EXPORT_DESTINATION       = "Choose export destination.";
    private static final String YAMAICA_EXPORT_PAGE             = "yamaicaExportPage";
    private static final String YAMAICA_EXPORT_WIZARDS          = ".yamaicaExportWizards";
    private IStructuredSelection selection;

    ExportPage(IWorkbench workbench, IStructuredSelection structuredSelection)
    {
        super(workbench, structuredSelection, YAMAICA_EXPORT_PAGE, true);

        this.selection = structuredSelection;
        setTitle(YamaicaUIConstants.SELECT);
        setMessage(CHOOSE_EXPORT_DESTINATION);
    }

    @Override
    protected Object getViewerInput()
    {
        LinkedList<IWizardDescriptor> yamaicaWizards = new LinkedList<IWizardDescriptor>();
        IWizardRegistry wizardRegistry = PlatformUI.getWorkbench().getExportWizardRegistry();

        for (IConfigurationElement configurationElement : Platform.getExtensionRegistry().getConfigurationElementsFor(
                Activator.PLUGIN_ID + YAMAICA_EXPORT_WIZARDS))
        {
            IWizardDescriptor descriptor = wizardRegistry.findWizard(configurationElement.getAttribute(EXPORT_WIZARD_ID));

            if (null != descriptor && !yamaicaWizards.contains(descriptor) && WizardSelector.isEnabledForSelection(configurationElement, selection))
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

        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + YAMAICA_EXPORT_SELECTION_DIALOG);
    }
}
