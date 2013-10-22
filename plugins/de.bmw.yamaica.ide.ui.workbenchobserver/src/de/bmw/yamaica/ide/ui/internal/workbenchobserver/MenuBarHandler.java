/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.ide.ui.internal.workbenchobserver;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.bmw.yamaica.ide.ui.workbenchobserver.Preferences;

public class MenuBarHandler implements IWindowListener, IPerspectiveListener3, Listener, IPropertyChangeListener
{
    private static final String DE_BMW_YAMAICA_IDE_UI_MAIN_PERSPECTIVE = "de.bmw.yamaica.ide.ui.mainPerspective";

    private static MenuBarHandler       instance          = null;

    private IWorkbench                  workbench         = null;
    private Map<IWorkbenchWindow, Menu> windows           = new HashMap<IWorkbenchWindow, Menu>();
    private int                         lastCtrlKeyTime   = 0;
    private String                      menuBarhidingRule = null;

    private MenuBarHandler()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.addPropertyChangeListener(this);

        menuBarhidingRule = store.getString(Preferences.MENU_BAR_HIDING_RULE);

        workbench = PlatformUI.getWorkbench();
        workbench.addWindowListener(this);

        for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
        {
            addWindow(window);
        }

        // Register listener for global key up and mouse down events
        Display display = Display.getDefault();
        display.addFilter(SWT.KeyUp, this);
        display.addFilter(SWT.MouseDown, this);
    }

    public static synchronized MenuBarHandler getInstance()
    {
        if (null == instance)
        {
            instance = new MenuBarHandler();
        }

        return instance;
    }

    public synchronized void dispose()
    {
        if (null != workbench)
        {
            workbench.removeWindowListener(this);

            for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
            {
                removeWindow(window);
            }
        }

        Display display = Display.getDefault();
        display.removeFilter(SWT.KeyUp, this);
        display.removeFilter(SWT.MouseDown, this);

        windows = null;
        workbench = null;
        instance = null;
    }

    private void addWindow(IWorkbenchWindow window)
    {
        window.addPerspectiveListener(this);

        if (!windows.containsKey(window))
        {
            windows.put(window, null);
        }

        updateWindow(window);
    }

    private void removeWindow(IWorkbenchWindow window)
    {
        window.removePerspectiveListener(this);
        windows.remove(window);
    }

    private synchronized void updateWindow(final IWorkbenchWindow window)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (shouldMenuBarBeVisibile(window))
                {
                    showMenuBar(window);
                }
                else
                {
                    hideMenuBar(window);
                }
            }
        });
    }

    private void showMenuBar(IWorkbenchWindow window)
    {
        Shell shell = window.getShell();
        Menu menu = windows.get(window);

        if (null != menu)
        {
            windows.put(window, null);
            shell.setMenuBar(menu);
        }
    }

    private void hideMenuBar(IWorkbenchWindow window)
    {
        Shell shell = window.getShell();
        Menu menu = shell.getMenuBar();

        if (null != menu)
        {
            windows.put(window, menu);
            shell.setMenuBar(null);
        }
    }

    private boolean shouldMenuBarBeVisibile(IWorkbenchWindow window)
    {
        if (menuBarhidingRule.equals(Preferences.ALWAYS_HIDE))
        {
            return false;
        }

        if (menuBarhidingRule.equals(Preferences.NEVER_HIDE))
        {
            return true;
        }

        if (menuBarhidingRule.equals(Preferences.YAMAICA_HIDE))
        {
            IWorkbenchPage page = window.getActivePage();
            IPerspectiveDescriptor perspective = (null == page) ? null : page.getPerspective();
            String perspectiveId = (null == perspective) ? null : perspective.getId();

            if (null == perspectiveId)
            {
                return true;
            }

            if (perspectiveId.equals(DE_BMW_YAMAICA_IDE_UI_MAIN_PERSPECTIVE))
            {
                return false;
            }
        }

        return true;
    }

    // BEGIN Implementation of interface IWindowListener //

    @Override
    public void windowActivated(IWorkbenchWindow window)
    {
        updateWindow(window);
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window)
    {

    }

    @Override
    public void windowClosed(IWorkbenchWindow window)
    {
        removeWindow(window);
    }

    @Override
    public void windowOpened(IWorkbenchWindow window)
    {
        addWindow(window);
    }

    // END Implementation of interface IWindowListener //

    // BEGIN Implementation of interface IPerspectiveListener3 //

    @Override
    public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
    {
        updateWindow(page.getWorkbenchWindow());
    }

    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
    {

    }

    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId)
    {

    }

    @Override
    public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective)
    {

    }

    @Override
    public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective)
    {
        updateWindow(page.getWorkbenchWindow());
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
    {

    }

    @Override
    public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective)
    {

    }

    // END Implementation of interface IPerspectiveListener3 //

    // BEGIN Implementation of interface Listener //

    @Override
    public void handleEvent(final Event event)
    {
        // We just want to know if the "F10" or the "Alt" key was released. NOT the "AltGr" key! A "AltGr" release
        // fires two events, a "Ctrl" and a "Alt" event which have both the same time. Thus we compare the time of
        // of the "Ctrl" and the "Alt" event. If they are equal "AltGr" was pressed and not "Alt".
        if ((event.stateMask & SWT.CTRL) > 0)
        {
            lastCtrlKeyTime = event.time;

            return;
        }

        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                if (shouldMenuBarBeVisibile(window))
                {
                    return;
                }

                // Do not handle event if the control which received the event is not a member of the current window.
                Widget widget = event.widget;

                if (widget instanceof Control && !((Control) widget).getShell().equals(window.getShell()))
                {
                    return;
                }

                if (null == windows.get(window))
                {
                    // Hide menu bar if an event occurs and the window currently has a visible menu bar.
                    hideMenuBar(window);
                }
                else if (event.keyCode == SWT.F10 || (event.keyCode == SWT.ALT && event.time != lastCtrlKeyTime))
                {
                    // Show menu bar if the "F10" or the "Alt" key was released (But not the "AltGr" key!).
                    showMenuBar(window);
                }
            }
        });
    }

    // END Implementation of interface Listener //

    // BEGIN Implementation of interface IPropertyChangeListener //

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getProperty().equals(Preferences.MENU_BAR_HIDING_RULE))
        {
            menuBarhidingRule = (String) event.getNewValue();

            for (IWorkbenchWindow window : windows.keySet())
            {
                updateWindow(window);
            }
        }
    }

    // END Implementation of interface IPropertyChangeListener //
}
