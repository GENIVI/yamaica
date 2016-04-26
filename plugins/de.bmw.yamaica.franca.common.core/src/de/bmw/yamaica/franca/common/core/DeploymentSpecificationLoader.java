/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.franca.deploymodel.dsl.fDeploy.FDModel;
import org.franca.deploymodel.dsl.fDeploy.FDSpecification;

public class DeploymentSpecificationLoader
{
    protected final ResourceSet resourceSet;
    protected FDModel           model;
    private final String        specificationURI;

    public String getSpecificationURI()
    {
        return specificationURI;
    }

    public DeploymentSpecificationLoader(ResourceSet resourceSet, URI uri)
    {
        this.resourceSet = resourceSet;

        specificationURI = uri.toString();

        try
        {
            Resource resource = resourceSet.getResource(uri, true);
            resource.load(Collections.EMPTY_MAP);
            this.model = (FDModel) resource.getContents().get(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public DeploymentSpecificationLoader(ResourceSet resourcesSet, String pluginId, String path)
    {
        this(resourcesSet, URI.createPlatformPluginURI(new Path(pluginId).append(path).toString(), true));
    }

    public ResourceSet getResourceSet()
    {
        return resourceSet;
    }

    public FDSpecification getDeploymentSpecificationByName(String name)
    {
        for (FDSpecification specification : getDeploymentSpecifications())
        {
            if (specification.getName().equals(name))
            {
                return specification;
            }
        }

        return null;
    }

    public Collection<FDSpecification> getDeploymentSpecifications()
    {
        return model.getSpecifications();
    }
}
