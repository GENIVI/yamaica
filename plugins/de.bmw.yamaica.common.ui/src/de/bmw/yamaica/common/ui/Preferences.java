package de.bmw.yamaica.common.ui;

import org.eclipse.jface.preference.IPreferenceStore;

import de.bmw.yamaica.common.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.common.ui.internal.Activator;

public class Preferences
{
    public static final String CONSOLE_BUFFER_SIZE     = "CONSOLE_BUFFER_SIZE";
    public static final String CONSOLE_TRIGGER_SIZE    = "CONSOLE_TRIGGER_SIZE";
    public static final String LIMIT_CONSOLE_OUTPUT    = "LIMIT_CONSOLE_OUTPUT";
    public static final String REDIRECT_SYSTEM_STREAMS = "REDIRECT_SYSTEM_STREAMS";
    public static final String IMPORT_FOLDER           = IResourcePropertyStore.IMPORT_FOLDER;
    public static final String TARGET_FOLDER           = IResourcePropertyStore.TARGET_FOLDER;

    // Enable others to access the internal preference store
    public static IPreferenceStore getPreferenceStore()
    {
        return Activator.getDefault().getPreferenceStore();
    }
}
