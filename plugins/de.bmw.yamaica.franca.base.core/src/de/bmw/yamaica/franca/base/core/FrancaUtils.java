/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.base.core;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.franca.core.franca.FModel;

public class FrancaUtils
{
    protected static final String   SPACES_ERROR                            = "The string \"%s\" contains spaces!";
    protected static final String   LEADING_NUMBERS_ERROR                   = "The string \"%s\" may not begin with a number!";
    protected static final String   SPECIAL_CHARS_ERROR                     = "The string \"%s\" may only contain characters of the group [a-zA-Z0-9_]!";
    protected static final String   KEYWORDS_ERROR                          = "The string \"%s\" is a Franca keyword!";
    protected static final String   ZERO_LENGTH_ERROR                       = "The string \"%s\" has length zero after normalization!";
    protected static final String   POINT_BEGIN_ERROR                       = "The string \"%s\" may not begin with a \".\"!";
    protected static final String   POINT_END_ERROR                         = "The string \"%s\" may not end with a \".\"!";

    public static final String      INTERFACE_DEFINITION_FILE_EXTENSION     = YamaicaFrancaConstants.FIDL;
    public static final String      DEPLOYMENT_SPECIFICATION_FILE_EXTENSION = YamaicaFrancaConstants.FDEPL;
    public static final String      DEPLOYMENT_DEFINITION_FILE_EXTENSION    = YamaicaFrancaConstants.FDEPL;
    public static final String      PACKAGE_SEPARATOR                       = ".";

    public static final int         SPACES                                  = 0x01;
    public static final int         LEADING_NUMBERS                         = 0x02;
    public static final int         SPECIAL_CHARS                           = 0x04;
    public static final int         INTERFACE_DEFINITION_KEYWORDS           = 0x08;
    public static final int         DEPLOYMENT_DEFINITION_KEYWORDS          = 0x10;
    public static final int         KEYWORDS                                = 0x18;
    public static final int         ZERO_LENGTH                             = 0x20;
    // TODO to be removed
    public static final int         CPP_KEYWORDS                            = 0x40;

    public static final int         NONE                                    = 0x0;
    public static final int         ALL_WITHOUT_KEYWORDS                    = SPACES | LEADING_NUMBERS | SPECIAL_CHARS | ZERO_LENGTH
                                                                                    | CPP_KEYWORDS;
    public static final int         ALL                                     = ALL_WITHOUT_KEYWORDS | KEYWORDS;
    public static final int         ALL_FOR_INTERFACE_DEFINITIONS           = ALL_WITHOUT_KEYWORDS | INTERFACE_DEFINITION_KEYWORDS;
    public static final int         ALL_FOR_DEPLOYMENT_DEFINITIONS          = ALL_WITHOUT_KEYWORDS | DEPLOYMENT_DEFINITION_KEYWORDS;

    public static final Set<String> keyWords                                = new HashSet<String>();

