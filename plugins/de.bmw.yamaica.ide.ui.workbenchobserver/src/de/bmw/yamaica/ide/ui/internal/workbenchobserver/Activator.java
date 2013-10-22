/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.workbenchobserver;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements BundleListener
{
    // The plug-in ID
    public static final String PLUGIN_ID           = "de.bmw.yamaica.ide.ui.workbenchobserver"; //$NON-NLS-1$
    public static final String IDE_PLUGIN_ID       = "org.eclipse.ui.ide";                     //$NON-NLS-1$
    public static final String RESOURCES_PLUGIN_ID = "org.eclipse.core.resources";

    // The shared instance
    private static Activator   plugin;

    private MenuBarHandler     menuBarHandler      = null;

    /**
     * The constructor
     */
    public Activator()
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        super.start(bundleContext);
        plugin = this;

        // We need to check if dependent plug-ins are already loaded (activated) since
        // this plug-in is started quite early within the startup process
        if (neededPluginsLoaded())
        {
            // Initialize observer immediately if dependent plug-ins are loaded
            initializeWorkbenchObserver();
        }
        else
        {
            // Add bundle listener if resource plug-in is not loaded yet
            bundleContext.addBundleListener(this);
        }
    }

    @Override
    public void bundleChanged(BundleEvent event)
    {
        // Remove bundle listener and initialize observer if dependent plug-ins were loaded
        if (neededPluginsLoaded())
        {
            getBundle().getBundleContext().removeBundleListener(this);

            // Check if workbench is already available. Initialize observer if workbench is
            // already available. If not, wait until display thread can initialize the observer.
            // The workbench will be available then for sure.
            try
            {
                PlatformUI.getWorkbench();
                initializeWorkbenchObserver();
            }
            catch (IllegalStateException e)
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initializeWorkbenchObserver();
                    }
                });
            }
        }
    }

    private void initializeWorkbenchObserver()
    {
        if (null == menuBarHandler)
        {
            menuBarHandler = MenuBarHandler.getInstance();
        }
    }

    private boolean neededPluginsLoaded()
    {
        // We need two plug-ins ("org.eclipse.ui.ide" and "org.eclipse.core.resources") first
        // to safely handle the menu bar of the workbench window(s). The ide plug-in initializes
        // the whole SWT stuff (display and shell instances), thus the SWT display instance is
        // available if the ide plug-in was started. The resource plug-in is loaded after the
        // user has confirmed the workspace launcher dialog (the dialog which asks for the
        // workspace location on startup). The workbench instance is not available before this
        // dialog was processed.
        if (Platform.getBundle(IDE_PLUGIN_ID).getState() == Bundle.ACTIVE
                && Platform.getBundle(RESOURCES_PLUGIN_ID).getState() == Bundle.ACTIVE)
        {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception
    {
        if (null != menuBarHandler)
        {
            menuBarHandler.dispose();
            menuBarHandler = null;
        }

        plugin = null;
        super.stop(context);
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
}
