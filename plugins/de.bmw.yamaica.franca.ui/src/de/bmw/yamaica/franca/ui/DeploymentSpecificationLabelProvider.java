/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.franca.deploymodel.dsl.fDeploy.FDPropertyHost;

import de.bmw.yamaica.base.ui.YamaicaUIConstants;
import de.bmw.yamaica.franca.ui.internal.Activator;

class DeploymentSpecificationLabelProvider extends ColumnLabelProvider
{
    private static final String    ICONS_FULL_EVIEW16_DEFAULTVIEW_MISC_GIF = "icons/full/eview16/defaultview_misc.gif";
    private static final String    ORG_FRANCA_CORE_DSL_UI                  = "org.franca.core.dsl.ui";
    private static final String    ICONS                                   = "icons/";
    private static final String    ARRAYS_PNG                              = "arrays.png";
    private static final String    NUMBERS_PNG                             = "numbers.png";
    private static final String    STRINGS_PNG                             = "strings.png";
    protected Map<Integer, String> propertyHostImageNames                  = new HashMap<Integer, String>();

    public DeploymentSpecificationLabelProvider()
    {
        propertyHostImageNames.put(FDPropertyHost.ARGUMENTS_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.ARRAYS_VALUE, ARRAYS_PNG);
        propertyHostImageNames.put(FDPropertyHost.ATTRIBUTES_VALUE, "attribute.gif");
        propertyHostImageNames.put(FDPropertyHost.BROADCASTS_VALUE, "event.png");
        propertyHostImageNames.put(FDPropertyHost.ENUMERATIONS_VALUE, "enum.gif");
        propertyHostImageNames.put(FDPropertyHost.ENUMERATORS_VALUE, "enumerator.gif");
        propertyHostImageNames.put(FDPropertyHost.FLOATS_VALUE, NUMBERS_PNG);
        propertyHostImageNames.put(FDPropertyHost.INSTANCES_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.INTEGERS_VALUE, NUMBERS_PNG);
        propertyHostImageNames.put(FDPropertyHost.INTERFACES_VALUE, "interface.png");
        propertyHostImageNames.put(FDPropertyHost.METHODS_VALUE, "method.gif");
        propertyHostImageNames.put(FDPropertyHost.NUMBERS_VALUE, NUMBERS_PNG);
        propertyHostImageNames.put(FDPropertyHost.PROVIDERS_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.STRINGS_VALUE, STRINGS_PNG);
        propertyHostImageNames.put(FDPropertyHost.STRUCT_FIELDS_VALUE, "field.gif");
        propertyHostImageNames.put(FDPropertyHost.STRUCTS_VALUE, "struct.gif");
        propertyHostImageNames.put(FDPropertyHost.TYPE_COLLECTIONS_VALUE, "types.gif");
        propertyHostImageNames.put(FDPropertyHost.UNION_FIELDS_VALUE, "field.gif");
        propertyHostImageNames.put(FDPropertyHost.UNIONS_VALUE, "union.gif");
    }

    @Override
    public Image getImage(Object element)
    {
        FDPropertyHost propertyHost = (FDPropertyHost) element;
        String imageName = propertyHostImageNames.get(propertyHost.getValue());

        if (null != imageName)
        {
            if (imageName.equals(STRINGS_PNG) || imageName.equals(NUMBERS_PNG) || imageName.equals(ARRAYS_PNG))
            {
                return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ICONS + imageName).createImage();
            }
            else
            {
                return Activator.imageDescriptorFromPlugin(ORG_FRANCA_CORE_DSL_UI, ICONS + imageName).createImage();
            }
        }

        return Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_PLUGIN_ID, ICONS_FULL_EVIEW16_DEFAULTVIEW_MISC_GIF)
                .createImage();
    }

    @Override
    public String getText(Object element)
    {
        FDPropertyHost propertyHost = (FDPropertyHost) element;
        StringBuilder name = new StringBuilder(propertyHost.getName().replace("_", " "));
        int i = 0;

        do
        {
            name.replace(i, i + 1, name.substring(i, i + 1).toUpperCase());
            i = name.indexOf(" ", i) + 1;
        }
        while (i > 0 && i < name.length());

        return name.toString();
    }
}