    // TODO to be removed
    static
    {
        keyWords.add("alignas");
        keyWords.add("alignof");
        keyWords.add("and");
        keyWords.add("and_eq");
        keyWords.add("asm");
        keyWords.add("auto");
        keyWords.add("bitand");
        keyWords.add("bitor");
        keyWords.add("bool");
        keyWords.add("break");
        keyWords.add("case");
        keyWords.add("catch");
        keyWords.add("char");
        keyWords.add("char16_t");
        keyWords.add("char32_t");
        keyWords.add("class");
        keyWords.add("compl");
        keyWords.add("const");
        keyWords.add("constexpr");
        keyWords.add("const_cast");
        keyWords.add("continue");
        keyWords.add("decltype");
        keyWords.add("default");
        keyWords.add("delete");
        keyWords.add("do");
        keyWords.add("double");
        keyWords.add("dynamic_cast");
        keyWords.add("else");
        keyWords.add("enum");
        keyWords.add("explicit");
        keyWords.add("export");
        keyWords.add("extern");
        keyWords.add("false");
        keyWords.add("float");
        keyWords.add("for");
        keyWords.add("friend");
        keyWords.add("goto");
        keyWords.add("if");
        keyWords.add("inline");
        keyWords.add("int");
        keyWords.add("long");
        keyWords.add("mutable");
        keyWords.add("namespace");
        keyWords.add("new");
        keyWords.add("noexcept");
        keyWords.add("not");
        keyWords.add("not_eq");
        keyWords.add("nullptr");
        keyWords.add("operator");
        keyWords.add("or");
        keyWords.add("or_eq");
        keyWords.add("private");
        keyWords.add("protected");
        keyWords.add("public");
        keyWords.add("register");
        keyWords.add("reinterpret_cast");
        keyWords.add("return");
        keyWords.add("short");
        keyWords.add("signed");
        keyWords.add("sizeof");
        keyWords.add("static");
        keyWords.add("static_assert");
        keyWords.add("static_cast");
        keyWords.add("struct");
        keyWords.add("switch");
        keyWords.add("template");
        keyWords.add("this");
        keyWords.add("thread_local");
        keyWords.add("throw");
        keyWords.add("true");
        keyWords.add("try");
        keyWords.add("typedef");
        keyWords.add("typeid");
        keyWords.add("typename");
        keyWords.add("union");
        keyWords.add("unsigned");
        keyWords.add("using");
        keyWords.add("virtual");
        keyWords.add("void");
        keyWords.add("volatile");
        keyWords.add("wchar_t");
        keyWords.add("while");
        keyWords.add("xor");
        keyWords.add("xor_eq");
    }

    /**
     * Returns a normalized version of the referred string parameter.
     */
    public static String normalizeName(final String name, int normalizeMask, int toleranceMask)
    {
        Assert.isNotNull(name);

        String normalizedName = name;

        // Search for spaces
        if (normalizedName.matches(".*\\s.*"))
        {
            if ((normalizeMask & SPACES) > 0)
            {
                normalizedName = normalizedName.replaceAll("\\s", "_");
            }
            else if ((toleranceMask & SPACES) == 0)
            {
                throw new IllegalArgumentException(String.format(SPACES_ERROR, name));
            }
        }

        // Search for leading numbers
        if (normalizedName.matches("[0-9].*"))
        {
            if ((normalizeMask & LEADING_NUMBERS) > 0)
            {
                normalizedName = "_" + normalizedName;
            }
            else if ((toleranceMask & LEADING_NUMBERS) == 0)
            {
                throw new IllegalArgumentException(String.format(LEADING_NUMBERS_ERROR, name));
            }
        }

        // Search for strings that contain characters other than "a-z", "A-Z", "0-9" or "_".
        // Do not match if the string does begin with "^".
        if (!normalizedName.matches("\\^?[a-zA-Z0-9_]+"))
        {
            if ((normalizeMask & SPECIAL_CHARS) > 0)
            {
                // Replace German special characters.
                normalizedName = normalizedName.replace("ä", "ae");
                normalizedName = normalizedName.replace("ö", "oe");
                normalizedName = normalizedName.replace("ü", "ue");
                normalizedName = normalizedName.replace("Ä", "Ae");
                normalizedName = normalizedName.replace("Ö", "Oe");
                normalizedName = normalizedName.replace("Ü", "Ue");
                normalizedName = normalizedName.replace("ß", "ss");

                // Do unicode normalization (e.g. � => e, � => i).
                normalizedName = Normalizer.normalize(normalizedName, Normalizer.Form.NFKD);
                normalizedName = normalizedName.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+", "");

                // Replace all remaining special characters with "_".
                normalizedName = normalizedName.replaceAll("[^a-zA-Z0-9_]", "_");
            }
            else if ((toleranceMask & SPECIAL_CHARS) == 0)
            {
                throw new IllegalArgumentException(String.format(SPECIAL_CHARS_ERROR, name));
            }
        }

        // Check if normalization led to a Franca interface definition keyword
        if (InterfaceDefinitionKeyword.isKeyword(normalizedName))
        {
            if ((normalizeMask & INTERFACE_DEFINITION_KEYWORDS) > 0)
            {
                normalizedName = "^" + normalizedName;
            }
            else if ((toleranceMask & INTERFACE_DEFINITION_KEYWORDS) == 0)
            {
                throw new IllegalArgumentException(String.format(KEYWORDS_ERROR, name));
            }
        }

        // Check if normalization led to a Franca deployment definition keyword
        if (DeploymentDefinitionKeyword.isKeyword(normalizedName))
        {
            if ((normalizeMask & DEPLOYMENT_DEFINITION_KEYWORDS) > 0)
            {
                normalizedName = "^" + normalizedName;
            }
            else if ((toleranceMask & DEPLOYMENT_DEFINITION_KEYWORDS) == 0)
            {
                throw new IllegalArgumentException(String.format(KEYWORDS_ERROR, name));
            }
        }

        // Check if normalization led to a string with length zero
        if (normalizedName.length() == 0)
        {
            if ((normalizeMask & ZERO_LENGTH) > 0)
            {
                normalizedName = "_";
            }
            else if ((toleranceMask & ZERO_LENGTH) == 0)
            {
                throw new IllegalArgumentException(String.format(ZERO_LENGTH_ERROR, name));
            }
        }

        // TODO to be removed
        if (keyWords.contains(normalizedName))
        {
            if ((normalizeMask & CPP_KEYWORDS) > 0)
            {
                normalizedName += "_";
            }
        }

        return normalizedName;
    }

