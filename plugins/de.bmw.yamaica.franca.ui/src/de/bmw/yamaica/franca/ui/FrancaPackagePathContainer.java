/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import org.eclipse.core.runtime.Assert;

public class FrancaPackagePathContainer
{
    public final String  packagePath;
    public final boolean wasNormalized;

    public FrancaPackagePathContainer(String packagePath, boolean wasNormalized)
    {
        Assert.isNotNull(packagePath);

        this.packagePath = packagePath;
        this.wasNormalized = wasNormalized;
    }

    @Override
    public int hashCode()
    {
        return packagePath.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FrancaPackagePathContainer)
        {
            return packagePath.equals(((FrancaPackagePathContainer) obj).packagePath);
        }

        return false;
    }
}
