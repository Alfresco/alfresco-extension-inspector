/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.ampalyser.analyser.util.DependencyVisitor;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

public class AsmDependencyVisitorTest
{
    @Test
    public void testAsmClassReader() throws IOException
    {
        try (final InputStream is = new FileInputStream("/home/cleahu/Desktop/SfdcContentModelBehaviours.class"))
        {
            final ClassReader reader = new ClassReader(is);
            final DependencyVisitor visitor = new DependencyVisitor();

            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            System.out.println("------");
            visitor.getClasses()
                   .stream()
                   .filter(s -> !s.startsWith("java/"))
                   .filter(s -> !s.startsWith("org/alfresco/"))
                   .forEach(System.out::println);
            System.out.println("======" + visitor);
        }
    }

    @Test
    public void testAsmClassReader2() throws IOException
    {
        try (final InputStream is = this.getClass().getResourceAsStream("/MatcherAggregator.class"))
        {
            final ClassReader reader = new ClassReader(is);
            final DependencyVisitor visitor = new DependencyVisitor();

            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            System.out.println("------");
            visitor.getClasses()
                   .stream()
                   .filter(s -> !s.startsWith("java/"))
                   .filter(s -> !s.startsWith("org/alfresco/"))
                   .forEach(System.out::println);
            System.out.println("======" + visitor);
        }
    }

    @Test
    public void testAsmClassReader3() throws IOException
    {
        try (final InputStream is = this.getClass().getResourceAsStream("/TextractAIRenditionProcessor.class"))
        {
            final ClassReader reader = new ClassReader(is);
            final DependencyVisitor visitor = new DependencyVisitor();

            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            visitor.visitEnd();

            System.out.println("------");
            visitor.getClasses()
                   .stream()
                   .filter(s -> !s.startsWith("java/"))
                   .filter(s -> !s.startsWith("org/alfresco/"))
                   .forEach(System.out::println);
            System.out.println("======" + visitor);
        }
    }
}
