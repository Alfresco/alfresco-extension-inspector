package org.alfresco.ampalyser;

import org.alfresco.ampalyser.command.CommandReceiver;
import org.alfresco.ampalyser.command.InventoryCommand;
import org.alfresco.ampalyser.command.CommandExecutor;
import org.alfresco.ampalyser.models.Command;
import org.alfresco.ampalyser.models.CommandOutput;
import org.alfresco.ampalyser.util.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class AmpalyserClient
{
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        public CommandOutput runInventoryAnalyserCommand(List<String> cmdOptions)
        {
                Command cmd = ctx.getBean(Command.class);
                CommandReceiver commReceiver = ctx.getBean(CommandReceiver.class);
                CommandExecutor executor = ctx.getBean(CommandExecutor.class);

                // Add additional inventory command options
                cmd.addCommandOptions(cmdOptions);
                InventoryCommand invCmd = new InventoryCommand(commReceiver, cmd);

                System.out.println("Running command: " + cmd.toString());
                CommandOutput cmdOut = executor.execute(invCmd);

                return cmdOut;
        }
}
