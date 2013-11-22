package de.bmw.yamaica.base.tests.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;
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

public class EMFHelper
{
    public static ResourceSet loadFilesIntoResourceSet(ResourceSet resourceSet, List<URI> files, String[] fileExtensions) throws Exception
    {
        Set<String> fileExtensionsSet = new HashSet<String>();

        if (null != fileExtensions)
        {
            Collections.addAll(fileExtensionsSet, fileExtensions);
        }

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

    public static List<Diff> compareResourceSets(ResourceSet resourceSet1, ResourceSet resourceSet2)
    {
        IComparisonScope scope = EMFCompare.createDefaultScope(resourceSet1, resourceSet2);
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
