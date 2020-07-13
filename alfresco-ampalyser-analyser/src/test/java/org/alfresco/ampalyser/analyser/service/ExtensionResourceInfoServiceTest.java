package org.alfresco.ampalyser.analyser.service;

import static org.alfresco.ampalyser.model.Resource.Type.BEAN;
import static org.alfresco.ampalyser.model.Resource.Type.CLASSPATH_ELEMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.ampalyser.model.BeanResource;
import org.alfresco.ampalyser.model.ClasspathElementResource;
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
    void retrieveBeanOverridesById()
    {
        doReturn(Set.of(
            "bean4",
            "bean5",
            "bean6"
        )).when(configService).getBeanOverrideWhitelist();

        doReturn(List.of(
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
    void retrieveClasspathElementsById()
    {
        doReturn(List.of(
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
    void retrieveFilesByDestination()
    {
        //todo
    }

    @Test
    void retrieveBeansOfAlfrescoTypes()
    {
        doReturn(List.of(
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
    void findMostSpecificMapping()
    {
        //todo
    }
}