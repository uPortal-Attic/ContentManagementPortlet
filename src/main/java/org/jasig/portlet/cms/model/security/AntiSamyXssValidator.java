/*
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 **/
package org.jasig.portlet.cms.model.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class AntiSamyXssValidator implements XssValidatorService {
	private final Log logger = LogFactory.getLog(getClass());
	private AntiSamy antiSamy = null;

	public AntiSamyXssValidator() {
		InputStream policyFile = null;
		try {
			logger.debug("Loading xss policy file");
			policyFile = getClass().getResourceAsStream("/properties/antiSamyPolicy.xml");
			final Policy policy = Policy.getInstance(policyFile);
			antiSamy = new AntiSamy(policy);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (policyFile != null)
					policyFile.close();
			} catch (final IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> scan(String html) {
		ArrayList<String> errorList = new ArrayList<String>();

		try {

			logger.debug("Unescaping html content");
			html = StringEscapeUtils.unescapeHtml(html);

			logger.debug("Validaing content for xss");
			final CleanResults cr = antiSamy.scan(html);

			if (cr.getNumberOfErrors() > 0) {
				logger.debug("Rejecting content for xss");
				errorList = cr.getErrorMessages();
			}

		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			errorList.add(e.getMessage());
		}
		return errorList;

	}

}
