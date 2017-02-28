package de.bmw.yamaica.common.console;

import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogUtils
{
    private static final Logger ROOT_LOGGER = Logger.getLogger("");

    public static void setupConsoleLogger()
    {
        // Check if there is already a 'ConsoleHandler' registered. If so, we assume that
        // the logger has been configured already for logging onto the console e.g. via
        // a "log.properties" file.
        //
        // If there is already a 'ConsoleHandler' registered, we don't add a new one.
        //
        // If there is already a 'FileHandler' registered (e.g. via "log.properties"), we keep
        // using it, regardless whether we add our own 'ConsoleHandler' or whether we keep the
        // possible already available 'ConsoleHandler'.
        //
        for (Handler handler : ROOT_LOGGER.getHandlers())
        {
            if (handler instanceof ConsoleHandler)
                return;
        }

        ConsoleHandler consoleHandler = new ConsoleHandler() {
            @Override
            public void publish(LogRecord record)
            {
                if (!isLoggable(record))
                    return;

                String msg;
                try {
                    msg = getFormatter().format(record);
                }
                catch (Exception ex) {
                    reportError(null, ex, ErrorManager.FORMAT_FAILURE);
                    return;
                }

                // Use same semantics as 'java.util.logging.ConsoleHandler.ConsoleHandler()' which is
                // outputting the message without a trailing newline. The newline was already appended by the
                // Formatter.
                //
                try {
                    if (record.getLevel().intValue() >= Level.WARNING.intValue())
                    {
                        System.err.print(msg);
                        System.err.flush();
                    }
                    else
                    {
                        System.out.print(msg);
                        System.out.flush();
                    }
                }
                catch (Exception ex) {
                    reportError(null, ex, ErrorManager.WRITE_FAILURE);
                }
            }
        };

        class SmartFormatter extends Formatter
        {
            String lineSeperator;

            SmartFormatter()
            {
                lineSeperator = System.getProperty("line.separator");
            }

            @Override
            public String format(LogRecord record)
            {
                if (record.getLevel().intValue() >= Level.WARNING.intValue())
                    return record.getLevel().getLocalizedName() + ": " + record.getMessage() + lineSeperator;
                else
                    return record.getMessage() + lineSeperator;
            }
        }

        consoleHandler.setFormatter(new SmartFormatter());
        consoleHandler.setLevel(Level.INFO);
        ROOT_LOGGER.addHandler(consoleHandler);
    }

    public static void removeConsoleLogger()
    {
        for (Handler handler : ROOT_LOGGER.getHandlers())
        {
            if (handler instanceof ConsoleHandler)
            {
                ROOT_LOGGER.removeHandler(handler);
                return;
            }
        }
    }
}
