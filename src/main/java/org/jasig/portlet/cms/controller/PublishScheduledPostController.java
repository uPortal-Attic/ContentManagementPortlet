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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.PortletConfiguration;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.mvc.AbstractController;

public class PublishScheduledPostController extends AbstractController {
	private RepositoryDao	repositoryDao	= null;
	private final Log		logger			= LogFactory.getLog(getClass());
	
	private RepositoryDao geRepositoryDao() {
		return repositoryDao;
	}
	
	@Override
	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		final PortletConfiguration pref = new PortletConfiguration();
		
		final String postPath = PortletRequestUtils.getRequiredStringParameter(request, "path");
		final String toNewPath = pref.getPortletRepositoryRoot();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Scheduled post is at " + postPath);
			logger.debug("Moving post to path " + toNewPath);
		}
		
		Post post = geRepositoryDao().getPost(postPath);
		if (post != null) {
			if (logger.isDebugEnabled())
				logger.debug("Retrieved scheduled post " + post);
			
			geRepositoryDao().removePost(post.getPath());
			post.setPath(toNewPath);
			
			post.setRateCount(0);
			post.setRate(0);

			geRepositoryDao().setPost(post);
			
			response.setPortletMode(PortletMode.VIEW);
		} else
			throw new PortletException(postPath + " does not exist");
	}
	
	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
}