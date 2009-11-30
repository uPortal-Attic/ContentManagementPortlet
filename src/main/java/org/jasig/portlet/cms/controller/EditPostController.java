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

import java.util.Calendar;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jcrom.JcrDataProvider;
import org.jcrom.JcrDataProviderImpl;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.multipart.MultipartActionRequest;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.util.PortletUtils;

public class EditPostController extends SimpleFormController {
	private final Log logger = LogFactory.getLog(getClass());
	private RepositoryDao repositoryDao = null;

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

	private RepositoryDao geRepositoryDao() {
		return repositoryDao;
	}

	@SuppressWarnings("unchecked")
	private void processPostAttachments(final Post post, final ActionRequest request) throws Exception {
		final MultipartActionRequest multipartRequest = (MultipartActionRequest) request;
		final Map<String, MultipartFile> filesMap = multipartRequest.getFileMap();

		post.getAttachments().clear();
		for (final String fileName : filesMap.keySet()) {
			final MultipartFile file = filesMap.get(fileName);

			if (!file.isEmpty()) {
				final Attachment attachment = new Attachment();
				attachment.setDataProvider(new JcrDataProviderImpl(JcrDataProvider.TYPE.STREAM, file
				        .getInputStream()));
				attachment.setName(file.getOriginalFilename());
				attachment.setMimeType(file.getContentType());
				attachment.setLastModified(Calendar.getInstance());
				post.getAttachments().add(attachment);
			}
		}

	}

	@Override
	protected Object formBackingObject(final PortletRequest request) throws Exception {

		logger.debug("Preparing post for edit");
		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		Post post = geRepositoryDao().getPost(pref.getPortletRepositoryRoot());

		if (post == null) {
			logger.debug("No post exists in repository. Configuring blank post");
			post = new Post();
			post.setAuthor(pref.getPortletUserName());
			post.setPath(pref.getPortletRepositoryRoot());
		}

		logger.debug("Post is at " + post.getPath());
		logger.debug("Post content is " + post.getContent());
		logger.debug("Post author is " + post.getAuthor());
		logger.debug("Post date is " + post.getDate());

		return post;
	}

	@Override
	protected void onSubmitAction(final ActionRequest request, final ActionResponse response,
	        final Object command, final BindException errors) throws Exception {
		logger.debug("Received post object");
		final Post post = (Post) command;

		processPostAttachments(post, request);

		logger.debug("Post is at " + post.getPath());
		logger.debug("Post content is " + post.getContent());
		logger.debug("Post author is " + post.getAuthor());
		logger.debug("Post date is " + post.getDate());

		logger.debug("Submitting post object");
		geRepositoryDao().setPost(post);

		logger.debug("Clearing render parameters");
		PortletUtils.clearAllRenderParameters(response);

		logger.debug("Switing to view mode");
		response.setPortletMode(PortletMode.VIEW);

	}
}
