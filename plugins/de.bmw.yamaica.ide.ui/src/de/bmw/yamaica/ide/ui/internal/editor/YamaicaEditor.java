/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.bmw.yamaica.ide.ui.internal.Activator;

public class YamaicaEditor extends FormEditor implements IResourceChangeListener
{
    private FormToolkit formToolkit;
    private FormPage    overviewPage;

    public YamaicaEditor()
    {

    }

    @Override
    protected FormToolkit createToolkit(Display display)
    {
        formToolkit = new FormToolkit(Activator.getDefault().getFormColors(display));

        return formToolkit;
    }

    @Override
    protected void addPages()
    {
        try
        {
            overviewPage = new OverviewPage(this, "de.bmw.yamaica.common.core.editor.main", "Overview");

            addPage(overviewPage);

            // Disable tab bar
            ((CTabFolder) getContainer()).setTabHeight(0);
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {

    }

    @Override
    public void doSaveAs()
    {

    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
    }

    @Override
    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        super.dispose();
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        IFileEditorInput fileEditorInput = (IFileEditorInput) getEditorInput();
        IFile file = fileEditorInput.getFile();
        List<IResourceDelta> resourceDeltas = getAllResourceDeltas(event.getDelta());

        for (IResourceDelta resourceDelta : resourceDeltas)
        {
            if (resourceDelta.getResource().equals(file))
            {
                final int kind = resourceDelta.getKind();

                if (kind == IResourceDelta.REMOVED || kind == IResourceDelta.REMOVED_PHANTOM)
                {
                    close(true);

                    return;
                }
            }
        }
    }

    protected List<IResourceDelta> getAllResourceDeltas(IResourceDelta resourceDelta)
    {
        List<IResourceDelta> list = new LinkedList<IResourceDelta>();

        if (null == resourceDelta)
        {
            return list;
        }

        list.add(resourceDelta);

        for (IResourceDelta childResourceDelta : resourceDelta.getAffectedChildren())
        {
            list.addAll(getAllResourceDeltas(childResourceDelta));
        }

        return list;
    }
}
