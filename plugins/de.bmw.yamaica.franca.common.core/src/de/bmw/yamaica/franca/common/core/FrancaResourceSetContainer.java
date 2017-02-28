/* Copyright (C) 2013-2016 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.xtext.parsetree.reconstr.XtextSerializationException;
import org.franca.core.franca.FModel;
import org.franca.deploymodel.dsl.fDeploy.FDModel;

import de.bmw.yamaica.common.core.YamaicaConstants;

public class FrancaResourceSetContainer
{
    protected final Map<FModel, Resource>  fModels       = new LinkedHashMap<FModel, Resource>();
    protected final Map<FDModel, Resource> fdModels      = new LinkedHashMap<FDModel, Resource>();

    protected final ResourceSet            resourceSet;
    protected final IPath                  rootSavePath;
    protected String                       headerComment = null;

    // Stores each FModel object paired with the origin file name.
    private final Map<FModel, String>      fileNameCache = new HashMap<>();

    private final Map<FModel, Resource>    fidlErrorCache= new LinkedHashMap<FModel, Resource>();
    private final Map<FDModel, Resource>   fdeplErrorCache= new LinkedHashMap<FDModel, Resource>();
    private static final Logger            LOGGER        = Logger.getLogger(FrancaResourceSetContainer.class.getName());

    public FrancaResourceSetContainer(ResourceSet resourceSet, IPath rootSavePath)
    {
        this.resourceSet = resourceSet;
        this.rootSavePath = rootSavePath;
    }

    /**
     * Add a Franca model to the resource set.
     * The URI for the model is derived from the Franca model package name.
     *
     * @param fModel Franca model
     */
    public void addModel(FModel fModel)
    {
        addModel(fModel, fModel.getName());
    }

    /**
     * Add a Franca model to the resource set.
     * The URI for the model is derived from the explicitly specified Franca model package name.
     *
     * @param fModel Franca model
     * @param fModelName Franc model package name to be used for deriving the URI
     */
    public void addModel(FModel fModel, String fModelName)
    {
        if (null == fModel || fModels.containsKey(fModel))
        {
            return;
        }
        if (fModel.eResource() != null)
        {
            resourceSet.getResources().add(fModel.eResource());
        }
        final IPath savePath = rootSavePath.append(FrancaUtils.getRelativeFidlPackagePath(fModelName));

        final Resource resource = createResourceByURI(savePath, fModel);
        resource.getContents().add(fModel);

        fModels.put(fModel, resource);
    }

    private Resource createResourceByURI(IPath savePath, FModel fModel)
    {
        final URI uri = URI.createURI(FrancaUtils.restoreOriginFileName(fileNameCache, savePath, fModel).toString(), true);

        LOGGER.log(Level.FINEST, String.format("Creating resource with URI: '%s'", uri));

        return resourceSet.createResource(uri);
    }

    /**
     * Add Franca models to resource set container.
     * The URIs for the models are derived from the Franca model package names.
     *
     * @param fmodels Franca models
     */
    public void addModels(Collection<FModel> fmodels)
    {
        for (FModel fmodel : fmodels)
        {
            addModel(fmodel);
        }
    }

    /**
     * Add a Franca Deployment model to the resource set container.
     * The URI for the Franca Deployment model is derived from the associated Franca model package name.
     *
     * @param model Franca model and Franca Deployment model
     */
    public void addModel(Map.Entry<FModel, FDModel> model)
    {
        addModel(model, true);
    }

    /**
     * Add a Franca Deployment model to the resource set container.
     * The URI for the Franca Deployment model is derived from the associated Franca model package name or
     * from the associated Franca model URI.
     *
     * @param model Franca model and Franca Deployment model
     * @param deriveDeploymentUriFromPackage How to derive the URI for the Franca Deployment model
     */
    public void addModel(Map.Entry<FModel, FDModel> model, boolean deriveDeploymentUriFromPackage)
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
        if (fModel.eResource() != null)
        {
            resourceSet.getResources().add(fModel.eResource());
        }
        if (fdModel.eResource() != null)
        {
            resourceSet.getResources().add(fdModel.eResource());
        }

        IPath savePath;
        if (deriveDeploymentUriFromPackage)
        {
            savePath = rootSavePath.append(FrancaUtils.getRelativeFidlPackagePath(fModel).removeFileExtension()
                    .addFileExtension(FrancaUtils.DEPLOYMENT_DEFINITION_FILE_EXTENSION));
        }
        else
        {
            URI fidlUri = fModel.eResource().getURI();
            IPath fidlPath = new Path(fidlUri.toString());
            savePath = fidlPath.removeFileExtension()
                    .addFileExtension(FrancaUtils.DEPLOYMENT_DEFINITION_FILE_EXTENSION);
        }

        final Resource resource = createResourceByURI(savePath, fModel);
        resource.getContents().add(fdModel);

        fdModels.put(fdModel, resource);
    }

    /**
     * Add a Franca Deployment model with a specified URI.
     * This function should be used only for Franca Deployment models which do not have an associated Franca model
     *
     * @param fdModel Franca Deployment model
     * @param path URI for the model file
     */
    public void addFDModel(FDModel fdModel, URI path)
    {
        if (fdModels.containsKey(fdModel))
        {
            return;
        }
        if (fdModel.eResource() != null)
        {
            resourceSet.getResources().add(fdModel.eResource());
        }
        Resource resource = resourceSet.createResource(path);
        resource.getContents().add(fdModel);
        fdModels.put(fdModel, resource);
    }

    /**
     * Adds to ResourceSet existing deployment files.
     */
    public void addFDModels(Collection<FDModel> fdModels)
    {
        for (FDModel fdModel : fdModels)
        {
            if (fdModel.eResource() != null)
            {
                resourceSet.getResources().add(fdModel.eResource());
            }
        }
    }

    public void addModels(Map<FModel, FDModel> fdModels)
    {
        addModels(fdModels, true);
    }

    public void addModels(Map<FModel, FDModel> fdModels, boolean deriveDeploymentUriFromPackage)
    {
        for (Map.Entry<FModel, FDModel> model : fdModels.entrySet())
        {
            addModel(model, deriveDeploymentUriFromPackage);
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

    public Map<FModel, Resource> getErrorCache()
    {
        return fidlErrorCache;
    }

    public Map<FDModel, Resource> getFDModelsErrorCache()
    {
        return fdeplErrorCache;
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
        Map<FModel, IPath> modelPaths = new HashMap<>();

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
        Map<FDModel, IPath> fdModelPaths = new HashMap<>();

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
        Map<Object, Object> saveOptions = new HashMap<>();
        saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, true);

        URIConverter uriConverter = resourceSet.getURIConverter();

        for (Resource resource : resources)
        {
            try (OutputStream outputStream = uriConverter.createOutputStream(resource.getURI(), saveOptions))
            {
                writeFormatedHeaderComment(outputStream);
                resource.save(outputStream, saveOptions);
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            catch (XtextSerializationException e)
            {
                if (resource.getContents() != null)
                {
                    EObject eObject = resource.getContents().get(0);
                    if (eObject instanceof FModel)
                    {
                        // Store all corrupt fidl-files from FrancaResourceSetContainer in errorCache
                        FModel model = (FModel) eObject;
                        if (model != null)
                        {
                            if (!fidlErrorCache.containsKey(model))
                            {
                                fidlErrorCache.put(model, resource);
                            }
                        }
                    }
                    else if (eObject instanceof FDModel)
                    {
                        // Store all corrupt fdepl-files from FrancaResourceSetContainer in errorCache
                        FDModel model = (FDModel) eObject;
                        if (model != null)
                        {
                            if (!fdeplErrorCache.containsKey(model))
                            {
                                fdeplErrorCache.put(model, resource);
                            }
                        }
                    }
                }
                LOGGER.log(Level.SEVERE, String.format("Could not save resource [ %s ]. Exception : %s", resource.getURI(), e.getMessage()));
            }
        }
    }

    public void save(FModel fModel)
    {
        Resource resource = fModels.get(fModel);

        Map<Object, Object> saveOptions = new HashMap<>();
        saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, true);

        URIConverter uriConverter = resourceSet.getURIConverter();

        try (OutputStream outputStream = uriConverter.createOutputStream(resource.getURI(), saveOptions))
        {
            writeFormatedHeaderComment(outputStream);
            resource.save(outputStream, saveOptions);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        catch (XtextSerializationException e)
        {
            if (resource.getContents() != null)
            {
                EObject eObject = resource.getContents().get(0);
                if (eObject instanceof FModel)
                {
                    // Store all corrupt fidl-files from FrancaResourceSetContainer in errorCache
                    FModel model = (FModel) eObject;
                    if (model != null)
                    {
                        if (!fidlErrorCache.containsKey(model))
                        {
                            fidlErrorCache.put(model, resource);
                        }
                    }
                }
            }
            LOGGER.log(Level.SEVERE, String.format("Could not save resource [ %s ]. Exception : %s", resource.getURI(), e.getMessage()));
        }
    }

    protected void writeFormatedHeaderComment(OutputStream outputStream) throws IOException
    {
        if (null == headerComment)
        {
            return;
        }

        final String newLineString = System.getProperty(YamaicaConstants.LINE_SEPARATOR);

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
        outputStreamWriter.flush(); // outputStreamWriter.close(); Exception by console Ticket-414
    }

    /**
     * Clears the file name cache and copies all entries of originFileNames.
     *
     * @param originFileNames
     *            Map of origin file names.
     */
    public void addOriginFileNames(Map<FModel, String> originFileNames)
    {
        fileNameCache.clear();
        fileNameCache.putAll(originFileNames);
    }

    public void addOriginFileName(FModel fModel, String originFileName)
    {
        fileNameCache.put(fModel, originFileName);
    }
}
