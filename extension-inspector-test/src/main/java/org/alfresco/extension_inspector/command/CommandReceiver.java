/*
 * Copyright 2021 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.alfresco.extension_inspector.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.extension_inspector.models.CommandModel;
import org.alfresco.extension_inspector.models.CommandOutput;
import org.springframework.stereotype.Component;

@Component
public class CommandReceiver
{
        private static final String FILE_CONFLICTS_SECTION = "File conflicts";
        private static final String BEAN_NAMING_CONFLICTS_SECTION = "Bean naming conflicts";
        private static final String CUSTOM_CODE_USING_INTERNAL_CLASSES_SECTION = "Custom code using internal classes";
        private static final String CLASSPATH_CONFLICTS_SECTION = "Classpath conflicts";
        private static final String USING_3_RD_PARTY_LIBS_SECTION = "Custom code using 3rd party libraries managed by the ACS repository";

        private CommandOutput cmdOut;


        public CommandOutput runAnalyserCmd(CommandModel comm)
        {
                cmdOut = new CommandOutput();

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(comm.getCommandOptions());
                try
                {
                        Process process = processBuilder.start();
                        processBuilder.redirectErrorStream(true);

                        if (comm.getCommandOptions().contains("--verbose"))
                        {
                                recordOutputVerbose(process, cmdOut);
                        }
                        else{
                                recordOutput(process, cmdOut);

                        }
                        int exitCode = process.waitFor();
                        cmdOut.setExitCode(exitCode);
                        System.out.println("\nExited with error code : " + exitCode);
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                        e.printStackTrace();
                }

                return cmdOut;
        }

        private void recordOutput(Process process, CommandOutput cmdOut) throws IOException
        {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = in.readLine()) != null)
                {
                        cmdOut.getOutput().add(line);
                        // Check file overwrite total line
                        Pattern fileOverwriteTotalPattern = Pattern.compile("\\|FILE_OVERWRITE\\s+\\|(\\d+)\\s+\\|");
                        Matcher fileOverwriteTotalMatcher = fileOverwriteTotalPattern.matcher(line);
                        // Check bean overwrite total line
                        Pattern beanOverwriteTotalPattern = Pattern.compile("\\|BEAN_OVERWRITE\\s+\\|(\\d+)\\s+\\|");
                        Matcher beanOverwriteTotalMatcher = beanOverwriteTotalPattern.matcher(line);
                        // Check publicAPI total line
                        Pattern publicAPITotalPattern = Pattern.compile("\\|ALFRESCO_INTERNAL_USAGE\\s*\\|(\\d+)\\s+\\|");
                        Matcher publicAPITotalMatcher = publicAPITotalPattern.matcher(line);
                        // Check classPath overwrite total line
                        Pattern classPathTotalPattern = Pattern.compile("\\|CLASSPATH_CONFLICT\\s+\\|(\\d+)\\s+\\|");
                        Matcher classPathTotalMatcher = classPathTotalPattern.matcher(line);
                        // Check thirdPartyLibrary total line
                        Pattern thirdPartyLibraryTotalPattern = Pattern.compile("\\|WAR_LIBRARY_USAGE\\s+\\|(\\d+)\\s+\\|");
                        Matcher thirdPartyLibraryTotalMatcher = thirdPartyLibraryTotalPattern.matcher(line);

                        if (fileOverwriteTotalMatcher.find())
                        {
                                int total = Integer.parseInt(fileOverwriteTotalMatcher.group(1));
                                cmdOut.setFileOverwriteTotal(total);
                        }
                        else if (beanOverwriteTotalMatcher.find())
                        {
                                int total = Integer.parseInt(beanOverwriteTotalMatcher.group(1));
                                cmdOut.setBeanOverwriteTotal(total);
                        }
                        else if (publicAPITotalMatcher.find())
                        {
                                int total = Integer.parseInt(publicAPITotalMatcher.group(1));
                                cmdOut.setPublicAPITotal(total);
                        }
                        else if (classPathTotalMatcher.find())
                        {
                                int total = Integer.parseInt(classPathTotalMatcher.group(1));
                                cmdOut.setClassPathConflictsTotal(total);
                        }
                        else if (thirdPartyLibraryTotalMatcher.find())
                        {
                                int total = Integer.parseInt(thirdPartyLibraryTotalMatcher.group(1));
                                cmdOut.setThirdPartyLibTotal(total);
                        }
                        else
                        {
                                findConflicts(line.trim(), in, cmdOut);
                        }
                }
        }

        private void findConflicts(String line, BufferedReader in, CommandOutput result)
            throws IOException
        {
                switch (line)
                {
                case FILE_CONFLICTS_SECTION:
                        in.readLine(); // skipping "--------------"
                        in.readLine(); // skipping section's header

                        while (!(line = in.readLine()).startsWith(
                            "The module management tool will reject modules"))
                        {
                                result.getFileOverwriteConflicts().add(line.trim());
                        }
                        break;
                case BEAN_NAMING_CONFLICTS_SECTION:
                        in.readLine(); // skipping "--------------"
                        in.readLine(); // skipping section's header

                        while (!(line = in.readLine()).startsWith(
                            "Spring beans are the basic building blocks"))
                        {
                                result.getBeanOverwriteConflicts().add(line.trim());
                        }
                        break;
                case CUSTOM_CODE_USING_INTERNAL_CLASSES_SECTION:
                        in.readLine(); // skipping "--------------"
                        in.readLine(); // skipping section's header

                        while (!(line = in.readLine())
                            .startsWith("Internal repository classes"))
                        {
                                result.getPublicAPIConflicts().add(line.trim());
                        }
                        break;
                case CLASSPATH_CONFLICTS_SECTION:
                        in.readLine(); // skipping "--------------"
                        in.readLine(); // skipping section's header

                        while (!(line = in.readLine()).startsWith(
                            "Ambiguous resources on the Java classpath"))
                        {
                                result.getClassPathConflicts().add(line.trim());
                        }
                        break;
                case USING_3_RD_PARTY_LIBS_SECTION:
                        in.readLine(); // skipping "--------------"
                        in.readLine(); // skipping section's header

                        while (!(line = in.readLine()).startsWith(
                            "These 3rd party libraries are managed by the ACS"))
                        {
                                result.getThirdPartyLibConflicts().add(line.trim());
                        }
                        break;
                }
        }

        private void recordOutputVerbose(Process process, CommandOutput cmdOut) throws IOException
        {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean isInFileOverwriteConflicts = false;
                boolean isInBeanOverwriteConflicts = false;
                boolean isInPublicAPIConflicts = false;
                boolean isInClassPathConflicts = false;
                boolean isThirdPartyLibraryConflicts = false;
                while ((line = in.readLine()) != null)
                {
                        line = line.trim();
                        cmdOut.getOutput().add(line);
                        // Check file overwrite total line
                        Pattern fileOverwriteTotalPattern = Pattern.compile("\\|FILE_OVERWRITE\\s+\\|(\\d+)\\s+\\|");
                        Matcher fileOverwriteTotalMatcher = fileOverwriteTotalPattern.matcher(line);
                        Pattern fileOverwriteRowPattern = Pattern.compile("\\|(.+)\\|(.+)\\|(.+)\\|");
                        Matcher fileOverwriteRowMatcher = fileOverwriteRowPattern.matcher(line);
                        // Check bean overwrite total line
                        Pattern beanOverwriteTotalPattern = Pattern.compile("\\|BEAN_OVERWRITE\\s+\\|(\\d+)\\s+\\|");
                        Matcher beanOverwriteTotalMatcher = beanOverwriteTotalPattern.matcher(line);
                        Pattern beanOverwriteRowPattern = Pattern.compile("\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|");
                        Matcher beanOverwriteRowMatcher = beanOverwriteRowPattern.matcher(line);
                        // Check publicAPI total line
                        Pattern publicAPITotalPattern = Pattern.compile("\\|ALFRESCO_INTERNAL_USAGE\\s*\\|(\\d+)\\s+\\|");
                        Matcher publicAPITotalMatcher = publicAPITotalPattern.matcher(line);
                        Pattern publicAPIRowPattern = Pattern.compile("\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|");
                        Matcher publicAPIRowMatcher = publicAPIRowPattern.matcher(line);
                        // Check classPath overwrite total line
                        Pattern classPathTotalPattern = Pattern.compile("\\|CLASSPATH_CONFLICT\\s+\\|(\\d+)\\s+\\|");
                        Matcher classPathTotalMatcher = classPathTotalPattern.matcher(line);
                        Pattern classPathRowPattern = Pattern.compile("\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|");
                        Matcher classPathRowMatcher = classPathRowPattern.matcher(line);
                        // Check thirdPartyLibrary total line
                        Pattern thirdPartyLibraryTotalPattern = Pattern.compile("\\|WAR_LIBRARY_USAGE\\s+\\|(\\d+)\\s+\\|");
                        Matcher thirdPartyLibraryTotalMatcher = thirdPartyLibraryTotalPattern.matcher(line);
                        Pattern thirdPartyLibraryRowPattern = Pattern.compile("\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|(.+)\\|");
                        Matcher thirdPartyLibraryRowMatcher = thirdPartyLibraryRowPattern.matcher(line);

                        if (fileOverwriteTotalMatcher.find())
                        {
                                int total = Integer.parseInt(fileOverwriteTotalMatcher.group(1));
                                cmdOut.setFileOverwriteTotal(total);
                        }
                        else if (beanOverwriteTotalMatcher.find())
                        {
                                int total = Integer.parseInt(beanOverwriteTotalMatcher.group(1));
                                cmdOut.setBeanOverwriteTotal(total);
                        }
                        else if (publicAPITotalMatcher.find())
                        {
                                int total = Integer.parseInt(publicAPITotalMatcher.group(1));
                                cmdOut.setPublicAPITotal(total);
                        }
                        else if (classPathTotalMatcher.find())
                        {
                                int total = Integer.parseInt(classPathTotalMatcher.group(1));
                                cmdOut.setClassPathConflictsTotal(total);
                        }
                        else if (thirdPartyLibraryTotalMatcher.find())
                        {
                                int total = Integer.parseInt(thirdPartyLibraryTotalMatcher.group(1));
                                cmdOut.setThirdPartyLibTotal(total);
                        }
                        else if (FILE_CONFLICTS_SECTION.equals(line))
                        {
                                isInFileOverwriteConflicts = true;
                        }
                        else if (fileOverwriteRowMatcher.find() && isInFileOverwriteConflicts)
                        {
                                String filePath = fileOverwriteRowMatcher.group(1).trim();
                                String version = fileOverwriteRowMatcher.group(2).trim();
                                String entry = String.format("%s,%s", filePath, version);
                                cmdOut.getFileOverwriteConflicts().add(entry);
                        }
                        else if (BEAN_NAMING_CONFLICTS_SECTION.equals(line))
                        {
                                isInBeanOverwriteConflicts = true;
                        }
                        else if (beanOverwriteRowMatcher.find() && isInBeanOverwriteConflicts)
                        {
                                String beanPath = beanOverwriteRowMatcher.group(1).trim();
                                String version = beanOverwriteRowMatcher.group(3).trim();
                                if (!beanPath.equals("overriding WAR Bean"))
                                {
                                        String entry = String.format("%s,%s", beanPath, version);
                                        cmdOut.getBeanOverwriteConflicts().add(entry);
                                }
                        }
                        else if (CUSTOM_CODE_USING_INTERNAL_CLASSES_SECTION.equals(line))
                        {
                                isInPublicAPIConflicts = true;
                                while(!in.readLine().startsWith("+-------------"))
                                {
                                        // go to table's content
                                }
                        }
                        else if (publicAPIRowMatcher.find() && isInPublicAPIConflicts)
                        {
                                String publicAPIPath = publicAPIRowMatcher.group(1).trim();
                                String version = publicAPIRowMatcher.group(4).trim();
                                if (publicAPIPath.length() > 0)
                                {
                                        String entry = String.format("%s,%s", publicAPIPath, version);
                                        cmdOut.getPublicAPIConflicts().add(entry);
                                }
                        }
                        else if (CLASSPATH_CONFLICTS_SECTION.equals(line))
                        {
                                isInClassPathConflicts = true;
                                while(!in.readLine().startsWith("+-------------"))
                                {
                                        // go to table's content
                                }
                        }
                        else if (classPathRowMatcher.find() && isInClassPathConflicts)
                        {
                                String classPath = classPathRowMatcher.group(1).trim();
                                String version = classPathRowMatcher.group(4).trim();
                                if (!classPath.isBlank() && !version.isBlank())
                                {
                                        if (!classPath.endsWith(".class"))
                                        {
                                                classPathRowMatcher = classPathRowPattern
                                                    .matcher(in.readLine());
                                                String remainingPath = classPathRowMatcher.find() ?
                                                    classPathRowMatcher.group(1).trim() :
                                                    "";
                                                classPath = classPath.concat(remainingPath);
                                        }
                                        String entry = String.format("%s,%s", classPath, version);
                                        cmdOut.getClassPathConflicts().add(entry);
                                }
                        }
                        else if (USING_3_RD_PARTY_LIBS_SECTION.equals(line))
                        {
                                isThirdPartyLibraryConflicts = true;
                                while(!in.readLine().startsWith("+-------------"))
                                {
                                        // go to table's content
                                }
                        }
                        else if (thirdPartyLibraryRowMatcher.find() && isThirdPartyLibraryConflicts)
                        {
                                String thirdPartyLibs = thirdPartyLibraryRowMatcher.group(1).trim();
                                String version = thirdPartyLibraryRowMatcher.group(4).trim();
                                if (!thirdPartyLibs.isBlank() && !version.isBlank())
                                {
                                        String entry = String.format("%s,%s", thirdPartyLibs, version);
                                        cmdOut.getThirdPartyLibConflicts().add(entry);
                                }
                        }
                        else if (line.length() == 0)
                        {
                                isInFileOverwriteConflicts = false;
                                isInBeanOverwriteConflicts = false;
                                isInPublicAPIConflicts = false;
                                isInClassPathConflicts = false;
                                isThirdPartyLibraryConflicts = false;
                        }
                }
        }

}
