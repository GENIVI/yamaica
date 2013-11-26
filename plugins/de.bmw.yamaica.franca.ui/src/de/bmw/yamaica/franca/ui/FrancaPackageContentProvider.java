/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.bmw.yamaica.franca.common.core.FrancaUtils;

public class FrancaPackageContentProvider implements IStructuredContentProvider
{
    public FrancaPackageContentProvider()
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {

    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        IPath packagePath = ((IPath) inputElement).removeFileExtension().makeRelative();
        int segmentCount = packagePath.segmentCount();
        List<FrancaPackagePathContainer> packageSuggestions = new ArrayList<FrancaPackagePathContainer>(segmentCount);

        for (int i = segmentCount - 1; i >= 0; i--)
        {
            IPath packageSuggestion = packagePath.removeFirstSegments(i);
            IPath noramlizedPackageSuggestion = FrancaUtils.normalizeNamespacePath(packageSuggestion,
                    FrancaUtils.ALL_FOR_INTERFACE_DEFINITIONS, FrancaUtils.ALL);
            boolean wasNormalized = !packageSuggestion.equals(noramlizedPackageSuggestion);
            String noramlizedPackageSuggestionAsString = FrancaUtils.path2NamespaceString(noramlizedPackageSuggestion.toString());

            packageSuggestions.add(new FrancaPackagePathContainer(noramlizedPackageSuggestionAsString, wasNormalized));
        }

        return packageSuggestions.toArray();
    }
}
