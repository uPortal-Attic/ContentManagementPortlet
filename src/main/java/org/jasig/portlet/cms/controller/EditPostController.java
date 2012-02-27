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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.portlet.PortletRequestContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.JcrRepositoryException;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.portlet.cms.view.PortletView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.multipart.MultipartActionRequest;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.util.PortletUtils;

public class EditPostController extends SimpleFormController {
	private final Log logger = LogFactory.getLog(getClass());

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


	private void processPostAttachments(final ActionRequest request, final Post post) throws Exception {
		if (FileUploadBase.isMultipartContent(new PortletRequestContext(request))) {

			/*
			 * Attachments may have been removed in the edit mode. We must
			 * refresh the session-bound post before updating attachments.
			 */
			final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
			final Post originalPost = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

			if (originalPost != null) {
				post.getAttachments().clear();
				post.getAttachments().addAll(originalPost.getAttachments());
			}

			final MultipartActionRequest multipartRequest = (MultipartActionRequest) request;

			for (int index = 0; index < multipartRequest.getFileMap().size(); index++) {
				final MultipartFile file = multipartRequest.getFile("attachment" + index);


				if (!file.isEmpty()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Uploading attachment file: " + file.getOriginalFilename());
						logger.debug("Attachment file size: " + file.getSize());
					}

					final Calendar cldr = Calendar.getInstance(request.getLocale());
					cldr.setTime(new Date());
					final Attachment attachment = Attachment.fromFile(file.getOriginalFilename(), file.getContentType(), cldr,
							file.getBytes());

					final String title = multipartRequest.getParameter("attachmentTitle" + index);
					attachment.setTitle(title);
					post.getAttachments().add(attachment);
				}

			}
		}
	}

	private void savePost(final ActionRequest request, final BindException errors, final Post post)
			throws PortletRequestBindingException, JcrRepositoryException {
		final Boolean postIsScheduled = PortletRequestUtils.getBooleanParameter(request, "postIsScheduled");
		final String scheduledDate = request.getParameter("scheduledPostPublishDate");

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		final Calendar cldr = Calendar.getInstance(request.getLocale());

		final DateTimeZone zone = DateTimeZone.forTimeZone(cldr.getTimeZone());
		final DateTime today = new DateTime(zone);

		final DateTimeFormatter fmt = DateTimeFormat.forPattern(PortletPreferencesWrapper.DEFAULT_POST_DATE_FORMAT);
		post.setLastModifiedDate(today.toString(fmt));

		post.setLanguage(request.getLocale().getLanguage());

		if (postIsScheduled != null && postIsScheduled) {
			if (StringUtils.isBlank(scheduledDate))
				errors.rejectValue("scheduledDate", "invalid.scheduled.post.publish.date");
			else {
				if (logger.isDebugEnabled())
					logger.debug("Post is scheduled to be published on " + scheduledDate);

				final DateTime dt = DateTime.parse(scheduledDate, fmt);
				post.setScheduledDate(dt.toString(fmt));
				getRepositoryDao().schedulePost(post, pref.getPortletRepositoryRoot());

				ensureRepositoryRootIsScheduled(pref);
			}
		} else
			getRepositoryDao().setPost(post);
	}

	@Override
	protected Object formBackingObject(final PortletRequest request) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("Preparing post for edit");

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		Post post = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

		if (post == null) {
			if (logger.isDebugEnabled())
				logger.debug("No post exists in repository. Configuring blank post");
			post = new Post();
			post.setAuthor(request.getUserPrincipal().getName());
			post.setPath(pref.getPortletRepositoryRoot());
		}

		if (logger.isDebugEnabled())
			logger.debug("Post: " + post);

		return post;
	}

	@Override
	protected void onSubmitAction(final ActionRequest request, final ActionResponse response, final Object command,
			final BindException errors) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("Received post object");

		final Post post = (Post) command;

		if (logger.isDebugEnabled())
			logger.debug("Processing post attachments...");
		processPostAttachments(request, post);

		if (logger.isDebugEnabled()) {
			logger.debug("Post: " + post);
			logger.debug("Submitting post object");
		}
		savePost(request, errors, post);

		if (errors.hasErrors())
			response.setPortletMode(PortletMode.EDIT);
		else {
			if (logger.isDebugEnabled())
				logger.debug("Clearing render parameters");
			PortletUtils.clearAllRenderParameters(response);

			if (!StringUtils.isBlank(post.getScheduledDate()))
				response.setRenderParameter(PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS, Boolean.TRUE.toString());

			if (logger.isDebugEnabled())
				logger.debug("Switching to view mode");

			response.setPortletMode(PortletMode.VIEW);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Map referenceData(final PortletRequest request, final Object command, final Errors errors) throws Exception {
		final Map<String, Object> data = new HashMap<String, Object>();

		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
		data.put("portletPreferencesWrapper", pref);
		data.put("scheduledRepositoryRoots", ScheduledPostsManager.getInstance().getRepositoryRoots());
		return data;
	}
}
