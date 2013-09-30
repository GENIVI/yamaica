/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor.xml;

import org.eclipse.jface.text.rules.*;

public class XMLPartitionScanner extends RuleBasedPartitionScanner
{
    public final static String XML_COMMENT = "__xml_comment";
    public final static String XML_TAG     = "__xml_tag";

    public XMLPartitionScanner()
    {

        IToken xmlComment = new Token(XML_COMMENT);
        IToken tag = new Token(XML_TAG);

        IPredicateRule[] rules = new IPredicateRule[2];

        rules[0] = new MultiLineRule("<!--", "-->", xmlComment);
        rules[1] = new TagRule(tag);

        setPredicateRules(rules);
    }
}
