package de.bmw.yamaica.common.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

public class CircleFinderTest
{
    @Test
    public void selfTest()
    {
        Map<String, Set<String>> map = new TreeMap<>();
        final String test = "test";

        Set<String> to = new HashSet<>();
        to.add(test);
        map.put(test, to);

        final boolean hasCircle = new CircleFinder<String>(map).hasCircle();
        assertTrue(hasCircle);
    }

    @Test
    public void circleTest()
    {
        Map<String, Set<String>> map = new TreeMap<>();

        final String a = "A";
        final String b = "B";
        final String c = "C";
        final String d = "D";
        final String e = "E";

        {
            Set<String> to = new HashSet<>();
            to.add(b);
            to.add(c);
            map.put(a, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(c);
            map.put(b, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(d);
            map.put(c, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(e);
            map.put(d, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(b);
            map.put(e, to);
        }

        final boolean hasCircle = new CircleFinder<String>(map).hasCircle();
        assertTrue(hasCircle);
    }

    @Test
    public void noCircleTest()
    {
        Map<String, Set<String>> map = new TreeMap<>();

        final String a = "A";
        final String b = "B";
        final String c = "C";
        final String d = "D";
        final String e = "E";

        {
            Set<String> to = new HashSet<>();
            to.add(b);
            to.add(c);
            map.put(a, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(c);
            map.put(b, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(d);
            map.put(c, to);
        }
        {
            Set<String> to = new HashSet<>();
            to.add(e);
            map.put(d, to);
        }
        {
            Set<String> to = new HashSet<>();
            map.put(e, to);
        }

        final boolean hasCircle = new CircleFinder<String>(map).hasCircle();
        assertFalse(hasCircle);
    }
}
