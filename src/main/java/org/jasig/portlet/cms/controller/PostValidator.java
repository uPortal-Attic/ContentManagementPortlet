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

import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.security.AntiVirusService;
import org.jasig.portlet.cms.model.security.XssValidatorService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;

public class PostValidator implements Validator {
	private AntiVirusService antiVirusService = null;
	private XssValidatorService xssValidatorService = null;

	private final Log logger = LogFactory.getLog(getClass());

	public void setAntiVirusService(final AntiVirusService svc) {
		antiVirusService = svc;
	}

	public void setXssValidatorService(final XssValidatorService svc) {
		xssValidatorService = svc;
	}

	@Override
	public boolean supports(@SuppressWarnings("rawtypes") final Class arg0) {
		return Post.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(final Object arg0, final Errors errors) {
		final Post post = (Post) arg0;

		if (logger.isDebugEnabled())
			logger.debug("Validaing post content " + errors.getFieldValue("content"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "invalid.post.content.empty");

		if (post.getContent().trim().isEmpty() && !errors.hasErrors())
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "invalid.post.content.empty");

		final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		final PortletRequest request = ((PortletRequestAttributes) requestAttributes).getRequest();
		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);

		if (pref.isXssValidationEnabled())
			validatePostContent(post, errors);

		validatePostAttachments(post, errors);

		if (logger.isDebugEnabled())
			if (errors.getErrorCount() == 0)
				logger.debug("Validated post successfully without errors");
			else
				logger.debug("Rejected post with " + errors.getErrorCount() + " errors");
	}

	private AntiVirusService getAntiVirusService() {
		return antiVirusService;
	}

	private XssValidatorService getXssValidatorService() {
		return xssValidatorService;
	}

	private void validatePostAttachments(final Post post, final Errors errors) {
		if (post.getAttachments().size() > 0)
			getAntiVirusService();

	}

	@SuppressWarnings("unchecked")
	private void validatePostContent(final Post post, final Errors errors) {
		final List<String> errorList = (List<String>) getXssValidatorService().scan(post.getContent());

		if (errorList != null && errorList.size() > 0)
			for (final String err : errorList)
				errors.rejectValue("content", "invalid.post.content.xss", new String[] { err }, null);
	}
}
