package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.InventoryCommand;

public class CommandImpl implements Command
{
        private CommandReceiver commReceiver;
        private InventoryCommand inventoryCommand;

        public CommandImpl(CommandReceiver commReceiver, InventoryCommand inventoryCommand)
        {
                this.commReceiver = commReceiver;
                this.inventoryCommand = inventoryCommand;
        }

        @Override
        public CommandOutput execute()
        {
                return commReceiver.runInventoryCmd(inventoryCommand);
        }
}
