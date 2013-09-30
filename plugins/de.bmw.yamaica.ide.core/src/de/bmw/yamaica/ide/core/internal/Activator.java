/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    // The plug-in ID
    public static final String     PLUGIN_ID        = "de.bmw.yamaica.ide.core"; //$NON-NLS-1$

    // The shared instance
    private static Activator       plugin;

    private static final String    ABOUT_MAPPINGS   = "$nl$/about.mappings";    //$NON-NLS-1$

    public static final String     VERSION_MAPPING  = "0";                      //$NON-NLS-1$
    public static final String     BUILD_ID_MAPPING = "1";                      //$NON-NLS-1$
    public static final String     VERSION          = "VERSION";                //$NON-NLS-1$
    public static final String     BUILD_ID         = "BUILD_ID";               //$NON-NLS-1$

    public HashMap<String, String> mappings         = null;

    /**
     * The constructor
     */
    public Activator()
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        super.start(bundleContext);
        plugin = this;

        mappings = loadMappings(Activator.getDefault().getBundle());

        IPreferenceStore store = getPreferenceStore();

        String currentVersion = mappings.get(VERSION_MAPPING);
        String currentBuildId = mappings.get(BUILD_ID_MAPPING);
        String lastVersion = store.getString(VERSION);
        String lastBuildId = store.getString(BUILD_ID);

        // Open welcome intro part if the current product is yamaica and if version number or
        // build id have changed since last startup.
        if (Platform.getProduct().getId().equals(PLUGIN_ID + ".product")
                && (!currentVersion.equals(lastVersion) || !currentBuildId.equals(lastBuildId)))
        {
            store.setValue(VERSION, currentVersion);
            store.setValue(BUILD_ID, currentBuildId);

            Display.getDefault().asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    workbench.getIntroManager().showIntro(window, false);
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext bundleContext) throws Exception
    {
        plugin = null;
        super.stop(bundleContext);
    }

    public String getMapping(String key)
    {
        return mappings.get(key);
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

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    // Copied from org.eclipse.ui.internal.ProductProperties (adjusted)
    private HashMap<String, String> loadMappings(Bundle definingBundle)
    {
        URL location = FileLocator.find(definingBundle, new Path(ABOUT_MAPPINGS), Collections.emptyMap());
        PropertyResourceBundle bundle = null;
        InputStream inputStream = null;

        if (null != location)
        {
            try
            {
                inputStream = location.openStream();
                bundle = new PropertyResourceBundle(inputStream);
            }
            catch (IOException e)
            {
                bundle = null;
            }
            finally
            {
                try
                {
                    if (null != inputStream)
                    {
                        inputStream.close();
                    }
                }
                catch (IOException e)
                {
                    // do nothing if we fail to close
                }
            }
        }

        HashMap<String, String> mappings = new HashMap<String, String>();

        if (null != bundle)
        {
            boolean found = true;

            for (int i = 0; true == found; i++)
            {
                try
                {
                    String key = Integer.toString(i);
                    mappings.put(key, bundle.getString(key));
                }
                catch (MissingResourceException e)
                {
                    found = false;
                }
            }
        }

        return mappings;
    }
}
