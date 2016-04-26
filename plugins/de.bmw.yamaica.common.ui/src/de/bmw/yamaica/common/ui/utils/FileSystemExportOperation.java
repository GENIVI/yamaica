/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import de.bmw.yamaica.common.ui.YamaicaUIConstants;

public class FileSystemExportOperation implements IRunnableWithProgress
{
    protected IPath           directoryPath;
    protected IContainer      source;
    protected IOverwriteQuery overwriteImplementor;
    protected List<IResource> resources;

    protected boolean         showOverwriteQuery = true;
    protected boolean         overwrite          = false;

    public FileSystemExportOperation(IPath directoryPath, IContainer source, IOverwriteQuery overwriteImplementor, List<IResource> resources)
    {
        Assert.isNotNull(directoryPath);
        Assert.isNotNull(source);
        Assert.isNotNull(overwriteImplementor);
        Assert.isNotNull(resources);

        this.directoryPath = directoryPath;
        this.source = source;
        this.overwriteImplementor = overwriteImplementor;
        this.resources = resources;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        int work = (null != resources) ? resources.size() : 0;
        IPath sourceContainerSystemPath = source.getLocation();

        monitor.beginTask(YamaicaUIConstants.EXPORTING_RESOURCES, work);

        for (IResource resource : resources)
        {
            boolean writeFile = false;
            IPath resourceSystemPath = resource.getLocation();

            monitor.subTask(resourceSystemPath.lastSegment());

            File destinationFile = directoryPath.append(resourceSystemPath.makeRelativeTo(sourceContainerSystemPath)).toFile();

            if (destinationFile.exists())
            {
                if (showOverwriteQuery)
                {
                    String code = overwriteImplementor.queryOverwrite(destinationFile.getPath());

                    if (code.equals(IOverwriteQuery.ALL))
                    {
                        overwrite = true;
                        showOverwriteQuery = false;
                    }
                    else if (code.equals(IOverwriteQuery.CANCEL))
                    {
                        throw new OperationCanceledException();
                    }
                    else if (code.equals(IOverwriteQuery.NO))
                    {
                        overwrite = false;
                    }
                    else if (code.equals(IOverwriteQuery.NO_ALL))
                    {
                        overwrite = false;
                        showOverwriteQuery = false;
                    }
                    else if (code.equals(IOverwriteQuery.YES))
                    {
                        overwrite = true;
                    }
                }

                writeFile = overwrite;
            }
            else
            {
                try
                {
                    File parentDirectory = destinationFile.getParentFile();

                    if ((parentDirectory.exists() || parentDirectory.mkdirs()) && destinationFile.createNewFile())
                    {
                        writeFile = true;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (writeFile)
            {
                FileChannel inChannel = null;
                FileChannel outChannel = null;

                try
                {
                    inChannel = new FileInputStream(resourceSystemPath.toOSString()).getChannel();
                    outChannel = new FileOutputStream(destinationFile).getChannel();

                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (null != inChannel)
                        {
                            inChannel.close();
                        }

                        if (null != outChannel)
                        {
                            outChannel.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            monitor.worked(1);

            if (monitor.isCanceled())
            {
                break;
            }
        }

        monitor.done();

        showOverwriteQuery = true;
        overwrite = false;
    }

    public void setOverwriteFiles(boolean value)
    {
        showOverwriteQuery = !value;
        overwrite = value;
    }
}
