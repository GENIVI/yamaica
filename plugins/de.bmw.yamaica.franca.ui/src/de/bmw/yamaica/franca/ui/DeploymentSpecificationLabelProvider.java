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

import de.bmw.yamaica.franca.ui.internal.Activator;

class DeploymentSpecificationLabelProvider extends ColumnLabelProvider
{
    protected Map<Integer, String> propertyHostImageNames = new HashMap<Integer, String>();

    public DeploymentSpecificationLabelProvider()
    {
        propertyHostImageNames.put(FDPropertyHost.ARGUMENTS_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.ARRAYS_VALUE, "arrays.png");
        propertyHostImageNames.put(FDPropertyHost.ATTRIBUTES_VALUE, "attribute.gif");
        propertyHostImageNames.put(FDPropertyHost.BROADCASTS_VALUE, "event.png");
        propertyHostImageNames.put(FDPropertyHost.ENUMERATIONS_VALUE, "enum.gif");
        propertyHostImageNames.put(FDPropertyHost.ENUMERATORS_VALUE, "enumerator.gif");
        propertyHostImageNames.put(FDPropertyHost.FLOATS_VALUE, "numbers.png");
        propertyHostImageNames.put(FDPropertyHost.INSTANCES_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.INTEGERS_VALUE, "numbers.png");
        propertyHostImageNames.put(FDPropertyHost.INTERFACES_VALUE, "interface.png");
        propertyHostImageNames.put(FDPropertyHost.METHODS_VALUE, "method.gif");
        propertyHostImageNames.put(FDPropertyHost.NUMBERS_VALUE, "numbers.png");
        propertyHostImageNames.put(FDPropertyHost.PROVIDERS_VALUE, null);
        propertyHostImageNames.put(FDPropertyHost.STRINGS_VALUE, "strings.png");
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
            if (imageName.equals("strings.png") || imageName.equals("numbers.png") || imageName.equals("arrays.png"))
            {
                return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/" + imageName).createImage();
            }
            else
            {
                return Activator.imageDescriptorFromPlugin("org.franca.core.dsl.ui", "icons/" + imageName).createImage();
            }
        }

        return Activator.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/eview16/defaultview_misc.gif").createImage();
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
