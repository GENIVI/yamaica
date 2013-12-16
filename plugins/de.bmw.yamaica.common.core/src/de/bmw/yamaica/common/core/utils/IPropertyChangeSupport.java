/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.utils;

import java.beans.PropertyChangeListener;

/**
 * Interface definition for the AbstractPropertyChangeSupport.java class.
 */
public interface IPropertyChangeSupport
{
    public void addPropertyChangeListener(PropertyChangeListener arg0);

    public void addPropertyChangeListener(String arg0, PropertyChangeListener arg1);

    public PropertyChangeListener[] getPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners(String arg0);

    public boolean hasListeners(String arg0);

    public void removePropertyChangeListener(PropertyChangeListener arg0);

    public void removePropertyChangeListener(String arg0, PropertyChangeListener arg1);
}
