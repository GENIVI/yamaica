/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui;

import de.bmw.yamaica.common.core.YamaicaConstants;

public interface YamaicaUIConstants extends YamaicaConstants
{
    // Labels
    String EMPTY_STRING                             = "";
    String EXPORT                                   = "Export";
    String IMPORT                                   = "Import";
    String NEW                                      = "New";
    String SELECT                                   = "Select";
    String FOLDER_SELECTION                         = "Folder Selection";
    String FILTER_FILE_EXTENSIONS                   = "Filter File Extensions";
    String OVERWRITE_EXISTING_FILES_WITHOUT_WARNING = "&Overwrite existing files without warning";
    String OVERWRITE_EXISTING_FILE                  = "&Overwrite existing file";
    String SELECT_A_FOLDER_TO_IMPORT_INTO           = "Select a folder to import into.";
    String WORKBENCHOBSERVER_PLUGIN_ID              = "de.bmw.yamaica.ide.ui.workbenchobserver";
    String RUN                                      = "run";
    String YAMAICA_TRANSFORM_TYPE_SELECTION         = ".yamaica_transform_type_selection";
    String STRING_IS_NOT_A_VALID_WORKBENCH_PATH     = "String is not a valid workbench path.";
    String LAUNCHING                                = "Launching ";
    String YAMAICA_DEFAULT                          = "yamaica Default";
    String EXPORTING_RESOURCES                      = "Exporting resources...";

    // Plug-in IDs
    String YAMAICA_COMMON_UI                        = "de.bmw.yamaica.common.ui";
    String ECLIPSE_UI_IDE_PLUGIN_ID                 = "org.eclipse.ui.ide";
    String ECLIPSE_UI_PLUGIN_ID                     = "org.eclipse.ui";
    String ECLIPSE_DEBUG_UI_PLUGIN_ID               = "org.eclipse.debug.ui";
    String ECLIPSE_BROWSER_PLUGIN_ID                = "org.eclipse.ui.browser";
    String ECLIPSE_TEXTEDITOR_PLUGIN_ID             = "org.eclipse.ui.workbench.texteditor";
    String ECLIPSE_JDT_UI_PLUGIN_ID                 = "org.eclipse.jdt.ui";

    // resource paths for ECLIPSE_UI_IDE_PLUGIN_ID
    String EXPORT_DIR_WIZARD_BANNER_PATH            = "icons/full/wizban/exportdir_wiz.png";
    String IMPORT_DIR_WIZARD_BANNER_PATH            = "icons/full/wizban/importdir_wiz.png";
    String NEW_FILE_WIZARD_BANNER_PATH              = "icons/full/wizban/newfile_wiz.png";
    String NEW_FILE_ICON_PATH                       = "icons/full/etool16/newfile_wiz.gif";
    String FILE_TYPE_ICON_PATH                      = "icons/full/obj16/fileType_filter.gif";
    String FILTER_ICON_PATH                         = "icons/full/elcl16/filter_ps.gif";
    String FILTER_DISABLED_ICON_PATH                = "icons/full/dlcl16/filter_ps.gif";
    String FOLDER_ICON_PATH                         = "icons/full/obj16/folder.gif";
    String EXPORT_ICON_PATH                         = "icons/full/etool16/export_wiz.gif";
    String IMPORT_ICON_PATH                         = "icons/full/etool16/import_wiz.gif";

    // resource paths for ECLIPSE_DEBUG_UI_PLUGIN_ID
    String RUN_WIZARD_BANNER_PATH                   = "icons/full/wizban/run_wiz.png";
    String SELECT_ALL_ICON_PATH                     = "icons/full/elcl16/enabled_co.gif";
    String SELECT_ALL_DISABLED_ICON_PATH            = "icons/full/dlcl16/enabled_co.gif";
    String DESELECT_ALL_ICON_PATH                   = "icons/full/elcl16/disabled_co.gif";
    String DESELECT_ALL_DISABLED_ICON_PATH          = "icons/full/dlcl16/disabled_co.gif";

    // resource paths for ECLIPSE_BROWSER_PLUGIN_ID
    String REFRESH_ICON_PATH                        = "icons/elcl16/nav_refresh.gif";
    String REFRESH_DISABLED_ICON_PATH               = "icons/dlcl16/nav_refresh.gif";

    // resource paths for ECLIPSE_UI_PLUGIN_ID
    String COPY_EDIT_ICON_PATH                      = "icons/full/etool16/copy_edit.gif";
    String COPY_EDIT_DISABLED_ICON_PATH             = "icons/full/dtool16/copy_edit.gif";
    String DELETE_EDIT_ICON_PATH                    = "icons/full/etool16/delete_edit.gif";
    String DELETE_EDIT_DISABLED_ICON_PATH           = "icons/full/dtool16/delete_edit.gif";
    String EXPORT_WIZARD_BANNER_PATH                = "icons/full/wizban/export_wiz.png";
    String IMPORT_WIZARD_BANNER_PATH                = "icons/full/wizban/import_wiz.png";
    String NEW_WIZARD_BANNER_PATH                   = "icons/full/wizban/new_wiz.png";

    // resource paths for ECLIPSE_TEXTEDITOR_PLUGIN_ID
    String EDIT_TEMPLATE_ICON_PATH                  = "icons/full/elcl16/edit_template.gif";
    String EDIT_TEMPLATE_DISABLED_ICON_PATH         = "icons/full/dlcl16/edit_template.gif";

    // resource paths for ECLIPSE_JDT_UI_PLUGIN_ID
    String PACKAGE_ICON_PATH                        = "icons/full/obj16/package_obj.png";
    String SHOW_QUALIFIED_ICON_PATH                 = "icons/full/elcl16/th_showqualified.png";
    String TRANSFORM_ICON_PATH                      = "icons/full/elcl16/javaassist_co.png";

    // resource paths for DE_BMW_YAMAICA_COMMON_UI
    String YAMAICA_ICON_PATH                        = "icons/yamaica.png";
}
