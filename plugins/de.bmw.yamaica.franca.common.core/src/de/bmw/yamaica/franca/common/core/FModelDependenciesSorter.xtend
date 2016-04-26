/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core

import java.util.Comparator
import org.franca.core.franca.FModel
import org.franca.core.franca.Import

class FModelDependenciesSorter implements Comparator<FModel>
{

    /**
 * Try to retrieve the FModel, where the given Import refers to
 * It must be contained in one of the lists
 */
    override int compare(FModel model1, FModel model2)
    {
        val imports1 = model1.imports

        for (import : imports1)
        {
            val isImport = modelNameEqualsImportName(model2, import)
            if(isImport)
            {
                return 1 //model2 < model1
            }
        }
        val imports2 = model2.imports
        for (import : imports2)
        {
            val isImport = modelNameEqualsImportName(model1, import)
            if(isImport)
            {
                return -1 //model1 < model2
            }
        }
        return 0
    }

    private def boolean modelNameEqualsImportName(FModel m, Import i)
    {

        // Check if model was loaded via fidl file reader
        var boolean result = false

        if(m.eResource() != null && m.eResource().getURI() != null)
        {
            result = i.getImportURI().toString.contains(m.eResource().getURI().lastSegment())
        }
        return result
    }
}
