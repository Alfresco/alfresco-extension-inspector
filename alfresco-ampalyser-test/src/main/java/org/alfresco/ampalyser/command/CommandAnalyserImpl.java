/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.AnalyserCommand;
import org.alfresco.ampalyser.models.CommandOutput;

public class CommandAnalyserImpl implements Command
{
        private CommandReceiver commReceiver;
        private AnalyserCommand analyserCommand;

        public CommandAnalyserImpl(CommandReceiver commReceiver, AnalyserCommand analyserCommand)
        {
                this.commReceiver = commReceiver;
                this.analyserCommand = analyserCommand;
        }

        @Override
        public CommandOutput execute()
        {
                return commReceiver.runAnalyserCmd(analyserCommand);
        }
}
