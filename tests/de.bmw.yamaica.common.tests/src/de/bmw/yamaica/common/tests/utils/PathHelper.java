/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
        return getFilesOfDirectory(directory, null);
    }

    public static List<File> getFilesOfDirectory(File directory, String fileExtension)
    {
        List<File> fileList = new LinkedList<>();

        if (!directory.isDirectory())
        {
            return fileList;
        }

        for (File fileInDirectory : directory.listFiles())
        {
            if (fileInDirectory.isDirectory())
            {
                fileList.addAll(getFilesOfDirectory(fileInDirectory, fileExtension));
            }
            else if (fileInDirectory.isFile() && (null == fileExtension || fileInDirectory.getName().endsWith("." + fileExtension)))
            {
                fileList.add(fileInDirectory);
            }
        }

        return fileList;
    }

    public static boolean deleteFile(File file)
    {
        if (!file.exists())
        {
            return true;
        }

        if (file.isDirectory())
        {
            for (File childFile : file.listFiles())
            {
                if (childFile.isDirectory())
                {
                    deleteFile(childFile);
                }
                else
                {
                    childFile.delete();
                }
            }
        }

        return file.delete();
    }

    public static boolean deleteBundleFile(String bundleId, String bundleRelativePath) throws URISyntaxException, IOException
    {
        return deleteFile(ResourceUtils.getResourceFileFromBundle(bundleId, bundleRelativePath));
    }

    public static List<URI> toFileUriList(List<File> files)
    {
        List<URI> fileUriList = new LinkedList<>();

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
