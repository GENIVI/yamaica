package de.bmw.yamaica.common.ui.internal.utils;

import org.eclipse.core.runtime.Assert;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.common.ui.internal.Activator;

public class YamaicaUIResourcesHelper
{
    static public void verifyUIResources()
    {
        try
        {
            // resource paths for ECLIPSE_UI_IDE_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.EXPORT_DIR_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.IMPORT_DIR_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.NEW_FILE_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.NEW_FILE_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.FILE_TYPE_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.FILTER_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.FILTER_DISABLED_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.FOLDER_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.EXPORT_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID, YamaicaUIConstants.IMPORT_ICON_PATH));

            // resource paths for ECLIPSE_DEBUG_UI_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID, YamaicaUIConstants.RUN_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID, YamaicaUIConstants.SELECT_ALL_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID, YamaicaUIConstants.SELECT_ALL_DISABLED_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID, YamaicaUIConstants.DESELECT_ALL_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_DEBUG_UI_PLUGIN_ID, YamaicaUIConstants.DESELECT_ALL_DISABLED_ICON_PATH));

            // resource paths for ECLIPSE_BROWSER_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_BROWSER_PLUGIN_ID, YamaicaUIConstants.REFRESH_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_BROWSER_PLUGIN_ID, YamaicaUIConstants.REFRESH_DISABLED_ICON_PATH));

            // resource paths for ECLIPSE_UI_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.COPY_EDIT_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.COPY_EDIT_DISABLED_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.DELETE_EDIT_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.DELETE_EDIT_DISABLED_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.EXPORT_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.IMPORT_WIZARD_BANNER_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, YamaicaUIConstants.NEW_WIZARD_BANNER_PATH));

            // resource paths for ECLIPSE_TEXTEDITOR_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_TEXTEDITOR_PLUGIN_ID, YamaicaUIConstants.EDIT_TEMPLATE_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_TEXTEDITOR_PLUGIN_ID, YamaicaUIConstants.EDIT_TEMPLATE_DISABLED_ICON_PATH));

            // resource paths for ECLIPSE_JDT_UI_PLUGIN_ID
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_JDT_UI_PLUGIN_ID, YamaicaUIConstants.PACKAGE_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_JDT_UI_PLUGIN_ID, YamaicaUIConstants.SHOW_QUALIFIED_ICON_PATH));
            Assert.isNotNull(Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_JDT_UI_PLUGIN_ID, YamaicaUIConstants.TRANSFORM_ICON_PATH));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
