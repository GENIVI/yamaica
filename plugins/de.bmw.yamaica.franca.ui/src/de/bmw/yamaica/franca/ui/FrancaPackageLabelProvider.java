/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;
import de.bmw.yamaica.franca.common.core.FrancaUtils;
import de.bmw.yamaica.franca.ui.internal.Activator;

public class FrancaPackageLabelProvider extends ColumnLabelProvider
{
    protected final Image packageImage;

    protected final Image normalizedPackageImage;

    public FrancaPackageLabelProvider()
    {
        // packageImage = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/package.gif").createImage();
        // packageImage = Activator.imageDescriptorFromPlugin("org.eclipse.pde.ui", "icons/elcl16/package_obj.gif").createImage();
        packageImage = Activator
                .imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_JDT_UI_PLUGIN_ID, YamaicaUIConstants.PACKAGE_ICON_PATH).createImage();
        normalizedPackageImage = Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_JDT_UI_PLUGIN_ID,
                YamaicaUIConstants.SHOW_QUALIFIED_ICON_PATH).createImage();
        // normalizedPackageImage = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/package_normalized.gif").createImage();
    }

    @Override
    public void dispose()
    {
        packageImage.dispose();
        normalizedPackageImage.dispose();
    }

    @Override
    public Image getImage(Object element)
    {
        FrancaPackagePathContainer packagePathContainer = (FrancaPackagePathContainer) element;

        return packagePathContainer.wasNormalized ? normalizedPackageImage : packageImage;
    }

    @Override
    public String getText(Object element)
    {
        FrancaPackagePathContainer packagePathContainer = (FrancaPackagePathContainer) element;

        return FrancaUtils.path2NamespaceString(packagePathContainer.packagePath);
    }
}
