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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.util.PortletUtils;

public abstract class AbstractAttachmentThumbnailCreator {
	private static final String		TEMP_DIRECTORY_NAME		= "temp";
	protected static final String	IMAGES_DIRECTORY_NAME	= "images";

	public static AbstractAttachmentThumbnailCreator createImageThumbnailCreator(
			final Attachment attachment, final ResourceRequest req, final PortletContext ctx,
			final ResourceResponse resp) {

		final String mimeType = attachment.getMimeType();

		AbstractAttachmentThumbnailCreator creator = null;

		if (mimeType.indexOf("image/") != -1)
			creator = new ImageAttachmentThumbnailCreator(attachment, req, ctx, resp);
		else if (mimeType.indexOf("video/") != -1)
			creator = new VideoAttachmentThumbnailCreator(attachment, req, ctx, resp);
		else if (mimeType.indexOf("audio/") != -1)
			creator = new AudioAttachmentThumbnailCreator(attachment, req, ctx, resp);
		else
			creator = new GenericAttachmentThumbnailCreator(attachment, req, ctx, resp);

		return creator;

	}

	private Attachment			attachment		= null;
	private final Log			logger			= LogFactory.getLog(getClass());
	private PortletContext		portletContext	= null;
	private ResourceRequest		request			= null;
	private ResourceResponse	response		= null;

	public AbstractAttachmentThumbnailCreator(final Attachment attachment,
			final ResourceRequest request, final PortletContext ctx, final ResourceResponse resp) {

		setResponse(resp);
		setAttachment(attachment);
		setRequest(request);
		setPortletContext(ctx);
	}

	public StringBuilder generateHtmlFragment() throws Exception {
		StringBuilder builder = new StringBuilder();

		final File tmpFile = File.createTempFile("cmp", "." + getAttachment().getFileName(),
				getTemporaryDirectory());
		tmpFile.deleteOnExit();

		getAttachment().write(tmpFile);
		if (logger.isDebugEnabled())
			logger.debug("Created attachment file at " + tmpFile.getAbsolutePath());

		final String rel = getGalleryGroupKey();

		builder = PortletUtils.appendFormat(builder, "<li class=''{0}''>",
				getListItemCssClassName());

		final String path = StringUtils.defaultIfBlank(getLinkElementAttachmentPath(tmpFile), "");

		builder = PortletUtils.appendFormat(builder,
				"<a id=''{0}'' href=''{1}'' rel=''{2}'' title=''{3}'' path=''{4}'' type=''{5}''>",
				getLinkElementId(), getPathToAttachment(tmpFile), rel, StringEscapeUtils
				.escapeHtml(getAttachment().getTitle()), path, getAttachment()
				.getMimeType());

		builder = PortletUtils.appendFormat(builder,
				"<img id=''{0}'' class=''{1}'' src=''{2}'' alt=''{3}'' />", getImageElementId(),
				getListItemImageCssClassName(), getPathToAttachmentImage(tmpFile),
				StringEscapeUtils.escapeHtml(getAttachment().getName()));

		builder.append("</a>");
		builder.append("</li>");

		return builder;
	}

	private String getListItemCssClassName() {
		return "thumbnail-list-item";
	}

	private String getListItemImageCssClassName() {
		return "thumbnail";
	}

	private File getTemporaryDirectory() {
		final File tempDir = new File(getPortletContext().getRealPath(TEMP_DIRECTORY_NAME));
		if (!tempDir.exists()) {
			tempDir.mkdir();

			if (logger.isDebugEnabled())
				logger.debug("Created temp directory at " + tempDir.getAbsolutePath());
		}
		return tempDir;
	}

	private void setAttachment(final Attachment attachment) {
		this.attachment = attachment;
	}

	private void setPortletContext(final PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	private void setRequest(final ResourceRequest request) {
		this.request = request;
	}

	private void setResponse(final ResourceResponse response) {
		this.response = response;
	}

	protected String getImageElementId() {
		return "thumbnailImageId" + getResponse().getNamespace();
	}

	protected String getLinkElementAttachmentPath(final File attachmentFile) {
		return "";
	}

	protected String getLinkElementId() {
		return "thumbnailLinkId" + getResponse().getNamespace();
	}

	protected String getPathToAttachment(final File attachmentFile) {
		final String href = getRequest().getContextPath() + "/" + TEMP_DIRECTORY_NAME + "/"
				+ attachmentFile.getName();
		return href;

	}

	protected abstract String getPathToAttachmentImage(File attachmentFile);

	protected abstract String getGalleryGroupKey();

	Attachment getAttachment() {
		return attachment;
	}

	PortletContext getPortletContext() {
		return portletContext;
	}

	ResourceRequest getRequest() {
		return request;
	}

	ResourceResponse getResponse() {
		return response;
	}

}