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
package org.jasig.portlet.cms.controller;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PostValidator implements Validator {

	private final Log _logger = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(final Class arg0) {
		return Post.class.isAssignableFrom(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(final Object arg0, final Errors errors) {

		_logger.debug("Validaing post path " + errors.getFieldValue("path"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "path", "invalid.post.path.empty");

		_logger.debug("Validaing post author " + errors.getFieldValue("author"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "invalid.post.author.empty");

		_logger.debug("Validaing post content " + errors.getFieldValue("content"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "invalid.post.content.empty");

		try {
			_logger.debug("Loading xss policy file");
			final InputStream policyFile = getClass().getResourceAsStream("/properties/antiSamyPolicy.xml");
			final Policy policy = Policy.getInstance(policyFile);
			final AntiSamy as = new AntiSamy();

			_logger.debug("Validaing post content for xss");
			final Post post = (Post) arg0;
			final CleanResults cr = as.scan(post.getContent(), policy);

			if (cr.getNumberOfErrors() > 0) {
				_logger.debug("Rejecting post content for xss");

				new StringBuilder(cr.getNumberOfErrors());
				final ArrayList<String> errorList = cr.getErrorMessages();
				for (final String err : errorList)
					errors.rejectValue("content", "invalid.post.content.xss", new String[] { err }, null);
			}

		} catch (final Exception e) {
			_logger.error(e.getMessage(), e);
			errors.rejectValue("content", "invalid.post.content.xss");
		}

		if (errors.getErrorCount() == 0)
			_logger.debug("Validated post successfully without errors");
		else
			_logger.debug("Rejected post object with " + errors.getErrorCount() + " errors");

	}
}
