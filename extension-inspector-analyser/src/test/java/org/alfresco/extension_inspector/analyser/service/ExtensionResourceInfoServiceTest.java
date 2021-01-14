/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.extension_inspector.analyser.service;

import static org.alfresco.extension_inspector.analyser.service.ExtensionResourceInfoService.findMostSpecificMapping;
import static org.alfresco.extension_inspector.model.Resource.Type.BEAN;
import static org.alfresco.extension_inspector.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.alfresco.extension_inspector.model.Resource.Type.FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.alfresco.extension_inspector.model.BeanResource;
import org.alfresco.extension_inspector.model.ClasspathElementResource;
import org.alfresco.extension_inspector.model.FileResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtensionResourceInfoServiceTest
{
    @Mock
    private ConfigService configService;
    @InjectMocks
    private ExtensionResourceInfoService service;

    @Test
    void testRetrieveBeanOverridesById()
    {
        doReturn(Set.of(
            "bean4",
            "bean5",
            "bean6"
        )).when(configService).getBeanOverrideAllowedList();

        doReturn(Set.of(
            new BeanResource("bean1", "context1.xml", "java.lang.String.class"),

            new BeanResource("bean2", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean2", "context2.xml", "java.lang.String.class"),

            new BeanResource("bean3", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean3", "context2.xml", "java.lang.String.class"),
            new BeanResource("bean3", "context3.xml", "java.lang.Integer.class"),

            new BeanResource("bean4", "context1.xml", "java.lang.String.class"),

            new BeanResource("bean5", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean5", "context2.xml", "java.lang.String.class"),

            new BeanResource("bean6", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean6", "context2.xml", "java.lang.String.class"),
            new BeanResource("bean6", "context3.xml", "java.lang.Integer.class")
        )).when(configService).getExtensionResources(any());

        final Map<String, Set<BeanResource>> result = service.retrieveBeanOverridesById();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(6, result.values().stream().mapToLong(Collection::size).sum());
        assertEquals(2, result.get("bean2").size());
        assertEquals(3, result.get("bean3").size());

        verify(configService, times(1)).getExtensionResources(BEAN);
        verifyNoMoreInteractions(configService);
    }

    @Test
    void testRetrieveClasspathElementsById()
    {
        doReturn(Set.of(
            new ClasspathElementResource("/package/Class1.class", "lib1.jar"),

            new ClasspathElementResource("/package/Class2", "lib1.jar"),
            new ClasspathElementResource("/package/Class2", "lib2.jar"),

            new ClasspathElementResource("/package/Class3", "lib1.jar"),
            new ClasspathElementResource("/package/Class3", "lib2.jar"),
            new ClasspathElementResource("/package/Class3", "lib3.jar")
        )).when(configService).getExtensionResources(any());

        final Map<String, Set<ClasspathElementResource>> result = service.retrieveClasspathElementsById();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(6, result.values().stream().mapToLong(Collection::size).sum());
        assertEquals(2, result.get("/package/Class2").size());
        assertEquals(3, result.get("/package/Class3").size());

        verify(configService, times(1)).getExtensionResources(CLASSPATH_ELEMENT);
        verifyNoMoreInteractions(configService);
    }

    @Test
    void testRetrieveFilesByDestination()
    {
        //todo
        doReturn(Map.of(
            "/web", "/web1",
            "/web/ignore", "/web2",
            "/web/foo", "/web3",
            "/web/foo/bar", "/web4",
            "/web/foo/bar/white", "",
            "/web/foo/bar/white/black", "/web5"
        )).when(configService).getFileMappings();

        doReturn(Set.of(
            new FileResource("/web/foo/file1", "a.jar"),
            new FileResource("/web/foo/file2", "a.jar"),
            new FileResource("/web/foo/file3", "b.jar"),
            new FileResource("/web/foo/bar/file4", "a.jar"),
            new FileResource("/web/foo/bar/white/file5", "a.jar"),
            new FileResource("/web/foo/bar/black/file6", "b.jar"),
            new FileResource("/web/foo/bar/white/black/file7", "b.jar"),
            new FileResource("/web/foo/bar/white/black", "a.jar"),
            new FileResource("/web/foo/bar/white/black.res", "a.jar")
        )).when(configService).getExtensionResources(eq(FILE));

        final Map<String, FileResource> expected = Map.of(
            "/web3/file1", new FileResource("/web/foo/file1", "a.jar"),
            "/web3/file2", new FileResource("/web/foo/file2", "a.jar"),
            "/web3/file3", new FileResource("/web/foo/file3", "b.jar"),
            "/web4/file4", new FileResource("/web/foo/bar/file4", "a.jar"),
            "/file5", new FileResource("/web/foo/bar/white/file5", "a.jar"),
            "/web4/black/file6", new FileResource("/web/foo/bar/black/file6", "b.jar"),
            "/web5/file7", new FileResource("/web/foo/bar/white/black/file7", "b.jar"),
            "/black", new FileResource("/web/foo/bar/white/black", "a.jar"),
            "/black.res", new FileResource("/web/foo/bar/white/black.res", "a.jar")
        );

        final Map<String, FileResource> result = service.retrieveFilesByDestination();

        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        expected.forEach((k, v) -> {
            assertTrue(result.containsKey(k));
            assertEquals(v, result.get(k));
        });
    }

    @Test
    void testRetrieveBeansOfAlfrescoTypes()
    {
        doReturn(Set.of(
            new BeanResource("bean1", "context1.xml", "java.lang.String.class"),

            new BeanResource("bean2", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean2", "context2.xml", "java.lang.String.class"),

            new BeanResource("bean3", "context1.xml", "java.lang.String.class"),
            new BeanResource("bean3", "context2.xml", "java.lang.String.class"),
            new BeanResource("bean3", "context3.xml", "java.lang.Integer.class"),

            new BeanResource("bean4", "context1.xml", "org.alfresco.Class1.class"),

            new BeanResource("bean5", "context1.xml", "org.alfresco.Class2.class"),
            new BeanResource("bean5", "context2.xml", "org.alfresco.package.Class3.class"),

            new BeanResource("bean6", "context1.xml", "org.alfresco.Class4.class"),
            new BeanResource("bean6", "context2.xml", "org.alfresco.Class4.class"),
            new BeanResource("bean6", "context3.xml", "org.alfresco.package.Class4.class")
        )).when(configService).getExtensionResources(any());

        final Set<BeanResource> expected = Set.of(
            new BeanResource("bean4", "context1.xml", "org.alfresco.Class1.class"),

            new BeanResource("bean5", "context1.xml", "org.alfresco.Class2.class"),
            new BeanResource("bean5", "context2.xml", "org.alfresco.package.Class3.class"),

            new BeanResource("bean6", "context1.xml", "org.alfresco.Class4.class"),
            new BeanResource("bean6", "context2.xml", "org.alfresco.Class4.class"),
            new BeanResource("bean6", "context3.xml", "org.alfresco.package.Class4.class")
        );

        final Set<BeanResource> result = service.retrieveBeansOfAlfrescoTypes();
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        verify(configService, times(1)).getExtensionResources(BEAN);
        verifyNoMoreInteractions(configService);
    }

    @Test
    void testFindMostSpecificMapping()
    {
        final String destination = findMostSpecificMapping(
            Map.of(
                "/web", "/web1",
                "/web/ignore", "/web2",
                "/web/foo", "/web3",
                "/web/foo/bar", "/web4",
                "/web/foo/bar/white", "",
                "/web/foo/bar/white/black", "/web5",
                "/web/foo/bar/white/black/file", "/web6",
                "/web/foo/bar/white/black/file.res", "/web7",
                "/web/foo/bar/white/black/file.res/nope", "/web8"
            ),
            new FileResource("/web/foo/bar/white/black/file.res", "a.jar")
        );

        assertEquals("/web/foo/bar/white/black", destination);
    }
}