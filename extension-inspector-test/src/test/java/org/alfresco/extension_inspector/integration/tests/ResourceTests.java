/*
 * Copyright 2023 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.integration.tests;

import org.alfresco.extension_inspector.ExtensionInspectorClient;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.alfresco.extension_inspector.util.AppConfig;
import org.alfresco.extension_inspector.util.TestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ResourceTests extends AbstractTestNGSpringContextTests
{
        private static final List<String> JAVAX_CLASSES = Arrays.asList("jakartamigrations.UseJavaxMail", "jakartamigrations.UseJavaxServlet");
        private static final List<String> JAKARTA_CLASSES = Arrays.asList("jakartamigrations.UseJakartaMail", "jakartamigrations.UseJakartaServlet");

        @Autowired
        private ExtensionInspectorClient client;

        private CommandOutput cmdOut;

        @Test
        public void testAnalyseFileOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                assertEquals(cmdOut.getFileOverwriteTotal(), 3);
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/pdf.png"));
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/mp4.gif"));
                assertFalse(cmdOut.isInFileOverwrite("/images/filetype/testfile.bmp"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);

                assertEquals(cmdOut.getFileOverwriteTotal(), 21);
                assertNotNull(cmdOut.retrieveOutputLine("ContextLoaderListener.class,6.0.0-6.2.2", "FILE_OVERWRITE"));
                assertNotNull(cmdOut.retrieveOutputLine("pdf.png,6.0.0-6.2.2", "FILE_OVERWRITE"));
                assertNotNull(cmdOut.retrieveOutputLine("mp4.gif,6.0.0-6.2.2", "FILE_OVERWRITE"));
        }

        @Test
        public void testAnalyseBeanOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                System.out.println(cmdOut.getBeanOverwriteConflicts().toString());

                assertEquals(cmdOut.getBeanOverwriteTotal(), 2);
                assertTrue(cmdOut.isInBeanOverwrite("trashcanSchedulerAccessor"));
                assertTrue(cmdOut.isInBeanOverwrite("trashcanCleaner"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);

                assertEquals(cmdOut.getBeanOverwriteTotal(), 14);
                assertNotNull(cmdOut.retrieveOutputLine("trashcanCleaner,6.0.0-6.2.2", "BEAN"));
                assertNotNull(cmdOut.retrieveOutputLine("trashcanSchedulerAccessor,6.0.0-6.2.2", "BEAN"));
        }

        @Test
        public void testAnalyseAmpPublicAPI()
        {
                // Run against Alfresco version 6.2.2
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                // Run against Alfresco version 6.0.0
                version = "6.0.0";
                List<String> cmdOptions1 = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions1);

                assertEquals(cmdOut.getPublicAPITotal(), 1);
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);

                assertEquals(cmdOut.getPublicAPITotal(), 12);
                assertNotNull(cmdOut.retrieveOutputLine("publicapi.UseDeprecatedPublicAPI,6.1.0-6.2.2", "PUBLIC_API"));
                assertNotNull(cmdOut.retrieveOutputLine("publicapi.UseInternalClass,6.0.0-6.2.2", "PUBLIC_API"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));
        }

        @Test
        public void testAnalyseJarPublicAPI()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.jar");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                //Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);

                assertEquals(cmdOut.getPublicAPITotal(), 12);
                assertNotNull(cmdOut.retrieveOutputLine("publicapi.UseDeprecatedPublicAPI,6.1.0-6.2.2", "PUBLIC_API"));
                assertNotNull(cmdOut.retrieveOutputLine("publicapi.UseInternalClass,6.0.0-6.2.2", "PUBLIC_API"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));
        }

        @Test public void testAnalyseClassPathOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 1);
                assertTrue(cmdOut.isClassPathConflicts("ContextLoaderListener.class"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 7);
                assertNotNull(cmdOut.retrieveOutputLine(
                    "/org/alfresco/web/app/ContextLoaderListener.class,6.0.0-6.2.2", "CLASS_PATH"));
        }

        @Test
        public void testAnalyseThirdPartyLibs()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getThirdPartyLibTotal(), 1); // 1 conflict meaning 1 extension resource using 3rd party libs (in this case, 3 libs)
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/commons-lang3-3.9.jar"));
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/mybatis-3.3.0.jar"));
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/jsoup-1.12.2.jar"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 7);
                assertNotNull(cmdOut.retrieveOutputLine("thirdpartylibs.ThirdPartyLibs,6.0.0-6.2.2",
                    "3RD_PARTY_LIBS")); // extension resource using 3rd party libs
        }

        @Test
        public void testAnalyseJakartaMigrationDependenciesForMigratedExtension()
        {
                // Run against Alfresco version 7.4.1 (7.4.1 is not jakarta migrated)
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest-jakarta.amp");
                String version = "7.4.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                verifyConflicts(2, true, JAKARTA_CLASSES);

                // Run against Alfresco version 23.1.0 (23.1.0 is jakarta migrated)
                version = "23.1.0";
                List<String> cmdOptions1 = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions1);
                verifyConflicts(0, false, JAKARTA_CLASSES);

                // Run against multiple Alfresco versions
                version = "7.3.0-23.1.0";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);
                verifyConflicts(8, true, JAKARTA_CLASSES);
                assertNotNull(cmdOut.retrieveOutputLine("jakartamigrations.UseJakartaMail" + ",7.3.0-7.4.1", "JAKARTA_MIGRATION_CONFLICT"));
                assertNotNull(cmdOut.retrieveOutputLine("jakartamigrations.UseJakartaServlet" + ",7.3.0-7.4.1", "JAKARTA_MIGRATION_CONFLICT"));
        }

        @Test
        public void testAnalyseJakartaMigrationDependenciesForNonMigratedExtension()
        {
                // Run against Alfresco version 7.4.1 (7.4.1 is not jakarta migrated)
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest-javax.amp");
                String version = "7.4.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions);
                verifyConflicts(0, false, JAVAX_CLASSES);

                // Run against Alfresco version 23.1.0 (23.1.0 is jakarta migrated)
                version = "23.1.0";
                List<String> cmdOptions1 = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions1);
                verifyConflicts(2, true, JAVAX_CLASSES);

                // Run against multiple Alfresco versions
                version = "7.3.0-23.1.0";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runExtensionInspectorAnalyserCommand(cmdOptions2);
                verifyConflicts(2, true, JAVAX_CLASSES);
                assertNotNull(cmdOut.retrieveOutputLine("jakartamigrations.UseJavaxMail" + ",23.1.0", "JAKARTA_MIGRATION_CONFLICT"));
                assertNotNull(cmdOut.retrieveOutputLine("jakartamigrations.UseJavaxServlet" + ",23.1.0", "JAKARTA_MIGRATION_CONFLICT"));
        }

        /* Helper method to assert the number of expected conflicts, and whether the classes are included in the report */
        private void verifyConflicts(int numberOfConflicts, boolean isClassConflicting, List<String> conflictedClasses)
        {
                assertEquals(cmdOut.getJakartaMigrationConflictsTotal(), numberOfConflicts);
                conflictedClasses.forEach(conflictedClass ->
                        assertEquals(cmdOut.isInJakartaMigrationConflicts(conflictedClass), isClassConflicting));
        }
}
