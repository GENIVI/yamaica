/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.base.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.franca.core.franca.FModel;
import org.franca.deploymodel.dsl.fDeploy.FDModel;

public class FrancaResourceSetContainer
{
    protected final Map<FModel, Resource>  fModels       = new HashMap<FModel, Resource>();
    protected final Map<FDModel, Resource> fdModels      = new HashMap<FDModel, Resource>();
    protected final ResourceSet            resourceSet;
    protected final IPath                  rootSavePath;
    protected String                       headerComment = null;

    public FrancaResourceSetContainer(ResourceSet resourceSet, IPath rootSavePath)
    {
        this.resourceSet = resourceSet;
        this.rootSavePath = rootSavePath;
    }

    public void addModel(FModel fModel)
    {
        if (null == fModel || fModels.containsKey(fModel))
        {
            return;
        }

        IPath savePath = rootSavePath.append(FrancaUtils.getRelativeFidlPackagePath(fModel));

        Resource resource = resourceSet.createResource(URI.createURI(savePath.toString(), true));
        resource.getContents().add(fModel);

        fModels.put(fModel, resource);
    }

    public void addModels(FModel[] fmodels)
    {
        for (FModel fmodel : fmodels)
        {
            addModel(fmodel);
        }
    }

    public void addModel(Map.Entry<FModel, FDModel> model)
    {
        if (null == model)
        {
            return;
        }

        FModel fModel = model.getKey();
        FDModel fdModel = model.getValue();

        if (fdModels.containsKey(fdModel) || !fModels.containsKey(fModel))
        {
            return;
        }

        IPath savePath = rootSavePath.append(FrancaUtils.getRelativeFidlPackagePath(fModel).removeFileExtension()
                .addFileExtension(FrancaUtils.DEPLOYMENT_DEFINITION_FILE_EXTENSION));

        Resource resource = resourceSet.createResource(URI.createURI(savePath.toString(), true));
        resource.getContents().add(fdModel);

        fdModels.put(fdModel, resource);
    }

    public void addModels(Map<FModel, FDModel> fdModels)
    {
        for (Map.Entry<FModel, FDModel> model : fdModels.entrySet())
        {
            addModel(model);
        }
    }

    public IPath getRootSavePath()
    {
        return rootSavePath;
    }

    public ResourceSet getResourceSet()
    {
        return resourceSet;
    }

    public Resource getResource(FModel fModel)
    {
        return fModels.get(fModel);
    }

    public Resource[] getResources()
    {
        return fModels.values().toArray(new Resource[fModels.size()]);
    }

    public FModel[] getModels()
    {
        return fModels.keySet().toArray(new FModel[fModels.size()]);
    }

    public Map<FModel, IPath> getModelSavePaths()
    {
        Map<FModel, IPath> modelPaths = new HashMap<FModel, IPath>();

        for (Map.Entry<FModel, Resource> model : fModels.entrySet())
        {
            // Remove "platform:/resource" part from resource URI and make it relative to root folder
            IPath modelPath = new Path(model.getValue().getURI().toString()).makeRelativeTo(rootSavePath);

            modelPaths.put(model.getKey(), modelPath);
        }

        return modelPaths;
    }

    public Map<FDModel, IPath> getFDModelSavePaths()
    {
        Map<FDModel, IPath> fdModelPaths = new HashMap<FDModel, IPath>();

        for (Map.Entry<FDModel, Resource> fdModel : fdModels.entrySet())
        {
            // Remove "platform:/resource" part from resource URI and make it relative to root folder
            IPath fdModelPath = new Path(fdModel.getValue().getURI().toString()).makeRelativeTo(rootSavePath);

            fdModelPaths.put(fdModel.getKey(), fdModelPath);
        }

        return fdModelPaths;
    }

    public String getHeaderComment()
    {
        return headerComment;
    }

    public void setHeaderComment(String headerComment)
    {
        this.headerComment = headerComment;
    }

    public void save()
    {
        save(fModels.values());
        save(fdModels.values());
    }

    protected void save(Collection<Resource> resources)
    {
        Map<Object, Object> saveOptions = new HashMap<Object, Object>();
        saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, true);

        URIConverter uriConverter = resourceSet.getURIConverter();

        for (Resource resource : resources)
        {
            try
            {
                OutputStream outputStream = uriConverter.createOutputStream(resource.getURI(), saveOptions);

                writeFormatedHeaderComment(outputStream);
                resource.save(outputStream, saveOptions);

                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void writeFormatedHeaderComment(OutputStream outputStream) throws IOException
    {
        if (null == headerComment)
        {
            return;
        }

        final String newLineString = System.getProperty("line.separator");

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/*");
        stringBuilder.append(newLineString);

        for (String headerCommentLine : headerComment.split(newLineString))
        {
            stringBuilder.append(" * ");
            stringBuilder.append(headerCommentLine);
            stringBuilder.append(newLineString);
        }

        stringBuilder.append(" */");
        stringBuilder.append(newLineString);
        outputStreamWriter.write(stringBuilder.toString());
        outputStreamWriter.close();
    }
}
