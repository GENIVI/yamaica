/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import java.util.Comparator;

import org.franca.core.franca.FArrayType;
import org.franca.core.franca.FEnumerationType;
import org.franca.core.franca.FMapType;
import org.franca.core.franca.FStructType;
import org.franca.core.franca.FType;
import org.franca.core.franca.FTypeDef;
import org.franca.core.franca.FUnionType;

class FrancaTypeComparator implements Comparator<FType>
{
    static val UNKNOWN_TYPE = 0;
    static val TYPE_DEF = 1;
    static val ENUMERATION_TYPE = 2;
    static val STRUCTURE_TYPE = 3;
    static val UNION_TYPE = 4;
    static val MAP_TYPE = 5;
    static val ARRAY_TYPE = 6;

    override int compare(FType francaType1, FType francaType2)
    {
        val typeDiff = francaType1.dataTypeConstant - francaType2.dataTypeConstant

        return if(typeDiff != 0)
        {
            typeDiff
        }
        else
        {
            francaType1.name.compareToIgnoreCase(francaType2.name)
        }
    }

    private def int getDataTypeConstant(FType francaType)
    {
        return switch francaType
        {
            FTypeDef: TYPE_DEF
            FEnumerationType: ENUMERATION_TYPE
            FStructType: STRUCTURE_TYPE
            FUnionType: UNION_TYPE
            FMapType: MAP_TYPE
            FArrayType: ARRAY_TYPE
            default: UNKNOWN_TYPE
        }
    }
}
