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

package org.jasig.portlet.cms.model;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jcrom.JcrDataProvider;
import org.jcrom.JcrDataProviderImpl;
import org.jcrom.JcrFile;
import org.jcrom.annotations.JcrNode;
import org.jcrom.annotations.JcrParentNode;
import org.jcrom.annotations.JcrProperty;

@JcrNode(nodeType = "nt:unstructured")
public class Attachment extends JcrFile implements Comparable<Attachment> {
	private static final long	serialVersionUID	= -6710317679666037037L;

	public static Attachment fromFile(final String fileName, final String contentType, final Calendar lastModified,
			final byte[] contents) {

		final Attachment attachment = new Attachment();
		attachment.setMimeType(contentType);
		attachment.setLastModified(lastModified);

		attachment.setFileName(fileName);
		attachment.setContents(contents);

		return attachment;
	}

	@JcrProperty
	private byte[]		contents;

	@JcrProperty(name = "jcr:mimeType")
	private String		contentType;

	@JcrProperty(name = "jcr:encoding")
	private String		encoding;

	@JcrProperty
	private String		fileName;

	@JcrProperty(name = "jcr:lastModified")
	private Calendar	lastModified;

	private final Log	logger	= LogFactory.getLog(getClass());

	@JcrParentNode private Post post;

	@JcrProperty
	private String		title;

	@Override
	public int compareTo(final Attachment o) {
		return getName().compareTo(o.getName());

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Attachment))
			return false;


		final Attachment rhs = (Attachment) obj;
		return new EqualsBuilder().append(getPath(), rhs.getPath()).isEquals();
	}

	public byte[] getContents() {
		return contents;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public Calendar getLastModified() {
		return lastModified;
	}

	@Override
	public String getMimeType() {
		return contentType;
	}

	@Override
	public String getName() {
		if (super.getName() == null)
			setName(getFileName());
		return super.getName();
	}

	public Post getPost() {
		return post;
	}

	public String getTitle() {
		if (StringUtils.isBlank(title))
			setTitle(getFileName());

		return title;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getPath()).toHashCode();
	}

	public void setContents(final byte[] contents) {
		this.contents = contents;
		ensureDataProviderExists();
	}

	@Override
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
		setName(fileName);
	}

	@Override
	public void setLastModified(final Calendar lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public void setMimeType(final String mimeType) {
		contentType = mimeType;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Attachment [name=" + name + ", path=" + path + ", contentType=" + contentType + ", encoding="
				+ encoding + ", fileName=" + fileName + ", lastModified=" + lastModified + ", title=" + title + "]";
	}

	public boolean write(final File file) {
		boolean wroteFile = false;

		try {
			ensureDataProviderExists();
			getDataProvider().writeToFile(file);

			wroteFile = file.exists();
		} catch (final IOException e) {
			if (logger.isErrorEnabled())
				logger.error(e);

			wroteFile = false;
		}

		return wroteFile;

	}

	private void ensureDataProviderExists() {
		setDataProvider(new JcrDataProviderImpl(JcrDataProvider.TYPE.BYTES, getContents()));
	}
}
