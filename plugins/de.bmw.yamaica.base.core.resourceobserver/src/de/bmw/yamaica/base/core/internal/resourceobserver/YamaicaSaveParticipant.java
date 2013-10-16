/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.core.internal.resourceobserver;

import java.io.File;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class YamaicaSaveParticipant implements ISaveParticipant
{
    private static YamaicaSaveParticipant instance = null;

    private YamaicaSaveParticipant() throws CoreException
    {
        ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(Activator.PLUGIN_ID, this);

        if (lastState == null)
        {
            return;
        }

        lastState.processResourceChangeEvents(YamaicaResourceUpdater.getInstance());
        IPath location = lastState.lookup(new Path(ResourceObserverConstants.SAVE));

        if (null == location)
        {
            return;
        }

        // the plugin instance should read any important state from the file.
        File file = Activator.getDefault().getStateLocation().append(location).toFile();
        readStateFrom(file);
    }

    protected void readStateFrom(File target)
    {

    }

    protected void writeImportantState(File target)
    {

    }

    public static synchronized YamaicaSaveParticipant getInstance()
    {
        if (null == instance)
        {
            try
            {
                instance = new YamaicaSaveParticipant();
            }
            catch (CoreException e)
            {
                e.printStackTrace();

                return null;
            }
        }

        return instance;
    }

    public synchronized void dispose()
    {
        ResourcesPlugin.getWorkspace().removeSaveParticipant(Activator.PLUGIN_ID);

        instance = null;
    }

    @Override
    public void doneSaving(ISaveContext context)
    {

    }

    @Override
    public void prepareToSave(ISaveContext context) throws CoreException
    {

    }

    @Override
    public void rollback(ISaveContext context)
    {

    }

    @Override
    public void saving(ISaveContext context) throws CoreException
    {
        context.needDelta();
    }
}
