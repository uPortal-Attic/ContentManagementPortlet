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

package org.jasig.portlet.cms.util;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;

public final class PortletUtilities {

    private final static Log logger = LogFactory.getLog(PortletUtilities.class);

    public static void downloadPostAttachment(Post post, String attachmentPath, ActionRequest request, ActionResponse response) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("Attachment path to download: " + attachmentPath);

        if (post != null) {

            if (logger.isDebugEnabled())
                logger.debug("Retrieved repository post " + post);

            final Iterator<Attachment> it = post.getAttachments().iterator();

            Attachment attachment = null;
            while (it.hasNext()) {
                attachment = it.next();
                if (attachment.getPath().equals(attachmentPath)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Found post attachment: " + attachment);

                    request.getPortletSession().setAttribute("attachment", attachment, PortletSession.APPLICATION_SCOPE);

                    final String contextPath = request.getContextPath();

                    final String url = new StringBuilder().append(contextPath).append("/").append("downloadPostAttachment").toString();

                    if (logger.isDebugEnabled())
                        logger.debug("Url is: " + url);

                    response.sendRedirect(url);
                }
            }

        }
    }

	public static StringBuilder appendFormat(StringBuilder bldr, final String format, final Object... args) {

		if (bldr == null)
			bldr = new StringBuilder();

		final String formattedMsg = MessageFormat.format(format, args);
		bldr.append(formattedMsg);
		return bldr;
	}
}
