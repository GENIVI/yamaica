/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.resourceproperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.bmw.yamaica.common.core.YamaicaConstants;
import de.bmw.yamaica.common.core.internal.Activator;
import de.bmw.yamaica.common.core.internal.resourceproperties.YamaicaResource;
import de.bmw.yamaica.common.core.internal.resourceproperties.YamaicaXmlConstants;
import de.bmw.yamaica.common.core.utils.AbstractPropertyChangeSupport;

public class YamaicaXmlModel extends AbstractPropertyChangeSupport implements IResourceChangeListener
{
    private static HashMap<IPath, YamaicaXmlModel> instances;

    private final String                           VERSION_NUMBER = "1.0";

    public static synchronized YamaicaXmlModel acquireInstance(IResource resource, Object reference)
    {
        Assert.isNotNull(reference);

        IFile file = getSettingsFile(resource);
        IPath path = file.getLocation();

        if (null == instances)
        {
            instances = new HashMap<IPath, YamaicaXmlModel>();
        }

        YamaicaXmlModel instance;

        if (instances.containsKey(path))
        {
            instance = instances.get(path);
        }
        else
        {
            instance = new YamaicaXmlModel(file);
            instances.put(path, instance);
        }

        // Add the reference to the list of objects which are using
        // this instance.
        if (!instance.referencingObjects.contains(reference))
        {
            instance.referencingObjects.add(reference);
        }

        return instance;
    }

    public static synchronized boolean releaseInstance(IResource resource, Object reference)
    {
        Assert.isNotNull(reference);

        IFile file = getSettingsFile(resource);

        if (null == file)
        {
            return false;
        }

        IPath path = file.getLocation();

        if (null == instances || !instances.containsKey(path))
        {
            return false;
        }

        YamaicaXmlModel instance = instances.get(path);

        if (instance.referencingObjects.contains(reference))
        {
            // Remove the reference from the list of objects which are
            // using this instance.
            instance.referencingObjects.remove(reference);

            if (instance.referencingObjects.size() == 0)
            {
                instance.dispose();

                removeHashMapEntry(path);
            }

            return true;
        }

        return false;
    }

    public static IFile getSettingsFile(IResource resource)
    {
        Assert.isNotNull(resource);

        if (!(resource instanceof IFile) && !(resource instanceof IProject))
        {
            throw new IllegalArgumentException("The referred resource must be of the type IFile or IProject.");
        }

        if (resource instanceof IFile)
        {
            IFile file = (IFile) resource;
            IFile destinationFile;

            if (file.exists()
                    && null != (destinationFile = file.getProject().getWorkspace().getRoot().getFileForLocation(file.getLocation())))
            {
                return destinationFile;
            }

            return file;
        }
        else
        {
            IProject project = (IProject) resource;

            if (project.exists())
            {
                IFolder projectSettingsFolder = project.getFolder(new ProjectScope(project).getLocation().makeRelativeTo(
                        resource.getLocation()));

                IFile defaultSettingsFile = projectSettingsFolder.getFile(Activator.PLUGIN_ID + "." + YamaicaConstants.XML_FILE_EXTENSION);

                if (defaultSettingsFile.exists())
                {
                    return defaultSettingsFile;
                }

                IFile oldSettingsFile = projectSettingsFolder.getFile(YamaicaConstants.OLD_SETTINGS_FILENAME + "."
                        + YamaicaConstants.XML_FILE_EXTENSION);

                if (oldSettingsFile.exists())
                {
                    return oldSettingsFile;
                }

                return defaultSettingsFile;
            }
        }

        return null;
    }

    private static void removeHashMapEntry(IPath path)
    {
        // Remove the model object from instances list if no
        // object instance does reference it.
        instances.remove(path);
        System.out.println("Deleted singleton because no object references it.");

        if (instances.size() == 0)
        {
            // Free the hash map if it is empty.
            instances = null;
            System.out.println("Deleted model hash map because no singelton is available.");
        }
    }

    /* ********************************************************************** */

    private Set<Object>                     referencingObjects = new HashSet<Object>();
    private HashMap<IPath, YamaicaResource> resources          = new HashMap<IPath, YamaicaResource>();
    private final IFile                     settingsFile;
    private final IPath                     settingsFilePath;
    private Document                        document           = null;
    private boolean                         needsSaving        = false;
    private boolean                         isDisposed         = false;

