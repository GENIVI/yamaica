/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager
{

    protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

    public void dispose()
    {
        Iterator<Color> e = fColorTable.values().iterator();
        while (e.hasNext())
            e.next().dispose();
    }

    public Color getColor(RGB rgb)
    {
        Color color = (Color) fColorTable.get(rgb);
        if (color == null)
        {
            color = new Color(Display.getCurrent(), rgb);
            fColorTable.put(rgb, color);
        }
        return color;
    }
}
