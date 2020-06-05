/*
 * Copyright 2015-2020 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.ampalyser.analyser.result;

/**
 * Represents the result that a {@link org.alfresco.ampalyser.analyser.checker.Checker} can find.
 * @author Lucian Tuca
 */
public interface Result
{
    enum Type
    {
        FILE_OVERWRITE(Constants.FILE_OVERWRITE);

        public static class Constants
        {
            public static final String FILE_OVERWRITE= "FILE_OVERWRITE";
        }

        private final String value;

        Type(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }
}
