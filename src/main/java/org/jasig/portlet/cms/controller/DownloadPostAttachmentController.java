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

import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.portlet.cms.model.Attachment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/downloadPostAttachment")
public class DownloadPostAttachmentController extends AbstractPortletController {
	
    @RequestMapping
    protected void handleRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		
		final HttpSession session = request.getSession();
		final Attachment attachment = (Attachment) session.getAttribute("attachment");
		
        logDebug("Attempting to download attachment: " + attachment);

		response.setContentType("application/x-download");
		
        logDebug("Set content type to: " + response.getContentType());
		
		final String encoding = response.getCharacterEncoding();

        logDebug("Encoded file name based on: " + encoding);
		
		final String fileName = URLEncoder.encode(attachment.getFileName(), encoding);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
        logDebug("Downloading file: " + fileName);
		
		final OutputStream out = response.getOutputStream();
		out.write(attachment.getContents());
		out.flush();
		
        logDebug("Clearing session attribute");
		session.setAttribute("attachment", null);

	}
	
}
