package org.alfresco.ampalyser.models;

import java.util.List;

public class Command
{
        private List<String> commandOptions;

        public Command(List<String> commandOptions)
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

        public String toString()
        {
                return String.join(" ", commandOptions);
        }
}
