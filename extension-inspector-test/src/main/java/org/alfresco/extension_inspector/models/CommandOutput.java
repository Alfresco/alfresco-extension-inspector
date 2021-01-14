/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.alfresco.extension_inspector.models;

import java.util.ArrayList;
import java.util.List;

public class CommandOutput
{
        private int exitCode;
        private List<String> output = new ArrayList<>();
        private List<String> fileOverwriteConflicts = new ArrayList<>();
        private int fileOverwriteTotal;
        private List<String> beanOverwriteConflicts = new ArrayList<>();
        private int beanOverwriteTotal;
        private List<String> publicAPIConflicts = new ArrayList<>();
        private int publicAPITotal;
        private List<String> classPathConflicts = new ArrayList<>();
        private int classPathConflictsTotal;
        private List<String> thirdPartyLibConflicts = new ArrayList<>();



        private int thirdPartyLibTotal;

        public List<String> getClassPathConflicts()
        {
                return classPathConflicts;
        }

        public void setClassPathConflicts(List<String> classPathConflicts)
        {
                this.classPathConflicts = classPathConflicts;
        }

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

        public List<String> getFileOverwriteConflicts()
        {
                return fileOverwriteConflicts;
        }

        public void setFileOverwriteConflicts(List<String> fileOverwriteConflicts)
        {
                this.fileOverwriteConflicts = fileOverwriteConflicts;
        }

        public List<String> getBeanOverwriteConflicts()
        {
                return beanOverwriteConflicts;
        }

        public void seBeanOverwriteConflicts(List<String> fileOverwriteConflicts)
        {
                this.beanOverwriteConflicts = fileOverwriteConflicts;
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

        public boolean isClassPathConflicts(String str)
        {
                return contains(classPathConflicts, str);
        }

        public boolean isInFileOverwrite(String str)
        {
                return contains(fileOverwriteConflicts, str);
        }

        public boolean isInBeanOverwrite(String str)
        {
                return contains(beanOverwriteConflicts, str);
        }

        private boolean contains(List<String> output, String message)
        {
                return output.stream().anyMatch(s -> s.contains(message));
        }

        public String retrieveOutputLine(String resource, String conflictType)
        {
                switch (conflictType)
                {
                case "PUBLIC_API":
                        return publicAPIConflicts.stream().filter(s -> s.contains(resource)).findFirst().get();
                case "3RD_PARTY_LIBS":
                        return thirdPartyLibConflicts.stream().filter(s -> s.contains(resource)).findFirst().get();
                case "BEAN":
                        return beanOverwriteConflicts.stream().filter(s -> s.contains(resource)).findFirst().get();
                case "FILE_OVERWRITE":
                        return fileOverwriteConflicts.stream().filter(s -> s.contains(resource)).findFirst().get();
                case "CLASS_PATH":
                        return classPathConflicts.stream().filter(s -> s.contains(resource)).findFirst().get();

                }

                return null;
        }

        public int getFileOverwriteTotal()
        {
                return fileOverwriteTotal;
        }

        public void setFileOverwriteTotal(int fileOverwriteTotal)
        {
                this.fileOverwriteTotal = fileOverwriteTotal;
        }

        public int getBeanOverwriteTotal()
        {
                return beanOverwriteTotal;
        }

        public void setBeanOverwriteTotal(int beanOverwriteTotal)
        {
                this.beanOverwriteTotal = beanOverwriteTotal;
        }

        public int getPublicAPITotal()
        {
                return publicAPITotal;
        }

        public void setPublicAPITotal(int publicAPITotal)
        {
                this.publicAPITotal = publicAPITotal;
        }

        public int getClassPathConflictsTotal()
        {
                return classPathConflictsTotal;
        }

        public void setClassPathConflictsTotal(int classPathConflictsTotal)
        {
                this.classPathConflictsTotal = classPathConflictsTotal;
        }
        public int getThirdPartyLibTotal()
        {
                return thirdPartyLibTotal;
        }

        public void setThirdPartyLibTotal(int thirdPartyLibTotal)
        {
                this.thirdPartyLibTotal = thirdPartyLibTotal;
        }
}
