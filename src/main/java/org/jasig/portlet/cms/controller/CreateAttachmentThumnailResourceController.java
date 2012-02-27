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

import java.io.Writer;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.controller.attachment.AbstractAttachmentThumbnailCreator;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.mvc.ResourceAwareController;

public class CreateAttachmentThumnailResourceController extends AbstractController implements ResourceAwareController {
	private final Log	logger	= LogFactory.getLog(getClass());

	private RepositoryDao repositoryDao = null;

	@Override
	public ModelAndView handleResourceRequest(final ResourceRequest request, final ResourceResponse response)
			throws Exception {

		final String postPath = PortletRequestUtils.getRequiredStringParameter(request, "postPath");
		final Writer writer = response.getWriter();

		if (logger.isDebugEnabled())
			logger.debug("Retrieving post to process attachments...");


		final Post post = getRepositoryDao().getPost(postPath);
		if (post != null) {

			if (logger.isDebugEnabled())
				logger.debug("Retrieved post to process attachments...");


			final List<Attachment> list = post.getAttachments();
			final StringBuilder builder = new StringBuilder();

			for (final Attachment attachment : list)
			{
				final AbstractAttachmentThumbnailCreator creator = AbstractAttachmentThumbnailCreator
						.createImageThumbnailCreator(attachment, request, getPortletContext(), response);

				builder.append(creator.generateHtmlFragment());
			}

			if (builder.length() > 0) {

				if (logger.isDebugEnabled())
					logger.debug("Writing output response data...");

				response.setContentType("text/html");
				writer.write(builder.toString());
				writer.flush();
			}
		}

		return null;

	}

	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}


}
