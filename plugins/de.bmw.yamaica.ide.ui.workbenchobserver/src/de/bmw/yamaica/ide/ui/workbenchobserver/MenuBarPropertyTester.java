/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.workbenchobserver;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.bmw.yamaica.ide.ui.internal.workbenchobserver.MenuBarHandler;

public class MenuBarPropertyTester extends PropertyTester
{
    public static final String PROPERTY_NAME = "menuBarEnabled";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (PROPERTY_NAME.equals(property) && expectedValue instanceof Boolean)
        {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            return MenuBarHandler.getInstance().shouldMenuBarBeVisibile(window) == (Boolean) expectedValue;
        }

        return false;
    }
}
