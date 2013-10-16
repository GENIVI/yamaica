/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.core.internal.resourceobserver;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin implements BundleListener
{
    // The plug-in ID
    public static final String     PLUGIN_ID = "de.bmw.yamaica.base.core.resourceobserver"; //$NON-NLS-1$

    // The shared instance
    private static Activator       plugin;

    private YamaicaResourceUpdater yamaicaResourceUpdater;
    private YamaicaSaveParticipant yamaicaSaveParticipant;

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
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        super.start(bundleContext);
        plugin = this;

        // We need to check if the resource plug-in is already loaded (activated) since
        // this plug-in is started quite early within the startup process
        Bundle resourceBundle = Platform.getBundle(ResourcesPlugin.PI_RESOURCES);

        if (null != resourceBundle)
        {
            if (resourceBundle.getState() == Bundle.ACTIVE)
            {
                // Initialize observer immediately if resource plug-in is loaded
                initializeResourceObserver();
            }
            else
            {
                // Add bundle listener if resource plug-in is not loaded yet
                bundleContext.addBundleListener(this);
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event)
    {
        // Remove bundle listener and initialize observer if resource plug-in was loaded
        if (event.getType() == BundleEvent.STARTED && event.getBundle().getSymbolicName().equals(ResourcesPlugin.PI_RESOURCES))
        {
            getBundle().getBundleContext().removeBundleListener(this);

            initializeResourceObserver();
        }
    }

    private void initializeResourceObserver()
    {
        yamaicaResourceUpdater = YamaicaResourceUpdater.getInstance();
        yamaicaSaveParticipant = YamaicaSaveParticipant.getInstance();
    }

    protected void readStateFrom(File target)
    {

    }

    protected void writeImportantState(File target)
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception
    {
        if (null != yamaicaSaveParticipant)
        {
            yamaicaSaveParticipant.dispose();
        }

        if (null != yamaicaResourceUpdater)
        {
            yamaicaResourceUpdater.dispose();
        }

        plugin = null;
        super.stop(bundleContext);
    }
}