    private YamaicaXmlModel(IFile settingsFile)
    {
        Assert.isNotNull(settingsFile);

        this.settingsFile = settingsFile;
        this.settingsFilePath = settingsFile.getLocation();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

        update();
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        // Check if the resource of this model instance was edited.
        IResourceDelta resourceDelta = event.getDelta();

        if (null != resourceDelta)
        {
            IResourceDelta settingsFileResourceDelta = resourceDelta.findMember(settingsFile.getFullPath());

            if (null != settingsFileResourceDelta)
            {
                final int kind = settingsFileResourceDelta.getKind();

                if (kind == IResourceDelta.REMOVED || kind == IResourceDelta.REMOVED_PHANTOM)
                {
                    dispose();

                    removeHashMapEntry(settingsFilePath);
                }
                else
                {
                    update();
                }
            }
        }
    }

    protected void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        isDisposed = true;
    }

    public boolean isDisposed()
    {
        return isDisposed;
    }

    public void update()
    {
        document = getSettingsFileDocument();

        update(document.getDocumentElement());
    }

    private void update(Element element)
    {
        // Create a new hash map with the current resources.
        HashMap<IPath, YamaicaResource> updatedResources = new HashMap<IPath, YamaicaResource>();
        NodeList nodes = element.getElementsByTagName(YamaicaXmlConstants.RESOURCE_TAG_NAME);

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element resourceElement = (Element) nodes.item(i);

            if (resourceElement.hasAttribute(YamaicaXmlConstants.PATH_ATTRIBUTE_NAME))
            {
                IPath path = new Path(resourceElement.getAttribute(YamaicaXmlConstants.PATH_ATTRIBUTE_NAME));
                YamaicaResource yamaicaResource = null;

                if (resources.containsKey(path))
                {
                    yamaicaResource = resources.get(path);
                }
                else
                {
                    IResource resource = getResourceFromPath(path);

                    if (null != resource)
                    {
                        yamaicaResource = new YamaicaResource(resource, this);
                    }
                }

                if (null != yamaicaResource)
                {
                    yamaicaResource.update(resourceElement);
                    updatedResources.put(path, yamaicaResource);
                }
            }
        }

        resources = updatedResources;
    }

    public void save()
    {
        try
        {
            if (needsSaving && null != document)
            {
                save(document.getDocumentElement());

                if (settingsFile.exists())
                {
                    settingsFile.setContents(convertDocumentToInputStream(document), IResource.NONE, null);
                }
                else
                {
                    createFile(settingsFile, convertDocumentToInputStream(document), IResource.NONE);
                }

                needsSaving = false;
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    private void save(Element element)
    {
        // Create a new hash map with the available resources in the DOM.
        HashMap<IPath, Element> availableResourceElements = new HashMap<IPath, Element>();
        NodeList nodes = element.getElementsByTagName(YamaicaXmlConstants.RESOURCE_TAG_NAME);

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element availableResourceElement = (Element) nodes.item(i);

            if (availableResourceElement.hasAttribute(YamaicaXmlConstants.PATH_ATTRIBUTE_NAME))
            {
                IPath resourcePath = new Path(availableResourceElement.getAttribute(YamaicaXmlConstants.PATH_ATTRIBUTE_NAME));

                availableResourceElements.put(resourcePath, availableResourceElement);
            }
        }

        // Delete all resources from the DOM which are either not inside the current hash map or
        // which have no properties.
        Iterator<Map.Entry<IPath, Element>> iterator = availableResourceElements.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<IPath, Element> resourceEntry = iterator.next();
            IPath path = resourceEntry.getKey();

            if (!resources.containsKey(path) || resources.get(path).getPropertyCount() == 0)
            {
                element.removeChild(resourceEntry.getValue());
                iterator.remove();
            }
        }

        // Write all resources either to the existing DOM elements or to newly created
        // DOM elements.
        for (Map.Entry<IPath, YamaicaResource> resourceEntry : resources.entrySet())
        {
            Element resourceElement;
            IPath path = resourceEntry.getKey();

            if (availableResourceElements.containsKey(path))
            {
                resourceElement = availableResourceElements.get(path);
            }
            else
            {
                resourceElement = element.getOwnerDocument().createElement(YamaicaXmlConstants.RESOURCE_TAG_NAME);
                element.appendChild(resourceElement);
            }

            resourceElement.setAttribute(YamaicaXmlConstants.PATH_ATTRIBUTE_NAME, path.toString());
            resourceEntry.getValue().save(resourceElement);
        }

        needsSaving = false;
    }

    public synchronized IResourcePropertyStore getResourcePropertyStore(IResource resource)
    {
        Assert.isNotNull(resource);

        IPath resourcePath = resource.getProjectRelativePath().makeAbsolute();

        if (resources.containsKey(resourcePath))
        {
            return resources.get(resourcePath);
        }
        else
        {
            YamaicaResource yamaicaResource = new YamaicaResource(resource, this);

            resources.put(resourcePath, yamaicaResource);

            return yamaicaResource;
        }
    }

    public int getResourceCount()
    {
        return resources.size();
    }

    public boolean needsSaving()
    {
        if (needsSaving)
        {
            return true;
        }

        for (YamaicaResource yamaicaResource : resources.values())
        {
            if (yamaicaResource.needsSaving())
            {
                return true;
            }
        }

        return false;
    }

    public void needsSaving(boolean value)
    {
        needsSaving = value;
    }

    private void createFolder(IFolder folder, int updateFlags)
    {
        IContainer parent = folder.getParent();

        if (!parent.exists() && parent instanceof IFolder)
        {
            createFolder((IFolder) parent, updateFlags);
        }

        if (!folder.exists())
        {
            try
            {
                folder.create(updateFlags, true, null);
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void createFile(IFile file, InputStream inputStream, int updateFlags)
    {
        IContainer parent = file.getParent();

        if (!parent.exists() && parent instanceof IFolder)
        {
            createFolder((IFolder) parent, updateFlags);
        }

        if (!file.exists())
        {
            try
            {
                if (null == inputStream)
                {
                    inputStream = new ByteArrayInputStream(new byte[0]);
                }

                file.create(inputStream, updateFlags, null);
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    private InputStream convertDocumentToInputStream(Document document)
    {
        Assert.isNotNull(document);

        try
        {
            document.setXmlStandalone(true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
        catch (TransformerFactoryConfigurationError e)
        {
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private Document getSettingsFileDocument()
    {
        try
        {
            if (!settingsFile.exists())
            {
                return getInitialSettingsFileDocument();
            }

            if (!settingsFile.isSynchronized(IResource.DEPTH_ZERO))
            {
                settingsFile.refreshLocal(IResource.DEPTH_ZERO, null);
            }

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(settingsFile.getContents());

            if (document.getDocumentElement().getTagName().equals(YamaicaXmlConstants.ROOT_TAG_NAME))
            {
                return document;
            }
            else
            {
                return getInitialSettingsFileDocument();
            }
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();

            return getInitialSettingsFileDocument();
        }
        catch (SAXException e)
        {
            e.printStackTrace();

            return getInitialSettingsFileDocument();
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return getInitialSettingsFileDocument();
        }
        catch (CoreException e)
        {
            e.printStackTrace();

            return getInitialSettingsFileDocument();
        }
    }

    private Document getInitialSettingsFileDocument()
    {
        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            document.appendChild(document.createComment("––––––––––––––––––––––––––––––––––––––––––––––"));
            document.appendChild(document.createComment(" This file is not meant to be edited by hand! "));
            document.appendChild(document.createComment("––––––––––––––––––––––––––––––––––––––––––––––"));

            Element rootElement = document.createElement(YamaicaXmlConstants.ROOT_TAG_NAME);
            rootElement.setAttribute("version", VERSION_NUMBER);
            document.appendChild(rootElement);

            needsSaving = true;

            return document;
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private IResource getResourceFromPath(IPath path)
    {
        IPath fullPath = settingsFile.getProject().getFullPath().append(path.makeRelative());

        return ResourcesPlugin.getWorkspace().getRoot().findMember(fullPath);
    }
}
