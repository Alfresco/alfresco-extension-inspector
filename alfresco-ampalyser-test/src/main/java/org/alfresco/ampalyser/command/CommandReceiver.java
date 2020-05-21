package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.Command;
import org.alfresco.ampalyser.models.CommandOutput;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CommandReceiver
{
        CommandOutput cmdOut;

        public CommandOutput runInventoryCmd(Command comm)
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
                while ((line = in.readLine()) != null)
                {
                        cmdOut.getOutput().add(line);
                        System.out.println(line);
                }
        }
}
