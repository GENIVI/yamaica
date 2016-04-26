/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
