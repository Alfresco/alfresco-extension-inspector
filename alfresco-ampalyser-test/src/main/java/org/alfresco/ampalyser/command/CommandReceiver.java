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
import java.util.ArrayList;
import java.util.List;

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
                catch (InterruptedException e) {
                        e.printStackTrace();
                }

                return cmdOut;
        }

        private void recordOutput(Process process, CommandOutput cmdOut) throws IOException
        {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                int lastPublicApiIndex = -1; // last index of the last added public api conflict
                boolean lastLinePublicApi = false; // was the last line a public api conflict?
                int lastBeanIndex = -1; // last index of the last added bean conflict
                boolean lastLineBean = false; // was the last line a bean conflict?

                while ((line = in.readLine()) != null)
                {
                        cmdOut.getOutput().add(line);
                        // Check if public api and is not header
                        if (line.contains("PublicAPI")  && !line.contains("Found usage of internal Alfresco classes!"))
                        {
                                cmdOut.getPublicAPIConflicts().add(line);
                                lastPublicApiIndex = cmdOut.getPublicAPIConflicts().size() - 1;
                                lastLinePublicApi = true;
                                lastLineBean = false;
                        }
                        else if (line.contains("3rd party"))
                        {
                                cmdOut.getThirdPartyLibConflicts().add(line);
                                lastLinePublicApi = false;
                                lastLineBean = false;
                        }
                        else if (line.contains("conflicting with"))
                        {
                                cmdOut.getFileOverwriteConflicts().add(line);
                                lastLinePublicApi = false;
                                lastLineBean = false;
                        }else if (line.contains("in conflict with bean defined"))
                        {
                                cmdOut.getBeanOverwriteConflicts().add(line);
                                lastBeanIndex = cmdOut.getBeanOverwriteConflicts().size() - 1;
                                lastLineBean = true;
                                lastLinePublicApi = false;
                        }else if(line.contains("Conflicting with")){
                                if(lastLinePublicApi){
                                        cmdOut.getPublicAPIConflicts().set(lastPublicApiIndex, cmdOut.getPublicAPIConflicts().get(lastPublicApiIndex) + " "+ line);
                                }
                                lastLinePublicApi = false;
                                lastLineBean = false;
                        }else if(line.contains("Overwriting bean")) {
                                if (lastLineBean)
                                {
                                        cmdOut.getBeanOverwriteConflicts().set(lastBeanIndex, cmdOut.getBeanOverwriteConflicts().get(lastBeanIndex) + " " + line);
                                }
                                lastLinePublicApi = false;
                                lastLineBean = false;
                        }else{
                                lastLinePublicApi = false;
                                lastLineBean = false;
                        }
                        System.out.println(line);
                }

        }
}
