/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core;

import org.franca.core.dsl.FrancaIDLRuntimeModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FrancaInjector
{
    public final static Injector INSTANCE = Guice.createInjector(new FrancaIDLRuntimeModule());

    private FrancaInjector()
    {
    }
}
