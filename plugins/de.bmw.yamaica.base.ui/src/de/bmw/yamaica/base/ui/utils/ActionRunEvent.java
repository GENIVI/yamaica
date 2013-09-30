/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.util.EventObject;

import org.eclipse.jface.action.IAction;

public class ActionRunEvent extends EventObject
{
    private static final long serialVersionUID = 7060217391914702066L;

    public final IAction      action;
    public final int          type;

    public ActionRunEvent(IAction action, int type)
    {
        super(action);

        this.action = action;
        this.type = type;
    }
}
