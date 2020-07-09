/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.model.Resource;
import org.objectweb.asm.ClassReader;

/**
 * @author Cezar Leahu
 * @author Lucian Tuca
 */
public class DependencyService
{

    /**
     * For a given .class file provided as byte[] this method finds all the classes this class uses.
     *
     * @param classData the .class file as byte[]
     * @return a {@link Set} of the used classes
     */
    public static Set<String> findDependenciesForClass(final byte[] classData)
    {
        final DependencyVisitor visitor = new DependencyVisitor();
        final ClassReader reader = new ClassReader(classData);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        visitor.visitEnd();

        return visitor
            .getClasses()
            .stream()
            .map(c -> c.replaceAll("/", "."))
            .collect(toUnmodifiableSet());
    }

    public static Set<String> compileDependencySet(final Collection<Resource> classpathResources)
    {
        return classpathResources
            .stream()
            .map(Resource::getId)
            .filter(s -> s.endsWith(".class"))
            .filter(s -> !s.startsWith("/org/alfresco/"))
            .map(s -> s.substring(1).replaceAll("/", "."))
            .collect(toUnmodifiableSet());
    }
}
