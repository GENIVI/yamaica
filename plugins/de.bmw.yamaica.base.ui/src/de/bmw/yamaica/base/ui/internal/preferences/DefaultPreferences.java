/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.bmw.yamaica.base.core.YamaicaConstants;
import de.bmw.yamaica.base.ui.Preferences;
import de.bmw.yamaica.base.ui.internal.Activator;

/**
 * This class is needed by the org.eclipse.core.runtime.preferences extension point
 * (see plugin.xml). This class defines the default preferences for the preference
 * page of this plug-in (see Preferences.java).
 */
public class DefaultPreferences extends AbstractPreferenceInitializer
{
    public DefaultPreferences()
    {

    }

    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(Preferences.CONSOLE_BUFFER_SIZE, 80000);
        store.setDefault(Preferences.CONSOLE_TRIGGER_SIZE, store.getInt(Preferences.CONSOLE_BUFFER_SIZE) + 10000);
        store.setDefault(Preferences.LIMIT_CONSOLE_OUTPUT, true);
        store.setDefault(Preferences.REDIRECT_SYSTEM_STREAMS, true);
        store.setDefault(Preferences.IMPORT_FOLDER, YamaicaConstants.WORK);
        store.setDefault(Preferences.TARGET_FOLDER, YamaicaConstants.GEN);
    }
}
