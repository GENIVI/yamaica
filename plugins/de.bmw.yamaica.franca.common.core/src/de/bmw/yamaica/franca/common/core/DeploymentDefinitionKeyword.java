/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DeploymentDefinitionKeyword
{
    ARGUMENTS("arguments"), ARRAY("array"), ARRAYS("arrays"), ATTRIBUTE("attribute"), ATTRIBUTES("attributes"), BOOLEAN("Boolean"), BROADCAST(
            "broadcast"), BROADCASTS("broadcasts"), DEFAULT("default"), DEFINE("define"), ENUMERATION("enumeration"), ENUMERATIONS(
            "enumerations"), ENUMERATORS("enumerators"), EXTENDS("extends"), FALSE("false"), FLOATS("floats"), FOR("for"), IMPORT("import"), INTEGER(
            "Integer"), INTERFACE("interface"), INTERFACES("interfaces"), INSTANCES("instances"), METHOD("method"), METHODS("methods"), NUMBERS(
            "numbers"), OPTIONAL("optional"), PROVIDER("provider"), PORVIDERS("providers"), SPECIFICATION("specification"), STRING("String"), STRINGS(
            "strings"), STRUCT("struct"), STRUCTS("structs"), STRUCT_FIELDS("struct_fields"), TRUE("true"), TYPE_COLLECTION(
            "typeCollection"), UNION("union"), UNIONS("unions"), UNION_FIELDS("union_fields"), USE("use");

    private static final DeploymentDefinitionKeyword[]     allKeywords       = new DeploymentDefinitionKeyword[] { ARGUMENTS, ARRAY,
            ARRAYS, ATTRIBUTE, ATTRIBUTES, BOOLEAN, BROADCAST, BROADCASTS, DEFAULT, DEFINE, ENUMERATION, ENUMERATIONS, ENUMERATORS,
            EXTENDS, FALSE, FLOATS, FOR, IMPORT, INTEGER, INTERFACE, INTERFACES, INSTANCES, METHOD, METHODS, NUMBERS, OPTIONAL, PROVIDER,
            PORVIDERS, SPECIFICATION, STRING, STRINGS, STRUCT, STRUCTS, STRUCT_FIELDS, TRUE, TYPE_COLLECTION, UNION, UNIONS, UNION_FIELDS,
            USE                                                             };

    private static final List<DeploymentDefinitionKeyword> allKeywordsAsList = Collections.unmodifiableList(Arrays.asList(allKeywords));

    public static List<DeploymentDefinitionKeyword> getAll()
    {
        return allKeywordsAsList;
    }

    public static DeploymentDefinitionKeyword getByName(String name)
    {
        for (DeploymentDefinitionKeyword francaKeyword : allKeywords)
        {
            if (francaKeyword.getName().equals(name))
            {
                return francaKeyword;
            }
        }

        return null;
    }

    public static boolean isKeyword(String name)
    {
        return null != getByName(name);
    }

    private final String name;

    private DeploymentDefinitionKeyword(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
