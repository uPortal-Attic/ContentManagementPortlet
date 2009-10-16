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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositoryDao;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.util.PortletUtils;

public class EditPostController extends SimpleFormController {

	private RepositoryDao _repositoryDao = null;

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		_repositoryDao = repositoryDao;
	}

	private RepositoryDao geRepositoryDao() {
		return _repositoryDao;
	}

	@Override
	protected Object formBackingObject(final PortletRequest request) throws Exception {

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		Post post = geRepositoryDao().getPost(pref.getPortletRepositoryRoot());

		if (post == null) {
			post = new Post();
			post.setAuthor(pref.getPortletUserName());
			post.setPath(pref.getPortletRepositoryRoot());
		}
		return post;
	}

	@Override
	protected void onSubmitAction(final ActionRequest request, final ActionResponse response,
	        final Object command, final BindException errors) throws Exception {
		final Post post = (Post) command;
		geRepositoryDao().setPost(post);
		PortletUtils.clearAllRenderParameters(response);
		response.setPortletMode(PortletMode.VIEW);
	}

}
