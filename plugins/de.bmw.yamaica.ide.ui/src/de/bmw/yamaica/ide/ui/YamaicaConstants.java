/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;

public interface YamaicaConstants extends YamaicaUIConstants
{
    // Labels
    String YAMAICA_EDITOR_TITLE       = "Import • Edit / Transform / Generate • Export";
    String IMPORTED_FILES_TITLE       = "Imported Files";
    String IMPORTED_FILES_DESCRIPTION = "Overview over all imported files.";
    String TARGET_FILES_TITLE         = "Generated Files";
    String TARGET_FILES_DESCRIPTION   = "Overview over all generated files.";
    String NEW                        = "New...";
    String EDIT                       = "Edit";
    String IMPORT                     = "Import...";
    String EXPORT                     = "Export...";
    String TRANSFORM                  = "Transform...";

    // Plug-in IDs
    String ECLIPSE_UI_INTRO_PLUGIN_ID = "org.eclipse.ui.intro";

    // Other IDs
    String DEFAULT_TEXT_EDITOR_ID     = "org.eclipse.ui.DefaultTextEditor";

    // Resource paths
    String YAMAICA_EDITOR_BANNER      = "icons/form_banner.gif";
}
