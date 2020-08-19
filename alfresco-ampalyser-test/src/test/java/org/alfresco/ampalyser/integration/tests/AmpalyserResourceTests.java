package org.alfresco.ampalyser.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.alfresco.ampalyser.util.TestResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmpalyserResourceTests
{
        @Autowired
        private AmpalyserClient client;

        private CommandOutput cmdOut;

        @Test
        public void testAnalyseFileOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runCommand(cmdOptions);
                assertEquals(cmdOut.getFileOverwriteTotal(), 3);
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/pdf.png"));
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/mp4.gif"));
                assertFalse(cmdOut.isInFileOverwrite("/images/filetype/testfile.bmp"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runCommand(cmdOptions2);

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

                cmdOut = client.runCommand(cmdOptions);
                System.out.println(cmdOut.getBeanOverwriteConflicts().toString());

                assertEquals(cmdOut.getBeanOverwriteTotal(), 2);
                assertTrue(cmdOut.isInBeanOverwrite("trashcanSchedulerAccessor"));
                assertTrue(cmdOut.isInBeanOverwrite("trashcanCleaner"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runCommand(cmdOptions2);

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

                cmdOut = client.runCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                // Run against Alfresco version 6.0.0
                version = "6.0.0";
                List<String> cmdOptions1 = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runCommand(cmdOptions1);

                assertEquals(cmdOut.getPublicAPITotal(), 1);
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runCommand(cmdOptions2);

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

                cmdOut = client.runCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseDeprecatedPublicAPI"));
                assertTrue(cmdOut.isInPublicAPIConflicts("publicapi.UseInternalClass"));
                assertFalse(cmdOut.isInPublicAPIConflicts("publicapi.UsePublicAPIClass"));

                //Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");

                cmdOut = client.runCommand(cmdOptions2);

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

                cmdOut = client.runCommand(cmdOptions);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 1);
                assertTrue(cmdOut.isClassPathConflicts("ContextLoaderListener.class"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runCommand(cmdOptions2);
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

                cmdOut = client.runCommand(cmdOptions);

                assertEquals(cmdOut.getThirdPartyLibTotal(), 1); // 1 conflict meaning 1 extension resource using 3rd party libs (in this case, 3 libs)
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/commons-lang3-3.9.jar"));
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/mybatis-3.3.0.jar"));
                assertTrue(cmdOut.isInThirdPartyLibConflicts("/WEB-INF/lib/jsoup-1.12.2.jar"));

                // Run against multiple Alfresco versions
                version = "6.0.0-6.2.2";
                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
                cmdOut = client.runCommand(cmdOptions2);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 7);
                assertNotNull(cmdOut.retrieveOutputLine("thirdpartylibs.ThirdPartyLibs,6.0.0-6.2.2",
                    "3RD_PARTY_LIBS")); // extension resource using 3rd party libs
        }
}
