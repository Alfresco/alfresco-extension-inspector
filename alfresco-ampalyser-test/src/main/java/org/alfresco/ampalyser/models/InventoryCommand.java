package org.alfresco.ampalyser.models;

import org.springframework.stereotype.Component;

import java.util.List;

public class InventoryCommand
{
        private List<String> commandOptions;

        public InventoryCommand(List<String> commandOptions)
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
