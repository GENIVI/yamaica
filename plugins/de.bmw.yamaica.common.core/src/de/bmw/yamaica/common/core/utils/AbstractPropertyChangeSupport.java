/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class is to be used as base class to equip the extended class with
 * Java beans property change support.
 */
public abstract class AbstractPropertyChangeSupport implements IPropertyChangeSupport
{
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public synchronized void addPropertyChangeListener(PropertyChangeListener arg0)
    {
        propertyChangeSupport.addPropertyChangeListener(arg0);
    }

    public synchronized void addPropertyChangeListener(String arg0, PropertyChangeListener arg1)
    {
        propertyChangeSupport.addPropertyChangeListener(arg0, arg1);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners()
    {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String arg0)
    {
        return propertyChangeSupport.getPropertyChangeListeners(arg0);
    }

    public synchronized boolean hasListeners(String arg0)
    {
        return propertyChangeSupport.hasListeners(arg0);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener arg0)
    {
        propertyChangeSupport.removePropertyChangeListener(arg0);
    }

    public synchronized void removePropertyChangeListener(String arg0, PropertyChangeListener arg1)
    {
        propertyChangeSupport.removePropertyChangeListener(arg0, arg1);
    }
}
