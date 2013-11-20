/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.tests.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.osgi.framework.Bundle;

public class TransformationAdapterBase
{
    public static URI getFileUriFromBundleRelativePath(String bundleId, String bundleRelativePath) throws URISyntaxException, IOException
    {
        Bundle bundle = Platform.getBundle(bundleId);
        URL url = FileLocator.find(bundle, new Path(""), null);

        // Append bundle relative path after resolving URL to allow the creation of URI to an non existing file or folder.
        return URI.createURI(FileLocator.resolve(url).toURI().toString() + bundleRelativePath);
    }

    public static List<File> getFilesOfDirectory(File directory)
    {
        List<File> fileList = new LinkedList<File>();

        if (!directory.isDirectory())
        {
            return fileList;
        }

        for (File fileInDirectory : directory.listFiles())
        {
            if (fileInDirectory.isDirectory())
            {
                fileList.addAll(getFilesOfDirectory(fileInDirectory));
            }
            else
            {
                fileList.add(fileInDirectory);
            }
        }

        return fileList;
    }

    public static List<URI> toFileUriList(List<File> files)
    {
        List<URI> fileUriList = new LinkedList<URI>();

        for (File file : files)
        {
            fileUriList.add(URI.createFileURI(file.getPath()));
        }

        return fileUriList;
    }

    public static ResourceSet loadFilesIntoResourceSet(ResourceSet resourceSet, List<URI> files, String[] fileExtensions) throws Exception
    {
        Set<String> fileExtensionsSet = new HashSet<String>();
        Collections.addAll(fileExtensionsSet, fileExtensions);

        for (URI file : files)
        {
            if (null == fileExtensions || fileExtensionsSet.contains(new Path(file.toString()).getFileExtension()))
            {
                resourceSet.getResource(URI.createURI(file.toString()), true).load(Collections.EMPTY_MAP);
            }
        }

        return resourceSet;
    }

    public static void validateResourceSet(ResourceSet resourceSet) throws Exception
    {
        for (Resource resource : resourceSet.getResources())
        {
            for (EObject eObject : resource.getContents())
            {
                Diagnostic validate = Diagnostician.INSTANCE.validate(eObject);
                assertEquals(Diagnostic.OK, validate.getSeverity());
            }
        }
    }

    public static List<Diff> compareResourceSets(ResourceSet genResourceSet, ResourceSet refResourceSet)
    {
        IComparisonScope scope = EMFCompare.createDefaultScope(genResourceSet, refResourceSet);
        Comparison comparison = EMFCompare.builder().build().compare(scope);

        return comparison.getDifferences();
    }

    public static void printDifferences(List<Diff> differences)
    {
        if (!differences.isEmpty())
        {
            for (Diff difference : differences)
            {
                System.out.println(difference.toString());
            }

            fail("differences in model comparison found");
        }
    }
}
