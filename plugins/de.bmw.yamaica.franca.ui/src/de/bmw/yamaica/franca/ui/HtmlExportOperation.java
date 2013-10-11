/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.franca.core.dsl.FrancaIDLRuntimeModule;
import org.franca.core.dsl.FrancaPersistenceManager;
import org.franca.core.franca.FModel;
import org.franca.generators.FrancaGenerators;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.bmw.yamaica.base.core.utils.ResourceUtils;
import de.bmw.yamaica.base.ui.YamaicaUIConstants;

public class HtmlExportOperation implements IRunnableWithProgress
{
    private static final String HTML_FILE_EXTENSION = ".html";
    protected Injector                 injector;
    protected FrancaPersistenceManager francaPersistenceManager;
    protected IPath                    directoryPath;
    protected List<IResource>          resources;

    public HtmlExportOperation(IPath directoryPath, IContainer source, IOverwriteQuery overwriteImplementor, List<IResource> resources)
    {
        Assert.isNotNull(directoryPath);
        Assert.isNotNull(resources);

        this.directoryPath = directoryPath;
        this.resources = resources;

        injector = Guice.createInjector(new FrancaIDLRuntimeModule());
        francaPersistenceManager = injector.getInstance(FrancaPersistenceManager.class);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        int work = null != resources ? resources.size() : 0;

        monitor.beginTask(YamaicaUIConstants.EXPORTING_RESOURCES, work);

        for (IResource resource : resources)
        {
            String resourceName = resource.getName();
            IPath resourcePath = ResourceUtils.getPlatformRelativePath(resource.getLocation());
            IPath rootPath = ResourceUtils.getPlatformRelativePath(new Path(String.valueOf(Path.SEPARATOR)));

            URI resourcePathUri = URI.createURI(resourcePath.toString(), true);
            URI rootPathUri = URI.createURI(rootPath.toString(), true);

            monitor.subTask(resourceName);

            FModel francaModel = (FModel) francaPersistenceManager.loadModel(resourcePathUri, rootPathUri);

            monitor.subTask(resourceName + " -> " + francaModel.getName() + HTML_FILE_EXTENSION);

            FrancaGenerators.instance().genHTML(francaModel, directoryPath.toOSString());

            monitor.worked(1);

            if (monitor.isCanceled())
            {
                break;
            }
        }

        monitor.done();
    }
}
