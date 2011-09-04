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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.PortletConfiguration;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.multipart.MultipartActionRequest;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.util.PortletUtils;

public class EditPostController extends SimpleFormController {
	private final Log		logger			= LogFactory.getLog(getClass());
	private RepositoryDao	repositoryDao	= null;
	
	@Override
	protected Object formBackingObject(final PortletRequest request) throws Exception {
		
		if (logger.isDebugEnabled())
			logger.debug("Preparing post for edit");
		
		final PortletConfiguration pref = new PortletConfiguration();
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
	
	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}
	
	@Override
	protected void onSubmitAction(final ActionRequest request, final ActionResponse response, final Object command,
			final BindException errors) throws Exception {
		
		if (logger.isDebugEnabled())
			logger.debug("Received post object");
		
		final Post post = (Post) command;
		
		if (logger.isDebugEnabled())
			logger.debug("Post: " + post);
		
		if (logger.isDebugEnabled())
			logger.debug("Submitting post object");
		
		processPostAttachments(request, post);
		
		Boolean postIsScheduled = PortletRequestUtils.getBooleanParameter(request, "postIsScheduled");
		String scheduledDate = request.getParameter("scheduledPostPublishDate");
		
		final PortletConfiguration pref = new PortletConfiguration();
		DateFormat fmt = new SimpleDateFormat(pref.getScheduledPostDateFormat(), request.getLocale());
		Calendar cldr = Calendar.getInstance(request.getLocale());
		fmt.setCalendar(cldr);
		post.setLastModifiedDate(fmt.format(cldr.getTime()));
		post.setLanguage(request.getLocale().getLanguage());
		
		if (postIsScheduled != null && postIsScheduled) {
			if (StringUtils.isBlank(scheduledDate))
				errors.rejectValue("scheduledDate", "invalid.scheduled.post.publish.date");
			else {
				if (logger.isDebugEnabled())
					logger.debug("Post is scheduled to be published on " + scheduledDate);
				
				Date dt = fmt.parse(scheduledDate);
				post.setScheduledDate(fmt.format(dt));
				getRepositoryDao().schedulePost(post, pref.getPortletRepositoryRoot());
			}
		} else
			getRepositoryDao().setPost(post);
		
		if (errors.hasErrors())
			response.setPortletMode(PortletMode.EDIT);
		else {
			if (logger.isDebugEnabled())
				logger.debug("Clearing render parameters");
			PortletUtils.clearAllRenderParameters(response);
			
			if (logger.isDebugEnabled())
				logger.debug("Switching to view mode");
			response.setPortletMode(PortletMode.VIEW);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void processPostAttachments(final ActionRequest request, final Post post) throws IOException {
		final MultipartActionRequest multipartRequest = (MultipartActionRequest) request;
		final Collection<MultipartFile> files = multipartRequest.getFileMap().values();
		
		for (final MultipartFile file : files)
			if (!file.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Uploading attachment file: " + file.getOriginalFilename());
					logger.debug("Attachment file size: " + file.getSize());
				}
				
				Calendar cldr = Calendar.getInstance(request.getLocale());
				cldr.setTime(new Date());
				final Attachment attachment = Attachment.fromFile(file.getOriginalFilename(), file.getContentType(), cldr,
						file.getBytes());
				
				post.getAttachments().add(attachment);
			}
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Map referenceData(final PortletRequest request, final Object command, final Errors errors) throws Exception {
		final Map<String, Object> data = new HashMap<String, Object>();
		
		final PortletConfiguration pref = new PortletConfiguration();
		data.put("portletPreferencesWrapper", pref);
		
		return data;
	}
	
	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
}
