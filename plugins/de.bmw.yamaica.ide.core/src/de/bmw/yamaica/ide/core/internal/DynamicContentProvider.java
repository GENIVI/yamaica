/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.core.internal;

import java.io.PrintWriter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;
import org.eclipse.ui.intro.config.IIntroXHTMLContentProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class DynamicContentProvider implements IIntroXHTMLContentProvider
{
    public DynamicContentProvider()
    {

    }

    public void init(IIntroContentProviderSite site)
    {

    }

    public void createContent(String id, PrintWriter out)
    {

    }

    public void createContent(String id, Composite parent, FormToolkit toolkit)
    {

    }

    public void createContent(String id, Element parent)
    {
        Document dom = parent.getOwnerDocument();
        Text textNode = dom.createTextNode("Welcome to yamaica " + Activator.getDefault().getMapping(Activator.VERSION_MAPPING));
        parent.setAttribute("class", "h1 welcomeText");
        parent.appendChild(textNode);
    }

    public void dispose()
    {

    }
}
