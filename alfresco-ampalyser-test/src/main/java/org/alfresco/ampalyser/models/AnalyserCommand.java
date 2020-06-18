package org.alfresco.ampalyser.models;

import java.util.List;

public class AnalyserCommand
{
        private List<String> commandOptions;

        public AnalyserCommand(List<String> commandOptions)
        {
                this.commandOptions = commandOptions;
        }

        public List<String> getCommandOptions()
        {
                return commandOptions;
        }

        public void addCommandOptions(List<String> commandOptions)
        {
                this.commandOptions.addAll(commandOptions);
        }

        @Override
        public String toString()
        {
                return String.join(" ", commandOptions);
        }
}
