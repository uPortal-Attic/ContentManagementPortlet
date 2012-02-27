/**
 * Licensed to Jasig under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. Jasig licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jasig.portlet.cms.controller;

import java.util.Collections;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.web.portlet.mvc.AbstractAjaxController;
import org.springframework.web.portlet.bind.PortletRequestUtils;

public class RemoveScheduledRepositoryRootAjaxController extends AbstractAjaxController {

	private final Log	logger	= LogFactory.getLog(getClass());

	@Override
	protected Map<String, ?> handleAjaxRequestInternal(final ActionRequest req, final ActionResponse res)
			throws Exception {

		final String rootPath = PortletRequestUtils.getRequiredStringParameter(req, "path");
		if (!StringUtils.isBlank(rootPath)) {

			if (logger.isDebugEnabled())
				logger.debug("Scheduled repository path to remove: " + rootPath);
			ScheduledPostsManager.getInstance().removeRepositoryRoot(rootPath);
		}
		return Collections.singletonMap("response", null);
	}

}
