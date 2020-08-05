package org.alfresco.ampalyser.integration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import java.util.List;

import org.alfresco.ampalyser.AmpalyserClient;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.alfresco.ampalyser.util.TestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmpalyserResourceTests extends AbstractTestNGSpringContextTests
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

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);
                assertEquals(cmdOut.getFileOverwriteTotal(), 3);
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/pdf.png"));
                assertTrue(cmdOut.isInFileOverwrite("/images/filetypes/mp4.gif"));
                assertFalse(cmdOut.isInFileOverwrite("/images/filetype/testfile.bmp"));
        }

        @Test
        public void testAnalyseBeanOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);
                System.out.println(cmdOut.getBeanOverwriteConflicts().toString());

                assertEquals(cmdOut.getBeanOverwriteTotal(), 2);
                assertTrue(cmdOut.isInBeanOverwrite("trashcanSchedulerAccessor"));
                assertTrue(cmdOut.isInBeanOverwrite("trashcanCleaner"));
//                //TO DO: Run against multiple Alfresco versions
//                version = "6.0.0-6.2.2";
//                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
//                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions2);
//
//                assertEquals(cmdOut.getBeanOverwriteConflicts().size(), 2);
//                assertTrue(cmdOut.retrieveOutputLine("trashcanCleaner", "BEAN")
//                        .contains("6.0.0 - 6.2.2"));
//                assertTrue(cmdOut.retrieveOutputLine("trashcanSchedulerAccessor", "BEAN")
//                        .contains("6.0.0 - 6.2.2"));
        }

        @Test
        public void testAnalyseAmpPublicAPI()
        {
                // Run against Alfresco version 6.2.2
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("UseDeprecatedPublicAPI.class"));
                assertTrue(cmdOut.isInPublicAPIConflicts("UseInternalClass.class"));
                assertFalse(cmdOut.isInPublicAPIConflicts("UsePublicAPIClass"));

                // Run against Alfresco version 6.0.0
                version = "6.0.0";
                List<String> cmdOptions1 = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions1);

                assertEquals(cmdOut.getPublicAPITotal(), 1);
                assertFalse(cmdOut.isInPublicAPIConflicts("UseDeprecatedPublicAPI.class"));
                assertTrue(cmdOut.isInPublicAPIConflicts("UseInternalClass.class"));
                assertFalse(cmdOut.isInPublicAPIConflicts("UsePublicAPIClass.class"));

//                //TO DO: Run against multiple Alfresco versions
//                version = "6.0.0-6.2.2";
//                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version, "--verbose");
//                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions2);
//
//                assertEquals(cmdOut.getPublicAPIConflicts().size(), 2);
//                assertTrue(cmdOut.retrieveOutputLine("UseDeprecatedPublicAPI.class", "PUBLIC_API")
//                                        .contains("6.1.0 - 6.2.2"));
//                assertTrue(cmdOut.retrieveOutputLine("UseInternalClass.class", "PUBLIC_API")
//                                        .contains("6.0.0 - 6.2.2"));
//                assertFalse(cmdOut.isInPublicAPIConflicts("UsePublicAPIClass.class"));
        }

        @Test
        public void testAnalyseJarPublicAPI()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.jar");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getPublicAPITotal(), 2);
                assertTrue(cmdOut.isInPublicAPIConflicts("UseDeprecatedPublicAPI.class"));
                assertTrue(cmdOut.isInPublicAPIConflicts("UseInternalClass.class"));
                assertFalse(cmdOut.isInPublicAPIConflicts("UsePublicAPIClass"));
        }

        @Test public void testAnalyseClassPathOverwrite()
        {
                // Run against Alfresco version 6.1.1
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.1.1";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);
                assertEquals(cmdOut.getClassPathConflictsTotal(), 1);
                assertTrue(cmdOut.isClassPathConflicts("ContextLoaderListener.class"));

//                //TO DO: Run against multiple Alfresco versions
//                version = "6.0.0-6.2.2";
//                List<String> cmdOptions2 = List.of(ampResourcePath, "--target-version=" + version);
//                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions2);
//                assertEquals(cmdOut.getClassPathConflictsTotal(), 1);

        }

        @Test
        public void testAnalyseThirdPartyLibs()
        {
                String ampResourcePath = TestResource.getTestResourcePath("analyserTest.amp");
                String version = "6.2.2";
                List<String> cmdOptions = List.of(ampResourcePath, "--target-version=" + version);

                cmdOut = client.runAmpalyserAnalyserCommand(cmdOptions);

                assertEquals(cmdOut.getThirdPartyLibTotal(), 1);
                assertTrue(cmdOut.isInThirdPartyLibConflicts("ThirdPartyLibs.class"));
                assertFalse(cmdOut.isInThirdPartyLibConflicts("AccessControlList.class"));
                assertFalse(cmdOut.isInThirdPartyLibConflicts("OtherThirdPartyLibs.class"));
        }
}
