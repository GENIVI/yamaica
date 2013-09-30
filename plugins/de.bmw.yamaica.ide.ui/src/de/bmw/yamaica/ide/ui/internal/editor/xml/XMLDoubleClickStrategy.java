/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor.xml;

import org.eclipse.jface.text.*;

public class XMLDoubleClickStrategy implements ITextDoubleClickStrategy
{
    protected ITextViewer fText;

    public void doubleClicked(ITextViewer part)
    {
        int pos = part.getSelectedRange().x;

        if (pos < 0)
            return;

        fText = part;

        if (!selectComment(pos))
        {
            selectWord(pos);
        }
    }

    protected boolean selectComment(int caretPos)
    {
        IDocument doc = fText.getDocument();
        int startPos, endPos;

        try
        {
            int pos = caretPos;
            char c = ' ';

            while (pos >= 0)
            {
                c = doc.getChar(pos);
                if (c == '\\')
                {
                    pos -= 2;
                    continue;
                }
                if (c == Character.LINE_SEPARATOR || c == '\"')
                    break;
                --pos;
            }

            if (c != '\"')
                return false;

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();
            c = ' ';

            while (pos < length)
            {
                c = doc.getChar(pos);
                if (c == Character.LINE_SEPARATOR || c == '\"')
                    break;
                ++pos;
            }
            if (c != '\"')
                return false;

            endPos = pos;

            int offset = startPos + 1;
            int len = endPos - offset;
            fText.setSelectedRange(offset, len);
            return true;
        }
        catch (BadLocationException x)
        {
        }

        return false;
    }

    protected boolean selectWord(int caretPos)
    {

        IDocument doc = fText.getDocument();
        int startPos, endPos;

        try
        {

            int pos = caretPos;
            char c;

            while (pos >= 0)
            {
                c = doc.getChar(pos);
                if (!Character.isJavaIdentifierPart(c))
                    break;
                --pos;
            }

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();

            while (pos < length)
            {
                c = doc.getChar(pos);
                if (!Character.isJavaIdentifierPart(c))
                    break;
                ++pos;
            }

            endPos = pos;
            selectRange(startPos, endPos);
            return true;

        }
        catch (BadLocationException x)
        {
        }

        return false;
    }

    private void selectRange(int startPos, int stopPos)
    {
        int offset = startPos + 1;
        int length = stopPos - offset;
        fText.setSelectedRange(offset, length);
    }
}