    /**
     * Returns a new path where all segments have been normalized.
     */
    public static IPath normalizeNamespacePath(IPath namespace, int normalizeMask, int toleranceMask)
    {
        IPath normalizedPath = Path.EMPTY;

        for (String namespaceSegment : namespace.segments())
        {
            normalizedPath = normalizedPath.append(normalizeName(namespaceSegment, normalizeMask, toleranceMask));
        }

        return normalizedPath;
    }

    /**
     * Returns a new String where all segments have been normalized.
     */
    public static String normalizeNamespaceString(String namespace, int normalizeMask, int toleranceMask)
    {
        Assert.isNotNull(namespace);
        Assert.isTrue(namespace.length() > 0);

        if (namespace.startsWith(PACKAGE_SEPARATOR))
        {
            throw new IllegalArgumentException(String.format(POINT_BEGIN_ERROR, namespace));
        }

        if (namespace.endsWith(PACKAGE_SEPARATOR))
        {
            throw new IllegalArgumentException(String.format(POINT_END_ERROR, namespace));
        }

        String[] segments = namespace.split("\\" + PACKAGE_SEPARATOR);
        String normalizedString = normalizeName(segments[0], normalizeMask, toleranceMask);

        for (int i = 1; i < segments.length; i++)
        {
            normalizedString += PACKAGE_SEPARATOR + normalizeName(segments[i], normalizeMask, toleranceMask);
        }

        return normalizedString;
    }

    /**
     * Returns the default relative save path of a Franca model. The name of the Franca model must be set
     * already.
     */
    public static IPath getRelativeFidlPackagePath(FModel francaModel)
    {
        IPath path = new Path(namespace2PathString(francaModel.getName()));

        return normalizeNamespacePath(path, ALL_FOR_INTERFACE_DEFINITIONS, ALL).addFileExtension(INTERFACE_DEFINITION_FILE_EXTENSION);
    }

    public static String namespace2PathString(String namespaceString)
    {
        return namespaceString.replace(PACKAGE_SEPARATOR, Character.toString(IPath.SEPARATOR));
    }

    public static String namespace2PathString(IPath namespacePath)
    {
        return namespace2PathString(namespacePath.toString());
    }

    public static String path2NamespaceString(String pathString)
    {
        return pathString.replace(Character.toString(IPath.SEPARATOR), PACKAGE_SEPARATOR);
    }

    public static String path2NamespaceString(IPath path)
    {
        return path2NamespaceString(path.toString());
    }
}
