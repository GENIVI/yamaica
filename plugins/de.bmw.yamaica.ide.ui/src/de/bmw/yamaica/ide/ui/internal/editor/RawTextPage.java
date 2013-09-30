/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;

import de.bmw.yamaica.ide.ui.internal.editor.xml.ColorManager;
import de.bmw.yamaica.ide.ui.internal.editor.xml.XMLConfiguration;
import de.bmw.yamaica.ide.ui.internal.editor.xml.XMLDocumentProvider;

public class RawTextPage extends TextEditor
{
    private ColorManager colorManager;

    public RawTextPage(FormEditor editor, String id)
    {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        setDocumentProvider(new XMLDocumentProvider());
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor)
    {
        super.doSave(progressMonitor);
    }

    public void dispose()
    {
        colorManager.dispose();
        super.dispose();
    }
}
