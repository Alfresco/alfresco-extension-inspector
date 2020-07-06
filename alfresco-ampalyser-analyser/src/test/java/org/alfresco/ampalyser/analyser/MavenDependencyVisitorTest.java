/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.shared.dependency.analyzer.asm.DependencyClassFileVisitor;
import org.junit.Test;

public class MavenDependencyVisitorTest
{
    @Test
    public void testMavenDependencyClassFileVisitor()
    {
        final DependencyClassFileVisitor visitor = new DependencyClassFileVisitor();

        final InputStream is = this.getClass().getResourceAsStream("/TextractAIRenditionProcessor.class");
        visitor.visitClass("foo", is);

        System.out.println("------");
        visitor
            .getDependencies()
            .stream()
            .filter(s -> !s.startsWith("java."))
            .filter(s -> !s.startsWith("org.alfresco."))
            .filter(s -> !s.contains(" is null"))
            .forEach(System.out::println);
        System.out.println("======" + visitor);
    }

    @Test
    public void testSomething() throws IOException
    {
        final DependencyClassFileVisitor visitor = new DependencyClassFileVisitor();

        try (final InputStream is = new FileInputStream("/home/cleahu/Desktop/SfdcContentModelBehaviours.class"))
        {
            visitor.visitClass("bar", is);
        }

        System.out.println("------");
        visitor
            .getDependencies()
            .stream()
            .filter(s -> !s.startsWith("java."))
            .filter(s -> !s.startsWith("org.alfresco."))
            .filter(s -> !s.contains(" is null"))
            .forEach(System.out::println);
        System.out.println("======" + visitor);
    }
}
