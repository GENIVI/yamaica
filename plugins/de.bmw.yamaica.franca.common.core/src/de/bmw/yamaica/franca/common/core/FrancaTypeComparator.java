package de.bmw.yamaica.franca.common.core;

import java.util.Comparator;

import org.franca.core.franca.FArrayType;
import org.franca.core.franca.FEnumerationType;
import org.franca.core.franca.FMapType;
import org.franca.core.franca.FStructType;
import org.franca.core.franca.FType;
import org.franca.core.franca.FTypeDef;
import org.franca.core.franca.FUnionType;

public class FrancaTypeComparator implements Comparator<FType>
{
    private static final int UNKNOWN_TYPE     = 0;
    private static final int TYPE_DEF         = 1;
    private static final int ENUMERATION_TYPE = 2;
    private static final int STRUCTURE_TYPE   = 3;
    private static final int UNION_TYPE       = 4;
    private static final int MAP_TYPE         = 5;
    private static final int ARRAY_TYPE       = 6;

    @Override
    public int compare(FType francaType1, FType francaType2)
    {
        final int typeDiff = getDataTypeConstant(francaType1) - getDataTypeConstant(francaType2);

        return (typeDiff != 0) ? typeDiff : francaType1.getName().compareToIgnoreCase(francaType2.getName());
    }

    private int getDataTypeConstant(FType francaType)
    {
        if (francaType instanceof FTypeDef)
        {
            return TYPE_DEF;
        }

        if (francaType instanceof FEnumerationType)
        {
            return ENUMERATION_TYPE;
        }

        if (francaType instanceof FStructType)
        {
            return STRUCTURE_TYPE;
        }

        if (francaType instanceof FUnionType)
        {
            return UNION_TYPE;
        }

        if (francaType instanceof FMapType)
        {
            return MAP_TYPE;
        }

        if (francaType instanceof FArrayType)
        {
            return ARRAY_TYPE;
        }

        return UNKNOWN_TYPE;
    }
}
