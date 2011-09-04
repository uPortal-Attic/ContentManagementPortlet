/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.cms.controller;

import java.util.Collections;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.RemoveScheduledPostResponse;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.web.portlet.mvc.AbstractAjaxController;
import org.springframework.web.portlet.bind.PortletRequestUtils;

public class RemovedScheduledPostAjaxController extends AbstractAjaxController {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private RepositoryDao repositoryDao = null;
	
	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}
	
	@Override
	protected Map<String, ?> handleAjaxRequestInternal(ActionRequest req, ActionResponse res) throws Exception {
		RemoveScheduledPostResponse response = new RemoveScheduledPostResponse();
		final String postPath = PortletRequestUtils.getRequiredStringParameter(req, "path");
		if (!StringUtils.isBlank(postPath)) {
			
			if (logger.isDebugEnabled())
				logger.debug("Scheduled post path to remove: " + postPath);
			getRepositoryDao().removePost(postPath);
			response.setRemoveSuccessful(true);
			response.setPostPath(postPath);
		}
		return Collections.singletonMap("response", response);
	}
	
	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
	
}
