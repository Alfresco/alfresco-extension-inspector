package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.Command;
import org.alfresco.ampalyser.models.CommandOutput;

public class InventoryCommand implements ICommand
{
        private CommandReceiver commReceiver;
        private Command command;

        public InventoryCommand(CommandReceiver commReceiver, Command command)
        {
                this.commReceiver = commReceiver;
                this.command = command;
        }

        @Override
        public CommandOutput execute()
        {
                return commReceiver.runInventoryCmd(command);
        }
}
