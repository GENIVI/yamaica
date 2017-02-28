/* Copyright (C) 2013-2016 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.wizards.IWizardDescriptor;

import de.bmw.yamaica.common.ui.internal.Activator;

public class WizardSelector
{
    private static final String ECLIPSE_WIZARD_EXPORT_FILE_SYSTEM = "org.eclipse.ui.wizards.export.FileSystem";
    private static final String ECLIPSE_WIZARD_IMPORT_FILE_SYSTEM = "org.eclipse.ui.wizards.import.FileSystem";
    private static final String YAMAICA_WIZARD_CATEGORY_FILE_SYSTEM = "fileSystem";
    private static final String YAMAICA_WIZARD_INPUT_FILE_EXTENSIONS = "extensions";

    public static boolean isEnabledForSelection(IConfigurationElement configurationElement, ISelection selection)
    {
        String extensions = configurationElement.getAttribute(YAMAICA_WIZARD_INPUT_FILE_EXTENSIONS);
        if (extensions != null)
        {
            String[] supportedFileExtensions = extensions.split(",");
            return isEnabledForSelection(supportedFileExtensions, selection);
        }

        // If there is no 'extensions' specification available at all, we treat it as "all files are supported"
        return true;
    }

    public static boolean isEnabledForSelection(String[] supportedFileExtensions, ISelection selection)
    {
        if (supportedFileExtensions.length > 0)
        {
            if (selection instanceof IStructuredSelection)
            {
                IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                for (Object sel : structuredSelection.toList())
                {
                    if (sel instanceof IResource)
                    {
                        IResource res = (IResource) sel;
                        String fileExtension = res.getFileExtension();
                        if (fileExtension != null)
                        {
                            for (String supportedFileExtension : supportedFileExtensions)
                            {
                                if (supportedFileExtension.trim().equals(fileExtension))
                                    return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isFileSystemWizard(IConfigurationElement configurationElement)
    {
        String wizardId = configurationElement.getAttribute("id");
        if (wizardId  != null)
        {
            if (isGenericFileSystemWizard(wizardId))
                return true;

            for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID + ".yamaicaImportWizards"))
            {
                if (wizardId.equals(e.getAttribute("importWizardId")))
                    return Boolean.valueOf(e.getAttribute(YAMAICA_WIZARD_CATEGORY_FILE_SYSTEM));
            }

            for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID + ".yamaicaExportWizards"))
            {
                if (wizardId.equals(e.getAttribute("exportWizardId")))
                    return Boolean.valueOf(e.getAttribute(YAMAICA_WIZARD_CATEGORY_FILE_SYSTEM));
            }
        }
        return false;
    }

    public static boolean isGenericFileSystemWizard(IWizardDescriptor wizard)
    {
        return isGenericFileSystemWizard(wizard.getId());
    }

    public static boolean isGenericFileSystemWizard(String wizardId)
    {
        if (wizardId.equals(ECLIPSE_WIZARD_IMPORT_FILE_SYSTEM))
            return true;
        if (wizardId.equals(ECLIPSE_WIZARD_EXPORT_FILE_SYSTEM))
            return true;
        return false;
    }
}
