////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2013 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.scanagent.util;

import java.io.IOException;

import com.denimgroup.threadfix.data.entities.Task;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonUtils {

	/**
	 * 
	 * @param jsonText
	 * @return
	 * @throws Exception
	 * @throws RuntimeException
	 */
	@Nullable
    public static Task convertJsonStringToTask(@NotNull String jsonText) throws Exception {
		Task retVal = null;
		
		try {
			retVal = new ObjectMapper().readValue(jsonText, Task.class);
		} catch (JsonParseException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
		return retVal;
	}
}