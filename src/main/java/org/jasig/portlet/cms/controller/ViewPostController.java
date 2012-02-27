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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.JcrRepositoryException;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.portlet.cms.view.PortletView;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.mvc.AbstractController;

public class ViewPostController extends AbstractController {
	private final Log		logger			= LogFactory.getLog(getClass());
	private RepositoryDao	repositoryDao	= null;

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

	private void ensureRepositoryRootIsScheduled(final PortletPreferencesWrapper pref) throws JcrRepositoryException {
		if (!ScheduledPostsManager.getInstance().containsRoot(pref.getPortletRepositoryRoot())) {
			final Collection<Post> col = getRepositoryDao().getScheduledPosts(pref.getPortletRepositoryRoot());
			if (col != null && col.size() > 0)
				ScheduledPostsManager.getInstance().addRepositoryRoot(pref.getPortletRepositoryRoot());
		}
	}

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	@Override
	protected ModelAndView handleRenderRequestInternal(final RenderRequest request, final RenderResponse response)
			throws Exception {

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);

		ensureRepositoryRootIsScheduled(pref);

		if (logger.isDebugEnabled())
			logger.debug("Retrieving repository post");

		final Post post = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("post", post);
		map.put("isAuthorRole", request.isUserInRole("author"));
		map.put("portletPreferences", pref);

		final Boolean postScheduledSuccess = PortletRequestUtils.getBooleanParameter(request,
				PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS, Boolean.FALSE);
		map.put(PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS, postScheduledSuccess);

		if (logger.isDebugEnabled())
			logger.debug("Returning repository post " + post);

		final ModelAndView view = new ModelAndView(PortletView.VIEW_POST_VIEW, map);
		return view;

	}

}
