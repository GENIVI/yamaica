/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.dbus.internal;

import org.franca.connectors.dbus.validators.DBusCompatibilityValidator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
    /**
     * Do not add anything else into this plug-in and/or 'Activator.start' function.
     *
     * This very plug-in is an "early-startup" plug-in which is used to work around a problem with a static ctor in
     * the 'org.franca.connectors.dbus' plug-in only.
     *
     * See also:
     * https://code.google.com/a/eclipselabs.org/p/franca/issues/detail?id=145
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        DBusCompatibilityValidator.setActive(false);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
    }
}
