/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor.xml;

import org.eclipse.jface.text.rules.*;

public class TagRule extends MultiLineRule
{

    public TagRule(IToken token)
    {
        super("<", ">", token);
    }

    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
    {
        int c = scanner.read();
        if (sequence[0] == '<')
        {
            if (c == '?')
            {
                // processing instruction - abort
                scanner.unread();
                return false;
            }
            if (c == '!')
            {
                scanner.unread();
                // comment - abort
                return false;
            }
        }
        else if (sequence[0] == '>')
        {
            scanner.unread();
        }
        return super.sequenceDetected(scanner, sequence, eofAllowed);
    }
}
