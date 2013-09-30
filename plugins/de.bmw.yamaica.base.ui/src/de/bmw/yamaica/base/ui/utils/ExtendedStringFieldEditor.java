/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.util.regex.Pattern;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class ExtendedStringFieldEditor extends StringFieldEditor
{
    private Pattern pattern;

    public ExtendedStringFieldEditor(String name, String labelText, Composite parent)
    {
        super(name, labelText, parent);
    }

    public ExtendedStringFieldEditor(String name, String labelText, int width, Composite parent)
    {
        super(name, labelText, width, parent);
    }

    public ExtendedStringFieldEditor(String name, String labelText, int width, int strategy, Composite parent)
    {
        super(name, labelText, width, strategy, parent);
    }

    public void setValidationPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public Pattern getValidationPattern()
    {
        return pattern;
    }

    @Override
    protected boolean doCheckState()
    {
        if (null != pattern)
        {
            return pattern.matcher(getStringValue()).matches();
        }

        return super.doCheckState();
    }

    @Override
    protected void init(String name, String text)
    {
        super.init(name, text);
    }

    public static Pattern getWorkbenchPathPattern()
    {
        // A workbench path may not contain the following characters: \ / : * ? " < > |
        // Indeed a '/' is allowed but "//" or "///" is not allowed
        return Pattern.compile("([^\\\\/:\\*\\?\"<>\\|]+/?)+");
    }
}
