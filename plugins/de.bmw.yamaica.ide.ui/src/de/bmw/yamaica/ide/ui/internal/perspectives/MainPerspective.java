/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * This class is needed by the org.eclipse.ui.perspectives extension point (see plugin.xml).
 */
public class MainPerspective implements IPerspectiveFactory
{
    @Override
    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);
        layout.addNewWizardShortcut("de.bmw.yamaica.ui.projectWizard");
    }
}
