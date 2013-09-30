/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
 package de.bmw.yamaica.franca.core

import java.util.List
import org.franca.deploymodel.dsl.fDeploy.FDPropertyHost
import org.franca.deploymodel.dsl.fDeploy.FDSpecification
import org.franca.deploymodel.dsl.fDeploy.FDeployFactory

class DeploymentDescriptionModelCreator {

    def create FDeployFactory::eINSTANCE.createFDModel createModel(String name, List<FDPropertyHost> propertyHosts) {
        val specification = FDeployFactory::eINSTANCE.createFDSpecification
        specification.name = name
        specification.addPropertyHosts(propertyHosts)

        specifications.add(specification)
    }

    def private addPropertyHosts(FDSpecification specification, List<FDPropertyHost> propertyHosts) {
        propertyHosts.forEach [
            val declaration = FDeployFactory::eINSTANCE.createFDDeclaration
            declaration.host = it
            specification.declarations.add(declaration)
        ]
    }
}
