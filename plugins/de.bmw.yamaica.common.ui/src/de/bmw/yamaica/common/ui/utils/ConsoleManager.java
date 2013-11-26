/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.MessageConsole;

/**
 * Helper class to create and to delete IOConsoles instances. You should create an IOConsole with
 * this class if you want to attach this console to a ConsoleStream instance.
 */
public class ConsoleManager
{
    private static final String YAMAICA_CONSOLE = "yamaica Console";
    private static HashMap<String, MessageConsole> consoles = new HashMap<String, MessageConsole>();
    public static final IOConsole                  console  = getOrCreateConsole(YAMAICA_CONSOLE, null);

    synchronized public static IOConsole getOrCreateConsole(String name, ImageDescriptor image)
    {
        if (consoles.containsKey(name))
        {
            MessageConsole console = consoles.get(name);

            return console;
        }
        else
        {
            MessageConsole console = new MessageConsole(name, image);

            consoles.put(name, console);

            return console;
        }
    }

    synchronized static void removeConsole(String name)
    {
        if (consoles.containsKey(name))
        {
            MessageConsole console = consoles.get(name);

            for (ConsoleStream stream : ConsoleStream.getConsoleStreams())
            {
                stream.detachConsole(console);
            }

            console.destroy();
            consoles.remove(name);
        }
    }

    static IOConsole[] getConsoles()
    {
        return consoles.values().toArray(new MessageConsole[consoles.size()]);
    }
}
