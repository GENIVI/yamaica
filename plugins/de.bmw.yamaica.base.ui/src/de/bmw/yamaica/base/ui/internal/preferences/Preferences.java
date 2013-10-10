/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal.preferences;

import de.bmw.yamaica.base.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.base.ui.internal.Activator;
import de.bmw.yamaica.base.ui.utils.ExtendedStringFieldEditor;
import de.bmw.yamaica.base.ui.utils.YamaicaUIConstants;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * This class is needed by the org.eclipse.ui.preferencePages extension point (see plugin.xml)
 * and defines the preference page of this plug-in.
 */
public class Preferences extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final String NEVER_HIDE = "Never Hide";
    private static final String ALWAYS_HIDE = "Always Hide";
    private static final String MENU_BAR_HIDING_RULE = "&Menu bar hiding rule:";
    private static final String GUI_SETTINGS = "GUI settings";
    private static final String CREATE_YAMAICA_EDITOR_LINK_BUTTON_TEXT = "&Create yamaica editor link";
    private static final String TARGET_FOLDER_BUTTON_TEXT = "&Target folder:";
    private static final String IMPORT_FOLDER_BUTTON_TEXT = "&Import folder:";
    private static final String LIMIT_CONSOLE_OUTPUT2 = "&Limit console output";
    private static final String REDIRECT_SYSTEM_STREAMS_TO_BUILT_IN_CONSOLE = "&Redirect system streams to built-in console";
    private static final String YAMAICA_PREFERENCES_CONTEXT = ".yamaica_preferences_context";
    public static final String CONSOLE_BUFFER_SIZE        = "CONSOLE_BUFFER_SIZE";
    public static final String CONSOLE_TRIGGER_SIZE       = "CONSOLE_TRIGGER_SIZE";
    public static final String LIMIT_CONSOLE_OUTPUT       = "LIMIT_CONSOLE_OUTPUT";
    public static final String REDIRECT_SYSTEM_STREAMS    = "REDIRECT_SYSTEM_STREAMS";
    public static final String IMPORT_FOLDER              = IResourcePropertyStore.IMPORT_FOLDER;
    public static final String TARGET_FOLDER              = IResourcePropertyStore.TARGET_FOLDER;
    public static final String CREATE_YAMAICA_EDITOR_LINK = "CREATE_YAMAICA_EDITOR_LINK";
    public static final String YAMAICA_EDITOR_LINK_NAME   = "YAMAICA_EDITOR_LINK_NAME";

    private BooleanFieldEditor redirectStreamsFieldEditor, limitConsoleOutputFieldEditor, createEditorLinkFieldEditor;
    private ExtendedStringFieldEditor importDirectoryFieldEditor, targetDirectoryFieldEditor;
    private ComboFieldEditor          menuBarHidingRuleFieldEditor;

    @Override
    public void init(IWorkbench workbench)
    {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        // setDescription("Example of a Description");
    }

    @Override
    protected Control createContents(Composite parent)
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + YAMAICA_PREFERENCES_CONTEXT);

        IPreferenceStore store = getPreferenceStore();

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);

        Group consoleSettings = new Group(composite, SWT.NONE);
        consoleSettings.setText("Console settings");
        consoleSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        redirectStreamsFieldEditor = new BooleanFieldEditor(REDIRECT_SYSTEM_STREAMS, REDIRECT_SYSTEM_STREAMS_TO_BUILT_IN_CONSOLE,
                consoleSettings);
        redirectStreamsFieldEditor.setPreferenceStore(store);
        redirectStreamsFieldEditor.setPage(this);
        redirectStreamsFieldEditor.load();

        limitConsoleOutputFieldEditor = new BooleanFieldEditor(LIMIT_CONSOLE_OUTPUT, LIMIT_CONSOLE_OUTPUT2, consoleSettings);
        limitConsoleOutputFieldEditor.setPreferenceStore(store);
        limitConsoleOutputFieldEditor.setPage(this);
        limitConsoleOutputFieldEditor.load();

        consoleSettings.setLayout(new GridLayout(1, false));

        Group projectSettings = new Group(composite, SWT.NONE);
        projectSettings.setText("Project settings");
        projectSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        importDirectoryFieldEditor = new ExtendedStringFieldEditor(IMPORT_FOLDER, IMPORT_FOLDER_BUTTON_TEXT, projectSettings);
        importDirectoryFieldEditor.setPreferenceStore(store);
        importDirectoryFieldEditor.setPage(this);
        importDirectoryFieldEditor.setValidationPattern(ExtendedStringFieldEditor.getWorkbenchPathPattern());
        importDirectoryFieldEditor.setErrorMessage(YamaicaUIConstants.STRING_IS_NOT_A_VALID_WORKBENCH_PATH);
        importDirectoryFieldEditor.setPropertyChangeListener(new IPropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateApplyButton();
            }
        });
        importDirectoryFieldEditor.fillIntoGrid(projectSettings, 2);
        importDirectoryFieldEditor.load();

        targetDirectoryFieldEditor = new ExtendedStringFieldEditor(TARGET_FOLDER, TARGET_FOLDER_BUTTON_TEXT, projectSettings);
        targetDirectoryFieldEditor.setPreferenceStore(store);
        targetDirectoryFieldEditor.setPage(this);
        targetDirectoryFieldEditor.setValidationPattern(ExtendedStringFieldEditor.getWorkbenchPathPattern());
        targetDirectoryFieldEditor.setErrorMessage(YamaicaUIConstants.STRING_IS_NOT_A_VALID_WORKBENCH_PATH);
        targetDirectoryFieldEditor.setPropertyChangeListener(new IPropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateApplyButton();
            }
        });
        targetDirectoryFieldEditor.fillIntoGrid(projectSettings, 2);
        targetDirectoryFieldEditor.load();

        createEditorLinkFieldEditor = new BooleanFieldEditor(CREATE_YAMAICA_EDITOR_LINK, CREATE_YAMAICA_EDITOR_LINK_BUTTON_TEXT, projectSettings);
        createEditorLinkFieldEditor.setPreferenceStore(store);
        createEditorLinkFieldEditor.setPage(this);
        createEditorLinkFieldEditor.fillIntoGrid(projectSettings, 2);
        createEditorLinkFieldEditor.load();

        projectSettings.setLayout(new GridLayout(2, false));

        // The de.bmw.yamaica.base.ui.workbenchobserver bundle is an optional dependency. Thus
        // we have to check if it is available. If not the GUI parts are not needed.
        if (null != Platform.getBundle(YamaicaUIConstants.WORKBENCHOBSERVER_PLUGIN_ID))
        {
            Group guiSettings = new Group(composite, SWT.NONE);
            guiSettings.setText(GUI_SETTINGS);
            guiSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            String[][] menuBarHidingRuleNamesAndValues = new String[][] {
                    { YamaicaUIConstants.YAMAICA_DEFAULT, de.bmw.yamaica.base.ui.workbenchobserver.Preferences.YAMAICA_HIDE },
                    { ALWAYS_HIDE, de.bmw.yamaica.base.ui.workbenchobserver.Preferences.ALWAYS_HIDE },
                    { NEVER_HIDE, de.bmw.yamaica.base.ui.workbenchobserver.Preferences.NEVER_HIDE } };

            menuBarHidingRuleFieldEditor = new ComboFieldEditor(de.bmw.yamaica.base.ui.workbenchobserver.Preferences.MENU_BAR_HIDING_RULE,
                    MENU_BAR_HIDING_RULE, menuBarHidingRuleNamesAndValues, guiSettings);
            menuBarHidingRuleFieldEditor.setPreferenceStore(de.bmw.yamaica.base.ui.workbenchobserver.Preferences.getPreferenceStore());
            menuBarHidingRuleFieldEditor.setPage(this);
            menuBarHidingRuleFieldEditor.load();

            guiSettings.setLayout(new GridLayout(2, false));
        }

        return composite;
    }

    @Override
    protected void performDefaults()
    {
        redirectStreamsFieldEditor.loadDefault();
        limitConsoleOutputFieldEditor.loadDefault();
        importDirectoryFieldEditor.loadDefault();
        targetDirectoryFieldEditor.loadDefault();
        createEditorLinkFieldEditor.loadDefault();

        if (null != menuBarHidingRuleFieldEditor)
        {
            menuBarHidingRuleFieldEditor.loadDefault();
        }

        super.performDefaults();
    }

    @Override
    protected void performApply()
    {
        redirectStreamsFieldEditor.store();
        limitConsoleOutputFieldEditor.store();
        importDirectoryFieldEditor.store();
        targetDirectoryFieldEditor.store();
        createEditorLinkFieldEditor.store();

        if (null != menuBarHidingRuleFieldEditor)
        {
            menuBarHidingRuleFieldEditor.store();
        }
    }

    @Override
    public boolean performOk()
    {
        performApply();

        return true;
    }

    @Override
    public boolean isValid()
    {
        boolean isValid = importDirectoryFieldEditor.isValid() && targetDirectoryFieldEditor.isValid();

        setValid(isValid);

        return isValid;
    }
}
