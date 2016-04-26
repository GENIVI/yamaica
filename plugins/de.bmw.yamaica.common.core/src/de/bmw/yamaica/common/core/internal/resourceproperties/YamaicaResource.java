/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.internal.resourceproperties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.bmw.yamaica.common.core.resourceproperties.IResourcePropertyStore;
import de.bmw.yamaica.common.core.resourceproperties.YamaicaXmlModel;
import de.bmw.yamaica.common.core.utils.AbstractPropertyChangeSupport;

public class YamaicaResource extends AbstractPropertyChangeSupport implements IResourcePropertyStore
{
    private HashMap<String, String> properties = new HashMap<String, String>();
    private IResource               resource;
    private boolean                 needsSaving;
    private final YamaicaXmlModel   model;

    public YamaicaResource(IResource resource, YamaicaXmlModel model)
    {
        Assert.isNotNull(resource);

        this.resource = resource;
        this.model = model;
    }

    public void update(Element element)
    {
        // Create a new hash map with the current properties.
        HashMap<String, String> updatedPropertyElements = new HashMap<String, String>();
        NodeList nodes = element.getElementsByTagName(YamaicaXmlConstants.PROPERTY_TAG_NAME);

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element propertyElement = (Element) nodes.item(i);

            if (propertyElement.hasAttribute(YamaicaXmlConstants.NAME_ATTRIBUTE_NAME))
            {
                String propertyName = propertyElement.getAttribute(YamaicaXmlConstants.NAME_ATTRIBUTE_NAME);
                String propertyValue = propertyElement.getTextContent();

                updatedPropertyElements.put(propertyName, propertyValue);
            }
        }

        // Delete all properties from hash map which are not inside the updated hash map.
        Iterator<Map.Entry<String, String>> iterator = properties.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<String, String> propertyEntry = iterator.next();
            String propertyName = propertyEntry.getKey();

            if (!updatedPropertyElements.containsKey(propertyName))
            {
                String oldValue = propertyEntry.getValue();

                iterator.remove();

                propertyChangeSupport.firePropertyChange(propertyName, oldValue, null);
            }
        }

        // Overwrite all properties of the hash map with the values of the updated hash map.
        // We have to do it that way to correctly fire the property change event.
        for (Map.Entry<String, String> propertyEntry : updatedPropertyElements.entrySet())
        {
            setProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }
    }

    public void save(Element element)
    {
        // Create a new hash map with the available properties in the DOM.
        HashMap<String, Element> availablePropertyElements = new HashMap<String, Element>();
        NodeList nodes = element.getElementsByTagName(YamaicaXmlConstants.PROPERTY_TAG_NAME);

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element propertyElement = (Element) nodes.item(i);

            if (propertyElement.hasAttribute(YamaicaXmlConstants.NAME_ATTRIBUTE_NAME))
            {
                String propertyName = propertyElement.getAttribute(YamaicaXmlConstants.NAME_ATTRIBUTE_NAME);

                availablePropertyElements.put(propertyName, propertyElement);
            }
        }

        // Delete all properties from the DOM which are not inside the current hash map.
        Iterator<Map.Entry<String, Element>> iterator = availablePropertyElements.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<String, Element> propertyEntry = iterator.next();

            if (!properties.containsKey(propertyEntry.getKey()))
            {
                element.removeChild(propertyEntry.getValue());
                iterator.remove();
            }
        }

        // Write all properties either to the existing DOM elements or to newly created
        // DOM elements.
        for (Map.Entry<String, String> propertyEntry : properties.entrySet())
        {
            Element propertyElement;
            String propertyName = propertyEntry.getKey();

            if (availablePropertyElements.containsKey(propertyName))
            {
                propertyElement = availablePropertyElements.get(propertyName);
            }
            else
            {
                propertyElement = element.getOwnerDocument().createElement(YamaicaXmlConstants.PROPERTY_TAG_NAME);
                element.appendChild(propertyElement);
            }

            propertyElement.setAttribute(YamaicaXmlConstants.NAME_ATTRIBUTE_NAME, propertyName);
            propertyElement.setTextContent(propertyEntry.getValue());
        }

        needsSaving = false;
    }

    public void setProperty(String name, String value)
    {
        Assert.isNotNull(name);

        String oldValue = null;

        if (null == value)
        {
            oldValue = properties.remove(name);
        }
        else
        {
            oldValue = properties.put(name, value);
        }

        needsSaving = true;
        model.needsSaving(true);
        propertyChangeSupport.firePropertyChange(name, oldValue, value);
    }

    public String getProperty(String name)
    {
        Assert.isNotNull(name);

        if (properties.containsKey(name))
        {
            return properties.get(name);
        }

        return null;
    }

    public String getProperty(String name, String value)
    {
        String property = getProperty(name);

        return null != property ? property : value;
    }

    public String[] getPropertyNames()
    {
        return properties.keySet().toArray(new String[properties.size()]);
    }

    public int getPropertyCount()
    {
        return properties.size();
    }

    public boolean hasProperty(String name)
    {
        return properties.containsKey(name);
    }

    public IResource getResource()
    {
        return resource;
    }

    public boolean needsSaving()
    {
        return needsSaving;
    }

    public void dispose()
    {

    }
}
