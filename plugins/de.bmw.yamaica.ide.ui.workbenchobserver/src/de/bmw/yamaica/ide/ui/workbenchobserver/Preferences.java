/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.workbenchobserver;

import org.eclipse.jface.preference.IPreferenceStore;

import de.bmw.yamaica.ide.ui.internal.workbenchobserver.Activator;

public class Preferences
{
    public static final String MENU_BAR_HIDING_RULE = "MENU_BAR_HIDING_RULE";
    public static final String ALWAYS_HIDE          = "ALWAYS_HIDE";
    public static final String NEVER_HIDE           = "NEVER_HIDE";
    public static final String YAMAICA_HIDE         = "YAMAICA_HIDE";

    // Enable others to access the internal preference store
    public static IPreferenceStore getPreferenceStore()
    {
        return Activator.getDefault().getPreferenceStore();
    }
}
