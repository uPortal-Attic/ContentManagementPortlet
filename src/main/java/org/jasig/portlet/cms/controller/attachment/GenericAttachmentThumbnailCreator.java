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

class GenericAttachmentThumbnailCreator extends AbstractAttachmentThumbnailCreator {
	public GenericAttachmentThumbnailCreator(final Attachment attachment, final ResourceRequest req,
			final PortletContext ctx, final ResourceResponse resp) {
		super(attachment, req, ctx, resp);
	}

	@Override
	protected String getPathToAttachmentImage(final File attachmentFile) {

		String fileName = attachmentFile.getName();

		String imgName = "file.png";
		if (fileName.endsWith(".doc") || fileName.endsWith(".docx"))
			imgName = "wordFile.png";
		else if (fileName.endsWith(".xls") || fileName.endsWith(".xlst"))
			imgName = "excelFile.png";
		else if (fileName.endsWith(".pdf"))
			imgName = "pdfFile.png";
		else if (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".rar"))
			imgName = "compressedFile.png";

		return getRequest().getContextPath() + "/" + IMAGES_DIRECTORY_NAME + "/" + imgName;
	}

	@Override
	protected String getGalleryGroupKey() {
		return "none";
	}

}
