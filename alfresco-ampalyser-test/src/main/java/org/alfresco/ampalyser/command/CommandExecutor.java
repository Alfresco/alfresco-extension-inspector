package org.alfresco.ampalyser.command;

import org.alfresco.ampalyser.models.CommandOutput;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor
{
        public CommandOutput execute(ICommand serviceCommand)
        {
                return serviceCommand.execute();
        }
}
