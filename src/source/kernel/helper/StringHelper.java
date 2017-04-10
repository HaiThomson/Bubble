/**
 * 版权所有 (c) 2017， 吕绪海. 保留所有权利
 * Copyright (c) 2017, Hai Thomson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.kernel.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hai Thomson
 */
public final class StringHelper {
    
    private StringHelper() {
    }
	
    /**
     * Splits a string into substrings based on the supplied delimiter
     * character. Each extracted substring will be trimmed of leading
     * and trailing whitespace.
     *
     * @param str The string to split
     * @param delimiter The character that delimits the string
     * @return A string array containing the resultant substrings
     */
    public static List split(String str, char delimiter) {
        // return no groups if we have an empty string
        if ((str == null) || "".equals(str)) {
            return new ArrayList();
        }

        ArrayList parts = new ArrayList();
        int currentIndex;
        int previousIndex = 0;

        while ((currentIndex = str.indexOf(delimiter, previousIndex)) > 0) {
            String part = str.substring(previousIndex, currentIndex).trim();
            parts.add(part);
            previousIndex = currentIndex + 1;
        }

        parts.add(str.substring(previousIndex, str.length()).trim());

        return parts;
    }
    
    /**
     * @param s the string to be checked
     * @return true if the string parameter contains at least one element 
     */
    public static boolean hasLength(String s) {
    	return (s != null) && (s.length() > 0);
    }
    
    /**
     * @param s the string to be checked
     * @return true if the string parameter is null or doesn't contain any element
     */
    public static boolean isEmpty(String s) {
        return (s == null) || (s.length() == 0);
    }
    
}
