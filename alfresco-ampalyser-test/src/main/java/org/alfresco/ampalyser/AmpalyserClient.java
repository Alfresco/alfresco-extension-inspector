/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser;

import static java.lang.String.join;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;

import java.util.List;
import java.util.stream.Stream;

import org.alfresco.ampalyser.command.AnalyserCommandReceiver;
import org.alfresco.ampalyser.models.CommandOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmpalyserClient
{
        @Value("${ampalyser.analyser.path}")
        private String pathToAnalyserAmp;

        @Autowired
        private AnalyserCommandReceiver commReceiver;

        public CommandOutput runCommand(List<String> cmdOptions)
        {
                // combine the java -jar command and the given options
                final List<String> commandAndOptions = concat(
                    Stream.of("java", "-jar", pathToAnalyserAmp),
                    cmdOptions.stream()
                ).collect(toUnmodifiableList());

                System.out.println("Running command: " + join(" ", commandAndOptions));
                return commReceiver.runAnalyserCmd(commandAndOptions);
        }
}
