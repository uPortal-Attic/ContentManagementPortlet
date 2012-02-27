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
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RemovePostAttachmentResponse;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.web.portlet.mvc.AbstractAjaxController;
import org.springframework.web.portlet.bind.PortletRequestUtils;

public class RemovePostAttachmentAjaxController extends AbstractAjaxController {

	private final Log logger = LogFactory.getLog(getClass());

	private RepositoryDao repositoryDao = null;

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	@Override
	protected Map<String, ?> handleAjaxRequestInternal(final ActionRequest request, final ActionResponse resp)
			throws Exception {

		final RemovePostAttachmentResponse response = new RemovePostAttachmentResponse();

		final String attachmentPath = PortletRequestUtils.getRequiredStringParameter(request,
				"attachmentPath");
		final String postPath = PortletRequestUtils.getRequiredStringParameter(request, "postPath");

		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving repository post at " + postPath);
			logger.debug("Attachment path to remove: " + attachmentPath);
		}

		final Post post = getRepositoryDao().getPost(postPath);
		if (post != null) {

			if (logger.isDebugEnabled())
				logger.debug("Retrieved repository post " + post);

			final Iterator<Attachment> it = post.getAttachments().iterator();

			Attachment attachment = null;
			boolean foundAttachment = false;
			while (!foundAttachment && it.hasNext()) {
				attachment = it.next();
				if (attachment.getPath().equals(attachmentPath)) {

					if (logger.isDebugEnabled())
						logger.debug("Removing post attachment: " + attachment);
					it.remove();
					foundAttachment = true;
					response.setRemoveSuccessful(foundAttachment);
				}
			}

			if (foundAttachment) {
				if (logger.isDebugEnabled())
					logger.debug("Saving post");
				getRepositoryDao().setPost(post);

				request.getPortletSession();

				if (logger.isDebugEnabled())
					logger.debug("Saved post: " + post);
			}
		}

		return Collections.singletonMap("response", response);
	}

}
