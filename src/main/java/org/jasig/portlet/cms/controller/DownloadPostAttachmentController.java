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

import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class DownloadPostAttachmentController extends AbstractController {

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
	throws Exception {

		final HttpSession session = request.getSession();
		final Attachment attachment = (Attachment) session.getAttribute("attachment");

		if (logger.isDebugEnabled())
			logger.debug("Attempting to download attachment: " + attachment);
		response.setContentType("application/x-download");

		if (logger.isDebugEnabled())
			logger.debug("Set content type to: " + response.getContentType());

		final String encoding = response.getCharacterEncoding();
		if (logger.isDebugEnabled())
			logger.debug("Encoded file name based on: " + encoding);

		final String fileName = URLEncoder.encode(attachment.getFileName(), encoding);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		if (logger.isDebugEnabled())
			logger.debug("Downloading file: " + fileName);

		final OutputStream out = response.getOutputStream();
		out.write(attachment.getContents());
		out.flush();

		if (logger.isDebugEnabled())
			logger.debug("Clearing session attribute");
		session.setAttribute("attachment", null);
		return null;
	}

}
