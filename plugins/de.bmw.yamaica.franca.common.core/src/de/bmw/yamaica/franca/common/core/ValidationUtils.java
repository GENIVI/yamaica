/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

public class ValidationUtils
{
    private static final Logger LOGGER = Logger.getLogger(ValidationUtils.class.getName());

    public interface IIssueLogger
    {
        void info(String msg);
        void warning(String msg);
        void error(String msg);
    }

    static class StandardIssueLogger implements IIssueLogger
    {
        @Override
        public void error(String msg)
        {
            LOGGER.log(Level.SEVERE, stripPrefix(msg, "ERROR:"));
        }

        @Override
        public void warning(String msg)
        {
            LOGGER.log(Level.WARNING, stripPrefix(msg, "WARNING:"));
        }

        @Override
        public void info(String msg)
        {
            LOGGER.log(Level.INFO, stripPrefix(msg, "INFO:"));
        }
    }

    public static boolean validate(ResourceSet resourceSet)
    {
        return validate(resourceSet, new StandardIssueLogger());
    }

    public static boolean validate(ResourceSet resourceSet, IIssueLogger issueLogger)
    {
        boolean hasValidationError = false;
        for (Resource resource : resourceSet.getResources())
        {
            if (!validate(resource, issueLogger))
                hasValidationError = true;
        }
        return !hasValidationError;
    }

    public static boolean validate(Resource resource, IIssueLogger issueLogger)
    {
        // Explicitly output the currently processed file name as some of the diagnostics may not contain a file name at all.
        //issueLogger.info("Validating " + getDisplayPath(resource.getURI()));

        boolean hasValidationError = false;
        IResourceServiceProvider resourceServiceProvider = IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(resource.getURI());
        if (resourceServiceProvider != null)
        {
            IResourceValidator resourceValidator = resourceServiceProvider.getResourceValidator();
            Collection<Issue> issues = resourceValidator.validate(resource, CheckMode.ALL, null);
            for (Issue issue : issues)
            {
                if (issue.getSeverity() == Severity.ERROR) {
                    hasValidationError = true;
                    issueLogger.error(issue.toString());
                }
                else if (issue.getSeverity() == Severity.WARNING)
                    issueLogger.warning(issue.toString());
            }
        }
        return !hasValidationError;
    }

    protected static String getDisplayPath(URI uri)
    {
        String displayPath = null;
        if (uri.isPlatformResource())
            displayPath = uri.toPlatformString(true);
        if (displayPath == null)
            displayPath = uri.toFileString();
        if (displayPath == null)
            displayPath = uri.toString();
        return displayPath;
    }

    protected static String stripPrefix(String msg, String prefix)
    {
        if (msg.startsWith(prefix))
            return msg.substring(prefix.length());
        else
            return msg;
    }
}
