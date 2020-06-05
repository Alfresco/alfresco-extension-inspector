/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.ampalyser.models;

import java.util.ArrayList;
import java.util.List;

public class CommandOutput
{
        private int exitCode;
        private List<String> output = new ArrayList<>();

        public List<String> getOutput()
        {
                return output;
        }

        public void setOutput(List<String> output)
        {
                this.output = output;
        }

        public int getExitCode()
        {
                return exitCode;
        }

        public void setExitCode(int exitCode)
        {
                this.exitCode = exitCode;
        }

        public boolean containsMessage(String message)
        {
                return output.stream().anyMatch(s -> s.contains(message));
        }
}
