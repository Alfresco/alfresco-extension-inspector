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
        private List<String> publicAPIConflicts = new ArrayList<>();
        private List<String> thirdPartyLibConflicts = new ArrayList<>();

        public List<String> getPublicAPIConflicts()
        {
                return publicAPIConflicts;
        }

        public void setPublicAPIConflicts(List<String> publicAPIConflicts)
        {
                this.publicAPIConflicts = publicAPIConflicts;
        }

        public List<String> getThirdPartyLibConflicts()
        {
                return thirdPartyLibConflicts;
        }

        public void setThirdPartyLibConflicts(List<String> thirdPartyLibConflicts)
        {
                this.thirdPartyLibConflicts = thirdPartyLibConflicts;
        }

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

        public boolean isInOutput(String message)
        {
                return contains(output, message);
        }

        public boolean isInThirdPartyLibConflicts(String str)
        {
                return contains(thirdPartyLibConflicts, str);
        }

        public boolean isInPublicAPIConflicts(String str)
        {
                return contains(publicAPIConflicts, str);
        }

        public boolean contains(List<String> output, String message)
        {
                return output.stream().anyMatch(s -> s.contains(message));
        }
}
