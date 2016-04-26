package de.bmw.yamaica.common.ui.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class WizardSelector
{
    public static boolean isEnabledForSelection(IConfigurationElement configurationElement, ISelection selection)
    {
        String extensions = configurationElement.getAttribute("extensions");
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
}
