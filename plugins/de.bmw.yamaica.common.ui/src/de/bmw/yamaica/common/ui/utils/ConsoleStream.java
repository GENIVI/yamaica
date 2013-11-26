/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.ui.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.bmw.yamaica.common.core.YamaicaConstants;

/**
 * This class must be used to create ConsoleStream instances. One or more IOConsoles (which were
 * created by the ConsoleManager) can then be attached to the ConsoleStream instance. The stream
 * will then be printed to every attached console.
 */
public class ConsoleStream
{
    private static HashMap<String, ConsoleStream> consoleStreams = new HashMap<String, ConsoleStream>();

    synchronized public static ConsoleStream getOrCreateConsoleStream(String name)
    {
        return getOrCreateConsoleStream(name, getNiceColor());
    }

    synchronized public static ConsoleStream getOrCreateConsoleStream(String name, Color color)
    {
        if (consoleStreams.containsKey(name))
        {
            ConsoleStream consoleStream = consoleStreams.get(name);

            return consoleStream;
        }
        else
        {
            ConsoleStream consoleStream = new ConsoleStream(name);
            consoleStream.attachConsole(ConsoleManager.console, color);

            consoleStreams.put(name, consoleStream);

            return consoleStream;
        }
    }

    synchronized public static void removeConsoleStream(String name)
    {
        if (consoleStreams.containsKey(name))
        {
            consoleStreams.get(name).close();
        }
    }

    public static ConsoleStream[] getConsoleStreams()
    {
        return consoleStreams.values().toArray(new ConsoleStream[consoleStreams.size()]);
    }

    private static Color getNiceColor()
    {
        Random random = new Random();
        java.awt.Color awtColor = java.awt.Color.getHSBColor(random.nextFloat(), 0.5f, 0.5f);

        return new Color(Display.getCurrent(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    private static HashMap<IOConsole, ConsoleData>    consoles         = new HashMap<IOConsole, ConsoleData>();
    private static ReentrantLock                      lock             = new ReentrantLock(true);

    private String                                    name;
    private PrintStream                               printStream;
    private HashMap<IOConsole, IOConsoleOutputStream> attachedConsoles = new HashMap<IOConsole, IOConsoleOutputStream>();
    private String                                    newLine          = System.getProperty(YamaicaConstants.LINE_SEPARATOR);

    private ConsoleStream(String name)
    {
        this.name = name;

        printStream = new PrintStream(new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                for (IOConsoleOutputStream ioConsoleOutputStream : attachedConsoles.values())
                {
                    ioConsoleOutputStream.write(b);
                }
            }
        }, true)
        {
            @Override
            public void println(boolean x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(char x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(int x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(long x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(float x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(double x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public void println(char[] x)
            {
                lock.lock();
                super.println(getLogTimeString() + addNewLineIndent(new String(x)));
                lock.unlock();
            }

            @Override
            public void println(String x)
            {
                lock.lock();
                super.println(getLogTimeString() + addNewLineIndent(x));
                lock.unlock();
            }

            @Override
            public void println(Object x)
            {
                lock.lock();
                super.println(getLogTimeString() + x);
                lock.unlock();
            }

            @Override
            public synchronized void write(byte[] buf, int off, int len)
            {
                lock.lock();

                for (IOConsole console : attachedConsoles.keySet())
                {
                    checkLastConsoleStream(console);
                }

                super.write(buf, off, len);

                lock.unlock();
            }
        };
    }

    public PrintStream getPrintStream()
    {
        return printStream;
    }

    public void attachConsole(IOConsole console)
    {
        attachConsole(console, Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    }

    public void attachConsole(IOConsole console, Color color)
    {
        if (attachedConsoles.containsKey(console))
        {
            attachedConsoles.get(console).setColor(color);
        }
        else
        {
            IOConsoleOutputStream stream = console.newOutputStream();
            stream.setColor(color);

            attachedConsoles.put(console, stream);

            if (consoles.containsKey(console))
            {
                ConsoleData consoleData = consoles.get(console);
                consoleData.streamCount++;
            }
            else
            {
                ConsoleData consoleData = new ConsoleData();
                consoleData.streamCount = 1;
                consoles.put(console, consoleData);
            }
        }
    }

    public void detachConsole(IOConsole console)
    {
        if (attachedConsoles.containsKey(console))
        {
            attachedConsoles.remove(console);

            ConsoleData consoleData = consoles.get(console);
            consoleData.streamCount--;

            if (consoleData.streamCount == 0)
            {
                consoles.remove(console);
            }
        }
    }

    private void checkLastConsoleStream(IOConsole console)
    {
        IOConsoleOutputStream stream = attachedConsoles.get(console);
        ConsoleData consoleData = consoles.get(console);

        if (consoleData.streamCount > 1 && stream != consoleData.lastStream)
        {
            try
            {
                stream.write(getNewLineIndent() + "[" + name + "]" + newLine);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            consoleData.lastStream = stream;
        }
    }

    private String getNewLineIndent()
    {
        // Number of white spaces must be the same like the length of the return value
        // of the getLogTimeString() method.
        return "               ";
    }

    private String getLogTimeString()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return String.format("[%1$tT.%1$tL] ", calendar);
    }

    private String addNewLineIndent(String logMessage)
    {
        return logMessage.replaceAll("\r\n|\n\r|\n|\r", "\n" + getNewLineIndent());
    }

    public void close()
    {
        ConsoleStream.removeConsoleStream(name);

        for (IOConsole console : attachedConsoles.keySet())
        {
            detachConsole(console);
        }

        printStream.close();
    }

    private class ConsoleData
    {
        public int                   streamCount = 0;
        public IOConsoleOutputStream lastStream  = null;
    }
}
