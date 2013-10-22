/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.internal;

import java.io.PrintStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.bmw.yamaica.base.ui.Preferences;
import de.bmw.yamaica.base.ui.YamaicaUIConstants;
import de.bmw.yamaica.base.ui.utils.ConsoleManager;
import de.bmw.yamaica.base.ui.utils.ConsoleStream;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    private static final String        SYSTEM_ERROR  = "System Error";

    private static final String        SYSTEM_OUT    = "System Out";

    // The plug-in ID
    public static final String         PLUGIN_ID     = "de.bmw.yamaica.base.ui";                           //$NON-NLS-1$

    // The shared instance
    private static Activator           plugin;

    private static final ConsoleStream consoleStream = ConsoleStream.getOrCreateConsoleStream(YamaicaUIConstants.YAMAICA_DEFAULT,
                                                             new Color(Display.getCurrent(), 204, 102, 0));
    public static final PrintStream    out           = consoleStream.getPrintStream();

    private IPreferenceStore           store;

    /**
     * The constructor
     */
    public Activator()
    {

    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        super.start(bundleContext);
        plugin = this;

        // Load workbench observer plug-in (it should be loaded already, since it is auto started)
        if (null != Platform.getBundle(YamaicaUIConstants.WORKBENCHOBSERVER_PLUGIN_ID))
        {
            de.bmw.yamaica.base.ui.internal.Activator.getDefault();
        }

        store = getPreferenceStore();

        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { ConsoleManager.console });
        redirectSystemStream(store.getBoolean(Preferences.REDIRECT_SYSTEM_STREAMS));

        getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getProperty().equals(Preferences.REDIRECT_SYSTEM_STREAMS))
                {
                    if (store.getBoolean(Preferences.REDIRECT_SYSTEM_STREAMS))
                    {
                        redirectSystemStream(true);
                    }
                    else
                    {
                        redirectSystemStream(false);
                    }
                }

                if (event.getProperty().equals(Preferences.LIMIT_CONSOLE_OUTPUT))
                {
                    if (store.getBoolean(Preferences.LIMIT_CONSOLE_OUTPUT))
                    {
                        limitConsoleOutput(true);
                    }
                    else
                    {
                        limitConsoleOutput(false);
                    }
                }
            }
        });

        if (store.getBoolean(Preferences.LIMIT_CONSOLE_OUTPUT))
        {
            limitConsoleOutput(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop(BundleContext bundleContext) throws Exception
    {
        // This call will dispose possibly available color instances
        redirectSystemStream(false);

        plugin = null;
        super.stop(bundleContext);
    }

    private void limitConsoleOutput(boolean limit)
    {
        if (limit)
        {
            ConsoleManager.console.setWaterMarks(store.getInt(Preferences.CONSOLE_BUFFER_SIZE),
                    store.getInt(Preferences.CONSOLE_TRIGGER_SIZE));
        }
        else
        {
            ConsoleManager.console.setWaterMarks(-1, -1);
        }
    }

    // Backup default System.out and System.err
    private final PrintStream systemOutStreamBackup = System.out;
    private final PrintStream systemErrStreamBackup = System.err;

    // Define colors for System.out and System.err
    private Color             outStreamColor        = null;
    private Color             errStreamColor        = null;

    private void redirectSystemStream(boolean redirect)
    {
        if (redirect)
        {
            final Display display = Display.getDefault();

            display.syncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    outStreamColor = display.getSystemColor(SWT.COLOR_BLACK);
                    errStreamColor = display.getSystemColor(SWT.COLOR_RED);
                }
            });

            // Assign our own PrintStream instances to System.out and System.err
            System.setOut(ConsoleStream.getOrCreateConsoleStream(SYSTEM_OUT, outStreamColor).getPrintStream());
            System.setErr(ConsoleStream.getOrCreateConsoleStream(SYSTEM_ERROR, errStreamColor).getPrintStream());
        }
        else
        {
            // Assign original PrintStream instances to System.out and System.err
            if (null != outStreamColor)
            {
                System.setOut(systemOutStreamBackup);
                outStreamColor.dispose();
                outStreamColor = null;
            }

            if (null != errStreamColor)
            {
                System.setErr(systemErrStreamBackup);
                errStreamColor.dispose();
                errStreamColor = null;
            }
        }
    }
}
