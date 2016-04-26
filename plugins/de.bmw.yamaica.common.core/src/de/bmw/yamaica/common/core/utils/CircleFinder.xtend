/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.utils

import java.util.Collections
import java.util.Map
import java.util.Set
import java.util.Stack
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Helper class to detect loop / circle / cycle / round trip.
 *
 * A simple map (required input) example may look like:
 * [
 *     "A" -> [ "B" ],
 *     "B" -> [ "C", "D" ],
 *     "C" -> [ "D" ],
 *     "D" -> [ "A" ],
 *     "E" -> [ "A", "B" ],
 * ]
 */
public class CircleFinder<T> {

    @Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
    static val Logger LOGGER = Logger.getLogger(typeof(CircleFinder).name);

    // Avoid null using this empty set.
    @Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
    val Set<T> EMPTY = Collections.unmodifiableSet(newHashSet())

    @Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
    val Map<T, Set<T>>        map

    @Accessors(PUBLIC_GETTER, PUBLIC_SETTER)
    var Level logLevel = Level.INFO

    /**
     * Holding one element (anyone) of each cycle find.
     */
    @Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
    var T circleElement = null

    public new(Map<T, Set<T>> newMap)
    {
        map = newMap;
    }

    public def boolean hasCircle()
    {
        circleElement = null
        // Check every key if loop exist (creating a new visiting set each time).
        // 1.) exists[ ] := Prints first circle only
        // 2.) Using !filter[ ].isEmpty := Prints all circles
        return map.keySet().exists[it != null && hasLoop(new Stack<T>())]
    }

    // Gets next of current. Returning an empty set to avoid null handling.
    private def Set<T> next(T current)
    {
        val next = map.get(current)
        return if(next == null) EMPTY else next.filterNull.toSet
    }

    private def boolean alreadyVisited(T current, Stack<T> stack) {
        // Cycle detected?
        if(stack.contains(current)) {
            // Add to stack as well (makes the cycle visible).
            stack.push(current)
            LOGGER.log(logLevel, "Detected cycle: " + stack)
            // Add to the list of found cycle elements.
            circleElement = current
            return true
        }
        return false
    }

    private def boolean hasLoop(T current, Stack<T> stack)
    {
        // Cycle detected (node already visited)?
        if(current.alreadyVisited(stack)) {
            return true
        }
        // Add current element to the path stack.
        stack.push(current)

        // Check if cycle could be found by next.
        if(current.next.exists[alreadyVisited(stack)]) {
            return true
        }

        // Recursive call using depth-first 'search'.
        for(T next : current.next) {
            if(hasLoop(next, stack)) {
                return true
            }
        }
        // Dead end: Remove last added element.
        stack.pop()
        return false
    }
}