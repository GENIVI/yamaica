package de.bmw.yamaica.common.tests.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import de.bmw.yamaica.common.core.utils.ResourceUtils;

public class PathHelper
{
    public static URI getFileUriFromBundleRelativePath(String bundleId, String bundleRelativePath) throws URISyntaxException, IOException
    {
        return createFileUri(ResourceUtils.getResourceFileFromBundle(bundleId, bundleRelativePath));
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
        return deleteFolder(ResourceUtils.getResourceFileFromBundle(bundleId, bundleRelativePath));
    }

    public static List<URI> toFileUriList(List<File> files)
    {
        List<URI> fileUriList = new LinkedList<URI>();

        for (File file : files)
        {
            fileUriList.add(createFileUri(file));
        }

        return fileUriList;
    }

    public static URI createFileUri(File file)
    {
        return URI.createFileURI(file.getPath());
    }
}
