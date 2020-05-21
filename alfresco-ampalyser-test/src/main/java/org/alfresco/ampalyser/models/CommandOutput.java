package org.alfresco.ampalyser.models;

import java.util.ArrayList;
import java.util.List;

public class CommandOutput
{
        private int exitCode;
        private List<String> output = new ArrayList<>();

        public List<String> getOutput()
        {
                return output;
        }

        public void setOutput(List<String> output)
        {
                this.output = output;
        }

        public int getExitCode()
        {
                return exitCode;
        }

        public void setExitCode(int exitCode)
        {
                this.exitCode = exitCode;
        }
}
