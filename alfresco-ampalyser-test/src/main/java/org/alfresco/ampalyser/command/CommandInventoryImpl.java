/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.models.CommandModel;

public class CommandInventoryImpl implements Command
{
        private CommandReceiver commReceiver;
        private CommandModel commandModel;

        public CommandInventoryImpl(CommandReceiver commReceiver, CommandModel commandModel)
        {
                this.commReceiver = commReceiver;
                this.commandModel = commandModel;
        }

        @Override
        public CommandOutput execute()
        {
                return commReceiver.runAnalyserCmd(commandModel);
        }
}
