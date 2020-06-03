package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.CommandOutput;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor
{
        public CommandOutput execute(Command serviceCommand)
        {
                return serviceCommand.execute();
        }
}
