/*
 * Copyright 2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.extension_inspector.models;

import java.util.List;

public class CommandModel
{
        private List<String> commandOptions;

        public CommandModel(List<String> commandOptions)
        {
                this.commandOptions = commandOptions;
        }

        public List<String> getCommandOptions()
        {
                return commandOptions;
        }

        public void addCommandOptions(List<String> commandOptions)
        {
                this.commandOptions.addAll(commandOptions);
        }

        @Override
        public String toString()
        {
                return String.join(" ", commandOptions);
        }
}
