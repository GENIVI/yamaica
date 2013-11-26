package de.bmw.yamaica.common.tests.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

public class PathHelper
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

    public static boolean deleteFolder(File folder)
    {
        if (!folder.exists())
        {
            return true;
        }

        for (File childFile : folder.listFiles())
        {
            if (childFile.isDirectory())
            {
                deleteFolder(childFile);
            }
            else
            {
                childFile.delete();
            }
        }

        return folder.delete();
    }

    public static boolean deleteBundleFolder(String bundleId, String bundleRelativePath) throws URISyntaxException, IOException
    {
        return deleteFolder(new File(getFileUriFromBundleRelativePath(bundleId, bundleRelativePath).toFileString()));
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
}
