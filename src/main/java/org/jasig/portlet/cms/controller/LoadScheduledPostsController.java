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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.LoadScheduledPostsResponse;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.web.portlet.mvc.AbstractAjaxController;

public class LoadScheduledPostsController extends AbstractAjaxController {

	private RepositoryDao	repositoryDao	= null;
	private final Log		logger			= LogFactory.getLog(getClass());

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	@Override
	protected Map<String, ?> handleAjaxRequestInternal(final ActionRequest request, final ActionResponse response)
			throws Exception {

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		final Collection<Post> results = getRepositoryDao().getScheduledPosts(pref.getPortletRepositoryRoot());
		if (logger.isDebugEnabled())
			if (results != null)
				logger.debug("Total schedued posts count is " + results.size());
			else
				logger.debug("No scheduled posts are available at this time.");
		final LoadScheduledPostsResponse res = new LoadScheduledPostsResponse();
		res.setScheduledPosts(results);
		return Collections.singletonMap("response", res);
	}

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

}
