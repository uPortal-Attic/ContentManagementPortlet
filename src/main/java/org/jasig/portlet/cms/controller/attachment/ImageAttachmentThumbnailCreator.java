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

package org.jasig.portlet.cms.controller.attachment;

import java.io.File;

import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jasig.portlet.cms.model.Attachment;

class ImageAttachmentThumbnailCreator extends AbstractAttachmentThumbnailCreator {
	public ImageAttachmentThumbnailCreator(final Attachment attachment, final ResourceRequest req,
			final PortletContext ctx, final ResourceResponse resp) {
		super(attachment, req, ctx, resp);
	}

	@Override
	protected String getPathToAttachmentImage(final File attachmentFile) {
		return getPathToAttachment(attachmentFile);
	}

	@Override
	protected String getGalleryGroupKey() {
		return "image-gallery" + getResponse().getNamespace();
	}
}
