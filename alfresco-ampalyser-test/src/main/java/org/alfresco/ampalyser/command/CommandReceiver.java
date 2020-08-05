/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.CommandModel;
import org.alfresco.ampalyser.models.CommandOutput;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandReceiver
{
        CommandOutput cmdOut;

        public CommandOutput runAnalyserCmd(CommandModel comm)
        {
                cmdOut = new CommandOutput();

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(comm.getCommandOptions());
                try
                {
                        Process process = processBuilder.start();
                        processBuilder.redirectErrorStream(true);
                        recordOutput(process, cmdOut);

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
                boolean isInFileOverwriteConflicts = false;
                boolean isInBeanOverwriteConflicts = false;
                boolean isInPublicAPIConflicts = false;
                boolean isInClassPathConflicts = false;
                boolean isThirdPartyLibraryConflicts = false;
                while ((line = in.readLine()) != null)
                {
                        cmdOut.getOutput().add(line);
                        // Check file overwrite total line
                        Pattern fileOverwriteTotalPattern = Pattern.compile("║FILE_OVERWRITE\\s+│(\\d+)\\s+║");
                        Matcher fileOverwriteTotalMatcher = fileOverwriteTotalPattern.matcher(line);
                        Pattern fileOverwriteRowPattern = Pattern.compile("║(.+)║");
                        Matcher fileOverwriteRowMatcher = fileOverwriteRowPattern.matcher(line);
                        // Check bean overwrite total line
                        Pattern beanOverwriteTotalPattern = Pattern.compile("║BEAN_OVERWRITE\\s+│(\\d+)\\s+║");
                        Matcher beanOverwriteTotalMatcher = beanOverwriteTotalPattern.matcher(line);
                        Pattern beanOverwriteRowPattern = Pattern.compile("║(.+)│(.+)│(.+)║");
                        Matcher beanOverwriteRowMatcher = beanOverwriteRowPattern.matcher(line);
                        // Check publicAPI total line
                        Pattern publicAPITotalPattern = Pattern.compile("║CUSTOM_CODE\\s+│(\\d+)\\s+║");
                        Matcher publicAPITotalMatcher = publicAPITotalPattern.matcher(line);
                        Pattern publicAPIRowPattern = Pattern.compile("║(.+)║");
                        Matcher publicAPIRowMatcher = publicAPIRowPattern.matcher(line);
                        // Check classPath overwrite total line
                        Pattern classPathTotalPattern = Pattern.compile("║CLASSPATH_CONFLICT\\s+│(\\d+)\\s+║");
                        Matcher classPathTotalMatcher = classPathTotalPattern.matcher(line);
                        Pattern classPathRowPattern = Pattern.compile("║(.+)│(.+)║");
                        Matcher classPathRowMatcher = classPathRowPattern.matcher(line);
                        // Check thirdPartyLibrary total line
                        Pattern thirdPartyLibraryTotalPattern = Pattern.compile("║WAR_LIBRARY_USAGE\\s+│(\\d+)\\s+║");
                        Matcher thirdPartyLibraryTotalMatcher = thirdPartyLibraryTotalPattern.matcher(line);
                        Pattern thirdPartyLibraryRowPattern = Pattern.compile("║(.+)");
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
                        else if (line.contains("Extension Resource ID overwriting WAR resource"))
                        {
                                isInFileOverwriteConflicts = true;
                        }
                        else if (fileOverwriteRowMatcher.find() && isInFileOverwriteConflicts)
                        {
                                String filePath = fileOverwriteRowMatcher.group(1);
                                cmdOut.getFileOverwriteConflicts().add(filePath.trim());
                        }
                        else if (line.contains("Extension Bean Resource") && !line.contains("Extension Bean Resource Defining Object"))
                        {
                                isInBeanOverwriteConflicts = true;
                        }
                        else if (beanOverwriteRowMatcher.find() && isInBeanOverwriteConflicts)
                        {
                                String beanPath = beanOverwriteRowMatcher.group(1);
                                if(!beanPath.trim().equals("ID")){
                                        cmdOut.getBeanOverwriteConflicts().add(beanPath.trim());
                                }
                        }
                        else if (line.contains("Extension Resource ID using Custom Code"))
                        {
                                isInPublicAPIConflicts = true;
                        }
                        else if (publicAPIRowMatcher.find() && isInPublicAPIConflicts)
                        {
                                String publicAPIPath = publicAPIRowMatcher.group(1);
                                cmdOut.getPublicAPIConflicts().add(publicAPIPath.trim());
                        }
                        else if (line.contains("Extension Bean Resource Defining Object"))
                        {
                                isInClassPathConflicts = true;
                        }
                        else if (classPathRowMatcher.find() && isInClassPathConflicts)
                        {
                                String classPath = classPathRowMatcher.group(1);
                                cmdOut.getClassPathConflicts().add(classPath.trim());
                        }
                        else if (line.contains("Extension Resource ID using 3rd Party library code"))
                        {
                                isThirdPartyLibraryConflicts = true;
                        }
                        else if (thirdPartyLibraryRowMatcher.find() && isThirdPartyLibraryConflicts)
                        {
                                String thirdPartyLibs = thirdPartyLibraryRowMatcher.group(1);
                                cmdOut.getThirdPartyLibConflicts().add(thirdPartyLibs.trim());
                        }
                        else if (line.length() == 0)
                        {
                                isInFileOverwriteConflicts = false;
                                isInBeanOverwriteConflicts = false;
                                isInPublicAPIConflicts = false;
                                isInClassPathConflicts = false;
                                isThirdPartyLibraryConflicts = false;
                        }
                        System.out.println(line);
                }
        }

}
